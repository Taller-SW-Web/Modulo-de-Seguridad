# H4 — tareas de Luis David Morales Brenis

**Épicas:** integración G1, EP-04 Segundo factor, EP-07 Bloqueo.
**Plan:** [H4 v1.1 §3](../../../plan/hito-4/README.md#luis-david--integración-g1-ep-04-segundo-factor-y-ep-07-bloqueo).
**Ramas:** `feat/INT-G1-marketplace` (primero), `feat/EP-04-mfa-v2` y `feat/EP-07-bloqueo-manual`.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama.

**Fuentes comunes:** documento «Coordinación G1–G7 Seguridad H4» (19-oct),
[plan §5](../../../plan/hito-4/README.md#5-integración-con-g1-marketplace),
[matriz de scopes](../../../docs/integracion/matriz-scopes.md),
[SPEC-10](../../../specs/funcionales/SPEC-10-activacion-mfa.md),
[SPEC-15](../../../specs/funcionales/SPEC-15-bloqueo-manual.md),
[FRONT-09](../../../specs/front/FRONT-09-segundo-factor.md),
[FRONT-08](../../../specs/front/FRONT-08-admin-detalle-usuario.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml), [OpenAPI v2](../../../specs/contrato-api/openapi-v2.yaml).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-11-T1 | Origen y URL de retorno de G1 por entorno | P1 | — | Pendiente |
| FEAT-11-T2 | **Prueba temprana con G1** | P1 · **sáb 25-oct** | T1; despliegue actual | Pendiente |
| FEAT-11-T3 | Paquete para G1 | P1 | T2 | Pendiente |
| FEAT-11-T4 | Seguimiento por operación | P1 | FEAT-01/03/04/05/06 al fusionarse | Pendiente |
| FEAT-11-T5 | Prueba conjunta final con G1 | P1 · S3 | T4 | Pendiente |
| FEAT-07-T1 | `POST /auth/mfa/cambios` (v2) | P2 | — | Pendiente |
| FEAT-07-T2 | Reenviar y confirmar (v2) | P2 | T1 | Pendiente |
| FEAT-07-T3 | Adaptador SMS simulado | P2 | T1 | Pendiente |
| FEAT-07-T4 | Pantalla FRONT-09 y evidencia | P2 | T1–T3 | Pendiente |
| FEAT-08-T1 | Bloquear y desbloquear por ADMIN | P2 | Backend #55 fusionado | Pendiente |
| FEAT-08-T2 | Acciones en FRONT-08 y evidencia | P2 | T1 | Pendiente |

## FEAT-11 — Integración con G1 Marketplace (P1)

David es el contacto técnico con G1. Cambios de alcance o del contrato pasan por Sergio (PO).

### FEAT-11-T1 — Configuración para G1
- **Resultado:** origen del frontend de G1 en `G7_CORS_ORIGENES` del despliegue (lo carga Christian) y sus URL de retorno en la lista permitida por entorno (la usa Eva en FEAT-01-T4).
- **Aceptación:** preflight CORS desde el origen de G1 → permitido; desde otro → rechazado.
- **Evidencia:** captura del preflight y lista de orígenes en la evidencia (sin secretos).

### FEAT-11-T2 — Prueba temprana (S1)
- **Resultado:** G1 inicia sesión contra el despliegue de G7 y llama a una operación protegida (`GET /auth/me`); también refresh y logout si alcanza.
- **Aceptación:** funciona con la cuenta A; sin token → 401; token alterado → 401.
- **Evidencia:** `docs/evidencia/h4/FEAT-11-temprana.md` con fecha, SHA desplegado y capturas sanitizadas.

### FEAT-11-T3 — Paquete para G1
- **Resultado:** matriz por operación (versión, estado, SHA, URL) y cuentas A, B, pendiente, bloqueada y con MFA entregadas por canal seguro (nunca en git ni en el grupo).
- **Evidencia:** la matriz en `docs/integracion/` (sin contraseñas).

### FEAT-11-T4 — Seguimiento por operación
- **Resultado:** cada vez que se fusiona FEAT-01, 03, 04, 05 o 06, se actualiza la matriz y se avisa a G1; el cliente técnico `modulo-marketplace` se entrega cuando Sergio lo registre.

### FEAT-11-T5 — Prueba conjunta final (S3)
- **Resultado:** dos usuarios (A y B) recorren F-001 a F-005 desde G1, y G1 obtiene un token de servicio con su scope.
- **Aceptación:** A no ve datos de B; operaciones fuera de scope → 403.
- **Evidencia:** `docs/evidencia/h4/FEAT-11.md`.

## FEAT-07 — Activación de MFA v2 (P2)

### FEAT-07-T1 — `POST /auth/mfa/cambios`
- **Resultado:** inicia el cambio de MFA (activar, cambiar canal o desactivar según SPEC-10) con reautenticación; envía código al canal nuevo.
- **Aceptación:** gestión (ADMIN) no puede desactivar MFA; no se implementan `/auth/otp/habilitar|deshabilitar` de v1.

### FEAT-07-T2 — `/auth/mfa/cambios/reenviar` y `/confirmar`
- **Resultado:** reenvío con cuota y confirmación que aplica el cambio, audita y avisa por correo.
- **Aceptación:** código vencido, usado o erróneo → error del catálogo.

### FEAT-07-T3 — SMS simulado
- **Resultado:** adaptador de salida que registra el envío en el receptor de pruebas (sin proveedor real).

### FEAT-07-T4 — FRONT-09
- **Resultado (frontend):** pantalla de segundo factor en Mi cuenta.
- **Evidencia:** `docs/evidencia/h4/FEAT-07.md`.

## FEAT-08 — Bloqueo manual (P2)

### FEAT-08-T1 — `POST /usuarios/{id}/bloquear` y `/desbloquear`
- **Resultado:** ADMIN bloquea con motivo y vencimiento opcional; revoca sesiones; desbloqueo manual; auditoría y eventos. El bloqueo ya se ve en el detalle (backend #55).
- **Aceptación:** un bloqueo manual no se levanta con el enlace de desbloqueo automático; sin permiso → 403.

### FEAT-08-T2 — Acciones en FRONT-08
- **Resultado (frontend):** botones Bloquear/Desbloquear con motivo en el detalle admin.
- **Evidencia:** `docs/evidencia/h4/FEAT-08.md`.
