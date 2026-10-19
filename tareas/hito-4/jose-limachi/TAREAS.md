# H4 — tareas de Jose Luis Limachi Sarmiento

**Épica:** EP-02 Sesiones. **Plan:** [H4 v1.2 §3](../../../plan/hito-4/README.md#jose-luis--ep-02-sesiones--seguridad-de-punta-a-punta).
**Rama:** `feat/EP-02-seguridad-e2e` (backend y frontend). Cada tarea es un commit `FEAT-12-Tn: …`.
La integración con G1 pasó a David (FEAT-11); esta FEAT cubre el criterio de seguridad
de la rúbrica: autenticación en pantallas y APIs, probada automáticamente.

**Fuentes comunes:** [SPEC-05](../../../specs/funcionales/SPEC-05-inicio-sesion.md),
[SPEC-06](../../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md),
[FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md),
[SPEC-12](../../../specs/funcionales/SPEC-12-registro-auditoria.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-12-T1 | `issuer`, discovery y `jwks_uri` por entorno | P1 | — | Pendiente |
| FEAT-12-T2 | Auditoría `SESION_CERRADA` en el logout | P1 | — | Pendiente |
| FEAT-12-T3 | Suite E2E Playwright | P1 | — | Pendiente |
| FEAT-12-T4 | Job E2E en el CI contra el despliegue | P1 | T3; Christian FEAT-09-T1/T2 | Pendiente |
| FEAT-12-T5 | Prueba conjunta con G5 | P1 | T4 | Pendiente |

### FEAT-12-T1 — Configuración por entorno del emisor
- **Resultado (backend):** `issuer`, `/.well-known/openid-configuration` y `jwks_uri` absolutos a partir de la URL pública del entorno (variable), no de `localhost`.
- **Aceptación:** en el despliegue, el `iss` del token coincide con el discovery y el `jwks_uri` responde.
- **Evidencia:** prueba de la configuración y `curl` sanitizado del discovery desplegado.

### FEAT-12-T2 — `SESION_CERRADA`
- **Resultado:** `CierreController` ya documenta el hecho pendiente; registrarlo con el puerto de auditoría de Christian, sin datos del token.
- **Aceptación:** aparece en `/auth/me/actividad` tras cerrar sesión; un logout repetido no duplica.
- **Evidencia:** prueba PostgreSQL y captura de la actividad.

### FEAT-12-T3 — E2E Playwright (frontend)
- **Resultado:** casos: login cliente; login admin con OTP (lee el código del receptor de correo de prueba); refresh silencioso; logout; ruta admin con CLIENTE → bloqueada; ruta privada sin sesión → login.
- **Aceptación:** corre en local contra el stack de pruebas sin credenciales reales en el repo (cuentas de fixtures, secretos por variables).
- **Evidencia:** reporte HTML de Playwright.

### FEAT-12-T4 — Job E2E en el CI
- **Resultado:** job que corre T3 contra el despliegue después del smoke test de Christian; reporte subido como artefacto.
- **Aceptación:** si falla un caso, el pipeline queda en rojo.
- **Evidencia:** enlace al run en verde.

### FEAT-12-T5 — Prueba conjunta con G5
- **Resultado:** token de usuario G7 aceptado por G5 en `/pedidos/me`; sin token, vencido, firma alterada, emisor incorrecto o `tipo` distinto de `acceso` → 401; usuario sin rol CLIENTE → 403. A solo ve sus pedidos y B no accede a los de A al manipular identificadores. No exigir `aud` adicional a tokens humanos (OpenAPI/kit vigente); esa validación corresponde al perfil de servicio.
- **Evidencia:** `docs/evidencia/h4/FEAT-12.md` sin tokens completos.
