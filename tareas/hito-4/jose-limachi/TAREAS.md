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
| FEAT-12-T1 | `issuer`, discovery y `jwks_uri` por entorno | P1 | — | En curso |
| FEAT-12-T2 | Auditoría `SESION_CERRADA` en el logout | P1 | — | En curso |
| FEAT-12-T3 | Suite E2E Playwright | P1 | — | En curso |
| FEAT-12-T4 | Job E2E en el CI contra el despliegue | P1 | T3; Christian FEAT-09-T1/T2 | Pendiente |
| FEAT-12-T5 | Prueba conjunta con G5 | P1 | T4 | Pendiente |

> **22-oct-2026 · FEAT-12-T1 (En curso):** commit local en `feat/EP-02-seguridad-e2e`
> (backend, sin PR/push). Prueba nueva `ConfiguracionEntornoHttpTest` (2/2 en verde):
> las tres URLs absolutas salen de la variable del entorno, no de `localhost` ni de la
> petición; el `iss` del token emitido coincide con el discovery y su firma se verifica
> con el JWKS publicado. `configuration` + `identidad`: 155 pruebas, 0 fallos (suite
> completa: los 19 fallos son solo por falta de Docker/Testcontainers). Despliegue
> comprobado en solo lectura: discovery y JWKS responden 200 con el issuer público de
> Render. **Pendiente:** `iss` de un token real en el despliegue (requiere credenciales
> por canal seguro; si no, junto con FEAT-12-T5 en S3) → todavía no **Verificada**.
> Evidencia: `docs/evidencia/h4/FEAT-12.md` del backend. Observación para FEAT-09: el
> discovery desplegado no incluye `token_endpoint`/`introspection_endpoint`, así que el
> SHA desplegado es anterior a FEAT-05.

> **24-oct-2026 · FEAT-12-T2 (En curso):** commit en la misma rama del backend; entra en
> el [PR #60](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/60) (borrador,
> CI en verde con T1). El hecho `SESION_CERRADA` sale por el puerto nuevo `EfectosCierrePort`
> y su adaptador hacia `AuditoriaPort` (Christian, EP-06): no crítico, se registra tras el
> commit, con ip/agente de la petición (`cerrar` ahora recibe `ContextoPeticion`) y con la
> familia solo por su id — sin tokens ni refrescos. Nace **solo** desde `Cerrada`: un logout
> repetido o un refresco desconocido responden 204 sin duplicar el hecho ni inventar
> identidad. El catálogo SPEC-12 no cambia (`SESION_CERRADA` ya existía, no crítica).
> Pruebas: `SesionServiceTest` 16/16 en verde local; `CerrarSesionPostgresTest` y
> `SesionesIntegracionPostgresTest` (HTTP + PostgreSQL; con T2 la integración incluye ya
> auditoría) acreditan que tras el logout aparece **un solo** `SESION_CERRADA` en
> `/auth/me/actividad` y que el repetido no lo duplica. **CI en verde** (run 40106674716,
> commit `cce2953`: los tres checks, con las pruebas Docker); el artefacto confirma
> `SesionesIntegracionPostgresTest` 9/9. Local: 790 pruebas, 21 fallos — todos la falta
> de Docker/Testcontainers, comprobado idéntico con la rama sin cambios
> (`git stash`). **Pendiente:** captura de la
> actividad en el despliegue (credenciales por canal seguro; si no, junto con
> FEAT-12-T5 en S3) → todavía no **Verificada**. Evidencia: `docs/evidencia/h4/FEAT-12.md`
> del backend (sección T2).

> **24-oct-2026 · FEAT-12-T3 (En curso):** rama homónima `feat/EP-02-seguridad-e2e` en el
> frontend (base `develop` `e9cd51d`), PR en borrador. Suite Playwright con los seis casos
> del tablero: login cliente; login admin con OTP leído por API del receptor de correo de
> pruebas; refresh silencioso (reloj de Playwright +900 s; se observa `POST /auth/refresh`
> y que el reenvío sale con token nuevo, sin salir de la pantalla); logout; ruta
> `/admin/usuarios` con CLIENTE bloqueada **sin** llamar a `GET /usuarios`; y ruta privada
> sin sesión → `/login?volver=…` con regreso tras iniciar sesión. Sin credenciales en el
> repo: cuentas fixture `@fixtures.g7.test` y la contraseña solo por `E2E_CUENTAS_CLAVE`;
> ningún caso muta el estado de las cuentas. `typecheck`, `lint`, `build` y las 98 pruebas
> Vitest en verde; Chromium instalado y smoke de reloj/SPA en verde. **Pendiente:** la
> ejecución completa contra el stack de pruebas, porque esta máquina no tiene Docker ni
> PostgreSQL; quedan listos `e2e/stack.yml` (PostgreSQL 16 + Mailpit) y `e2e/README.md`
> con la receta, que también servirá a FEAT-12-T4. Evidencia:
> `docs/evidencia/h4/FEAT-12.md` del frontend.

> **24-oct-2026 · FEAT-12-T3 (aceptación local en verde):** la ejecución contra el stack
> de pruebas ya no está pendiente: se hizo en local **sin Docker** con binarios
> desechables (PostgreSQL 16.15 portable con esquema + `V101`–`V106`/`V108` y catálogos
> `R__`, Mailpit v1.32, backend `bootRun` con secretos generados en el momento y solo
> por variables de entorno; ninguno en el repo). **6/6 casos en verde en dos corridas
> consecutivas** (informe HTML en `playwright-report/`, gitignored); `typecheck`,
> `lint`, `build` y 98 pruebas Vitest en verde. Correcciones aplicadas al ejecutar:
> selectores del login por rol de accesibilidad (ambigüedad con «Mostrar contraseña»
> y el `*` de Mantine), navegación en la SPA (la sesión vive solo en memoria:
> `page.goto` la perdía) y aserción del modal por heading. Acreditado de paso: el
> reenvío del cambio de segundo factor renueva el token antes de enviar
> (`POST /auth/refresh` observado). Sigue sin **Verificada** hasta la comprobación en
> el despliegue (S3, junto con T1/T2). Evidencia: `docs/evidencia/h4/FEAT-12.md`
> del frontend.

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
