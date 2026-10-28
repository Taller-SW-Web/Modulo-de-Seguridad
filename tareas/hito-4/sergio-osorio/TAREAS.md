# H4 — tareas de Sergio Alejandro Osorio Montenegro

**Épicas:** EP-05 Roles y permisos + identidad técnica (TEC-IDENTIDAD). Product Owner.
**Plan:** [H4 v1.2 §3](../../../plan/hito-4/README.md#sergio--ep-05--identidad-técnica-po).
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
| PO-T1 | Respuesta a G1 y consulta «crear roles» al profesor | P1 · mar 20-oct | — | «Crear roles» cerrado (ver plan §8). Scopes de `modulo-marketplace` con G6: **confirmado 26-oct** (`catalogo:leer`/`api-productos`, relevo de David); registrado en `matriz-scopes.md` y `concesiones-servicio.sql`, falta que Christian aprovisione. Reparto a G1: confirmado por David como contacto técnico |
| PO-T2 | Publicar cambios del OpenAPI por FEAT | P1 · continuo | Pedido del dueño | Al día — FEAT-05/06 no requirió cambio de contrato (confirmado en #59); se publica antes de fusionar cada FEAT siguiente |
| PO-T3 | `canalOrigen` en recuperación (retorno con G1) | P1 · 26-oct | David/G1 confirmaron su login de retorno | Publicado 26-oct: OpenAPI (`/password/recuperar`), SPEC-08 (RF-08.9, ESC-08.8) y FRONT-05 («Retorno por canal», UI-05.8/9) con el mismo mecanismo de FRONT-03. Pendiente: Juan José implementa en FEAT-03-T4/T5 (ya cerradas, reabren con este alcance) |
| FEAT-05-T1 | Registro de clientes y scopes | P1 | Christian V108+ si hay migración | Hecho (#59) |
| FEAT-05-T2 | `POST /auth/token` (`client_credentials`) | P1 · lun 26-oct | T1 | Hecho (#59) |
| FEAT-05-T3 | `POST /auth/introspeccion` | P1 | T2 | Hecho (#59) |
| FEAT-05-T4 | Rotación de clave con dos `kid` | P1 | — | Hecho (#59) |
| FEAT-05-T5 | `GET /roles` y `GET /permisos` | P1 | — | Hecho (#59) |
| FEAT-06-T1 | `GET /usuarios/{id}` con token de servicio | P1 | FEAT-05-T2 | Hecho (#69); desplegado, comprobado 401 sin token |
| FEAT-06-T2 | `POST /usuarios/lote` | P1 | T1 | Bloqueado — método de lote pendiente en `CuentasPort` de Eva; propuesta de firma enviada (25-oct) |
| FEAT-06-T3 | `direcciones:leer` | P1 | Valery FEAT-04-T1/T2 fusionadas por corte temprano; FEAT-05-T2 | Hecho (#71); desplegado, comprobado 401 sin token |
| FEAT-06-T4 | Evidencia desplegada con G1 y G5 | P1 | T1–T3 | En curso — verificación sin credenciales hecha (`docs/evidencia/h4/FEAT-05-06.md`); falta que Christian provisione un `cliente_servicio` de prueba y la aprobación del grant de G1 |

## Como PO

### PO-T1 — Respuesta a G1 y alcance
- «Crear roles» resuelto el 23-oct: es alta administrativa de un trabajador nuevo con un rol existente (usuario, contraseña y correo los da el admin); no cambia SPEC-11 ni el contrato. Registrado en plan §8.
- Falta: confirmar a G1 el reparto (David es su contacto técnico).
- Falta: pedir a G6 los scopes de `modulo-marketplace` en `api-productos`.

### PO-T2 — Contrato
- Publicar en `specs/contrato-api/` lo que pidan los dueños antes de fusionar su FEAT; cambios incompatibles → v2.

## FEAT-05 — Tokens de servicio (G1 INT-001)

### FEAT-05-T1 — Clientes y scopes
- **Resultado:** carga de `cliente_servicio` y `scope` desde la matriz aprobada; secreto con hash, entregado por canal seguro.
- **Aceptación:** ningún `client_secret` en git, logs ni respuestas; scope no aprobado → no asignable.

### FEAT-05-T2 — `POST /auth/token`
- **Resultado:** `client_credentials` → JWT RS256 con `tipo=servicio`, `aud` de la API dueña y `scope` (RFC 9068).
- **Aceptación:** secreto erróneo o cliente inactivo → 401 `CLIENTE_INVALIDO` (`application/problem+json`). Emitir únicamente scopes concedidos según RF-17.5/OpenAPI, aunque se soliciten más; no introducir `invalid_scope` ni cambiar la política publicada. Respuesta con `accessToken`, `tokenType`, `expiresIn` y `scopes`; JWT con `scope` separado por espacios y `aud` como lista de APIs dueñas.

### FEAT-05-T3 — Introspección
- **Resultado:** `activo` y los campos del contrato vigente; validar el token de servicio con `tokens:introspeccion`. Usuario desactivado, bloqueado o con roles cambiados → 200 con `activo: false` y motivo; token de servicio inválido → 401, scope insuficiente → 403.

### FEAT-05-T4 — Rotación de clave
- **Resultado:** JWKS publica la clave actual y la anterior simultáneamente; conservar la anterior al menos 24 horas (RF-17.3) y comprobar que los tokens vigentes siguen validándose durante la transición. Sin claves efímeras ni privadas en git.

### FEAT-05-T5 — Catálogo
- **Resultado:** `GET /roles` y `GET /permisos` con los seis roles fijos y sus permisos; token de servicio con `roles:leer`, errores 401/403 contractuales.

## FEAT-06 — Consulta de identidad (SPEC-18)

### FEAT-06-T1/T2 — `GET /usuarios/{id}` y `POST /usuarios/lote` con `usuarios:leer`
- **Aceptación:** para servicios, validar firma, emisor, tipo, audiencia `api-seguridad` y `usuarios:leer`; credencial inválida → 401 antes de consultar existencia, scope insuficiente → 403 sin revelar el faltante. Conservar en `GET /usuarios/{id}` el acceso de administración por token de usuario con `usuario.ver`; no aplicar una prohibición global de tokens humanos.
- **Datos y lote:** máximo 100 IDs; devolver encontrados y no hallados por separado según RF-18.2. Nunca hashes; documento enmascarado salvo `usuarios:leer:documento`. Mantener los campos privados del perfil fuera de respuestas de servicio.
- **Auditoría:** registrar módulo, endpoint, usuario consultado, fecha y documento en claro/enmascarado según RF-18.6, sin copiar valores sensibles.

### FEAT-06-T3 — `direcciones:leer`
- **Resultado:** `GET /usuarios/{id}/direcciones` para servicios con `direcciones:leer`, consumiendo el puerto implementado de Valery por un adaptador puente; sin documento ni fecha de nacimiento. Mantener la autorización del titular/administración prevista por SPEC-16. Grant de G1 pendiente de aprobación explícita; no heredarlo de otro módulo.

### FEAT-06-T4 — Evidencia
- **Evidencia:** `docs/evidencia/h4/FEAT-05-06.md`: token de servicio de G1 y lectura con scope, sin tokens completos.
