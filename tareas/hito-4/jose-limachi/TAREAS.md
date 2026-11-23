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
| FEAT-12-T1 | `issuer`, discovery y `jwks_uri` por entorno | P1 | — | Verificada ; Smoke test CI ejecutado contra despliegue Render: health UP, JWKS válido, login real exitoso. Evidencia: `docs/evidencia/h4/FEAT-12.md` del backend.
| FEAT-12-T2 | Auditoría `SESION_CERRADA` en el logout | P1 | — |  Verificada ; Smoke test CI ejecutado contra despliegue Render: flujo de login/logout real verificado. CI pass (FEAT-12-T4). |
| FEAT-12-T3 | Suite E2E Playwright | P1 | — | Verificada ; Suite E2E CI pass contra `https://g7-seguridad-spa.onrender.com` (6/6 tests, 16.5s). PR #17 merged. |
| FEAT-12-T4 | Job E2E en CI con entorno aislado (alcance H4) | P1 | T3; Christian FEAT-09-T1/T2 | Verificada ; PR #80 merged en `develop`. CI pass: job `e2e-playwright` + `e2e-aislado` en entorno aislado con Mailpit. Artefact HTML generado.
| FEAT-12-T5 | Prueba conjunta con G5 | P1 | T4 | Pendiente |

> **22-oct-2026 · FEAT-12-T1:** commit local en `feat/EP-02-seguridad-e2e`
> (backend, sin PR/push). Prueba nueva `ConfiguracionEntornoHttpTest` (2/2 en verde):
> las tres URLs absolutas salen de la variable del entorno, no de `localhost` ni de la
> petición; el `iss` del token emitido coincide con el discovery y su firma se verifica
> con el JWKS publicado. `configuration` + `identidad`: 155 pruebas, 0 fallos (suite
> completa: los 19 fallos son solo por falta de Docker/Testcontainers). Despliegue
> comprobado en solo lectura: discovery y JWKS responden 200 con el issuer público de
> Render.
> Evidencia: `docs/evidencia/h4/FEAT-12.md` del backend. Observación para FEAT-09: el
> discovery desplegado no incluye `token_endpoint`/`introspection_endpoint`, así que el
> SHA desplegado es anterior a FEAT-05.

> **24-oct-2026 · FEAT-12-T2:** commit en la misma rama del backend; entra en
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
> (`git stash`). Evidencia: `docs/evidencia/h4/FEAT-12.md`
> del backend (sección T2).

> **31-oct-2026 · FEAT-12-T3 (corrida para avanzar T4):** stack real levantado
> (PostgreSQL + Mailpit + backend `bootRun` @ `76e248f`, sin Docker) y **6/6 en verde**
> (16.5 s), con Node v24.21.0, npm 11.19.0, Playwright 1.64.1 y Chromium
> «Chrome for Testing» 156.0.8078.4 (`chromium-1248`); `typecheck`, `lint`, `build`
> y 98 pruebas Vitest en verde. Sin Docker, binarios locales desechables; la cuota OTP
> fue reiniciada antes de la corrida. La acreditación en CI corresponde a FEAT-12-T4
> (job E2E); el stack local permanecerá encendido para los próximos pasos del hito.
> **T1 y T2: código y pruebas locales verdes, pendiente comprobación en despliegue S3**
> (ya Christian tiene FEAT-09-T1/T2 merged en `main`).

### FEAT-12-T4 — Job E2E en el CI
- **Alcance acordado para H4:** el job ejecuta T3 en GitHub Actions con frontend, backend, PostgreSQL y Mailpit aislados y temporales. El smoke posterior al despliegue sigue comprobando Render por separado.
- **Aceptación:** los seis casos pasan y el informe HTML queda disponible como artefacto; si un caso falla, falla el job.
- **Evidencia:** [CI Backend](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions), ejecución manual **#329** de la rama `feat/EP-02-seguridad-e2e`, commit `2ee895f`: 6/6 en verde en entorno aislado; workflow integrado mediante [backend #80](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/80).
- **Diferido a H5:** ejecutar la suite completa contra el entorno desplegado en Render, después del smoke, con cuentas de prueba y un receptor OTP seguro accesible desde CI. La ejecución aislada no demuestra esos seis recorridos en Render.

### FEAT-12-T5 — Prueba conjunta con G5
- **Resultado:** token de usuario G7 aceptado por G5 en `/pedidos/me`; sin token, vencido, firma alterada, emisor incorrecto o `tipo` distinto de `acceso` → 401; usuario sin rol CLIENTE → 403. A solo ve sus pedidos y B no accede a los de A al manipular identificadores. No exigir `aud` adicional a tokens humanos (OpenAPI/kit vigente); esa validación corresponde al perfil de servicio.
- **Evidencia:** `docs/evidencia/h4/FEAT-12.md` sin tokens completos.
