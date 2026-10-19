# H4 — tareas de Christian Gabriel Arancivia Salas

**Épicas:** TEC-INFRA y EP-06 Auditoría.
**Plan:** [H4 v1.1 §3](../../../plan/hito-4/README.md#christian--ep-06-auditoría--infraestructura).
**Ramas:** `feat/TEC-INFRA-cicd` (backend y frontend) y `feat/EP-06-auditoria-admin`.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama.

**Situación de partida (19-oct):** el CI de ambos repos (`.github/workflows/ci.yml`) hace build,
pruebas e imagen Docker (backend) o lint, typecheck, test y build (frontend), pero **no
despliega**: Render despliega por su cuenta al recibir el push. La rúbrica pide el
recorrido completo **build → compile → test → deploy** en el pipeline.

**Fuentes comunes:** [SPEC-13](../../../specs/funcionales/SPEC-13-consulta-auditoria.md),
[FRONT-14](../../../specs/front/FRONT-14-admin-auditoria.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml) (`/auditoria`, `/auditoria/exportar`),
`docs/migraciones-propuestas/` del repo backend.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-09-T1 | Job de despliegue en GitHub Actions | P1 · dom 26-oct | — | Pendiente |
| FEAT-09-T2 | Smoke test en el pipeline | P1 · dom 26-oct | T1 | Pendiente |
| FEAT-09-T3 | Frontend desplegado desde el pipeline | P1 | — | Pendiente |
| FEAT-09-T4 | Ventana de migraciones V108+ | P1 · continuo | PR de cada FEAT | Pendiente |
| FEAT-09-T5 | CloudAMQP y SMTP real | P1 | — | Pendiente |
| FEAT-09-T6 | Evidencia del recorrido CI/CD | P1 · S3 | T1–T5 | Pendiente |
| FEAT-10-T1 | `GET /auditoria` con filtros | P2 | — | Pendiente |
| FEAT-10-T2 | `GET /auditoria/exportar` | P2 | T1 | Pendiente |
| FEAT-10-T3 | Pantalla FRONT-14 | P2 | T1 | Pendiente |

## FEAT-09 — CI/CD (rúbrica)

### FEAT-09-T1 — Despliegue desde el pipeline
- **Resultado:** job `deploy` que corre solo si build y test pasan en la rama desplegada; dispara el deploy hook de Render (secreto de GitHub) y espera la versión nueva. Desactivar el autodeploy propio de Render para que no despliegue código sin CI.
- **Aceptación:** un push con pruebas en rojo no despliega.
- **Decisión con el equipo:** qué rama despliega (hoy `develop`; el plan propone `main` tras la versión del 5-nov).

### FEAT-09-T2 — Smoke test
- **Resultado:** tras desplegar, comprobar `/actuator/health`, JWKS y un login de prueba con una cuenta de fixtures (credenciales en secretos de GitHub).
- **Aceptación:** si falla, el run queda en rojo. Jose Luis encadena aquí su job E2E (FEAT-12-T4).

### FEAT-09-T3 — Frontend
- **Resultado:** despliegue del SPA (Vercel u otro) desde el pipeline con `VITE_API_BASE_URL` por entorno; agregar ese origen a `G7_CORS_ORIGENES`.

### FEAT-09-T4 — Migraciones V108+
- **Resultado:** revisar y aplicar en Supabase las migraciones de cada FEAT en su ventana, con aviso al grupo y runbook. Sin baseline-on-migrate, repair ni reset.

### FEAT-09-T5 — Mensajería y correo reales
- **Resultado:** `G7_EVENTOS_PUBLICAR=true` con CloudAMQP y SMTP Brevo por el puerto 2525; `G7_SALUD_BROKER` y `G7_SALUD_CORREO` en verde.
- **Evidencia:** correo de OTP real recibido y evento visible en la consola de CloudAMQP (sin datos personales).

### FEAT-09-T6 — Evidencia
- **Evidencia:** `docs/evidencia/h4/FEAT-09.md` con el enlace a un run completo build → test → deploy → smoke → E2E y la checklist de secretos.

## FEAT-10 — Auditoría administrativa (P2)

### FEAT-10-T1/T2 — Consulta y exportación
- **Resultado:** `GET /auditoria` paginado con filtros de SPEC-13 y `/auditoria/exportar` (CSV), solo con permiso de auditoría.
- **Aceptación:** sin permiso → 403; la exportación no incluye secretos ni documento en claro.

### FEAT-10-T3 — FRONT-14
- **Resultado (frontend):** pantalla admin de auditoría y pestaña de historial en FRONT-08.
- **Evidencia:** `docs/evidencia/h4/FEAT-10.md`.
