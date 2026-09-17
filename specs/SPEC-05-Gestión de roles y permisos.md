# SPEC-05 — Gestión de roles y permisos

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Hito objetivo** | Hito 4 (Sem. 11) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

---

## Contexto — ¿por qué?

El marketplace opera con un modelo de control de acceso basado en roles (RBAC). Para garantizar la segregación de funciones entre los diferentes módulos consumidores (Ventas, Despacho, Productos, etc.), el proveedor de identidad debe calcular de manera centralizada los permisos efectivos que posee cada persona y empaquetarlos dentro de su `accessToken`.

Dado que un usuario puede tener asignado más de un rol simultáneamente, el cálculo de los permisos efectivos debe realizarse de forma eficiente sin que los demás módulos tengan que consultar la base de datos de seguridad.

---

## Propósito — ¿para qué?

Administrar el conjunto cerrado de roles del sistema, permitir la asignación y revocación de roles a los usuarios por parte de los administradores, y calcular los permisos efectivos (unión sin duplicados) que serán incluidos en las claims del token JWT.

El resultado observable es la actualización inmediata de la matriz de accesos del usuario, la revocación/rotación de tokens para forzar la actualización de permisos y la publicación del evento asíncrono `usuario.roles_cambiados`.

---

## Alcance — ¿hasta dónde?

Esta especificación comprende:

- Gestión del catálogo cerrado de los 6 roles fijos del sistema:
  - `CLIENTE`
  - `VENDEDOR`
  - `ADMIN_VENTAS`
  - `GESTOR_DESPACHO`
  - `GESTOR_COMERCIAL`
  - `ADMIN_SISTEMA`
- Asignación y desasignación de roles a usuarios por parte de `ADMIN_SISTEMA`.
- Cálculo del conjunto de **permisos efectivos** (unión de permisos otorgados por cada rol asignado).
- Inclusión del claim `roles` y `permissions` en el `accessToken` durante la emisión/refresco.
- Revocación obligatoria de la familia de tokens activa cuando los roles de un usuario son modificados.
- Publicación del evento `usuario.roles_cambiados`.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-05.1 | El sistema debe soportar de forma exclusiva el conjunto cerrado de 6 roles definidos en el contrato, rechazando códigos en inglés o roles no contemplados. |
| RF-05.2 | El sistema debe permitir que un usuario posea múltiples roles asignados simultáneamente. |
| RF-05.3 | Un usuario con rol `ADMIN_SISTEMA` debe poder asignar o remover roles a cualquier cuenta del sistema. |
| RF-05.4 | El sistema debe calcular los permisos efectivos realizando la unión sin duplicados de todas las capacidades concedidas por cada rol activo del usuario. |
| RF-05.5 | Al cambiar los roles de un usuario, el sistema debe revocar inmediatamente todos los `refreshToken` vigentes del usuario para forzar una nueva autenticación. |
| RF-05.6 | Tras modificar exitosamente los roles de un usuario, el sistema debe publicar el evento `usuario.roles_cambiados` en RabbitMQ. |
| RF-05.7 | Ningún usuario podrá desasignarse a sí mismo el rol `ADMIN_SISTEMA` si es el único administrador activo del sistema (prevención de orfandad). |
| RF-05.8 | Toda asignación o modificación de roles debe quedar registrada mediante el módulo de auditoría de SPEC-06. |

---

## Escenarios — ¿cómo verificamos?

### ESC-05.1 Asignación exitosa de un nuevo rol

**Dado** un usuario autenticado como `ADMIN_SISTEMA` y una cuenta objetivo en estado `ACTIVO` que solo posee el rol `VENDEDOR`.

**Cuando** realiza una solicitud `PUT /api/v1/usuarios/{id}/roles` con el payload `["VENDEDOR", "GESTOR_DESPACHO"]`.

**Entonces** el sistema:
1. Actualiza los roles del usuario.
2. Invalida los refresh tokens activos de dicha cuenta.
3. Responde con código HTTP `200 OK`.
4. Publica el evento `usuario.roles_cambiados`.

---

### ESC-05.2 Asignación de rol inexistente o no permitido *(caso borde)*

**Dado** un `ADMIN_SISTEMA`.

**Cuando** envía una solicitud a `PUT /api/v1/usuarios/{id}/roles` incluyendo un código invalido como `"BUYER"` o `"SUPER_ADMIN"`.

**Entonces** el sistema responde con código HTTP `400 BAD_REQUEST`, no modifica la base de datos y detalla que el código de rol no pertenece al catálogo cerrado.

---

### ESC-05.3 Intento de modificación por un usuario no autorizado *(caso borde de seguridad)*

**Dado** un usuario autenticado con rol `ADMIN_VENTAS`.

**Cuando** intenta invocar el endpoint `PUT /api/v1/usuarios/{id}/roles`.

**Entonces** el sistema responde con código HTTP `403 SCOPE_INSUFFICIENT` o `403 FORBIDDEN` y registra el intento de violación de acceso en auditoría (SPEC-06).

---

### ESC-05.4 Cálculo de permisos efectivos en la emisión del token

**Dado** un usuario que posee los roles `ADMIN_VENTAS` (permisos: `pedido:leer`, `pedido:aprobar`) y `GESTOR_COMERCIAL` (permisos: `producto:crear`, `pedido:leer`).

**Cuando** se emite o renueva su `accessToken`.

**Entonces** el array de permisos efectivos en las claims del JWT debe contener exactamente la lista sin duplicados: `["pedido:leer", "pedido:aprobar", "producto:crear"]`.

---

### ESC-05.5 Prevención de eliminación del último administrador *(caso borde)*

**Dado** que existe un único usuario activo con el rol `ADMIN_SISTEMA`.

**Cuando** ese mismo usuario (o un script) intenta remover su propio rol `ADMIN_SISTEMA`.

**Entonces** el sistema rechaza la operación con código HTTP `422 UNPROCESSABLE_ENTITY`, emite el mensaje "No se puede eliminar el único administrador del sistema" y mantiene el rol intacto.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El cálculo de permisos efectivos no debe añadir más de 15 ms al tiempo de generación/firma del JWT.
- Las claims del JWT no deben superar un tamaño de 4 KB para evitar problemas con cabeceras HTTP en gateways o proxies reverse.
- Los nombres de los permisos deben seguir la estructura estricta `modulo:accion` en minúsculas.
- Las mutaciones de roles deben ejecutarse de manera transaccional y atómica en PostgreSQL.

---

## Fuera de alcance — ¿qué NO hará?

- Creación dinámicas de nuevos roles en tiempo de ejecución (el catálogo de 6 roles es fijo).
- Asignación de permisos individuales directamente a un usuario sin pasar por un rol.
- Evaluación en tiempo real dentro del backend de los módulos consumidores (cada módulo evalúa el token en local).

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v1/usuarios/{id}/roles` | Añade | ⬜ |
| `PUT /api/v1/usuarios/{id}/roles` | Modifica | ⬜ |
| `usuario.roles_cambiados` | Publica evento | ⬜ |

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube