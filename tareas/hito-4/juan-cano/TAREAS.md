# H4 — tareas de Juan José Cano Vasquez

**Épica:** EP-03 Credenciales. **Plan:** [H4 v1.1 §3](../../../plan/hito-4/README.md#juan-josé--ep-03-credenciales).
**Rama:** `feat/EP-03-recuperacion` (backend y frontend). Cada tarea es un commit `FEAT-03-Tn: …`.

**Fuentes comunes:** [SPEC-07](../../../specs/funcionales/SPEC-07-politica-cambio-contrasena.md),
[SPEC-08](../../../specs/funcionales/SPEC-08-recuperacion-contrasena.md),
[FRONT-05](../../../specs/front/FRONT-05-recuperar-contrasena.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml) (`/password/recuperar`, `/password/restablecer`),
[errores](../../../specs/catalogo-errores.md), [backend hexagonal](../../../plan/hito-3/BACKEND-HEXAGONAL.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-03-T1 | Propósitos de token de un uso habilitados | P1 · **mié 22-oct** | — | Pendiente |
| FEAT-03-T2 | `POST /password/recuperar` | P1 | T1 | Pendiente |
| FEAT-03-T3 | `POST /password/restablecer` | P1 | T2 | Pendiente |
| FEAT-03-T4 | Pantallas FRONT-05 | P1 | T2, T3 | Pendiente |
| FEAT-03-T5 | Pruebas y evidencia desplegada | P1 | T1–T4 | Pendiente |

> **T1 desbloquea a Eva (FEAT-01-T2) y Valery (FEAT-04-T3).** Única excepción del plan:
> T1 va en su propia rama corta `feat/EP-03-tokens-un-uso`, fusionada el miércoles, porque
> la usan tres FEAT. T2–T5 van en `feat/EP-03-recuperacion`, creada desde `develop` ya con T1.

### FEAT-03-T1 — Habilitar propósitos en `TokensUnUsoPort`
- **Resultado:** el enum `PropositoTokenUnUso` ya trae `VERIFICAR_CORREO`, `CAMBIO_CORREO` y `RECUPERACION`;
  hoy solo se usa `DESBLOQUEO_AUTOMATICO`. Habilitar emisión, consumo atómico e invalidación para los tres, con su caducidad por propósito.
- **Aceptación:** un token de un propósito no sirve para otro; consumo único bajo concurrencia; hash, nunca el valor en tabla.
- **Evidencia:** pruebas PostgreSQL por propósito y aviso al grupo con la firma del puerto.

### FEAT-03-T2 — `POST /password/recuperar`
- **Resultado:** respuesta idéntica exista o no la cuenta; si existe y está activa, token `RECUPERACION` y correo con enlace por el outbox; límite con `solicitud_limitada`.
- **Aceptación:** mismos código, cuerpo y tiempo aproximado para cuenta inexistente; 429 al exceder.
- **Evidencia:** pruebas del caso neutro y del límite.

### FEAT-03-T3 — `POST /password/restablecer`
- **Resultado:** consume el token, aplica la política SPEC-07 e historial, revoca todas las familias de refresh, audita y envía aviso de cambio.
- **Aceptación:** válido, usado, vencido, inválido y contraseña rechazada; el refresh anterior deja de funcionar.
- **Evidencia:** pruebas PostgreSQL; nada sensible en logs.

### FEAT-03-T4 — Pantallas FRONT-05
- **Resultado (frontend):** «¿Olvidaste tu contraseña?» en el login, solicitar y restablecer con medidor; el token se lee del enlace y se retira de la barra de direcciones.
- **Aceptación:** criterios de FRONT-05; mensaje neutro al solicitar.
- **Evidencia:** pruebas Vitest y capturas del despliegue.

### FEAT-03-T5 — Recorrido de punta a punta
- **Resultado:** solicitar → correo → restablecer → login con la nueva, en el despliegue; repetirlo con G1 junto con David (F-004/F-005).
- **Evidencia:** `docs/evidencia/h4/FEAT-03.md` con SHAs y capturas sanitizadas.
