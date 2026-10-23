# H4 — tareas de Juan José Cano Vasquez

**Épica:** EP-03 Credenciales. **Plan:** [H4 v1.2 §3](../../../plan/hito-4/README.md#juan-josé--ep-03-credenciales).
**Rama:** `feat/EP-03-recuperacion` (backend y frontend). Cada tarea es un commit `FEAT-03-Tn: …`.

**Fuentes comunes:** [SPEC-07](../../../specs/funcionales/SPEC-07-politica-cambio-contrasena.md),
[SPEC-08](../../../specs/funcionales/SPEC-08-recuperacion-contrasena.md),
[FRONT-05](../../../specs/front/FRONT-05-recuperar-contrasena.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml) (`/password/recuperar`, `/password/restablecer`),
[errores](../../../specs/catalogo-errores.md), [backend hexagonal](../../../plan/hito-3/BACKEND-HEXAGONAL.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-03-T1 | Propósitos de token de un uso habilitados | P1 · **jue 22-oct** | — | Integrada en `develop` por PR #58; confirmar la firma con Eva y Valery |
| FEAT-03-T2 | `POST /password/recuperar` | P1 | T1 | Implementada y verificada localmente; pendiente de revisión/merge |
| FEAT-03-T3 | `POST /password/restablecer` | P1 | T2 | Implementada y verificada localmente; pendiente de revisión/merge |
| FEAT-03-T4 | Pantallas FRONT-05 | P1 | T2, T3 | Implementada y verificada localmente; evidencia desplegada pendiente |
| FEAT-03-T5 | Pruebas y evidencia desplegada | P1 | T1–T4 | Recorrido local completo aprobado; despliegue y prueba conjunta con G1 pendientes |

> **T1 desbloquea a Eva (FEAT-01-T2) y Valery (FEAT-04-T3).** Entrega temprana compartida (plan §6):
> T1 va en su propia rama corta `feat/EP-03-tokens-un-uso`, fusionada el jueves 22, porque
> la usan tres FEAT. T2–T5 van en `feat/EP-03-recuperacion`, creada desde `develop` ya con T1.

### FEAT-03-T1 — Reutilizar y completar propósitos en `TokensUnUsoPort`
- **Resultado:** reutilizar la emisión, consumo e invalidación existentes para `VERIFICAR_CORREO` y `RECUPERACION`; revisar caducidad y pruebas de cada propósito. Completar el soporte de `CAMBIO_CORREO` vinculando el token con el usuario y la dirección nueva para que los flujos de Eva/Valery puedan consumirlo. Esta tarea no implementa la solicitud, el envío de confirmación ni la actualización del correo. No duplicar repositorios ni mecanismos existentes.
- **Dependencias:** Eva puede avanzar con las capacidades de verificación existentes; el corte T1 completa lo faltante y publica el contrato interno estable, no obliga a rehacer los tres propósitos.
- **Aceptación:** un token de un propósito no sirve para otro; consumo único bajo concurrencia; hash, nunca el valor en tabla.
- **Evidencia:** pruebas PostgreSQL por propósito y aviso al grupo con la firma del puerto.
- **Avance (actualizado 23-oct-2026):** implementado en `feat/EP-03-tokens-un-uso`; el código está integrado en el `develop` local por PR #58 y forma parte de la base de `feat/EP-03-recuperacion`. Define TTL de 24 h para `VERIFICAR_CORREO` y `CAMBIO_CORREO`, máximo 30 min para `RECUPERACION`, y persiste el destino pendiente de `CAMBIO_CORREO` en el snapshot interno con redacción en `toString`. La verificación focalizada con JDK 21 y PostgreSQL 16.6 en Docker/Testcontainers pasó 22/22. Evidencia: `Modulo-de-Seguridad-Backend/docs/evidencia/FEAT-03-T1.md`. Aún falta confirmar la firma del puerto con Eva y Valery; no se conectó a Supabase.

### FEAT-03-T2 — `POST /password/recuperar`
- **Resultado:** respuesta idéntica exista o no la cuenta; si la cuenta es elegible conforme a SPEC-08, incluso con bloqueo automático, token `RECUPERACION` y correo con enlace por el outbox; límite con `solicitud_limitada`.
- **Aceptación:** mismos código, cuerpo y tiempo aproximado para cuenta inexistente; límite de 3 solicitudes por hora y correo, exista o no; cuarta → 429. Incluir una cuenta con bloqueo automático.
- **Evidencia:** pruebas del caso neutro y del límite.

### FEAT-03-T3 — `POST /password/restablecer`
- **Resultado:** consume el token, aplica la política SPEC-07 e historial, revoca todas las familias de refresh, levanta solo el bloqueo automático y reinicia su contador si existe, audita y envía aviso de cambio.
- **Aceptación:** válido, usado, vencido, inválido y contraseña rechazada; el refresh anterior deja de funcionar; la cuenta con bloqueo automático recupera el acceso. Un bloqueo manual permanece, según SPEC-15. Token máximo 30 minutos e invalidación del anterior al reemitir.
- **Evidencia:** pruebas PostgreSQL; nada sensible en logs.

### FEAT-03-T4 — Pantallas FRONT-05
- **Resultado (frontend):** «¿Olvidaste tu contraseña?» en el login, solicitar y restablecer con medidor; el token se lee del enlace y se retira de la barra de direcciones.
- **Aceptación:** criterios de FRONT-05; mensaje neutro al solicitar.
- **Evidencia:** pruebas Vitest y capturas del despliegue.

### FEAT-03-T5 — Recorrido de punta a punta
- **Resultado:** solicitar → correo → restablecer → login con la nueva, en el despliegue; repetirlo con G1 junto con David (F-004/F-005).
- **Evidencia:** `docs/evidencia/h4/FEAT-03.md` con SHAs y capturas sanitizadas.
- **Actualización (23-oct-2026):** recorrido local de navegador aprobado con backend real de la rama, PostgreSQL 16.6 desechable y Mailpit: HTTP 202 → correo recibido → HTTP 204 → login HTTP 200; el token se retiró de la URL. Evidencia y capturas sanitizadas en `G7-Modulo-de-Seguridad-Frontend/docs/evidencia/h4/FEAT-03.md`. El 401 observado el 22-oct no se reprodujo; no se atribuye una causa retrospectiva. La SPA pública responde 200, pero salud y política del backend no respondieron dentro de 20 s y no se confirmó el SHA desplegado. Falta ejecutar el recorrido desplegado y la prueba F-004/F-005 coordinada con G1/David; FEAT-03-T5 y el cierre de la FEAT siguen pendientes.
