# H4 — tareas de Eva Lucía Moreno Zevallos

**Épica:** EP-01 Cuentas. **Plan:** [H4 v1.1 §3](../../../plan/hito-4/README.md#eva--ep-01-cuentas).
**Ramas:** `feat/EP-01-registro-verificacion` (backend y frontend, mismo nombre) y
`feat/EP-01-baja-reactivacion`. Cada tarea es un commit `FEAT-0x-Tn: …` en su rama.

**Fuentes comunes:** [SPEC-01](../../../specs/funcionales/SPEC-01-registro-clientes.md),
[SPEC-02](../../../specs/funcionales/SPEC-02-verificacion-correo.md),
[SPEC-04](../../../specs/funcionales/SPEC-04-baja-reactivacion.md),
[FRONT-02](../../../specs/front/FRONT-02-registro-cliente.md),
[FRONT-03](../../../specs/front/FRONT-03-verificacion-correo.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml), [errores](../../../specs/catalogo-errores.md),
[eventos](../../../specs/catalogo-eventos.md), [backend hexagonal](../../../plan/hito-3/BACKEND-HEXAGONAL.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-01-T1 | `POST /auth/registro` | P1 | — | Pendiente |
| FEAT-01-T2 | Token de verificación y correo | P1 | Juan José FEAT-03-T1 (mié 22) | Pendiente |
| FEAT-01-T3 | Verificar y reenviar | P1 | T2 | Pendiente |
| FEAT-01-T4 | Retorno por canal | P1 | T3; David FEAT-11-T1 (URL de G1) | Pendiente |
| FEAT-01-T5 | Pantallas FRONT-02 y FRONT-03 | P1 | T1, T3 | Pendiente |
| FEAT-01-T6 | Pruebas y evidencia desplegada | P1 | T1–T5; Christian V108+ si hay migración | Pendiente |
| FEAT-02-T1 | Baja y reactivación | P2 | FEAT-01 fusionada | Pendiente |

## FEAT-01 — Registro y verificación de correo (G1 F-001)

### FEAT-01-T1 — `POST /auth/registro`
- **Resultado:** caso de uso de registro en el módulo de cuentas: datos de SPEC-01, `canalOrigen`,
  política de contraseña de Juan José (`/password/politica`), cuenta `PENDIENTE_VERIFICACION` y rol CLIENTE, sin sesión.
- **Aceptación:** correo existente → la misma respuesta que uno nuevo (sin enumeración); contraseña
  rechazada → error del catálogo; documento cifrado/HMAC como en V107.
- **Evidencia:** pruebas unitarias del servicio y PostgreSQL del adaptador; ArchUnit en verde.

### FEAT-01-T2 — Token de verificación y correo
- **Resultado:** emite token `VERIFICAR_CORREO` con `TokensUnUsoPort` y encola el correo por el outbox existente (enlace, nunca el token en logs).
- **Aceptación:** un token activo por cuenta; reemitir invalida el anterior.
- **Evidencia:** prueba con el receptor SMTP local que muestra el correo, sin pegar el token.

### FEAT-01-T3 — `POST /auth/verificar-correo` y `/auth/verificar-correo/reenviar`
- **Resultado:** válido → `ACTIVO` + evento `usuario.creado` + auditoría; usado, vencido o inválido → error neutro.
  Reenvío limitado con `solicitud_limitada`. Dejar el caso de uso reutilizable para el cambio de correo de Valery.
- **Aceptación:** los cuatro casos del token y el límite de reenvío con 429.
- **Evidencia:** pruebas PostgreSQL de cada caso.

### FEAT-01-T4 — Retorno por canal
- **Resultado:** tras verificar, redirige según `canalOrigen` (MARKETPLACE → login de G1) usando una lista de URL permitidas por entorno.
- **Aceptación:** URL fuera de la lista → retorno por defecto del SPA; nunca token ni correo en la URL.
- **Evidencia:** prueba de la lista y captura con G1 (coordinar con David).

### FEAT-01-T5 — Pantallas FRONT-02 y FRONT-03
- **Resultado (frontend):** formulario de registro con medidor de contraseña y pantalla de verificación (éxito, vencido, reenviar), rutas públicas.
- **Aceptación:** criterios de FRONT-02/03; errores del backend mostrados sin enumerar cuentas.
- **Evidencia:** pruebas Vitest y capturas del despliegue.

### FEAT-01-T6 — Pruebas de punta a punta
- **Resultado:** recorrido registro → correo → verificación → login en el despliegue.
- **Evidencia:** `docs/evidencia/h4/FEAT-01.md` con fecha, SHAs y capturas sanitizadas. Pedir a Sergio el cambio del OpenAPI si hace falta.

## FEAT-02 — Baja y reactivación (P2)

### FEAT-02-T1 — `DELETE /usuarios/{id}` y `POST /usuarios/{id}/reactivar`
- **Fuentes:** SPEC-04; FRONT-08 (acciones del detalle).
- **Resultado:** baja lógica que revoca sesiones y audita; reactivación por ADMIN; botones con confirmación en FRONT-08; eventos del catálogo.
- **Aceptación:** sin permiso → 403; cuenta dada de baja no inicia sesión; reactivada sí.
- **Evidencia:** pruebas PostgreSQL + captura del detalle admin.
