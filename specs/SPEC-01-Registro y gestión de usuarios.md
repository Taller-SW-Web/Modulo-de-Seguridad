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
- Alta administrativa de usuarios con roles específicos (`VENDEDOR`, `ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`).
- Validación de formato y unicidad de correo electrónico.
- Encriptación/Hashing seguro de contraseñas con Argon2id o BCrypt.
- Generación de token de verificación de correo electrónico de vida corta (24 horas) y de uso único.
- Proceso de confirmación/verificación de correo electrónico.
- Transiciones de estado de cuenta de `PENDIENTE_VERIFICACION` a `ACTIVO`.
- Reenvío de enlace de verificación con límite de tasa (*rate limiting*).
- Publicación del evento asíncrono `usuario.creado` hacia RabbitMQ.

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
| RF-01.7 | El sistema debe permitir a un usuario solicitar el reenvío del correo de verificación, invalidando tokens previos y aplicando una ventana de restricción de 3 solicitudes por hora. |
| RF-01.8 | Un usuario con rol `ADMIN_SISTEMA` debe poder dar de alta cuentas directamente con roles administrativos o `VENDEDOR`, pudiendo omitir o exigir la verificación de correo según la política. |
| RF-01.9 | El sistema debe publicar el evento `usuario.creado` en RabbitMQ inmediatamente después de que la cuenta pase al estado `ACTIVO`. |
| RF-01.10 | Todas las acciones de registro, verificación y fallos deben registrarse mediante el catálogo de auditoría de SPEC-06. |

---

## Escenarios — ¿como verificamos?

### ESC-01.1 Registro público de cliente exitoso

**Dado** un visitante no autenticado que proporciona un correo no registrado `cliente@correo.com` y una contraseña válida.

**Cuando** envía una solicitud `POST /api/v1/usuarios/registro`.

**Entonces** el sistema responde con código HTTP `201 Created`, crea la cuenta con estado `PENDIENTE_VERIFICACION`, genera el token de verificación y envía el correo electrónico asíncronamente sin emitir tokens de sesión.

---

### ESC-01.2 Intentos de registro con correo duplicado *(caso borde de seguridad)*

**Dado** que el correo `existente@correo.com` ya pertenece a una cuenta en el sistema.

**Cuando** se intenta registrar una nueva cuenta con ese mismo correo.

**Entonces** el sistema responde con código HTTP `409 Conflict` (o `400 Bad Request` genérico según política de enum) indicando que la solicitud no puede ser procesada, sin revelar datos del propietario previo y registrando el evento en auditoría.

---

### ESC-01.3 Verificación exitosa de correo electrónico

**Dado** un usuario registrado con estado `PENDIENTE_VERIFICACION` y un token de verificación válido no expirado.

**Cuando** envía una solicitud `POST /api/v1/auth/verificar-correo` con el token.

**Entonces** el sistema:
1. Cambia el estado de la cuenta a `ACTIVO`.
2. Invalida el token de verificación utilizado.
3. Responde con código HTTP `200 OK`.
4. Publica el evento `usuario.creado` en el broker de mensajería.

---

### ESC-01.4 Uso de token de verificación expirado o reusado *(caso borde)*

**Dado** un token de verificación con más de 24 horas de generación o que ya fue consumido previamente.

**Cuando** se envía a `POST /api/v1/auth/verificar-correo`.

**Entonces** el sistema responde con código HTTP `400 TOKEN_EXPIRED` o `400 TOKEN_INVALID`, mantiene la cuenta en `PENDIENTE_VERIFICACION` y no publica ningún evento.

---

### ESC-01.5 Intento de inicio de sesión de cuenta no verificada

**Dado** un usuario en estado `PENDIENTE_VERIFICACION`.

**Cuando** intenta autenticarse mediante `POST /api/v1/auth/login` con sus credenciales correctas.

**Entonces** el sistema responde con código HTTP `403 CUENTA_NO_DISPONIBLE` (o el estándar de credenciales/estado definido) y no emite `accessToken` ni `refreshToken`.

---

### ESC-01.6 Reenvío de token con límite de tasa excedido *(caso borde)*

**Dado** un usuario que ya solicitó 3 reenvíos de correo de verificación en la última hora.

**Cuando** solicita un cuarto reenvío a través de `POST /api/v1/auth/reenviar-verificacion`.

**Entonces** el sistema responde con código HTTP `429 DEMASIADAS_SOLICITUDES` y no genera un nuevo correo ni invalida el último token activo.

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

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/usuarios/registro` | Añade | ⬜ |
| `POST /api/v1/auth/verificar-correo` | Añade | ⬜ |
| `POST /api/v1/auth/reenviar-verificacion` | Añade | ⬜ |
| `usuario.creado` | Publica evento | ⬜ |

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube