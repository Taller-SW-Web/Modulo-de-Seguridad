# Hito 4 — repartición por FEAT

**Corte:** 19-oct-2026. **Presentación:** sábado 7-nov-2026. **Responsable del plan:** Sergio (PO).
**Versión 1.1 (19-oct):** incorpora los ajustes de David: prueba temprana con G1,
criterio de «terminado» para el 75 %, evidencia del recorrido CI/CD completo y
«crear roles» como decisión pendiente. David asume la integración con G1.
Tareas detalladas en los [tableros H4](../../tareas/hito-4/README.md).

Se basa en los [planes H3](../hito-3/README.md) («Después de H3» de cada dueño), los
[tableros H3](../../tareas/hito-3/README.md), la rúbrica del Hito 4 y la
[coordinación G1–G7 para H4](#5-integración-con-g1-marketplace) del 19-oct.

**Meta del equipo:** cerrar H4 con **al menos el 75 %** del módulo implementado, probado
y desplegado. Con las FEAT **P1** llegamos al **77 %**; las **P2** suben a ~96 %.

Las épicas no cambian de dueño: cada quien sigue con la continuación de su épica de H3,
de punta a punta (pantalla, endpoint, datos, pruebas, demo).

---

## 1. Dónde estamos y cómo llegamos al 75 %

Medido por requisitos funcionales (RF) de las 18 specs: 161 RF en total.
Es una **estimación para planificar**, no una certificación.

**Cuándo cuenta un RF como hecho:** solo si funciona **con pantalla (cuando la spec la
tiene), API, despliegue y evidencia** sanitizada en `docs/evidencia/`. Código fusionado
sin desplegar o sin pantalla no suma. Con ese criterio, los 80 RF de H3 se vuelven a
comprobar en el despliegue durante S1 (FEAT-09-T6, FEAT-12-T3 y FEAT-11-T2).

| Spec | RF | Hecho en H3 | Tras P1 | Tras P1+P2 | FEAT H4 |
|---|---:|---:|---:|---:|---|
| 01 Registro de clientes | 7 | 0 | 7 | 7 | FEAT-01 Eva |
| 02 Verificación de correo | 8 | 0 | 8 | 8 | FEAT-01 Eva |
| 03 Alta y consulta de cuentas | 9 | 4 | 4 | 4 | (alta v2 → H5) |
| 04 Baja y reactivación | 5 | 0 | 0 | 5 | FEAT-02 Eva (P2) |
| 05 Inicio de sesión | 7 | 7 | 7 | 7 | — |
| 06 Renovación y cierre | 6 | 6 | 6 | 6 | — |
| 07 Política y cambio de contraseña | 11 | 10 | 11 | 11 | FEAT-03 Juan José |
| 08 Recuperación de contraseña | 8 | 0 | 8 | 8 | FEAT-03 Juan José |
| 09 MFA en el login | 17 | 14 | 14 | 17 | FEAT-07 Luis (P2) |
| 10 Activación de MFA (v2) | 10 | 0 | 0 | 10 | FEAT-07 Luis (P2) |
| 11 Roles y permisos | 10 | 10 | 10 | 10 | — |
| 12 Registro de auditoría | 7 | 7 | 7 | 7 | — |
| 13 Consulta de auditoría | 5 | 2 | 2 | 5 | FEAT-10 Christian (P2) |
| 14 Bloqueo automático | 12 | 12 | 12 | 12 | — |
| 15 Bloqueo manual | 8 | 0 | 0 | 8 | FEAT-08 Luis (P2) |
| 16 Atributos y perfil | 10 | 4 | 7 | 9 | FEAT-04 Valery |
| 17 Tokens de servicio | 15 | 4 | 15 | 15 | FEAT-05 Sergio |
| 18 Consulta de identidad | 6 | 0 | 6 | 6 | FEAT-06 Sergio |
| **Total** | **161** | **80 (50 %)** | **124 (77 %)** | **155 (96 %)** | |

**Lectura:** las P1 (FEAT-01, 03, 04, 05 y 06) bastan para pasar el 75 %. Son, además,
justo lo que G1 necesita. Las P2 se trabajan en paralelo; si una se atrasa, no
compromete la meta.

## 2. Rúbrica del Hito 4 → quién la cubre

Los cuatro criterios de la rúbrica; el peso de cada uno lo confirma el profesor.

| Criterio | Cómo lo cubrimos | Dueños |
|---|---|---|
| CI/CD (build, compile, test, deploy) | Hoy el CI de ambos repos hace build, pruebas e imagen Docker, pero **no despliega**: Render despliega por su cuenta. FEAT-09 añade el job de despliegue y el smoke test al pipeline, y deja un run completo como evidencia | Christian (FEAT-09) |
| Diseño de APIs | OpenAPI v1/v2 actualizado por FEAT, ejemplos y pruebas de contrato; el PO publica | Sergio + cada dueño propone |
| Demo funcional: nuevas funcionalidades | Registro, verificación, recuperación, direcciones, tokens de servicio, bloqueo manual, MFA v2, auditoría admin | Todos |
| Seguridad: autenticación en pantallas y APIs | Guards por permiso en SPA, tokens de usuario y de servicio validados por scope/aud, E2E en CI, integración G1 y G5 | Jose Luis (FEAT-12) + David (FEAT-11) + Sergio |

## 3. Reparto por integrante

Prioridad: **P1** = necesaria para el 75 %, la rúbrica o G1. **P2** = suma funcionalidades
a la demo, pero no compromete la meta. Las tareas `Tn` se hacen como commits dentro de
la rama de su FEAT (ver §6).

### Eva — EP-01 Cuentas

**FEAT-01 `feat/EP-01-registro-verificacion` (P1, G1 F-001)** — SPEC-01, SPEC-02, FRONT-02, FRONT-03
- T1 `POST /auth/registro` con `canalOrigen`, política de contraseña y 409 neutro; cuenta `PENDIENTE_VERIFICACION` sin sesión.
- T2 Token de verificación (consume `TokensUnUsoPort` de Juan José) y correo por el outbox existente.
- T3 `POST /auth/verificar-correo` y `/reenviar`: válido, usado, vencido; límite con `solicitud_limitada`.
- T4 Retorno por canal (MARKETPLACE → login G1) con lista de URL permitidas por entorno; nunca el token en la URL de retorno.
- T5 Pantallas FRONT-02 y FRONT-03; eventos `usuario.creado` y auditoría.
- T6 Pruebas: unitarias, PostgreSQL y recorrido de punta a punta con el receptor de correo de prueba.

**FEAT-02 `feat/EP-01-baja-reactivacion` (P2)** — SPEC-04: `DELETE /usuarios/{id}`, `/reactivar`, acciones en FRONT-08 y eventos.

### Jose Luis — EP-02 Sesiones + seguridad de punta a punta

**FEAT-12 `feat/EP-02-seguridad-e2e` (P1, rúbrica de seguridad)**
- T1 Configuración por entorno: `issuer`, discovery y `jwks_uri` absolutos según la URL pública.
- T2 Auditoría `SESION_CERRADA` en el logout (pendiente de H3).
- T3 Pruebas E2E automatizadas del frontend (Playwright): login, OTP, refresh, logout y guards por permiso.
- T4 Job E2E en el CI contra el despliegue, después del smoke test de Christian.
- T5 Prueba conjunta con G5 (`/pedidos/me`) con evidencia sanitizada.

### Juan José — EP-03 Credenciales

**FEAT-03 `feat/EP-03-recuperacion` (P1, G1 F-004/F-005)** — SPEC-08, FRONT-05
- T1 *(semana 1, desbloquea a Eva y Valery; va en rama corta propia `feat/EP-03-tokens-un-uso`)* Habilitar en `TokensUnUsoPort` los propósitos `VERIFICAR_CORREO`, `CAMBIO_CORREO` y `RECUPERACION` (el enum ya existe; hoy solo se usa el desbloqueo).
- T2 `POST /password/recuperar`: respuesta neutra exista o no la cuenta, límite de frecuencia y correo con enlace.
- T3 `POST /password/restablecer`: token de un solo uso, caducidad y política; revoca todas las sesiones y audita.
- T4 Pantallas FRONT-05 (solicitar y restablecer) y retorno seguro.
- T5 Pruebas: válido, usado, vencido, inválido, contraseña rechazada; nada sensible en logs.

### Luis David — integración G1, EP-04 Segundo factor y EP-07 Bloqueo

**FEAT-11 `feat/INT-G1-marketplace` (P1, G1 y rúbrica de seguridad)** — David la pidió el 19-oct. Va primero.
- T1 Alta del origen y las URL de retorno de G1 por entorno (`G7_CORS_ORIGENES` y lista de retorno).
- T2 **Prueba temprana con G1 (S1, hasta el sáb 25-oct):** login + una operación protegida (`GET /auth/me`) desde G1 contra el despliegue.
- T3 Paquete para G1: matriz por operación (versión, estado, SHA, URL) y cuentas A, B, pendiente, bloqueada y MFA por canal seguro.
- T4 Seguimiento por operación a medida que se fusionan FEAT-01, 03, 04, 05 y 06; avisa a G1 de cada una.
- T5 Prueba conjunta final con dos usuarios (F-001 a F-005 y cliente técnico) con evidencia sanitizada.

**FEAT-07 `feat/EP-04-mfa-v2` (P2)** — SPEC-10, FRONT-09: `POST /auth/mfa/cambios`, `/reenviar` y `/confirmar` (v2); adaptador SMS simulado de SPEC-09.

**FEAT-08 `feat/EP-07-bloqueo-manual` (P2)** — SPEC-15: `POST /usuarios/{id}/bloquear` y `/desbloquear` del admin, con motivo; acciones en FRONT-08 y eventos. Se apoya en el bloqueo ya visible en el detalle (backend #55).

### Sergio — EP-05 + identidad técnica (PO)

**FEAT-05 `feat/TEC-IDENTIDAD-tokens-servicio` (P1, G1 INT-001)** — SPEC-17
- T1 Registro de clientes y scopes desde la [matriz](../../docs/integracion/matriz-scopes.md) (`cliente_servicio`, `scope`); secretos con hash, entregados por canal seguro.
- T2 `POST /auth/token` con `client_credentials`: `tipo=servicio`, `aud` por API dueña y `scope` RFC 9068.
- T3 `POST /auth/introspeccion` con el estado actual del usuario.
- T4 Rotación de clave con dos `kid` publicados.
- T5 `GET /roles` y `GET /permisos`.

**FEAT-06 `feat/TEC-IDENTIDAD-consulta-identidad` (P1)** — SPEC-18: `GET /usuarios/{id}` y `/usuarios/lote` con token de servicio (`usuarios:leer`), `direcciones:leer` consumiendo el puerto de Valery; 401/403 neutros.

**Como PO:** publica los cambios del OpenAPI que pidan los dueños, responde a G1 y prioriza.

### Christian — EP-06 Auditoría + infraestructura

**FEAT-09 `feat/TEC-INFRA-cicd` (P1, rúbrica CI/CD)**
- T1 Job de despliegue en GitHub Actions: tras el CI verde en la rama desplegada, dispara el deploy hook de Render (secreto en GitHub) y espera la versión nueva.
- T2 Smoke test en el pipeline (`/actuator/health`, JWKS, login de prueba); si falla, el run queda en rojo.
- T3 Frontend en Vercel con `VITE_API_BASE_URL` por entorno, desplegado desde el pipeline.
- T4 Ventana de migraciones V108+ de las FEAT en Supabase, con su runbook.
- T5 Eventos reales en CloudAMQP (`G7_EVENTOS_PUBLICAR=true`) y SMTP real (Brevo por el 2525).
- T6 Evidencia del recorrido completo build → compile → test → deploy → smoke (enlace al run) y checklist de secretos.

**FEAT-10 `feat/EP-06-auditoria-admin` (P2)** — SPEC-13, FRONT-14: `GET /auditoria` con filtros, `/auditoria/exportar` y pestañas del historial en FRONT-08.

### Valery — EP-08 Perfil

**FEAT-04 `feat/EP-08-direcciones-correo` (P1)** — SPEC-16, FRONT-06
- T1 `GET` y `POST /usuarios/{id}/direcciones` del titular (tabla `direccion` ya existe).
- T2 Puerto exportado de lectura de direcciones para FEAT-06 de Sergio (G1 F-022).
- T3 `POST /usuarios/{id}/correo`: cambio con contraseña actual; usa la verificación de Eva (semana 2).
- T4 Mi cuenta (FRONT-06) con direcciones y cambio de correo; eventos `usuario.atributos_actualizados`.
- T5 Pruebas y evidencia desplegada.
- T6 *(P2)* Edición administrativa de atributos con `atributos.editar`.

## 4. Calendario (3 sprints)

| Sprint | Fechas | Objetivo |
|---|---|---|
| S1 | lun 20 – dom 26 oct | Juan José entrega T1 (tokens de un uso) el miércoles. **David: prueba temprana con G1 (login + `/auth/me`) hasta el sábado 25.** Cada FEAT con su backend principal y su PR en borrador. Christian: job de despliegue y smoke test. Sergio: respuesta a G1, contrato y consulta al profesor sobre «crear roles» |
| S2 | lun 27 oct – dom 2 nov | Pantallas y pruebas completas. Dependencias cruzadas: verificación → cambio de correo; direcciones → `direcciones:leer` |
| S3 | lun 3 – sáb 7 nov | **Congelamiento el jueves 5-nov.** Fusión `develop → main`, despliegue, prueba conjunta G1/G5 y ensayo de la demo |

**Reunión corta cada lunes y jueves:** qué terminó, qué bloquea y qué toma cada uno.

## 5. Integración con G1 (Marketplace)

G1 pidió confirmar operaciones, configuración por entorno y una prueba conjunta con dos usuarios.
**El contacto técnico con G1 es David (FEAT-11).** Sergio responde como PO cuando cambia
el alcance o el contrato.

| Pedido de G1 | Respuesta en H4 |
|---|---|
| F-001 Registro + verificación de correo | FEAT-01 Eva |
| F-002 Login, MFA, perfil y continuidad | Ya disponible (v1); prueba temprana en S1 (FEAT-11-T2 David) |
| F-003 Logout, refresh y revocación | Ya disponible; prueba conjunta en FEAT-11 David |
| F-004/F-005 Recuperación y restablecimiento | FEAT-03 Juan José |
| Cliente técnico `modulo-marketplace` para `api-productos` | FEAT-05 Sergio; los scopes los confirma G6 por cliente (la aprobación de Chatbot no aplica a G1) |
| Direcciones del titular / `direcciones:leer` | FEAT-04 Valery + FEAT-06 Sergio |
| Origen, URL de retorno, matriz por operación, SHAs y cuentas A/B | FEAT-11 David |

Reglas que ya están acordadas y no se cambian: no implementar las rutas v1 históricas
de habilitar o deshabilitar OTP (se usa v2); no prometer revocación instantánea del
JWT de acceso; nada de tokens, OTP ni enlaces en URL de retorno ni en logs.

## 6. Cómo trabajamos con ramas por FEAT

En H3 abrimos una rama y un PR por tarea. Para H4, **una FEAT = una rama = un PR por repo**,
y las tareas son commits dentro de esa rama.

### Nombres

- Rama: `feat/<ÉPICA>-<funcionalidad>`, por ejemplo `feat/EP-01-registro-verificacion`.
- Si la FEAT toca backend y frontend, la rama lleva **el mismo nombre en los dos repos**
  y cada PR enlaza al otro.
- Commits: `<ID-tarea>: <qué hace>`, por ejemplo `FEAT-01-T3: verificar-correo con token de un uso`.

### Flujo

```text
develop ──► feat/EP-01-registro-verificacion
              ├─ commit FEAT-01-T1
              ├─ commit FEAT-01-T2      ← PR en borrador desde el primer push
              ├─ commit FEAT-01-T3        (el CI corre en cada push)
              └─ … T6
           ──► PR listo → CI verde → merge a develop → borrar la rama
develop ──► (jue 5-nov) PR release H4 → main → despliegue automático
```

1. Crear la rama desde `develop` actualizado: `git switch develop && git pull && git switch -c feat/...`.
2. Abrir el PR **en borrador** desde el primer push, con la lista de tareas de la FEAT como checklist.
3. Hacer un commit por tarea. Si la FEAT dura más de unos días, traer `develop` a la rama
   (`git merge origin/develop`) para no acumular conflictos.
4. Pasar el PR a «Ready for review» cuando la FEAT esté **completa de punta a punta**:
   backend, pantalla, pruebas y evidencia.
5. Fusionar con el CI en verde. No se exige aprobación, pero conviene pedir que alguien la mire.
   Después, borrar la rama.
6. `main` solo recibe el PR de versión desde `develop` (jueves 5-nov). Render despliega `main`.

### Reglas que no cambian

- Nada directo a `develop` ni a `main`; todo por PR con el CI en verde.
- **El contrato OpenAPI lo edita solo el PO (Sergio)**. El dueño de la FEAT pide el cambio
  en su PR o en un issue y Sergio lo publica antes de que la FEAT se fusione.
- Migraciones nuevas (V108+) en `db/migration` con su gemelo en `docs/migraciones-propuestas` (repo backend);
  las aplica Christian en Supabase en su ventana, con aviso al grupo.
- Nunca en git: contraseñas, `client_secret`, claves, JWT completos ni datos de Supabase.
- La rama de una FEAT no se reutiliza para otra: al terminar se borra.

## 7. Definición de terminado de una FEAT

- [ ] Endpoints del contrato implementados, con errores `problem+json` y sin enumeración de cuentas.
- [ ] Pantallas FRONT de la spec conectadas al backend real.
- [ ] Pruebas unitarias, PostgreSQL y de arquitectura en verde en el CI.
- [ ] Recorrido de punta a punta probado en el despliegue, con evidencia sanitizada en `docs/evidencia/`.
- [ ] Tablero actualizado (estado y PR) y el contrato OpenAPI publicado si cambió.

## 8. Decisiones pendientes (no bloquean)

| Tema | Situación | Quién | Cuándo |
|---|---|---|---|
| «Crear roles» | Las specs definen un [catálogo cerrado de seis roles](../../specs/funcionales/SPEC-11-roles-permisos.md) (RF-11.1). Asignar y retirar roles ya funciona. Crear roles nuevos cambiaría el alcance de SPEC-11 y el contrato | Sergio consulta al profesor | S1; si se pide, entra como FEAT nueva en S2, sin tocar las demás |
| Scopes de `modulo-marketplace` en `api-productos` | G6 debe confirmarlos por cliente | Sergio con G6 | S1 |
| Fusión del backend #55 (bloqueo en el detalle) | Abierto; FEAT-08 se apoya en él. Fusionar despliega | Equipo | Inicio de S1 |
