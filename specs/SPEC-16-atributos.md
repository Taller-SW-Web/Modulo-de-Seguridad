# SPEC-16 — Gestión de atributos de usuarios

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Hito objetivo** | Hito 5 (Sem. 14) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Es la antigua SPEC-08 «Gestión de atributos de usuarios», sin cambios de contenido salvo uno: ahora es dueña de `GET /api/v1/auth/me`, que exponía la antigua SPEC-01. Cambió de número al dividir el set de 9 a 18 specs (20 de septiembre). Ver [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Los datos del perfil de un usuario (nombres, apellidos, número de teléfono, dirección de contacto) cambian con el tiempo. El proveedor de identidad centraliza esta información y debe asegurar que el propio usuario o un administrador autorizado puedan actualizar estos atributos manteniendo la integridad de los datos.

Ciertos cambios en atributos críticos (como la dirección de correo electrónico o el número de teléfono celular) impactan directamente los mecanismos de autenticación, la verificación por OTP y las notificaciones de los demás módulos del marketplace.

---

## Propósito — ¿para qué?

Proporcionar endpoints seguros para la lectura y actualización del perfil del usuario, así como la gestión de datos de contacto verificados.

El resultado observable es la actualización atómica del perfil en la base de datos de seguridad, el desencadenamiento de procesos de re-verificación si se cambian datos sensibles (correo/celular) y la publicación del evento asíncrono `usuario.atributos_actualizados` hacia los módulos consumidores.

---

## Alcance — ¿hasta dónde?

Esta especificación comprende:

- Los atributos del propio perfil, que se leen con `GET /api/v1/auth/me` (esta spec es su dueña y define qué atributos contiene).
- Edición de atributos no críticos del propio perfil: nombres, apellidos y teléfono.
- Atributos de cliente: tipo y número de documento de identidad, y fecha de nacimiento.
- Atributos de vendedor: código de vendedor, tienda y fecha de ingreso.
- Cifrado y enmascarado del número de documento.
- Direcciones de entrega del cliente.
- Cambio de correo electrónico, confirmado en la dirección nueva.
- Cambio de número celular, verificado por OTP (SPEC-10).
- Edición de los atributos de cualquier cuenta por un administrador.
- Publicación de `usuario.atributos_actualizados` y registro en auditoría (SPEC-12).

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-16.1 | El usuario autenticado debe consultar sus propios atributos con `GET /api/v1/auth/me`; esta spec **no añade un segundo endpoint de lectura**. Un administrador consulta los de cualquiera con `GET /api/v1/usuarios/{id}` (SPEC-03, permiso `usuario.ver`). |
| RF-16.2 | El usuario debe poder actualizar sus propios atributos no críticos —nombres, apellidos, teléfono— con `PATCH /api/v1/usuarios/{id}/atributos`, recibiendo la entidad actualizada. Solo puede modificar los suyos. |
| RF-16.3 | Un cliente tiene como atributos el tipo de documento (`DNI`, `CE`, `PASAPORTE`), el número de documento y la fecha de nacimiento. Un vendedor tiene código de vendedor, tienda y fecha de ingreso. Asignar a una cuenta un atributo que no corresponde a sus roles responde `422 ATRIBUTO_NO_APLICABLE`. |
| RF-16.4 | El número de documento debe almacenarse cifrado con AES, con la clave fuera de la base de datos, y devolverse enmascarado mostrando los últimos tres dígitos (`*****234`), salvo a un módulo con el scope `usuarios:leer:documento` (SPEC-18). |
| RF-16.5 | Un cliente debe poder registrar direcciones de entrega (`POST /api/v1/usuarios/{id}/direcciones`) y marcar una como predeterminada. Las consultan el titular, un administrador y los módulos con el scope `direcciones:leer` (SPEC-18). |
| RF-16.6 | Para cambiar su correo, el titular debe enviar la dirección nueva y su contraseña actual (`POST /api/v1/usuarios/{id}/correo`). Se envía un enlace a la dirección nueva y un aviso a la anterior, que sigue operativa hasta la confirmación. La confirmación usa el mismo `POST /api/v1/auth/verificar-correo` y el mismo mecanismo de token de SPEC-02. |
| RF-16.7 | Si un usuario modifica su número celular, el canal SMS debe marcarse como no verificado hasta completar una prueba OTP según SPEC-10. |
| RF-16.8 | Quien tenga el permiso `atributos.editar` (`ADMIN_SISTEMA`) debe poder consultar y editar los atributos de cualquier cuenta. |
| RF-16.9 | Tras cualquier modificación confirmada de atributos, incluido un cambio de correo, el sistema debe publicar `usuario.atributos_actualizados` con la lista de campos que cambiaron, **nunca sus valores**. |
| RF-16.10 | Toda modificación de atributos debe registrarse en SPEC-12 como `ATRIBUTOS_ACTUALIZADOS`, y un cambio de correo, como `CORREO_CAMBIADO`. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-16.1 Consulta del propio perfil

**Dado** un usuario autenticado con un `accessToken` válido.

**Cuando** envía una solicitud `GET /api/v1/auth/me`.

**Entonces** el sistema responde `200` con sus datos y atributos —identificador, correo, nombres, apellidos, roles, estado, banderas de verificación de contacto— y el documento **enmascarado**.

### ESC-16.2 Actualización de atributos no críticos

**Dado** un cliente autenticado.

**Cuando** realiza un `PATCH /api/v1/usuarios/{suId}/atributos` con `{"nombres": "Eva Lucía", "apellidos": "Moreno"}`.

**Entonces** el sistema actualiza los datos, responde `200` con la entidad actualizada, registra `ATRIBUTOS_ACTUALIZADOS` y publica `usuario.atributos_actualizados` con `"campos": ["nombres", "apellidos"]`.

### ESC-16.3 Cambio de correo electrónico *(caso sensible)*

**Dado** un usuario activo con correo `actual@correo.com`.

**Cuando** envía `POST /api/v1/usuarios/{suId}/correo` con `nuevo@correo.com` y su contraseña actual.

**Entonces** el sistema:
1. Responde `202` y envía un enlace de confirmación a `nuevo@correo.com`.
2. Envía un aviso a `actual@correo.com`.
3. Mantiene el login con `actual@correo.com`.
4. Al confirmarse el enlace con `POST /api/v1/auth/verificar-correo`, aplica el cambio, registra `CORREO_CAMBIADO` y publica `usuario.atributos_actualizados` con `"campos": ["correo"]`.

### ESC-16.4 Cambio de correo con la contraseña equivocada *(caso borde de seguridad)*

**Dado** un usuario autenticado cuya sesión quedó abierta en un equipo compartido.

**Cuando** alguien intenta cambiar el correo sin conocer la contraseña actual.

**Entonces** el sistema responde `401 CREDENCIALES_INVALIDAS`, no envía ningún enlace y el correo no cambia.

### ESC-16.5 Modificación de atributos ajenos *(caso borde de seguridad)*

**Dado** un cliente autenticado.

**Cuando** envía `PATCH /api/v1/usuarios/{id_de_otro}/atributos`.

**Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no modifica nada y registra `ACCESO_DENEGADO` mediante SPEC-12.

### ESC-16.6 Atributo que no corresponde al rol *(caso borde)*

**Dado** una cuenta con rol `CLIENTE`.

**Cuando** se intenta asignarle `codigoVendedor` y `tienda`.

**Entonces** el sistema responde `422 ATRIBUTO_NO_APLICABLE` y no modifica nada.

### ESC-16.7 Documento enmascarado para un módulo sin el scope

**Dado** un cliente con documento `12345234` y un módulo con el scope `usuarios:leer` pero sin `usuarios:leer:documento`.

**Cuando** el módulo consulta `GET /api/v1/usuarios/{id}`.

**Entonces** recibe `documentoEnmascarado: "*****234"` y no el número completo.

### ESC-16.8 Documento en claro solo con el scope *(caso borde de seguridad)*

**Dado** un módulo con el scope `usuarios:leer:documento`.

**Cuando** consulta el mismo cliente.

**Entonces** recibe el documento en claro y se registra `MODULO_OBTUVO_DOCUMENTO` mediante SPEC-12.

### ESC-16.9 Registro de una dirección de entrega

**Dado** un cliente autenticado con una dirección predeterminada.

**Cuando** envía `POST /api/v1/usuarios/{suId}/direcciones` con otra dirección marcada como predeterminada.

**Entonces** el sistema la guarda, responde `201`, y la anterior deja de ser la predeterminada: nunca hay dos a la vez.

### ESC-16.10 Un administrador edita los atributos de un vendedor

**Dado** un `ADMIN_SISTEMA` autenticado y una cuenta con rol `VENDEDOR`.

**Cuando** envía `PATCH /api/v1/usuarios/{id}/atributos` con `{"tienda": "Miraflores"}`.

**Entonces** el sistema actualiza el atributo, responde `200` y publica `usuario.atributos_actualizados` con `"campos": ["tienda"]`.

---

## Requisitos no funcionales — ¿con qué condiciones?

- Las operaciones de actualización de perfil deben ejecutarse en menos de 300 ms en el percentil 95.
- El número de documento se cifra con AES y la clave vive fuera de la base de datos. Nunca aparece en claro en registros, eventos ni auditoría.
- El evento `usuario.atributos_actualizados` se publica únicamente después de confirmar la transacción en PostgreSQL (*after-commit*), y lleva los nombres de los campos, nunca sus valores.
- La API devuelve solo los campos que el solicitante está autorizado a ver.
- Cumplimiento de la Ley N.º 29733 (derecho ARCO de rectificación).

---

## Fuera de alcance — ¿qué NO hará?

- Eliminación física (`DELETE` SQL) de registros de usuarios de la base de datos.
- Cambio de contraseña mediante la actualización de perfil (debe usarse SPEC-07).
- Modificación directa de los roles del usuario desde el endpoint de perfil (debe usarse SPEC-11).
- **La baja lógica y la reactivación de una cuenta, que son de SPEC-04.** Esta spec gestiona atributos, no el ciclo de vida de la cuenta.
- Un segundo endpoint de lectura del propio perfil: se usa `GET /api/v1/auth/me`.
- Verificación del documento contra fuentes externas, o firma electrónica.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v1/auth/me` | Utiliza contrato existente; esta spec es su dueña y define sus atributos | ⬜ |
| `PATCH /api/v1/usuarios/{id}/atributos` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/usuarios/{id}/correo` | Añade | ⬜ |
| `POST /api/v1/auth/verificar-correo` | Reutiliza el de SPEC-02 | ⬜ |
| `GET` y `POST /api/v1/usuarios/{id}/direcciones` | Utiliza contrato existente | ⬜ |
| `usuario.atributos_actualizados` | Publica evento | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
