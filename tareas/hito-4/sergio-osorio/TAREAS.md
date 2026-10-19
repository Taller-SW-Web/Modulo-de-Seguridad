# H4 — tareas de Sergio Alejandro Osorio Montenegro

**Épicas:** EP-05 Roles y permisos + identidad técnica (TEC-IDENTIDAD). Product Owner.
**Plan:** [H4 v1.1 §3](../../../plan/hito-4/README.md#sergio--ep-05--identidad-técnica-po).
**Ramas:** `feat/TEC-IDENTIDAD-tokens-servicio` y `feat/TEC-IDENTIDAD-consulta-identidad`.
Cada tarea es un commit `FEAT-0x-Tn: …` en su rama.

**Fuentes comunes:** [SPEC-17](../../../specs/funcionales/SPEC-17-tokens-servicio.md),
[SPEC-18](../../../specs/funcionales/SPEC-18-consulta-identidad.md),
[plan SPEC-17/18](../../../specs/funcionales/SPEC-17-18-api-identidad.plan.md),
[SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md),
[matriz de scopes](../../../docs/integracion/matriz-scopes.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| PO-T1 | Respuesta a G1 y consulta «crear roles» al profesor | P1 · lun 20-oct | — | Pendiente |
| PO-T2 | Publicar cambios del OpenAPI por FEAT | P1 · continuo | Pedido del dueño | Pendiente |
| FEAT-05-T1 | Registro de clientes y scopes | P1 | Christian V108+ si hay migración | Pendiente |
| FEAT-05-T2 | `POST /auth/token` (`client_credentials`) | P1 · dom 26-oct | T1 | Pendiente |
| FEAT-05-T3 | `POST /auth/introspeccion` | P1 | T2 | Pendiente |
| FEAT-05-T4 | Rotación de clave con dos `kid` | P1 | — | Pendiente |
| FEAT-05-T5 | `GET /roles` y `GET /permisos` | P1 | — | Pendiente |
| FEAT-06-T1 | `GET /usuarios/{id}` con token de servicio | P1 | FEAT-05-T2 | Pendiente |
| FEAT-06-T2 | `POST /usuarios/lote` | P1 | T1 | Pendiente |
| FEAT-06-T3 | `direcciones:leer` | P1 | Valery FEAT-04-T2 | Pendiente |
| FEAT-06-T4 | Evidencia desplegada con G1 y G5 | P1 | T1–T3 | Pendiente |

## Como PO

### PO-T1 — Respuesta a G1 y alcance
- Confirmar a G1 el reparto (David es su contacto técnico) y preguntar al profesor si «crear roles» significa
  roles nuevos (cambia SPEC-11 RF-11.1) o asignar los seis existentes (ya hecho). Registrar la respuesta en el plan §8.
- Pedir a G6 los scopes de `modulo-marketplace` en `api-productos`.

### PO-T2 — Contrato
- Publicar en `specs/contrato-api/` lo que pidan los dueños antes de fusionar su FEAT; cambios incompatibles → v2.

## FEAT-05 — Tokens de servicio (G1 INT-001)

### FEAT-05-T1 — Clientes y scopes
- **Resultado:** carga de `cliente_servicio` y `scope` desde la matriz aprobada; secreto con hash, entregado por canal seguro.
- **Aceptación:** ningún `client_secret` en git, logs ni respuestas; scope no aprobado → no asignable.

### FEAT-05-T2 — `POST /auth/token`
- **Resultado:** `client_credentials` → JWT RS256 con `tipo=servicio`, `aud` de la API dueña y `scope` (RFC 9068).
- **Aceptación:** secreto erróneo → `invalid_client`; scope pedido fuera de lo asignado → `invalid_scope`; cliente inactivo → rechazado.

### FEAT-05-T3 — Introspección
- **Resultado:** `active` + estado actual del usuario para tokens de usuario; solo clientes autorizados.

### FEAT-05-T4 — Rotación de clave
- **Resultado:** JWKS publica la clave actual y la anterior; los tokens firmados con la anterior siguen válidos hasta vencer. Sin claves efímeras ni privadas en git.

### FEAT-05-T5 — Catálogo
- **Resultado:** `GET /roles` y `GET /permisos` con los seis roles fijos y sus permisos.

## FEAT-06 — Consulta de identidad (SPEC-18)

### FEAT-06-T1/T2 — `GET /usuarios/{id}` y `POST /usuarios/lote` con `usuarios:leer`
- **Aceptación:** token de usuario o sin scope → 403 neutro; ids inexistentes no se distinguen de los no permitidos; límite del lote.

### FEAT-06-T3 — `direcciones:leer`
- **Resultado:** `GET /usuarios/{id}/direcciones` para servicios, consumiendo el puerto de Valery por un adaptador puente.

### FEAT-06-T4 — Evidencia
- **Evidencia:** `docs/evidencia/h4/FEAT-05-06.md`: token de servicio de G1 y lectura con scope, sin tokens completos.
