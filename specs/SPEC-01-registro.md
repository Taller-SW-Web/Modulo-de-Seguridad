# SPEC-01 — Registro y gestión de usuarios

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

---

## Contexto — ¿por qué?

El Marketplace Multicanal de Productos Deportivos requiere que clientes, vendedores y administradores puedan darse de alta de forma segura y centralizada. Como proveedor de identidad del sistema, este módulo es el único dueño de la entidad usuario.

Ningún módulo consumidor guarda credenciales ni administra datos primarios de usuario en sus bases de datos. Por ello, el proceso de registro debe garantizar la integridad de las direcciones de correo electrónico declaradas, el almacenamiento seguro de contraseñas mediante hashing resistente y la asignación inicial de roles y estados de cuenta según el canal o perfil correspondiente.

---

## Propósito — ¿para qué?

Permitir la creación de cuentas de usuario en el sistema garantizando la verificación de la dirección de correo electrónico antes de habilitar el acceso. 

El resultado observable es la creación de un registro en estado `PENDIENTE_VERIFICACION`, el envío asíncrono de un enlace con token de verificación de un solo uso y la posterior transición a estado `ACTIVO` una vez confirmado el correo, emitiendo además el evento de dominio `usuario.creado` para sincronización con el resto del marketplace.

---

## Alcance — ¿hasta dónde?

Esta especificación comprende:

- Registro público de nuevos clientes a través de la interfaz web o canales autorizados.
- Alta administrativa de `VENDEDOR` y de los cuatro roles de gestión, incluido `ADMIN_SISTEMA`.
- Validación de formato y unicidad de correo electrónico.
- Encriptación/Hashing seguro de contraseñas con Argon2id o BCrypt.
- Generación de token de verificación de correo electrónico de vida corta (24 horas) y de uso único.
- Proceso de confirmación/verificación de correo electrónico.
- Transiciones de estado de cuenta de `PENDIENTE_VERIFICACION` a `ACTIVO`.
- Reenvío de enlace de verificación con límite de tasa (*rate limiting*).
- Listado y consulta de cuentas para el panel de administración.
- Baja lógica (estado `INACTIVO`) y reactivación de una cuenta.
- Protección del propio administrador y del último `ADMIN_SISTEMA` activo frente a la baja.
- Publicación de los eventos `usuario.creado`, `usuario.desactivado` y `usuario.reactivado`.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-01.1 | El sistema debe permitir el registro público de nuevos usuarios asignándoles por defecto el rol `CLIENTE` y el estado de cuenta `PENDIENTE_VERIFICACION`. |
| RF-01.2 | El sistema debe validar la unicidad del correo electrónico antes de registrar al usuario, rechazando duplicados sin revelar información sensible. |
| RF-01.3 | El sistema debe almacenar la contraseña aplicando un algoritmo de hashing seguro (BCrypt con costo >= 12 o Argon2id) y nunca en texto plano. |
| RF-01.4 | Tras un registro exitoso, el sistema debe generar un token de verificación de correo de un solo uso con vigencia de 24 horas y enviar un mensaje asíncrono vía SMTP. |
| RF-01.5 | El sistema debe permitir confirmar la cuenta mediante la verificación del token enviado por correo, cambiando el estado del usuario de `PENDIENTE_VERIFICACION` a `ACTIVO`. |
| RF-01.6 | El sistema debe rechazar la autenticación y emisión de tokens a cualquier usuario que se encuentre en estado `PENDIENTE_VERIFICACION`. |
| RF-01.7 | El sistema debe permitir solicitar el reenvío del correo de verificación (`POST /api/v1/auth/verificar-correo/reenviar`), invalidando el enlace anterior. Responde `202` exista o no la cuenta, y admite 3 solicitudes por hora **por dirección de correo**, para que ni la respuesta ni el límite revelen qué cuentas existen. |
| RF-01.8 | Un `ADMIN_SISTEMA` debe poder dar de alta cuentas con rol `VENDEDOR` o con cualquiera de los cuatro roles de gestión (`POST /api/v1/usuarios`). La cuenta nace `ACTIVO`, sin verificación de correo, y se publica `usuario.creado`. Los clientes solo se crean mediante el registro público. |
| RF-01.9 | El sistema debe publicar el evento `usuario.creado` en RabbitMQ inmediatamente después de que la cuenta pase al estado `ACTIVO`. |
| RF-01.10 | Todas las acciones de registro, verificación y fallos deben registrarse mediante el catálogo de auditoría de SPEC-06. |
| RF-01.11 | Quien tenga el permiso `usuario.ver` (`ADMIN_SISTEMA`, SPEC-05) debe poder listar las cuentas con filtros por estado, rol y correo, paginadas (`GET /api/v1/usuarios`), y consultar una cuenta (`GET /api/v1/usuarios/{id}`, el mismo endpoint que usan los módulos consumidores). |
| RF-01.12 | Un `ADMIN_SISTEMA` debe poder dar de baja una cuenta (`DELETE /api/v1/usuarios/{id}`): pasa a `INACTIVO` sin borrarse, se revocan todos sus tokens de refresco y se publica `usuario.desactivado`. Desde ese momento el login responde `401 CREDENCIALES_INVALIDAS`, igual que ante una contraseña incorrecta. |
| RF-01.13 | Nadie puede darse de baja a sí mismo por esta vía, ni dar de baja al último `ADMIN_SISTEMA` activo: responde `422 ADMINISTRADOR_PROTEGIDO`, la misma regla que aplican SPEC-05 al revocar el rol y SPEC-07 al bloquear. |
| RF-01.14 | Un `ADMIN_SISTEMA` debe poder reactivar una cuenta `INACTIVO` (`POST /api/v1/usuarios/{id}/reactivar`): vuelve a `ACTIVO` con la misma identidad, el mismo correo y los mismos roles, sin repetir la verificación y sin sesiones abiertas, y se publica `usuario.reactivado`. |
| RF-01.15 | El token de verificación de correo es el **único** mecanismo de confirmación de correo del módulo. SPEC-08 lo reutiliza para confirmar un cambio de correo, con otro propósito, a través del mismo `POST /api/v1/auth/verificar-correo`. |
| RF-01.16 | El registro debe exigir la aceptación expresa de los términos y del tratamiento de datos personales (`aceptaTerminos: true`), conforme a la Ley N.º 29733. Sin ella responde `400 VALIDACION` y no crea la cuenta. El sistema guarda la fecha de aceptación y la versión del texto aceptado, para poder demostrar el consentimiento. |
| RF-01.17 | El celular debe registrarse en formato internacional peruano: `+51` seguido de 9 dígitos. Otro formato responde `400 VALIDACION`. |

---

## Escenarios — ¿como verificamos?

### ESC-01.1 Registro público de cliente exitoso

**Dado** un visitante no autenticado que proporciona un correo no registrado `cliente@correo.com` y una contraseña válida.

**Cuando** envía una solicitud `POST /api/v1/auth/registro`.

**Entonces** el sistema responde con código HTTP `201 Created`, crea la cuenta con estado `PENDIENTE_VERIFICACION`, genera el token de verificación y envía el correo electrónico asíncronamente sin emitir tokens de sesión.

---

### ESC-01.2 Intentos de registro con correo duplicado *(caso borde de seguridad)*

**Dado** que el correo `existente@correo.com` ya pertenece a una cuenta en el sistema.

**Cuando** se intenta registrar una nueva cuenta con ese mismo correo.

**Entonces** el sistema responde con código HTTP `409 CORREO_NO_DISPONIBLE` indicando que la solicitud no puede ser procesada, sin revelar datos del propietario previo y registrando el evento en auditoría.

---

### ESC-01.3 Verificación exitosa de correo electrónico

**Dado** un usuario registrado con estado `PENDIENTE_VERIFICACION` y un token de verificación válido no expirado.

**Cuando** envía una solicitud `POST /api/v1/auth/verificar-correo` con el token.

**Entonces** el sistema:
1. Cambia el estado de la cuenta a `ACTIVO`.
2. Invalida el token de verificación utilizado.
3. Responde con código HTTP `204 No Content`.
4. Publica el evento `usuario.creado` en el broker de mensajería.

---

### ESC-01.4 Uso de token de verificación expirado o reusado *(caso borde)*

**Dado** un token de verificación con más de 24 horas de generación o que ya fue consumido previamente.

**Cuando** se envía a `POST /api/v1/auth/verificar-correo`.

**Entonces** el sistema responde `410 ENLACE_EXPIRADO` o `410 ENLACE_YA_USADO`, mantiene la cuenta en `PENDIENTE_VERIFICACION` y no publica ningún evento.

---

### ESC-01.5 Intento de inicio de sesión de cuenta no verificada

**Dado** un usuario en estado `PENDIENTE_VERIFICACION`.

**Cuando** intenta autenticarse mediante `POST /api/v1/auth/login` con sus credenciales correctas.

**Entonces** el sistema responde `401 CREDENCIALES_INVALIDAS`, exactamente igual que ante una contraseña incorrecta, y no emite `accessToken` ni `refreshToken`.

---

### ESC-01.6 Reenvío de token con límite de tasa excedido *(caso borde)*

**Dado** un usuario que ya solicitó 3 reenvíos de correo de verificación en la última hora.

**Cuando** solicita un cuarto reenvío a través de `POST /api/v1/auth/verificar-correo/reenviar`.

**Entonces** el sistema responde con código HTTP `429 DEMASIADAS_SOLICITUDES` y no genera un nuevo correo ni invalida el último token activo.

---

### ESC-01.7 Reenvío a un correo que no existe *(caso borde de seguridad)*

**Dado** un correo que no pertenece a ninguna cuenta.

**Cuando** se solicita `POST /api/v1/auth/verificar-correo/reenviar` con ese correo.

**Entonces** el sistema responde `202`, igual que con una cuenta real, y no envía nada. A partir de la cuarta solicitud en una hora responde `429 DEMASIADAS_SOLICITUDES`, también igual.

---

### ESC-01.8 Alta de un vendedor por un administrador

**Dado** un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios` con `{ correo, contrasena, rol: VENDEDOR }`.

**Entonces** el sistema crea la cuenta directamente en `ACTIVO`, sin enviar correo de verificación, responde `201` y publica `usuario.creado`.

---

### ESC-01.9 Listado de cuentas sin permiso *(caso borde de seguridad)*

**Dado** un usuario autenticado con rol `CLIENTE` o `VENDEDOR`.

**Cuando** envía `GET /api/v1/usuarios`.

**Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no devuelve ninguna cuenta y registra `ACCESO_DENEGADO` mediante SPEC-06.

---

### ESC-01.10 Baja lógica de una cuenta

**Dado** un `ADMIN_SISTEMA` y una cuenta `ACTIVO` con dos sesiones abiertas.

**Cuando** envía `DELETE /api/v1/usuarios/{id}`.

**Entonces** la cuenta pasa a `INACTIVO` sin borrarse, se revocan sus dos sesiones, se registra `USUARIO_DESACTIVADO`, se publica `usuario.desactivado` y el sistema responde `204`. Un login posterior de esa cuenta responde `401 CREDENCIALES_INVALIDAS`.

---

### ESC-01.11 Baja del último administrador *(caso borde)*

**Dado** que existe un único `ADMIN_SISTEMA` activo.

**Cuando** alguien intenta darlo de baja, o él intenta darse de baja a sí mismo.

**Entonces** el sistema responde `422 ADMINISTRADOR_PROTEGIDO` y la cuenta sigue `ACTIVO`.

---

### ESC-01.12 Reactivación de una cuenta dada de baja

**Dado** una cuenta `INACTIVO` con rol `CLIENTE` y un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios/{id}/reactivar`.

**Entonces** la cuenta vuelve a `ACTIVO` con el mismo identificador, correo y roles, sin repetir la verificación y sin ninguna sesión abierta; se registra `USUARIO_REACTIVADO` y se publica `usuario.reactivado`, **no** `usuario.creado`.

### ESC-01.13 Registro sin aceptar los términos *(caso borde legal)*

**Dado** un visitante que completa el formulario de registro correctamente.

**Cuando** envía `POST /api/v1/auth/registro` con `aceptaTerminos: false` o sin ese campo.

**Entonces** el sistema responde `400 VALIDACION` con el error en el campo `aceptaTerminos`, no crea la cuenta y no envía ningún correo. Con `aceptaTerminos: true`, la cuenta guarda la fecha de aceptación y la versión de los términos vigente.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El hashing de contraseña debe tardar al menos 200 ms por diseño para prevenir ataques de fuerza bruta.
- El endpoint de registro debe responder en menos de 600 ms P95 (sin incluir el tiempo de envío de red del correo SMTP, el cual debe ser asíncrono/reactivo).
- Los tokens de verificación deben ser generados mediante aleatoriedad criptográfica (`SecureRandom`) de al menos 32 bytes (256 bits) codificados en URL-safe Base64.
- Las credenciales o contraseñas en texto plano nunca deben aparecer en logs ni eventos de RabbitMQ.
- Compatible con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- Autenticación inmediata tras el registro sin verificación previa.
- Recuperación o cambio de contraseña (gestionado por SPEC-03).
- Autenticación por proveedores OAuth2 / Social Login.
- Asignación pública de roles privilegiados.
- Los atributos de perfil, el documento de identidad y las direcciones, que son de SPEC-08.
- La baja por decisión del propio titular: está pendiente de decisión (Q10). Hoy solo da de baja un administrador.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/registro` | Modifica: añade `aceptaTerminos` obligatorio y el formato del celular (RF-01.16, RF-01.17) | ⬜ |
| `POST /api/v1/auth/verificar-correo` | Utiliza contrato existente; lo reutiliza SPEC-08 | ⬜ |
| `POST /api/v1/auth/verificar-correo/reenviar` | Añade | ⬜ |
| `GET /api/v1/usuarios` | Añade | ⬜ |
| `GET /api/v1/usuarios/{id}` | Amplía: también con token de administrador (`usuario.ver`) | ⬜ |
| `POST /api/v1/usuarios` | Utiliza contrato existente | ⬜ |
| `DELETE /api/v1/usuarios/{id}` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/usuarios/{id}/reactivar` | Añade | ⬜ |
| `usuario.creado` · `usuario.desactivado` · `usuario.reactivado` | Publica eventos | ⬜ |

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube