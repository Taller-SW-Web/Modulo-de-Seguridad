# SPEC-17 — Claves públicas, tokens de servicio e introspección

| Campo | Valor |
|---|---|
| **Responsable** | Sergio Alejandro Osorio Montenegro (Product Owner) |
| **Hito objetivo** | Hito 1 (Sem. 4) — contrato · Hito 4 (Sem. 11) — implementación |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-09 «API de identidad para los demás módulos» el 20 de septiembre, por indicación del profesor: una spec por función. Conserva la verificación de tokens —JWKS, token de servicio, introspección— y las reglas comunes de acceso y error; la consulta de datos de usuario pasó a SPEC-18. El contrato publicado no cambió: solo se repartieron sus requisitos. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El marketplace lo construyen siete equipos. Seis de ellos —marketplace cliente, chatbot, retail, ventas y postventa, despacho y entrega, productos y ofertas— tienen que saber **quién** está detrás de cada petición y **qué se le permite hacer**. La matriz cruzada del curso les prohíbe expresamente consultar nuestra base de datos: toda relación pasa por API.

Esta especificación define la mitad de esa frontera que **bloquea a equipos que no son el nuestro**: sin la clave pública y sin token de servicio, los otros seis no pueden ni empezar a programar sus llamadas. Por eso se publica en la semana 4, antes que la implementación (ver [ADR-003](../docs/arquitectura/adr/003-contrato-antes-que-codigo.md)).

Las funcionalidades internas del módulo están en SPEC-01 a SPEC-16. Esta no las repite: describe lo que ocurre **desde fuera**, cuando quien llama no es nuestra propia SPA sino otro microservicio.

---

## Propósito — ¿para qué?

Permitir que los seis módulos consumidores verifiquen la identidad de un usuario final sin llamarnos en el camino ordinario, que se identifiquen ellos mismos con un token de servicio, y que pregunten por el estado actual de una sesión antes de una operación sensible.

El resultado observable es un JWKS público y rotable, un token de servicio con los scopes justos, una introspección que refleja el estado del usuario en el instante de la consulta, y un entorno simulado contra el que los otros equipos programan desde la semana 4.

---

## Alcance — ¿hasta dónde?

Publicación de la clave pública de verificación (JWKS) y su rotación; documento
de descubrimiento; emisión de tokens de servicio por módulo consumidor;
introspección de tokens de usuario; las reglas comunes de autenticación, scope y
formato de error que aplican a toda la API de integración; y el entorno simulado.

### Superficie del contrato

Todos los recursos cuelgan de `/api/v1`. Los endpoints de consulta de datos están en SPEC-18.

| Método y ruta | Descripción | Acceso |
|---|---|---|
| `GET /auth/.well-known/openid-configuration` | Documento de descubrimiento del emisor | Público |
| `GET /auth/.well-known/jwks.json` | Clave pública de verificación de firma | Público |
| `POST /auth/token` | Emite el token de servicio de un módulo consumidor | `client_id` + `client_secret` |
| `POST /auth/introspeccion` | Estado actual de un token de usuario | `tokens:introspeccion` |

### Las dos identidades de un módulo consumidor

Confundirlas es el error más caro de esta integración:

- **La identidad del usuario final**, que viaja en el token que el módulo recibe
  y que él solo **verifica**. Nunca la emite ni la modifica.
- **Su propia identidad de servicio**, con la que se autentica cuando *él* nos
  llama. Es un `client_id` propio de cada módulo, para que la auditoría pueda
  decir qué módulo consultó qué dato y para poder revocar uno sin afectar a los
  otros cinco.

### Las tres vías, y cuál es la que se usa

| Vía | Cuándo | Coste |
|---|---|---|
| **1. Validación local con el JWKS** | En cada petición ordinaria que recibe el módulo | Ninguna llamada de red. No detecta cambios de estado hasta que el token vence |
| **2. Llamada síncrona a esta API** | Antes de operaciones sensibles, o para datos que el token no lleva (SPEC-18) | Una llamada de red y una dependencia de nuestra disponibilidad |
| **3. Eventos asíncronos** | Para enterarse de bajas y bloqueos sin preguntar | **Fuera del alcance de esta spec** |

**La vía 1 es la que hay que usar por defecto.** Un módulo que invoque la
introspección en cada petición nos convierte en su punto único de fallo.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-17.1 | El sistema debe publicar la clave pública de verificación en `GET /auth/.well-known/jwks.json`, sin autenticación, con un `kid` que identifique cada clave. |
| RF-17.2 | El sistema debe publicar un documento de descubrimiento en `GET /auth/.well-known/openid-configuration` que declare el `issuer`, el `jwks_uri` y los scopes soportados, para que los consumidores se configuren con una sola URL en vez de cablear el JWKS a mano. |
| RF-17.3 | Durante una rotación de clave el sistema debe publicar la clave nueva y la anterior simultáneamente, y mantener la anterior al menos 24 horas. |
| RF-17.4 | El sistema debe emitir un token de servicio a un módulo consumidor que presente su `client_id` y `client_secret` mediante `grant_type=client_credentials`. |
| RF-17.5 | El token de servicio debe llevar `tipo: "servicio"` y el `client_id` en `sub`, y debe conceder exactamente los scopes asignados a ese cliente, aunque haya solicitado más. |
| RF-17.6 | La introspección debe devolver el estado del usuario **en el instante de la consulta**, no el que llevaba el token cuando se emitió. |
| RF-17.7 | Ante el token de un usuario desactivado, bloqueado o cuyos roles cambiaron, la introspección debe responder `200` con `activo: false` y el motivo, sin implicar que la firma sea inválida. |
| RF-17.8 | Una petición sin token de servicio válido debe responder `401` con un cuerpo idéntico exista o no el usuario consultado, y esa decisión debe tomarse antes de comprobar la existencia. |
| RF-17.9 | Una petición con token válido pero scope insuficiente debe responder `403` sin revelar qué scope habría hecho falta. |
| RF-17.10 | Todos los errores de la API deben responder con `application/problem+json` (RFC 7807) y un `code` estable sobre el que los consumidores puedan ramificar. |
| RF-17.11 | Debe existir un entorno simulado con datos y credenciales de prueba conocidas, disponible desde la semana 4, antes de que exista la implementación. |

### Códigos de rol del contrato

Estos seis códigos **viajan dentro del claim `roles` del token** que leen los
otros seis equipos. Son parte del contrato y no pueden cambiarse
unilateralmente. La decisión de fijarlos en español está registrada en
[ADR-004](../docs/arquitectura/adr/004-idioma-del-contrato.md).

| Código | Perfil |
|---|---|
| `CLIENTE` | Cliente que compra |
| `VENDEDOR` | Vendedor que atiende en tienda |
| `ADMIN_VENTAS` | Administra pedidos: anulaciones y reembolsos |
| `GESTOR_DESPACHO` | Administra rutas y entregas |
| `GESTOR_COMERCIAL` | Administra catálogo y precios |
| `ADMIN_SISTEMA` | Personal de la plataforma |

### Scopes del token de servicio

Se conceden por escrito en la sincronización entre equipos, no por petición.

| Scope | Qué autoriza | Spec |
|---|---|---|
| `tokens:introspeccion` | Introspección de tokens de usuario | SPEC-17 |
| `usuarios:leer` | Datos básicos de usuario, individuales y por lote | SPEC-18 |
| `usuarios:leer:documento` | El número de documento en claro en vez de enmascarado | SPEC-18 |
| `direcciones:leer` | Direcciones de entrega del cliente | SPEC-18 |
| `roles:leer` | Catálogo de roles y de permisos | SPEC-18 |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-17.1 Validación local del token con el JWKS

- **Dado** que el módulo de Productos y Ofertas recibe una petición con `Authorization: Bearer <token>` emitido por nosotros,
- **Cuando** obtiene la clave pública de `GET /api/v1/auth/.well-known/jwks.json`, selecciona la clave cuyo `kid` coincide con el de la cabecera del token y verifica la firma,
- **Entonces** confirma la validez del token y lee `sub`, `roles` y `permisos`, **sin realizar ninguna llamada a este módulo**, y el resultado es el mismo aunque nuestra API esté caída mientras el JWKS siga en su caché.

### ESC-17.2 Rotación de la clave de firma *(caso borde)*

- **Dado** que hay tokens en circulación firmados con la clave `kid: "2026-09"`,
- **Cuando** se rota la clave y se empieza a emitir con `kid: "2026-10"`,
- **Entonces** el JWKS publica **las dos** claves, la anterior se mantiene publicada al menos 24 horas y ningún token en circulación deja de validarse durante la rotación.

### ESC-17.3 Obtención de un token de servicio

- **Dado** que el equipo de Despacho y Entrega tiene un `client_id` y un `client_secret` propios,
- **Cuando** envía `POST /api/v1/auth/token` con `grant_type=client_credentials`,
- **Entonces** el sistema responde `200` con `accessToken`, `tokenType: "Bearer"`, `expiresIn` y los `scopes` concedidos a ese cliente, y el token emitido lleva `tipo: "servicio"` y el `client_id` en `sub`, no un identificador de usuario.

### ESC-17.4 El token de servicio no hereda permisos de usuario *(caso borde)*

- **Dado** que un módulo posee un token de servicio válido con todos sus scopes,
- **Cuando** intenta invocar un endpoint que actúa en nombre de un usuario, como `GET /auth/me` o el cambio de contraseña,
- **Entonces** el sistema responde `403`, no ejecuta la operación y registra el intento como evento de seguridad: un módulo no puede suplantar a una persona.

### ESC-17.5 Introspección de un token de usuario vigente

- **Dado** que el módulo de Ventas y Postventa va a anular un pedido y necesita el estado **actual** de quien lo pide,
- **Cuando** envía `POST /api/v1/auth/introspeccion` con el token del usuario y su propio token de servicio con scope `tokens:introspeccion`,
- **Entonces** el sistema responde `200` con `activo: true`, el `sub`, los `roles` y los `permisos` vigentes en este instante, reflejando cualquier cambio de rol posterior a la emisión del token.

### ESC-17.6 Introspección del token de un usuario desactivado *(caso borde)*

- **Dado** que un usuario fue desactivado hace 3 minutos y su token de acceso, emitido antes, aún no vence,
- **Cuando** un módulo lo introspecciona,
- **Entonces** el sistema responde `200` con `activo: false` y `motivo: "USUARIO_INACTIVO"`; la firma del token sigue siendo criptográficamente válida y lo que la respuesta afirma es que **no es utilizable**, no que esté mal firmado.

> Este escenario es la razón de ser de la vía 2. La vía 1 no puede detectarlo: la
> firma es correcta y el `exp` todavía no ha llegado.

### ESC-17.7 Petición sin token de servicio *(caso borde)*

- **Dado** que un módulo consulta los datos básicos de un usuario,
- **Cuando** no incluye ninguna cabecera `Authorization`,
- **Entonces** el sistema responde `401` con `code: TOKEN_INVALIDO` y un cuerpo **idéntico exista o no el usuario consultado**, porque la decisión se toma antes de comprobar la existencia.

### ESC-17.8 Petición con scope insuficiente *(caso borde)*

- **Dado** que el módulo de Chatbot presenta un token de servicio válido,
- **Cuando** invoca un endpoint para el que no tiene scope,
- **Entonces** el sistema responde `403` con `code: SCOPE_INSUFICIENTE`, registra el intento en auditoría como evento de seguridad y **no revela qué scope habría hecho falta**.

### ESC-17.9 Un equipo consumidor programa antes de que exista la implementación

- **Dado** que el equipo de Retail quiere integrarse en la semana 5 y la implementación real llega en la semana 11,
- **Cuando** levanta el entorno simulado a partir de `specs/openapi.yaml` y apunta su cliente a él,
- **Entonces** recibe respuestas con los ejemplos del contrato, incluidos los caminos de error, y al llegar la implementación solo cambia la URL base.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La introspección debe responder en menos de **200 ms** en el percentil 95 con 50 usuarios concurrentes. Si es lenta, nadie la usará y todos se quedarán en la vía 1, incluso donde no deben.
- El JWKS debe ser cacheable y tolerar la rotación manteniendo la clave anterior al menos **24 horas**.
- Los módulos consumidores **deben cachear el JWKS**, no descargarlo por petición. Si el JWKS no responde y el consumidor tiene copia en caché, debe seguir validando con ella.
- Ninguna respuesta a un módulo consumidor puede permitir enumerar qué cuentas existen.
- La documentación OpenAPI se escribe a mano mientras no exista implementación y **se genera desde el código** a partir del Hito 4.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- **La consulta de datos de usuario, direcciones y catálogos**, que es de SPEC-18.
- **Los eventos asíncronos** (vía 3), que se especifican en [`catalogo-eventos.md`](catalogo-eventos.md) y los publica cada spec dueña del cambio.
- **La autorización de las operaciones de negocio.** Entregamos identidad, roles y permisos; qué permite hacer cada módulo con ellos lo decide ese módulo.
- **La pasarela de API centralizada** del marketplace y la infraestructura de red compartida.
- **La limitación de tasa por módulo consumidor.**
- **La federación de identidad** con proveedores externos.
- **El aprovisionamiento de los clientes de servicio**: cómo se crean, se entregan y se rotan los `client_secret` es trabajo de operaciones (Christian).

---

## Decisiones de diseño registradas

**La ventana de incoherencia de 15 minutos es deliberada, no una carencia.** Un
token de acceso sigue siendo válido hasta su `exp` aunque el usuario haya sido
desactivado. Es el precio de que los seis módulos validen sin llamarnos. Quien
no pueda pagarlo usa la introspección. Ver
[ADR-001](../docs/arquitectura/adr/001-rs256-frente-a-hs256.md) y
[ADR-002](../docs/arquitectura/adr/002-validacion-local-frente-a-introspeccion.md).

**Credenciales por módulo y no una clave compartida.** Si un `client_secret` se
filtra, se revoca ese cliente sin afectar a los otros cinco, y la auditoría
puede decir qué módulo consultó qué dato.

**Un cambio incompatible no se aplica, se añade.** Si hace falta renombrar un
campo que otros módulos ya consumen, se añade el nuevo manteniendo el anterior,
se comunica la fecha de retirada con al menos una semana de antelación y el
antiguo se elimina solo tras confirmación de los módulos afectados. Un cambio
que no admita ese periodo de convivencia obliga a `/api/v2`.

---

## Impacto en el contrato

Esta spec y SPEC-18 **son** el contrato de integración. Cualquier otra spec que
quiera añadir, modificar o eliminar un endpoint pasa por el Product Owner.

| Endpoint | Añade, modifica o elimina | Acordado |
|---|---|---|
| `GET /api/v1/auth/.well-known/openid-configuration` | Añade | ⬜ |
| `GET /api/v1/auth/.well-known/jwks.json` | Añade | ⬜ |
| `POST /api/v1/auth/token` | Añade | ⬜ |
| `POST /api/v1/auth/introspeccion` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
