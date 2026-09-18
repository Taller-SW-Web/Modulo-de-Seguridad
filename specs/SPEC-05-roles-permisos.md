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
- Inclusión de los claims `roles` y `permisos` en el `accessToken` durante la emisión/refresco.
- Catálogo de permisos de este módulo.
- Revocación de todas las sesiones del usuario cuando cambian sus roles.
- Publicación del evento `usuario.roles_cambiados`.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-05.1 | El sistema debe soportar de forma exclusiva el conjunto cerrado de 6 roles definidos en el contrato, rechazando códigos en inglés o roles no contemplados. |
| RF-05.2 | El sistema debe permitir que un usuario posea múltiples roles asignados simultáneamente. |
| RF-05.3 | Un usuario con rol `ADMIN_SISTEMA` (permiso `rol.asignar`) debe poder asignar un rol (`POST /api/v1/usuarios/{id}/roles`) o revocarlo (`DELETE /api/v1/usuarios/{id}/roles/{rol}`), **de uno en uno**. No se reemplaza la lista entera: dos administradores que editan a la vez no se pisan los cambios. |
| RF-05.4 | El sistema debe calcular los permisos efectivos realizando la unión sin duplicados de todas las capacidades concedidas por cada rol activo del usuario. |
| RF-05.5 | Al cambiar los roles de un usuario, el sistema debe revocar inmediatamente todos sus tokens de refresco vigentes para forzar una nueva autenticación. |
| RF-05.6 | Tras modificar exitosamente los roles de un usuario, el sistema debe publicar el evento `usuario.roles_cambiados` en RabbitMQ. |
| RF-05.7 | Un `ADMIN_SISTEMA` no puede quitarse a sí mismo el rol `ADMIN_SISTEMA`, y nadie puede quitárselo al último `ADMIN_SISTEMA` activo: responde `422 ADMINISTRADOR_PROTEGIDO`, la misma regla que aplican SPEC-01 a la baja y SPEC-07 al bloqueo. |
| RF-05.8 | Toda asignación o modificación de roles debe quedar registrada mediante el módulo de auditoría de SPEC-06. |
| RF-05.9 | El sistema debe definir el catálogo de permisos de este módulo (tabla siguiente) y concederlo solo a `ADMIN_SISTEMA`. Los permisos de cada módulo consumidor se acuerdan con su equipo y se añaden a este catálogo; hasta entonces, `permisos` lleva solo los de este módulo y los consumidores autorizan por `roles`. |
| RF-05.10 | Las acciones de un usuario sobre su propia cuenta —ver su perfil, editar sus atributos, cambiar su contraseña— no requieren permiso: se autorizan por titularidad, comparando el `sub` del token con la cuenta afectada. |

### Catálogo de permisos de este módulo

Se escriben `recurso.accion`, con punto. Los dos puntos quedan para los scopes de
los módulos consumidores (SPEC-09), y así un permiso y un scope no se confunden
nunca.

| Permiso | Qué autoriza | Lo exige | Rol |
|---|---|---|---|
| `usuario.ver` | Listar y consultar cualquier cuenta | SPEC-01 | `ADMIN_SISTEMA` |
| `usuario.crear` | Dar de alta vendedores y roles de gestión | SPEC-01 | `ADMIN_SISTEMA` |
| `usuario.desactivar` | Dar de baja una cuenta | SPEC-01 | `ADMIN_SISTEMA` |
| `usuario.reactivar` | Reactivar una cuenta dada de baja | SPEC-01 | `ADMIN_SISTEMA` |
| `rol.asignar` | Asignar y revocar roles | SPEC-05 | `ADMIN_SISTEMA` |
| `auditoria.ver` | Consultar y exportar la auditoría | SPEC-06 | `ADMIN_SISTEMA` |
| `cuenta.bloquear` | Bloquear y desbloquear cuentas | SPEC-07 | `ADMIN_SISTEMA` |
| `atributos.editar` | Editar los atributos de cualquier cuenta | SPEC-08 | `ADMIN_SISTEMA` |

---

## Escenarios — ¿cómo verificamos?

### ESC-05.1 Asignación exitosa de un nuevo rol

**Dado** un usuario autenticado como `ADMIN_SISTEMA` y una cuenta objetivo en estado `ACTIVO` que solo posee el rol `VENDEDOR`.

**Cuando** realiza una solicitud `POST /api/v1/usuarios/{id}/roles` con el payload `{ "rol": "GESTOR_DESPACHO" }`.

**Entonces** el sistema:
1. Actualiza los roles del usuario.
2. Revoca todas las sesiones de dicha cuenta.
3. Responde con código HTTP `200 OK`.
4. Publica el evento `usuario.roles_cambiados`.

---

### ESC-05.2 Asignación de rol inexistente o no permitido *(caso borde)*

**Dado** un `ADMIN_SISTEMA`.

**Cuando** envía una solicitud a `POST /api/v1/usuarios/{id}/roles` con un código inválido como `"BUYER"` o `"SUPER_ADMIN"`.

**Entonces** el sistema responde con código HTTP `400 VALIDACION`, no modifica la base de datos y detalla que el código de rol no pertenece al catálogo cerrado.

---

### ESC-05.3 Intento de modificación por un usuario no autorizado *(caso borde de seguridad)*

**Dado** un usuario autenticado con rol `ADMIN_VENTAS`.

**Cuando** intenta invocar `POST /api/v1/usuarios/{id}/roles`.

**Entonces** el sistema responde con código HTTP `403 SCOPE_INSUFICIENTE` y registra el intento de violación de acceso en auditoría (SPEC-06).

---

### ESC-05.4 Cálculo de permisos efectivos en la emisión del token

**Dado** un usuario que posee los roles `ADMIN_VENTAS` (permisos: `pedido.leer`, `pedido.aprobar`) y `GESTOR_COMERCIAL` (permisos: `producto.crear`, `pedido.leer`). *Son permisos de ejemplo de los módulos consumidores, pendientes de acordar con sus equipos.*

**Cuando** se emite o renueva su `accessToken`.

**Entonces** el array de permisos efectivos en las claims del JWT debe contener exactamente la lista sin duplicados: `["pedido.leer", "pedido.aprobar", "producto.crear"]`.

---

### ESC-05.5 Prevención de eliminación del último administrador *(caso borde)*

**Dado** que existe un único usuario activo con el rol `ADMIN_SISTEMA`.

**Cuando** ese mismo usuario (o un script) intenta quitarse su propio rol con `DELETE /api/v1/usuarios/{id}/roles/ADMIN_SISTEMA`.

**Entonces** el sistema rechaza la operación con código HTTP `422 ADMINISTRADOR_PROTEGIDO`, emite el mensaje "No se puede eliminar el único administrador del sistema" y mantiene el rol intacto.

---

### ESC-05.6 Revocación de un rol

**Dado** un `ADMIN_SISTEMA` y una cuenta con los roles `VENDEDOR` y `GESTOR_DESPACHO`.

**Cuando** envía `DELETE /api/v1/usuarios/{id}/roles/GESTOR_DESPACHO`.

**Entonces** la cuenta conserva solo `VENDEDOR`, se revocan sus sesiones, se registra `ROL_REVOCADO`, se publica `usuario.roles_cambiados` con la lista completa resultante y el sistema responde `204`.

---

### ESC-05.7 Permisos de este módulo en el token de un administrador

**Dado** un usuario con el rol `ADMIN_SISTEMA`.

**Cuando** inicia sesión y recibe su `accessToken`.

**Entonces** el claim `permisos` contiene los ocho permisos del catálogo de este módulo (`usuario.ver`, `usuario.crear`… `atributos.editar`), sin duplicados y con punto.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El cálculo de permisos efectivos no debe añadir más de 15 ms al tiempo de generación/firma del JWT.
- Las claims del JWT no deben superar un tamaño de 4 KB para evitar problemas con cabeceras HTTP en gateways o proxies reverse.
- Los nombres de los permisos deben seguir la estructura `recurso.accion`, en minúsculas y con punto; los dos puntos quedan para los scopes de SPEC-09. Los permisos van en infinitivo (`usuario.crear`) y los eventos en participio (`usuario.creado`), para no confundirlos.
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
| `POST /api/v1/usuarios/{id}/roles` | Utiliza contrato existente | ⬜ |
| `DELETE /api/v1/usuarios/{id}/roles/{rol}` | Utiliza contrato existente | ⬜ |
| `GET /api/v1/roles` · `GET /api/v1/permisos` | Llena el catálogo que publica SPEC-09 | ⬜ |
| Claim `permisos` del token | Pasa de vacío a los permisos de este módulo | ⬜ |
| `usuario.roles_cambiados` | Publica evento | ⬜ |

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube