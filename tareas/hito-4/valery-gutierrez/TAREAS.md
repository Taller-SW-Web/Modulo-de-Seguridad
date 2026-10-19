# H4 — tareas de Valery Cristin Gutierrez Bendezu

**Épica:** EP-08 Perfil. **Plan:** [H4 v1.1 §3](../../../plan/hito-4/README.md#valery--ep-08-perfil).
**Rama:** `feat/EP-08-direcciones-correo` (backend y frontend). Cada tarea es un commit `FEAT-04-Tn: …`.

**Fuentes comunes:** [SPEC-16](../../../specs/funcionales/SPEC-16-atributos.md),
[FRONT-06](../../../specs/front/FRONT-06-mi-cuenta.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml) (`/usuarios/{id}/direcciones`, `/usuarios/{id}/correo`),
[eventos](../../../specs/catalogo-eventos.md), [backend hexagonal](../../../plan/hito-3/BACKEND-HEXAGONAL.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-04-T1 | `GET` y `POST /usuarios/{id}/direcciones` | P1 | — (tabla `direccion` ya existe) | Pendiente |
| FEAT-04-T2 | Puerto exportado de lectura de direcciones | P1 · **dom 26-oct** | T1 | Pendiente |
| FEAT-04-T3 | `POST /usuarios/{id}/correo` | P1 | Juan José FEAT-03-T1; Eva FEAT-01-T3 | Pendiente |
| FEAT-04-T4 | Mi cuenta (FRONT-06) con direcciones y correo | P1 | T1, T3 | Pendiente |
| FEAT-04-T5 | Pruebas y evidencia desplegada | P1 | T1–T4 | Pendiente |
| FEAT-04-T6 | Edición administrativa de atributos | P2 | T5 | Pendiente |

### FEAT-04-T1 — Direcciones del titular
- **Resultado:** listar y crear direcciones propias en el módulo de usuario; validaciones de SPEC-16; evento `usuario.atributos_actualizados`.
- **Aceptación:** otro usuario → 403/404 neutro; campos obligatorios y límite de direcciones según la spec.
- **Evidencia:** pruebas PostgreSQL del adaptador y HTTP del controlador.

### FEAT-04-T2 — Puerto `ConsultaDireccionesPort` exportado
- **Resultado:** puerto de entrada de solo lectura (por id de usuario) para la FEAT-06 de Sergio (`direcciones:leer`, G1 F-022). Sin exponer entidades JPA.
- **Aceptación:** ArchUnit en verde; Sergio lo consume por un adaptador puente.
- **Evidencia:** aviso al grupo con la firma del puerto.

### FEAT-04-T3 — Cambio de correo
- **Resultado:** pide la contraseña actual, emite token `CAMBIO_CORREO` al correo nuevo y aviso al anterior; reutiliza la verificación de Eva para confirmar.
- **Aceptación:** correo ya usado → respuesta neutra; contraseña mala → error del catálogo; el correo cambia solo al confirmar.
- **Evidencia:** pruebas PostgreSQL y receptor SMTP local.

### FEAT-04-T4 — Mi cuenta (FRONT-06)
- **Resultado (frontend):** secciones de direcciones (lista y alta) y cambio de correo en `MiCuentaPage`.
- **Aceptación:** criterios de FRONT-06; errores visibles sin datos sensibles.
- **Evidencia:** Vitest y capturas del despliegue.

### FEAT-04-T5 — Recorrido de punta a punta
- **Evidencia:** `docs/evidencia/h4/FEAT-04.md`: alta de dirección, cambio de correo confirmado y login con el correo nuevo, con SHAs.

### FEAT-04-T6 — Edición administrativa (P2)
- **Resultado:** `PATCH /usuarios/{id}/atributos` por un admin con `atributos.editar` y su botón en FRONT-08.
- **Aceptación:** sin permiso → 403; auditoría del cambio.
