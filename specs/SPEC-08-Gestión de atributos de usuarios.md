# SPEC-08 — Gestión de atributos de usuarios

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Hito objetivo** | Hito 5 (Sem. 14) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

---

## Contexto — ¿por qué?

Los datos del perfil de un usuario (nombres, apellidos, número de teléfono, dirección de contacto) cambian con el tiempo. El proveedor de identidad centraliza esta información y debe asegurar que el propio usuario o un administrador autorizado puedan actualizar estos atributos manteniendo la integridad de los datos.

Ciertos cambios en atributos críticos (como la dirección de correo electrónico o el número de teléfono celular) impactan directamente los mecanismos de autenticación, la verificación por OTP y las notificaciones de los demás módulos del marketplace.

---

## Propósito — ¿para qué?

Proporcionar endpoints seguros para la lectura y actualización del perfil del usuario, así como la gestión de datos de contacto verificados.

El resultado observable es la actualización atómica del perfil en la base de datos de seguridad, el desencadenamiento de procesos de re-verificación si se cambian datos sensibles (correo/celular) y la publicación del evento asíncrono `usuario.actualizado` hacia los módulos consumidores.

---

## Alcance — ¿hasta dónde?

Esta especificación comprende:

- Consulta del perfil de usuario autenticado (`/me`) y consulta por ID para administradores.
- Edición de atributos no críticos (nombres, apellidos, preferencias).
- Edición de atributos críticos (correo electrónico, número celular) con activación de estado no verificado y requerimiento de OTP/Token.
- Aplicación de bajas lógicas (cambio a estado `INACTIVO`).
- Publicación del evento `usuario.actualizado` en RabbitMQ.
- Registro de auditoría de modificaciones de perfil según SPEC-06.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-08.1 | El sistema debe permitir a cualquier usuario autenticado consultar sus propios atributos mediante el endpoint `GET /api/v1/usuarios/me`. |
| RF-08.2 | El sistema debe permitir a un usuario actualizar sus atributos no críticos (nombres, apellidos) retornando la entidad actualizada. |
| RF-08.3 | Si un usuario solicita cambiar su correo electrónico, el sistema debe marcar el nuevo correo como pendiente, enviar un token de verificación a la nueva dirección y mantener el correo anterior operativo hasta la confirmación. |
| RF-08.4 | Si un usuario modifica su número celular, el canal SMS deberá marcarse como no verificado hasta completar una prueba OTP según SPEC-04. |
| RF-08.5 | Un `ADMIN_SISTEMA` debe poder consultar y editar los atributos de cualquier usuario del sistema. |
| RF-08.6 | El sistema debe soportar la baja lógica de un usuario, cambiando su estado a `INACTIVO`. Ninguna cuenta se elimina físicamente de la base de datos. |
| RF-08.7 | La baja lógica (estado `INACTIVO`) debe revocar inmediatamente todos los `refreshToken` de la cuenta y responder `403 CUENTA_NO_DISPONIBLE` ante cualquier intento posterior de login. |
| RF-08.8 | Tras cualquier modificación confirmada de atributos o cambio a `INACTIVO`, el sistema debe publicar el evento `usuario.actualizado` en RabbitMQ. |

---

## Escenarios — ¿cómo verificamos?

### ESC-08.1 Consulta de perfil propio (`/me`)

**Dado** un usuario autenticado con un `accessToken` válido.

**Cuando** envía una solicitud `GET /api/v1/usuarios/me`.

**Entonces** el sistema responde con código HTTP `200 OK` retornando sus datos (ID, correo, nombres, apellidos, roles, estado y banderas de verificación de contacto).

---

### ESC-08.2 Actualización de atributos no críticos

**Dado** un cliente autenticado.

**Cuando** realiza un `PATCH /api/v1/usuarios/me` enviando los nuevos valores `{"nombres": "Eva Lucía", "apellidos": "Moreno"}`.

**Entonces** el sistema actualiza la base de datos, responde `200 OK` con los datos actualizados, registra la acción en auditoría y publica el evento `usuario.actualizado`.

---

### ESC-08.3 Cambio de correo electrónico *(caso borde/sensible)*

**Dado** un usuario activo con correo `actual@correo.com`.

**Cuando** solicita cambiar su correo a `nuevo@correo.com` vía `PATCH /api/v1/usuarios/me`.

**Entonces** el sistema:
1. Registra `nuevo@correo.com` en un campo temporal de solicitud.
2. Envía un token de confirmación a `nuevo@correo.com`.
3. Mantiene el login activo con `actual@correo.com`.
4. Solo efectúa el cambio definitivo cuando el token es verificado.

---

### ESC-08.4 Baja lógica de un usuario (Inactivación)

**Dado** un usuario con rol `ADMIN_SISTEMA` y un `id_usuario` objetivo en estado `ACTIVO`.

**Cuando** realiza la solicitud `DELETE /api/v1/usuarios/{id}`.

**Entonces** el sistema:
1. Cambia el estado de la cuenta a `INACTIVO`.
2. Revoca la familia completa de tokens de refresco del usuario.
3. Responde con código HTTP `200 OK` o `204 No Content`.
4. Publica el evento `usuario.actualizado` indicando estado `INACTIVO`.

---

### ESC-08.5 Intento de modificación de atributos de otro usuario sin permisos *(caso borde de seguridad)*

**Dado** un cliente autenticado.

**Cuando** intenta enviar una solicitud `PATCH /api/v1/usuarios/{id_de_otro}` intentando modificar datos ajenos.

**Entonces** el sistema responde con código HTTP `403 FORBIDDEN` y registra el evento de seguridad en SPEC-06.

---

## Requisitos no funcionales — ¿con qué condiciones?

- Las operaciones de actualización de perfil deben ejecutarse en menos de 300 ms en el percentil 95.
- La baja lógica no debe borrar ningún registro de las tablas de auditoría ni de historial de logins.
- El evento `usuario.actualizado` debe ser atómico y enviarse únicamente cuando la transacción en PostgreSQL se haya completado con éxito (`after-commit`).
- Cumplimiento de la Ley N.º 29733 (derecho de ARCO / rectificación y cancelación lógica).

---

## Fuera de alcance — ¿qué NO hará?

- Eliminación física (`DELETE` SQL) de registros de usuarios de la base de datos.
- Cambio de contraseña mediante la actualización de perfil (debe usarse SPEC-03).
- Modificación directa de los roles del usuario desde el endpoint de perfil (debe usarse SPEC-05).

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v1/usuarios/me` | Añade | ⬜ |
| `PATCH /api/v1/usuarios/me` | Añade | ⬜ |
| `DELETE /api/v1/usuarios/{id}` | Añade | ⬜ |
| `usuario.actualizado` | Publica evento | ⬜ |

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube