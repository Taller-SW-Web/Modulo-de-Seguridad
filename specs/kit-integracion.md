# Kit de integración — para los otros seis equipos

**Módulo de Seguridad y Autenticación (G7)** · Marketplace Multicanal · UNMSM 2026-II

Si vienes de otro equipo, esta es la única página que necesitas leer. El
contrato completo está en [`openapi.yaml`](openapi.yaml); esto es cómo usarlo.

> **Lo esencial en tres frases.** Vuestros usuarios inician sesión contra
> nosotros y reciben un token. Vosotros **verificáis ese token en local** con
> nuestra clave pública, sin llamarnos. Solo antes de operaciones sensibles nos
> preguntáis por el estado actual del usuario.

---

## 1. Empezad ahora, no cuando esté implementado

El contrato está congelado antes que el código. Levantad el entorno simulado:

```bash
git clone https://github.com/Taller-SW-Web/Modulo-de-Seguridad.git
cd Modulo-de-Seguridad
npx @stoplight/prism-cli mock specs/openapi.yaml -p 4010
```

Comprobad que responde:

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

**Parametrizad la URL base en vuestra configuración** y al cambiar de uno a otro
no tocaréis ni una línea de código.

---

## 2. Vía 1 — Validar el token en local *(el 99% de vuestro tráfico)*

Vuestro frontend os manda el token en `Authorization: Bearer <token>`. Lo
verificáis con nuestra clave pública. **No nos llamáis.**

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
disteis. Contra el mock, apuntad directamente al JWKS:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:4010/auth/.well-known/jwks.json
```

Nuestros roles viajan en el claim `roles`, no en `scope`, así que necesitáis un
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

// Una sola vez al arrancar: la librería cachea las claves por vosotros.
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

1. **Cachead el JWKS.** Descargarlo en cada validación anula toda la ventaja y
   nos convierte en vuestro punto único de fallo. Las tres librerías de arriba
   lo cachean solas.
2. **Si el JWKS no responde y tenéis copia en caché, seguid validando con ella.**
   Rechazad solo cuando llegue un token con un `kid` que no conozcáis y el JWKS
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

> **`permisos` viaja vacío por ahora.** El catálogo de permisos granulares
> (`pedido:crear`, `producto:editar`…) lo define nuestra SPEC-05, todavía en
> redacción. **Autorizad por `roles` mientras tanto.** Cuando el catálogo
> exista, la lista se llenará sin que cambie nada del contrato: añadir
> contenido a una lista vacía es compatible.

---

## 4. Vía 2 — Preguntarnos, cuando hace falta

### Pedid vuestro token de servicio

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
> Para todo lo demás, validad en local.**

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
  -H 'Authorization: Bearer <vuestro-token-de-servicio>' \
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
necesitáis un campo que vuestro scope no cubre, pedidlo en esa reunión.

| Módulo | Scopes | Para qué |
|---|---|---|
| Marketplace Cliente | `usuarios:leer` | Mostrar el nombre del comprador |
| Chatbot Cliente | `usuarios:leer` | Confirmar identidad en la conversación |
| Retail Vendedor | `usuarios:leer`, `roles:leer` | Buscar clientes y comprobar el rol del vendedor |
| Ventas y Postventa | `usuarios:leer`, `usuarios:leer:documento`, `tokens:introspeccion` | Emitir boletas y autorizar anulaciones |
| Despacho y Entrega | `usuarios:leer`, `direcciones:leer` | Entregar el paquete |
| Productos y Ofertas | `tokens:introspeccion`, `roles:leer` | Autorizar cambios de precio |

### El documento se enmascara según el scope

Sin `usuarios:leer:documento` recibiréis `documentoEnmascarado: "*****234"` en
vez de `documento: "45781234"`. **No es un error y no hay que reintentarlo**: se
responde con menos, no con un fallo.

---

## 6. Datos de prueba del entorno simulado

Publicados a propósito, para que probéis vuestros caminos de error sin
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

El mock obedece la cabecera `Prefer`. Esto es lo que os permite probar lo que
pasa cuando algo sale mal, **antes** de que os pase en la demo:

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

## 7. Si tenéis vuestra propia pantalla de login

Podéis tenerla. Lo que no podéis es guardar contraseñas ni usuarios: el
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
canjeáis con `POST /auth/otp/verificar`.

**El acceso dura 15 minutos.** Renovadlo con `POST /auth/refresh` antes de que
venza. El refresco **se usa una sola vez**: cada renovación os devuelve uno
nuevo, y reutilizar uno viejo cierra todas las sesiones de ese usuario. Guardad
siempre el último.

---

## 8. Errores: ramificad por `code`, nunca por el texto

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
| `TOKEN_INVALIDO` | Falta el token o no es válido | Pedid uno nuevo |
| `SCOPE_INSUFICIENTE` | Vuestro token no cubre esa operación | Pedid el scope en la sincronización. **No reintentéis** |
| `CREDENCIALES_INVALIDAS` | Correo o contraseña incorrectos | Mensaje genérico al usuario |
| `CUENTA_NO_DISPONIBLE` | Bloqueada, inactiva o sin verificar | Mensaje genérico. No digáis cuál |
| `REFRESCO_INVALIDO` | Refresco vencido o ya usado | Volver a iniciar sesión |
| `LOTE_DEMASIADO_GRANDE` | Más de 100 identificadores | Partid el lote |
| `NO_DISPONIBLE` | No podemos responder ahora | Usad vuestra caché del JWKS |

**No leáis `detail` para decidir.** Ese texto puede cambiar; `code` no.

---

## 9. Qué NO hacemos por vosotros

Para que nadie lo asuma por interpretación:

- **No autorizamos vuestras operaciones de negocio.** Os damos identidad, roles
  y permisos. Qué permite hacer cada uno lo decidís vosotros.
- **No emitimos tokens en nombre de un usuario a petición vuestra.** Un token de
  servicio identifica a un módulo, no a una persona, y no hereda los permisos
  del usuario que introspecciona.
- **Todavía no publicamos eventos asíncronos.** `usuario.desactivado`,
  `usuario.bloqueado` y `usuario.roles_cambiados` llegan en la semana 12. Hasta
  entonces, la ventana de desfase es de 15 minutos y quien no la tolere usa la
  introspección.
- **No limitamos la tasa por módulo.** Si saturáis la API, la saturáis.

---

## 10. Cómo cambia este contrato

| Regla | Detalle |
|---|---|
| Versionado | Todo cuelga de `/api/v1`. Un cambio incompatible obliga a `v2`, nunca a modificar `v1` |
| Aviso | Todo cambio se comunica en el canal de integración con **al menos una semana** de anticipación |
| Preferencia | Añadimos campos antes que renombrarlos o eliminarlos |
| Retirada | Un campo en desuso convive con su sustituto hasta que confirméis que migrasteis |

**Congelamiento del contrato: jueves 17 de septiembre.** A partir de ahí, todo
cambio pasa por el canal de líderes.

Dudas: **Sergio Osorio**, Product Owner del G7, en el canal de líderes de
módulo.
