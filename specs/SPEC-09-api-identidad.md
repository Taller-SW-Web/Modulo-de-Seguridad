# SPEC-09 — API de identidad para los demás módulos

| Campo | Valor |
|---|---|
| **Responsable** | Sergio Alejandro Osorio Montenegro (Product Owner) |
| **Hito objetivo** | Hito 1 (Sem. 4) — contrato · Hito 4 (Sem. 11) — implementación |
| **Estado** | Borrador — pendiente de revisión del equipo |
| **Aprobada por** | — |

---

## Contexto — ¿por qué?

El marketplace lo construyen siete equipos. Seis de ellos —marketplace cliente,
chatbot, retail, ventas y postventa, despacho y entrega, productos y ofertas—
tienen que saber **quién** está detrás de cada petición y **qué se le permite
hacer**. La matriz cruzada del curso les prohíbe expresamente consultar nuestra
base de datos: toda relación pasa por API.

Esa prohibición no es burocracia. Si el módulo de despacho leyera nuestra tabla
`usuario`, cualquier cambio de esquema nuestro rompería su despliegue, y una
consulta suya mal escrita expondría hashes de contraseña. La API es la frontera
que impide que ese acoplamiento exista.

Esta especificación define esa frontera. Es **la única del módulo que bloquea a
equipos que no son el nuestro**: mientras no exista, los otros seis no pueden ni
empezar a programar sus llamadas. Por eso se publica en la semana 4, antes que
la implementación, y no después (ver [ADR-003](../docs/arquitectura/adr/003-contrato-antes-que-codigo.md)).

Las funcionalidades internas del módulo están en SPEC-01 a SPEC-08. Esta no las
repite: describe lo que ocurre **desde fuera**, cuando quien llama no es nuestra
propia SPA sino otro microservicio.

---

## Propósito — ¿para qué?

Permitir que los seis módulos consumidores verifiquen la identidad de un usuario
final y consulten los datos de identidad que necesitan, sin acceder a nuestra
base de datos y sin quedar bloqueados por nuestra disponibilidad en el camino
ordinario.

El resultado observable es un contrato versionado y un entorno simulado contra
el que los otros equipos programan desde la semana 4, aunque la implementación
real llegue en la semana 11.

---

## Alcance — ¿hasta dónde?

Publicación de la clave pública de verificación (JWKS) y su rotación; emisión de
tokens de servicio por módulo consumidor; introspección de tokens de usuario;
consulta de datos básicos de usuario, individual y por lote; consulta de
direcciones de entrega; consulta del catálogo de roles y de permisos; formato
uniforme de errores; auditoría del acceso entre módulos; y el entorno simulado
que desbloquea a los seis equipos.

### Superficie del contrato

Todos los recursos cuelgan de `/api/v1`.

| Método y ruta | Descripción | Acceso |
|---|---|---|
| `GET /auth/.well-known/openid-configuration` | Documento de descubrimiento del emisor | Público |
| `GET /auth/.well-known/jwks.json` | Clave pública de verificación de firma | Público |
| `POST /auth/token` | Emite el token de servicio de un módulo consumidor | `client_id` + `client_secret` |
| `POST /auth/introspeccion` | Estado actual de un token de usuario | `tokens:introspeccion` |
| `GET /usuarios/{id}` | Datos básicos de un usuario | `usuarios:leer` |
| `POST /usuarios/lote` | Consulta de hasta 100 usuarios en una llamada | `usuarios:leer` |
| `GET /usuarios/{id}/direcciones` | Direcciones de entrega del cliente | `direcciones:leer` |
| `GET /roles` | Catálogo de roles y sus permisos | `roles:leer` |
| `GET /permisos` | Catálogo de permisos por módulo | `roles:leer` |

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
| **2. Llamada síncrona a esta API** | Antes de operaciones sensibles, o para datos que el token no lleva | Una llamada de red y una dependencia de nuestra disponibilidad |
| **3. Eventos asíncronos** | Para enterarse de bajas y bloqueos sin preguntar | **Fuera del alcance de esta spec** |

**La vía 1 es la que hay que usar por defecto.** Un módulo que invoque la
introspección en cada petición nos convierte en su punto único de fallo: si
nosotros caemos, cae el marketplace entero. La vía 2 existe para el puñado de
operaciones en las que quince minutos de desfase son inaceptables.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-09.1 | El sistema debe publicar la clave pública de verificación en `GET /auth/.well-known/jwks.json`, sin autenticación, con un `kid` que identifique cada clave. |
| RF-09.1b | El sistema debe publicar un documento de descubrimiento en `GET /auth/.well-known/openid-configuration` que declare el `issuer`, el `jwks_uri` y los scopes soportados, para que los consumidores se configuren con una sola URL en vez de cablear el JWKS a mano. |
| RF-09.2 | Durante una rotación de clave el sistema debe publicar la clave nueva y la anterior simultáneamente, y mantener la anterior al menos 24 horas. |
| RF-09.3 | El sistema debe emitir un token de servicio a un módulo consumidor que presente su `client_id` y `client_secret` mediante `grant_type=client_credentials`. |
| RF-09.4 | El token de servicio debe llevar `tipo: "servicio"` y el `client_id` en `sub`, y debe conceder exactamente los scopes asignados a ese cliente, aunque haya solicitado más. |
| RF-09.5 | La introspección debe devolver el estado del usuario **en el instante de la consulta**, no el que llevaba el token cuando se emitió. |
| RF-09.6 | Ante el token de un usuario desactivado, bloqueado o cuyos roles cambiaron, la introspección debe responder `200` con `activo: false` y el motivo, sin implicar que la firma sea inválida. |
| RF-09.7 | La consulta de datos básicos debe devolver identificador, nombre, correo, teléfono, estado y roles, y **nunca** el hash de contraseña. |
| RF-09.8 | La consulta por lote debe aceptar hasta 100 identificadores, devolver los encontrados y los no hallados por separado, y no fallar entera por los que faltan. |
| RF-09.9 | El sistema debe exponer las direcciones de entrega de un cliente sin incluir su número de documento ni su fecha de nacimiento. |
| RF-09.10 | El número de documento debe entregarse en claro solo a un cliente con el scope `usuarios:leer:documento`; a los demás, enmascarado mostrando los últimos tres dígitos. |
| RF-09.11 | Una petición sin token de servicio válido debe responder `401` con un cuerpo idéntico exista o no el usuario consultado, y esa decisión debe tomarse antes de comprobar la existencia. |
| RF-09.12 | Una petición con token válido pero scope insuficiente debe responder `403` sin revelar qué scope habría hecho falta. |
| RF-09.13 | El sistema debe exponer el catálogo de roles con sus permisos y el catálogo de permisos por módulo, para que ningún consumidor los codifique a mano. |
| RF-09.14 | Cada acceso de un módulo consumidor debe registrarse en auditoría con el módulo solicitante, el endpoint, el identificador consultado, si el dato sensible se entregó en claro o enmascarado, y la fecha. |
| RF-09.15 | Todos los errores de la API deben responder con `application/problem+json` (RFC 7807) y un `code` estable sobre el que los consumidores puedan ramificar. |
| RF-09.16 | Debe existir un entorno simulado con datos y credenciales de prueba conocidas, disponible desde la semana 4, antes de que exista la implementación. |

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

Se conceden por escrito en la sincronización entre equipos, no por petición. Un
módulo que necesite un campo nuevo lo pide en esa reunión; no se concede sobre
la marcha.

| Scope | Qué autoriza |
|---|---|
| `tokens:introspeccion` | Introspección de tokens de usuario |
| `usuarios:leer` | Datos básicos de usuario, individuales y por lote |
| `usuarios:leer:documento` | El número de documento en claro en vez de enmascarado |
| `direcciones:leer` | Direcciones de entrega del cliente |
| `roles:leer` | Catálogo de roles y de permisos |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-09.1 Validación local del token con el JWKS
- **Dado** que el módulo de Productos y Ofertas recibe una petición con `Authorization: Bearer <token>` emitido por nosotros,
- **Cuando** obtiene la clave pública de `GET /api/v1/auth/.well-known/jwks.json`, selecciona la clave cuyo `kid` coincide con el de la cabecera del token y verifica la firma,
- **Entonces** confirma la validez del token y lee `sub`, `roles` y `permisos`, **sin realizar ninguna llamada a este módulo**, y el resultado es el mismo aunque nuestra API esté caída mientras el JWKS siga en su caché.

### ESC-09.2 Rotación de la clave de firma *(caso borde)*
- **Dado** que hay tokens en circulación firmados con la clave `kid: "2026-09"`,
- **Cuando** se rota la clave y se empieza a emitir con `kid: "2026-10"`,
- **Entonces** el JWKS publica **las dos** claves, la anterior se mantiene publicada al menos 24 horas y ningún token en circulación deja de validarse durante la rotación.

### ESC-09.3 Obtención de un token de servicio
- **Dado** que el equipo de Despacho y Entrega tiene un `client_id` y un `client_secret` propios,
- **Cuando** envía `POST /api/v1/auth/token` con `grant_type=client_credentials`,
- **Entonces** el sistema responde `200` con `accessToken`, `tokenType: "Bearer"`, `expiresIn` y los `scopes` concedidos a ese cliente, y el token emitido lleva `tipo: "servicio"` y el `client_id` en `sub`, no un identificador de usuario.

### ESC-09.4 El token de servicio no hereda permisos de usuario *(caso borde)*
- **Dado** que un módulo posee un token de servicio válido con todos sus scopes,
- **Cuando** intenta invocar un endpoint que actúa en nombre de un usuario, como `GET /auth/me` o el cambio de contraseña,
- **Entonces** el sistema responde `403`, no ejecuta la operación y registra el intento como evento de seguridad: un módulo no puede suplantar a una persona.

### ESC-09.5 Introspección de un token de usuario vigente
- **Dado** que el módulo de Ventas y Postventa va a anular un pedido y necesita el estado **actual** de quien lo pide,
- **Cuando** envía `POST /api/v1/auth/introspeccion` con el token del usuario y su propio token de servicio con scope `tokens:introspeccion`,
- **Entonces** el sistema responde `200` con `activo: true`, el `sub`, los `roles` y los `permisos` vigentes en este instante, reflejando cualquier cambio de rol posterior a la emisión del token.

### ESC-09.6 Introspección del token de un usuario desactivado *(caso borde)*
- **Dado** que un usuario fue desactivado hace 3 minutos y su token de acceso, emitido antes, aún no vence,
- **Cuando** un módulo lo introspecciona,
- **Entonces** el sistema responde `200` con `activo: false` y `motivo: "USUARIO_INACTIVO"`; la firma del token sigue siendo criptográficamente válida y lo que la respuesta afirma es que **no es utilizable**, no que esté mal firmado.

> Este escenario es la razón de ser de la vía 2. La vía 1 no puede detectarlo: la
> firma es correcta y el `exp` todavía no ha llegado.

### ESC-09.7 Consulta de datos básicos de un usuario
- **Dado** que el módulo de Ventas y Postventa necesita mostrar el nombre del cliente de un pedido,
- **Cuando** envía `GET /api/v1/usuarios/{id}` con un token de servicio con scope `usuarios:leer`,
- **Entonces** el sistema responde `200` con identificador, nombre, correo, teléfono, estado y roles, **sin** el hash de contraseña y sin ningún campo que su scope no autorice.

### ESC-09.8 Consulta por lote con identificadores inexistentes *(caso borde)*
- **Dado** que el módulo de Despacho necesita los datos de 10 usuarios y 2 de esos identificadores no corresponden a ninguna cuenta,
- **Cuando** envía `POST /api/v1/usuarios/lote` con los 10 identificadores,
- **Entonces** el sistema responde `200` con los 8 usuarios encontrados en `usuarios` y los 2 identificadores no hallados en `noEncontrados`, y la petición **no falla entera** por los que faltan.

> Un reporte de 50 clientes en el que uno se dio de baja sigue funcionando, en
> vez de quedarse en blanco.

### ESC-09.9 Lote por encima del límite *(caso borde)*
- **Dado** que un módulo envía 140 identificadores en una sola llamada,
- **Cuando** invoca `POST /api/v1/usuarios/lote`,
- **Entonces** el sistema responde `400` con `code: LOTE_DEMASIADO_GRANDE` y **no trunca la lista en silencio**; los identificadores duplicados dentro del límite se colapsan a una sola aparición, y un identificador malformado es `400 VALIDACION`, no un «no encontrado».

### ESC-09.10 Consulta de direcciones por el módulo de despacho
- **Dado** que el módulo de Despacho necesita la dirección de entrega de un pedido,
- **Cuando** envía `GET /api/v1/usuarios/{id}/direcciones` con scope `direcciones:leer`,
- **Entonces** el sistema responde `200` con las direcciones del cliente, **sin** el número de documento ni la fecha de nacimiento, que no hacen falta para entregar un paquete.

### ESC-09.11 Dato sensible con el scope que lo autoriza
- **Dado** que el módulo de Ventas debe emitir una boleta y necesita el documento del cliente,
- **Cuando** consulta sus datos con un token de servicio con scope `usuarios:leer:documento`,
- **Entonces** el sistema responde con el nombre completo y el número de documento **en claro**, y registra el acceso en auditoría con el módulo solicitante, el identificador consultado y la fecha.

### ESC-09.12 Dato sensible sin el scope que lo autoriza *(caso borde)*
- **Dado** que el módulo de Chatbot consulta los datos de un cliente y **no** tiene el scope `usuarios:leer:documento`,
- **Cuando** pide los datos de ese cliente,
- **Entonces** el sistema responde `200` con el documento **enmascarado**, mostrando solo los últimos tres dígitos, y **no rechaza la petición**: responde con menos, no con un error.

> Se enmascara en vez de devolver `403` porque el chatbot sí necesita confirmar
> ante el cliente los últimos dígitos de su documento. Un `403` le obligaría a
> pedir el scope completo para un caso que no lo justifica.

### ESC-09.13 Petición sin token de servicio *(caso borde)*
- **Dado** que un módulo consulta los datos básicos de un usuario,
- **Cuando** no incluye ninguna cabecera `Authorization`,
- **Entonces** el sistema responde `401` con `code: TOKEN_INVALIDO` y un cuerpo **idéntico exista o no el usuario consultado**, porque la decisión se toma antes de comprobar la existencia.

### ESC-09.14 Petición con scope insuficiente *(caso borde)*
- **Dado** que el módulo de Chatbot presenta un token de servicio válido,
- **Cuando** invoca un endpoint para el que no tiene scope,
- **Entonces** el sistema responde `403` con `code: SCOPE_INSUFICIENTE`, registra el intento en auditoría como evento de seguridad y **no revela qué scope habría hecho falta**.

### ESC-09.15 Consulta del catálogo de roles
- **Dado** que un módulo consumidor necesita saber qué significa el rol `GESTOR_DESPACHO` que le llegó en un token,
- **Cuando** envía `GET /api/v1/roles` con scope `roles:leer`,
- **Entonces** el sistema responde `200` con cada rol, su descripción y sus permisos, de modo que el módulo resuelve los permisos sin consultar nuestra base de datos ni codificarlos a mano.

### ESC-09.16 Un equipo consumidor programa antes de que exista la implementación
- **Dado** que el equipo de Retail quiere integrarse en la semana 5 y la implementación real llega en la semana 11,
- **Cuando** levanta el entorno simulado a partir de `specs/openapi.yaml` y apunta su cliente a él,
- **Entonces** recibe respuestas con los ejemplos del contrato, incluidos los caminos de error, y al llegar la implementación solo cambia la URL base.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La introspección debe responder en menos de **200 ms** en el percentil 95 con 50 usuarios concurrentes. Si es lenta, nadie la usará y todos se quedarán en la vía 1, incluso donde no deben.
- La consulta por lote debe aceptar hasta **100 identificadores** y responder en menos de **1 segundo**.
- El JWKS debe ser cacheable y tolerar la rotación manteniendo la clave anterior al menos **24 horas**.
- Los módulos consumidores **deben cachear el JWKS**, no descargarlo por petición. Si el JWKS no responde y el consumidor tiene copia en caché, debe seguir validando con ella; solo rechaza cuando llega un `kid` desconocido y el JWKS no responde.
- Ninguna respuesta a un módulo consumidor puede permitir enumerar qué cuentas existen.
- Ningún registro de auditoría puede contener contraseñas ni el número de documento en claro.
- La documentación OpenAPI se escribe a mano mientras no exista implementación y **se genera desde el código** a partir del Hito 4, para que no se desincronice.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

Nada de lo siguiente se implementa en esta funcionalidad. Si hace falta, se
especifica aparte.

- **Los eventos asíncronos.** La publicación en RabbitMQ de `usuario.creado`, `usuario.desactivado`, `usuario.bloqueado`, `usuario.roles_cambiados` y `usuario.atributos_actualizados` es la vía 3 y se especifica por separado. Hasta que exista, la ventana de incoherencia es de 15 minutos y quien no la tolere usa la introspección.
- **La autorización de las operaciones de negocio.** Entregamos identidad, roles y permisos; qué permite hacer cada módulo con ellos lo decide ese módulo.
- **El catálogo de roles y permisos**, que es SPEC-05. Esta spec fija **cómo se consulta**, no qué contiene.
- **Los atributos de perfil y las direcciones**, cuyo modelo define SPEC-08. Aquí solo se especifica cómo se consultan desde otro módulo, no cómo se crean ni se validan.
- **La pasarela de API centralizada** del marketplace y la infraestructura de red compartida.
- **La limitación de tasa por módulo consumidor.** Un consumidor mal programado puede saturarnos; mitigarlo es anti-abuso y se especifica aparte.
- **La federación de identidad** con proveedores externos.
- **El aprovisionamiento de los clientes de servicio.** Esta spec asume que cada módulo tiene su `client_id` y su `client_secret`; cómo se crean, se entregan y se rotan es trabajo de operaciones (Christian).

---

## Dependencias conocidas

| Dependencia | Estado | Cómo se resuelve mientras tanto |
|---|---|---|
| El claim `permisos` presupone un catálogo de permisos por módulo (`pedido:crear`, `producto:editar`…) que define **SPEC-05** | Sin especificar | Esta spec fija el **formato** —lista de códigos, sin duplicados, unión de los roles del usuario—. Hasta que SPEC-05 exista, `permisos` viaja como lista vacía y los consumidores autorizan por `roles` |
| La forma de los datos de usuario expuestos depende de **SPEC-01** y **SPEC-08** | En redacción | El contrato fija los campos mínimos; añadir campos es compatible, renombrarlos no |
| Los `client_id` y `client_secret` de los seis módulos | Sin crear | El entorno simulado usa credenciales de prueba conocidas y publicadas |

---

## Decisiones de diseño registradas

**La ventana de incoherencia de 15 minutos es deliberada, no una carencia.** Un
token de acceso sigue siendo válido hasta su `exp` aunque el usuario haya sido
desactivado. Es el precio de que los seis módulos validen sin llamarnos. Quien
no pueda pagarlo usa la introspección. Reducir la vida del token multiplicaría
las renovaciones sin cerrar la ventana del todo. Ver
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

Esta spec **es** el contrato: define los nueve endpoints de la tabla de alcance,
los códigos de rol, los scopes y el formato de error. Cualquier otra spec que
quiera añadir, modificar o eliminar un endpoint pasa por el Product Owner.

| Endpoint | Añade, modifica o elimina | Acordado |
|---|---|---|
| `GET /api/v1/auth/.well-known/openid-configuration` | Añade — **incorporado al escribir el contrato** | ⬜ |
| `GET /api/v1/auth/.well-known/jwks.json` | Añade | ⬜ |
| `POST /api/v1/auth/token` | Añade | ⬜ |
| `POST /api/v1/auth/introspeccion` | Añade | ⬜ |
| `GET /api/v1/usuarios/{id}` | Añade | ⬜ |
| `POST /api/v1/usuarios/lote` | Añade | ⬜ |
| `GET /api/v1/usuarios/{id}/direcciones` | Añade | ⬜ |
| `GET /api/v1/roles` | Añade | ⬜ |
| `GET /api/v1/permisos` | Añade | ⬜ |

---

## Lista de completitud

- [ ] Los 16 requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los nueve endpoints figuran en `specs/openapi.yaml` publicado
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
