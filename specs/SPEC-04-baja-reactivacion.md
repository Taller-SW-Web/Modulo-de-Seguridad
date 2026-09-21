# SPEC-04 — Baja y reactivación de cuentas

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-01 «Registro y gestión de usuarios» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Una cuenta que deja de usarse —un vendedor que ya no trabaja en la tienda, un cliente que lo pide— no puede simplemente borrarse: su historial de pedidos, sus registros de auditoría y las referencias de los otros seis módulos apuntan a su identificador.

Por eso la baja es lógica: la cuenta deja de operar pero sigue existiendo, y puede reactivarse con la misma identidad. Además, el sistema no puede quedarse nunca sin un administrador capaz de gestionarlo.

---

## Propósito — ¿para qué?

Permitir que un `ADMIN_SISTEMA` dé de baja una cuenta sin borrarla y la reactive después.

El resultado observable es el paso de la cuenta a `INACTIVO` con sus sesiones cerradas y la publicación de `usuario.desactivado`, y, al reactivarla, su vuelta a `ACTIVO` con la misma identidad y la publicación de `usuario.reactivado`.

---

## Alcance — ¿hasta dónde?

- Baja lógica con `DELETE /api/v1/usuarios/{id}`: estado `INACTIVO`, sin borrado.
- Revocación de todas las sesiones de la cuenta dada de baja.
- Protección del propio administrador y del último `ADMIN_SISTEMA` activo.
- Reactivación con `POST /api/v1/usuarios/{id}/reactivar`.
- Publicación de `usuario.desactivado` y `usuario.reactivado`, y registro en auditoría.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-04.1 | Un `ADMIN_SISTEMA` debe poder dar de baja una cuenta (`DELETE /api/v1/usuarios/{id}`): pasa a `INACTIVO` sin borrarse, se revocan todos sus tokens de refresco y se publica `usuario.desactivado`. Desde ese momento el login responde `401 CREDENCIALES_INVALIDAS`, igual que ante una contraseña incorrecta. |
| RF-04.2 | Nadie puede darse de baja a sí mismo por esta vía, ni dar de baja al último `ADMIN_SISTEMA` activo: responde `422 ADMINISTRADOR_PROTEGIDO`, la misma regla que aplican SPEC-11 al revocar el rol y SPEC-15 al bloquear. |
| RF-04.3 | Un `ADMIN_SISTEMA` debe poder reactivar una cuenta `INACTIVO` (`POST /api/v1/usuarios/{id}/reactivar`): vuelve a `ACTIVO` con la misma identidad, el mismo correo y los mismos roles, sin repetir la verificación y sin sesiones abiertas, y se publica `usuario.reactivado`. |
| RF-04.4 | La baja y la reactivación deben registrarse mediante SPEC-12 (`USUARIO_DESACTIVADO`, `USUARIO_REACTIVADO`). Ambas son acciones **críticas**: si no se pueden auditar, la operación se revierte. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-04.1 Baja lógica de una cuenta

**Dado** un `ADMIN_SISTEMA` y una cuenta `ACTIVO` con dos sesiones abiertas.

**Cuando** envía `DELETE /api/v1/usuarios/{id}`.

**Entonces** la cuenta pasa a `INACTIVO` sin borrarse, se revocan sus dos sesiones, se registra `USUARIO_DESACTIVADO`, se publica `usuario.desactivado` y el sistema responde `204`. Un login posterior de esa cuenta responde `401 CREDENCIALES_INVALIDAS`.

### ESC-04.2 Baja del último administrador *(caso borde)*

**Dado** que existe un único `ADMIN_SISTEMA` activo.

**Cuando** alguien intenta darlo de baja, o él intenta darse de baja a sí mismo.

**Entonces** el sistema responde `422 ADMINISTRADOR_PROTEGIDO` y la cuenta sigue `ACTIVO`.

### ESC-04.3 Reactivación de una cuenta dada de baja

**Dado** una cuenta `INACTIVO` con rol `CLIENTE` y un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios/{id}/reactivar`.

**Entonces** la cuenta vuelve a `ACTIVO` con el mismo identificador, correo y roles, sin repetir la verificación y sin ninguna sesión abierta; se registra `USUARIO_REACTIVADO` y se publica `usuario.reactivado`, **no** `usuario.creado`.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La baja y la revocación de las sesiones deben ocurrir en la misma transacción: no puede quedar una cuenta `INACTIVO` con un token de refresco vigente.
- Los eventos se publican solo después de confirmar la transacción.
- Tras la baja, el inicio de sesión responde exactamente igual que ante una contraseña incorrecta.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El borrado físico de la cuenta o de su historial.
- La baja por decisión del propio titular: está pendiente de decisión (Q10). Hoy solo da de baja un administrador.
- El bloqueo, que es un estado distinto y pertenece a SPEC-14 y SPEC-15.
- La revocación de roles, que es de SPEC-11.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `DELETE /api/v1/usuarios/{id}` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/usuarios/{id}/reactivar` | Añade | ⬜ |
| `usuario.desactivado` · `usuario.reactivado` | Publica eventos | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
