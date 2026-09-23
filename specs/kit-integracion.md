# Kit de integración — para los otros seis equipos

**Módulo de Seguridad y Autenticación (G7)** · Marketplace Multicanal · UNMSM 2026-II

Si vienes de otro equipo, esta es la única página que necesitas leer. El
contrato completo está en [`openapi.yaml`](openapi.yaml); esto es cómo usarlo.

> **Lo esencial en tres frases.** Vuestros usuarios inician sesión contra
> nosotros y reciben un token. Ustedes **verifican ese token en local** con
> nuestra clave pública, sin llamarnos. Solo antes de operaciones sensibles nos
> preguntan por el estado actual del usuario.

---

## 1. Empiecen ahora, no cuando esté implementado

El contrato está congelado antes que el código. Levanten el entorno simulado:

```bash
git clone https://github.com/Taller-SW-Web/Modulo-de-Seguridad.git
cd Modulo-de-Seguridad
npx @stoplight/prism-cli mock specs/openapi.yaml -p 4010
```

Comprueben que responde:

```bash
curl http://localhost:4010/auth/.well-known/jwks.json
```

Solo hace falta **Node 18 o superior**. No se instala nada: `npx` lo descarga y
lo ejecuta.

### ⚠️ El prefijo `/api/v1`

Prism sirve las rutas tal como están escritas en el contrato, **sin el
prefijo**. El backend real sí lo lleva:

| | URL base |
|---|---|
| Mock (Prism) | `http://localhost:4010` |
| Backend real | `http://localhost:8080/api/v1` |

**Parametricen la URL base en su configuración** y al cambiar de uno a otro
no tocarán ni una línea de código.

---

## 2. Vía 1 — Validar el token en local *(el 99% de su tráfico)*

El frontend de ustedes les manda el token en `Authorization: Bearer <token>`. Lo
verifican con nuestra clave pública. **No nos llaman.**

### Spring Boot

Una línea de configuración, gracias al documento de descubrimiento:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/api/v1/auth
```

**Contra el mock eso no sirve**, y conviene saber por qué: el mock sí publica el
documento de descubrimiento, pero el `issuer` que declara apunta al backend
real, y Spring rechaza la configuración cuando no coincide con la URL que le
dieron. Contra el mock, apunten directamente al JWKS:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:4010/auth/.well-known/jwks.json
```

Nuestros roles viajan en el claim `roles`, no en `scope`, así que necesitan un
converter para que `hasRole()` funcione:

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return roles == null ? List.of()
            : roles.stream()
                   .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                   .map(GrantedAuthority.class::cast)
                   .toList();
    });

    http.oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)));
    return http.build();
}
```

Y a partir de ahí:

```java
@PreAuthorize("hasRole('GESTOR_DESPACHO')")
public void reasignarReparto(UUID pedidoId) { ... }
```

### Node

```js
import { createRemoteJWKSet, jwtVerify } from 'jose';

// Una sola vez al arrancar: la librería cachea las claves por ustedes.
const jwks = createRemoteJWKSet(
  new URL('http://localhost:4010/auth/.well-known/jwks.json')
);

export async function verificarToken(authorizationHeader) {
  const token = authorizationHeader.replace(/^Bearer /, '');
  const { payload } = await jwtVerify(token, jwks, {
    issuer: 'auth-service',
  });
  return payload; // { sub, email, roles, permisos, tipo, exp, jti }
}
```

### .NET

`options.Authority` hace descubrimiento, así que sirve contra el backend real.
Contra el mock, fijad `options.MetadataAddress` al JWKS o resolved la clave a
mano, por el mismo motivo que en Spring.

```csharp
builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.Authority = "http://localhost:8080/api/v1/auth";
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidIssuer = "auth-service",
            RoleClaimType = "roles"   // nuestros roles no van en "scope"
        };
    });
```

### Reglas de la vía 1

1. **Cacheen el JWKS.** Descargarlo en cada validación anula toda la ventaja y
   nos convierte en su punto único de fallo. Las tres librerías de arriba
   lo cachean solas.
2. **Si el JWKS no responde y tienen copia en caché, sigan validando con ella.**
   Rechacen solo cuando llegue un token con un `kid` que no conozcan y el JWKS
   no responda.
3. **Durante una rotación conviven dos claves.** La antigua se mantiene
   publicada al menos 24 horas. El `kid` de la cabecera del token dice cuál usar.

---

## 3. Qué trae el token

```json
{
  "sub": "11111111-1111-1111-1111-111111111111",
  "email": "maria@ejemplo.com",
  "roles": ["CLIENTE"],
  "permisos": [],
  "tipo": "acceso",
  "iss": "auth-service",
  "iat": 1789270000,
  "exp": 1789270900,
  "jti": "8f2a91c4-b7e5-4d03-a1f6-2c9b8e0d4a77"
}
```

Los seis códigos de rol del marketplace:

| Código | Perfil |
|---|---|
| `CLIENTE` | Cliente que compra |
| `VENDEDOR` | Vendedor que atiende en tienda |
| `ADMIN_VENTAS` | Administra pedidos, anulaciones y reembolsos |
| `GESTOR_DESPACHO` | Administra rutas, zonas y entregas |
| `GESTOR_COMERCIAL` | Administra catálogo, precios y promociones |
| `ADMIN_SISTEMA` | Personal de la plataforma |

> **`permisos` todavía no lleva los de ustedes.** Hoy solo contiene los de
> nuestro módulo (`usuario.ver`, `auditoria.ver`…), definidos en SPEC-11. Los
> de cada módulo (`pedido.crear`, `producto.editar`…) se acuerdan con cada
> equipo. **Autorizad por `roles` mientras tanto.** Cuando se acuerden, la
> lista crecerá sin que cambie nada del contrato: añadir permisos es
> compatible. Fijaos en el punto: los permisos se escriben `recurso.accion`;
> los dos puntos son de los scopes.

---

## 4. Vía 2 — Preguntarnos, cuando hace falta

### Pidan su token de servicio

Cada equipo tiene su propio `client_id` y `client_secret`, para que la auditoría
diga qué módulo consultó qué dato y para poder revocar uno sin afectar a los
otros cinco.

```bash
curl -X POST http://localhost:4010/auth/token \
  -d 'grant_type=client_credentials' \
  -d 'client_id=modulo-despacho' \
  -d 'client_secret=secreto-de-prueba'
```

### Cuándo usar introspección y cuándo no

Es la pregunta que más cuesta, así que va con una regla concreta:

> **Si la operación mueve dinero, cancela algo o cambia permisos, introspeccionad.
> Para todo lo demás, validen en local.**

| Operación | Vía | Por qué |
|---|---|---|
| Listar productos, ver el catálogo | Local | Un desfase de 15 minutos no hace daño |
| Añadir al carrito | Local | Reversible |
| Consultar el estado de un pedido | Local | Solo lectura |
| **Anular un pedido** | **Introspección** | Irreversible y con impacto económico |
| **Aprobar un reembolso** | **Introspección** | Mueve dinero |
| **Cambiar un precio** | **Introspección** | Afecta a todos los canales |
| **Asignar o revocar un rol** | **Introspección** | Escala privilegios |

```bash
curl -X POST http://localhost:4010/auth/introspeccion \
  -H 'Authorization: Bearer <su-token-de-servicio>' \
  -H 'Content-Type: application/json' \
  -d '{"token":"<token-del-usuario>"}'
```

La respuesta es **siempre `200`**; el resultado va en `activo`:

```json
{ "activo": false, "motivo": "USUARIO_INACTIVO" }
```

`activo: false` con motivo `USUARIO_INACTIVO` significa que la firma es
correcta pero el token **no es utilizable**. Negad la operación.

### Consultar datos de usuario

```bash
# Uno
curl http://localhost:4010/usuarios/11111111-1111-1111-1111-111111111111 \
  -H 'Authorization: Bearer <token-de-servicio>'

# Hasta 100 de una vez — no falla por los que no existan
curl -X POST http://localhost:4010/usuarios/lote \
  -H 'Authorization: Bearer <token-de-servicio>' \
  -H 'Content-Type: application/json' \
  -d '{"ids":["1111...","9999..."]}'
```

El lote devuelve los encontrados en `usuarios` y los demás en `noEncontrados`.
Un reporte de 50 clientes en el que uno se dio de baja sigue funcionando.

---

## 5. Qué scope pide cada módulo

Los scopes se conceden por escrito en la sincronización entre equipos. Si
necesitan un campo que su scope no cubre, pídanlo en esa reunión.

| Módulo | Scopes | Para qué |
|---|---|---|
| Marketplace Cliente | `usuarios:leer` | Mostrar el nombre del comprador |
| Chatbot Cliente | `usuarios:leer` | Confirmar identidad en la conversación |
| Retail Vendedor | `usuarios:leer`, `roles:leer` | Buscar clientes y comprobar el rol del vendedor |
| Ventas y Postventa | `usuarios:leer`, `usuarios:leer:documento`, `tokens:introspeccion` | Emitir boletas y autorizar anulaciones |
| Despacho y Entrega | `usuarios:leer`, `direcciones:leer` | Entregar el paquete |
| Productos y Ofertas | `tokens:introspeccion`, `roles:leer` | Autorizar cambios de precio |

### El documento se enmascara según el scope

Sin `usuarios:leer:documento` recibirán `documentoEnmascarado: "*****234"` en
vez de `documento: "45781234"`. **No es un error y no hay que reintentarlo**: se
responde con menos, no con un fallo.

---

## 6. Datos de prueba del entorno simulado

Publicados a propósito, para que prueben sus caminos de error sin
pedirnos nada.

| Concepto | Valor |
|---|---|
| Cliente activo | `11111111-1111-1111-1111-111111111111` |
| Vendedor activo | `22222222-2222-2222-2222-222222222222` |
| Usuario desactivado | `33333333-3333-3333-3333-333333333333` |
| Identificador inexistente | `99999999-9999-9999-9999-999999999999` |
| `client_id` | `modulo-ventas`, `modulo-despacho`, `modulo-productos`, `modulo-marketplace`, `modulo-chatbot`, `modulo-retail` |
| `client_secret` de prueba | `secreto-de-prueba` |

### Forzar cualquier respuesta, incluidos los errores

El mock obedece la cabecera `Prefer`. Esto es lo que les permite probar lo que
pasa cuando algo sale mal, **antes** de que les pase en la demo:

```bash
# Credenciales inválidas
curl -X POST http://localhost:4010/auth/login \
  -H 'Content-Type: application/json' -H 'Prefer: code=401' \
  -d '{"correo":"x@y.com","contrasena":"mala"}'

# Un usuario desactivado
curl -X POST http://localhost:4010/auth/introspeccion \
  -H 'Content-Type: application/json' -H 'Prefer: example=usuarioDesactivado' \
  -d '{"token":"loquesea"}'

# Login de una cuenta con segundo factor
curl -X POST http://localhost:4010/auth/login \
  -H 'Content-Type: application/json' -H 'Prefer: example=conMfa' \
  -d '{"correo":"carlos@ejemplo.com","contrasena":"Marketplace2026!"}'
```

Los nombres de ejemplo están junto a cada endpoint en `openapi.yaml`.

---

## 7. Si tienen su propia pantalla de login

Pueden tenerla. Lo que no pueden es guardar contraseñas ni usuarios: el
formulario hace `POST` a nuestra API.

```bash
curl -X POST http://localhost:4010/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"correo":"maria@ejemplo.com","contrasena":"Marketplace2026!"}'
```

Dos respuestas posibles, ambas con `200`:

```json
// Sin segundo factor
{ "accessToken": "...", "refreshToken": "...", "tokenType": "Bearer",
  "expiresIn": 900, "usuario": { "id": "...", "roles": ["CLIENTE"] } }

// Con segundo factor activo — todavía no hay tokens
{ "mfaRequerido": true, "challengeToken": "chg_...", "canal": "EMAIL", "expiraEn": 300 }
```

Si llega `mfaRequerido`, pedís el código con `POST /auth/otp/solicitar` y lo
canjean con `POST /auth/otp/verificar`.

**El acceso dura 15 minutos.** Renovadlo con `POST /auth/refresh` antes de que
venza. El refresco **se usa una sola vez**: cada renovación les devuelve uno
nuevo, y reutilizar uno viejo cierra todas las sesiones de ese usuario. Guarden
siempre el último.

---

## 8. Si registran cuentas desde su canal

También pueden. Lo que no pueden es guardar usuarios: la cuenta se crea aquí.

```bash
curl -X POST http://localhost:4010/auth/registro \
  -H 'Content-Type: application/json' \
  -d '{"correo":"maria@ejemplo.com","contrasena":"Marketplace2026!",
       "nombres":"María","apellidos":"Quispe Rojas",
       "celular":"+51987654321","aceptaTerminos":true}'
```

```json
{ "id": "11111111-1111-1111-1111-111111111111", "estado": "PENDIENTE_VERIFICACION" }
```

**Es público: no hace falta su token de servicio.** Y **no devuelve
tokens**: la cuenta nace `PENDIENTE_VERIFICACION` y **no puede iniciar sesión**
hasta que el titular confirme su correo. Si intentan `POST /auth/login` antes
de eso, recibirán `401 CREDENCIALES_INVALIDAS`, igual que con una contraseña
incorrecta.

### Los dos campos que suelen sorprender

| Campo | Por qué es obligatorio |
|---|---|
| `contrasena` | Debe cumplir nuestra política. Pídanla con `GET /password/politica` y validen en su formulario; nosotros volvemos a validar siempre. Si no cumple: `422 POLITICA_INCUMPLIDA`, con las reglas falladas en `errores[]` |
| `aceptaTerminos` | Es consentimiento expreso de tratamiento de datos personales (Ley N.º 29733). Tienen que mostrarle el texto al usuario; guardamos la fecha y la versión aceptada. Sin él: `400 VALIDACION` |

El celular va en formato internacional peruano: `+51` y nueve dígitos.

**Si su canal no puede pedir contraseña ni mostrar los términos** —un chat,
por ejemplo—, la salida simple es que le envíen al usuario un enlace a nuestra
pantalla de registro y se despreocupen del resto.

### Confirmación del correo

Nosotros enviamos el correo con un enlace de un solo uso, válido **24 horas**.
Al abrirlo, la pantalla que lo recibe confirma la cuenta:

```bash
curl -X POST http://localhost:4010/auth/verificar-correo \
  -H 'Content-Type: application/json' -d '{"token":"tok_del_enlace"}'
```

Responde `204` y la cuenta pasa a `ACTIVO`. Si el enlace venció o ya se usó,
`410 ENLACE_EXPIRADO` o `410 ENLACE_YA_USADO`; entonces se pide otro:

```bash
curl -X POST http://localhost:4010/auth/verificar-correo/reenviar \
  -H 'Content-Type: application/json' -d '{"correo":"maria@ejemplo.com"}'
```

**Siempre responde `202`, exista o no la cuenta**, y admite 3 por hora y correo;
a la cuarta, `429 DEMASIADAS_SOLICITUDES`. Es a propósito: si respondiéramos
distinto, serviría para averiguar qué correos están registrados.

### Lo que no hacemos en el registro

- **No les decimos si un correo o un celular ya existen.** No hay ningún endpoint
  para preguntarlo, y `409 CORREO_NO_DISPONIBLE` está redactado para no
  confirmarlo. Permitiría enumerar las cuentas del marketplace.
- **No tenemos cuenta de invitado.** O hay cuenta real, con su verificación, o
  el pedido va sin cuenta y el invitado lo llevan ustedes. Cuando después
  exista cuenta, enlazan el pedido por el identificador de usuario.
- **No devolvemos tokens al registrar.** Para tener sesión, `POST /auth/login`
  después de verificar el correo.

### Dos cosas pendientes de acordar

Si su canal necesita alguna, pídanla en la sincronización de líderes; no
las den por hechas:

1. **Que el enlace de verificación vuelva al frontend de ustedes** en vez de al
   nuestro, cuando el registro se origine en su canal. Se resolverá con un
   canal declarado, de una lista cerrada; nunca con una URL que nos envíen.
2. **Validar un correo o un celular con un código de un solo uso**, a petición
   de su canal. Está especificado en nuestra SPEC-10, pero su endpoint y su
   scope todavía no están definidos.

---

## 9. Errores: ramifiquen por `code`, nunca por el texto

Todos nuestros errores son `application/problem+json` (RFC 7807):

```json
{
  "type": "https://g7.unmsm.pe/errores/scope-insuficiente",
  "title": "Permisos insuficientes",
  "status": 403,
  "code": "SCOPE_INSUFICIENTE"
}
```

| `code` | Qué significa | Qué hacer |
|---|---|---|
| `TOKEN_INVALIDO` | Falta el token o no es válido | Pidan uno nuevo |
| `SCOPE_INSUFICIENTE` | Su token no cubre esa operación | Pidan el scope en la sincronización. **No reintenten** |
| `CREDENCIALES_INVALIDAS` | Correo o contraseña incorrectos | Mensaje genérico al usuario |
| `CUENTA_NO_DISPONIBLE` | Bloqueada, inactiva o sin verificar | Mensaje genérico. No digan cuál |
| `REFRESCO_INVALIDO` | Refresco vencido o ya usado | Volver a iniciar sesión |
| `LOTE_DEMASIADO_GRANDE` | Más de 100 identificadores | Partan el lote |
| `NO_DISPONIBLE` | No podemos responder ahora | Usen su caché del JWKS |
| `VALIDACION` | Falta un campo o tiene mal el formato | Corrijan y reintenten; el detalle va en `errores[]` |
| `POLITICA_INCUMPLIDA` | La contraseña no cumple la política | Muestren las reglas de `errores[]`. Pidan la política con `GET /password/politica` |
| `CORREO_NO_DISPONIBLE` | No se pudo usar ese correo al registrar | Mensaje genérico. **No digan que la cuenta ya existe** |
| `ENLACE_EXPIRADO` · `ENLACE_YA_USADO` | El enlace de verificación venció o ya se usó | Ofrezcan reenviarlo |
| `DEMASIADAS_SOLICITUDES` | Superaron el límite de reenvíos | Esperen; no reintenten en bucle |

**No lean `detail` para decidir.** Ese texto puede cambiar; `code` no.

---

## 10. Qué NO hacemos por ustedes

Para que nadie lo asuma por interpretación:

- **No autorizamos sus operaciones de negocio.** Les damos identidad, roles
  y permisos. Qué permite hacer cada uno lo deciden ustedes.
- **No emitimos tokens en nombre de un usuario a petición de ustedes.** Un token de
  servicio identifica a un módulo, no a una persona, y no hereda los permisos
  del usuario que introspecciona.
- **Todavía no publicamos eventos asíncronos.** `usuario.desactivado`,
  `usuario.bloqueado` y `usuario.roles_cambiados` llegan en la semana 12. Hasta
  entonces, la ventana de desfase es de 15 minutos y quien no la tolere usa la
  introspección.
- **No limitamos la tasa por módulo.** Si saturan la API, la saturan.

---

## 11. Mapa rápido: endpoint, spec y errores

Para que no tengan que deducirlo. **Esta tabla es la referencia**: si en algún
documento ven otro número de spec, manda este. La numeración cambió el 20 de
septiembre, cuando pasamos de 9 specs a 18; la equivalencia con los números
viejos está en [`trazabilidad.md`](trazabilidad.md) §8.

### Lo que llama su módulo con su token de servicio

| Endpoint | Para qué | Spec | Scope | Errores propios |
|---|---|---|---|---|
| `GET /auth/.well-known/jwks.json` | Validar tokens en local | 17 | Público | — |
| `GET /auth/.well-known/openid-configuration` | Descubrir el emisor | 17 | Público | — |
| `POST /auth/token` | Su token de servicio | 17 | `client_id` + `client_secret` | `CLIENTE_INVALIDO` |
| `POST /auth/introspeccion` | ¿La sesión sigue viva? | 17 | `tokens:introspeccion` | — |
| `GET /usuarios/{id}` | Datos básicos | 18 | `usuarios:leer` | `NO_ENCONTRADO` |
| `POST /usuarios/lote` | Hasta 100 de una vez | 18 | `usuarios:leer` | `LOTE_DEMASIADO_GRANDE` |
| `GET /usuarios/{id}/direcciones` | Dirección de entrega | 16 · 18 | `direcciones:leer` | `NO_ENCONTRADO` |
| `GET /roles` · `GET /permisos` | Catálogos | 11 · 18 | `roles:leer` | — |

Todos pueden devolver `TOKEN_INVALIDO` (401) y `SCOPE_INSUFICIENTE` (403).

### Lo que llama su frontend en nombre de una persona

Con el token del usuario, nunca con el de servicio.

| Endpoint | Para qué | Spec | Errores propios |
|---|---|---|---|
| `POST /auth/registro` | Autorregistro de un cliente | 01 · 07 | `CORREO_NO_DISPONIBLE`, `POLITICA_INCUMPLIDA` |
| `GET /password/politica` | Reglas para el medidor de fuerza | 07 | — |
| `POST /auth/verificar-correo` | Confirmar el correo | 02 | `ENLACE_EXPIRADO`, `ENLACE_YA_USADO` |
| `POST /auth/verificar-correo/reenviar` | Otro enlace | 02 | `DEMASIADAS_SOLICITUDES` |
| `POST /auth/login` | Iniciar sesión | 05 | `CREDENCIALES_INVALIDAS`, `PASSWORD_CADUCADA` |
| `POST /auth/otp/solicitar` · `/verificar` | Segundo factor del login | 09 | `CODIGO_INVALIDO`, `OTP_EXPIRADO`, `OTP_INTENTOS_AGOTADOS`, `DEMASIADAS_SOLICITUDES` |
| `POST /auth/refresh` | Renovar la sesión | 06 | `REFRESCO_INVALIDO` |
| `POST /auth/logout` | Cerrar sesión | 06 | — |
| `GET /auth/me` | Perfil del usuario del token | 16 | `TOKEN_NO_APLICABLE` |
| `POST /password/recuperar` · `/restablecer` | Recuperar la contraseña | 08 | `TOKEN_RECUPERACION_INVALIDO`, `TOKEN_RECUPERACION_EXPIRADO` |
| `POST /password/cambiar` | Cambiarla con sesión iniciada | 07 | `CREDENCIALES_INVALIDAS`, `POLITICA_INCUMPLIDA` |

Todos pueden devolver `VALIDACION` (400), `TOKEN_INVALIDO` (401) y
`NO_DISPONIBLE` (503).

### Tres confusiones que vemos seguido

1. **El login nunca devuelve `CUENTA_NO_DISPONIBLE`.** Cuenta bloqueada,
   inactiva o sin verificar responden `401 CREDENCIALES_INVALIDAS`, igual que
   una contraseña incorrecta. Si respondiera distinto, serviría para averiguar
   cuáles existen. `CUENTA_NO_DISPONIBLE` solo aparece cuando un administrador
   intenta bloquear una cuenta que no está operativa.
2. **`GET /auth/me` y cualquier endpoint de usuario rechazan el token de
   servicio** con `403 TOKEN_NO_APLICABLE`. Un módulo no actúa en nombre de una
   persona; para eso está el token del usuario.
3. **Las direcciones con token de servicio exigen `direcciones:leer`**, que hoy
   solo tiene Despacho. Con el token del titular no hace falta scope.

---

## 12. Cómo cambia este contrato

| Regla | Detalle |
|---|---|
| Versionado | Todo cuelga de `/api/v1`. Un cambio incompatible obliga a `v2`, nunca a modificar `v1` |
| Aviso | Todo cambio se comunica en el canal de integración con **al menos una semana** de anticipación |
| Preferencia | Añadimos campos antes que renombrarlos o eliminarlos |
| Retirada | Un campo en desuso convive con su sustituto hasta que confirmen que migraron |

**Congelamiento del contrato: jueves 17 de septiembre.** A partir de ahí, todo
cambio pasa por el canal de líderes.

Dudas: **Sergio Osorio**, Product Owner del G7, en el canal de líderes de
módulo.
