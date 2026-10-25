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
| FEAT-03-T1 | Propósitos de token de un uso habilitados | P1 · **jue 22-oct** | — | Completada; integrada en `develop`, contrato confirmado con Eva y Valery |
| FEAT-03-T2 | `POST /password/recuperar` | P1 | T1 | Completada; corrección del outbox integrada y validada |
| FEAT-03-T3 | `POST /password/restablecer` | P1 | T2 | Completada; integrada y validada |
| FEAT-03-T4 | Pantallas FRONT-05 | P1 | T2, T3 | Completada; integrada, validada y usada |
| FEAT-03-T5 | Pruebas y evidencia desplegada | P1 | T1–T4 | Completada; recorrido desplegado y prueba conjunta con G1 evidenciados |

> **T1 desbloquea a Eva (FEAT-01-T2) y Valery (FEAT-04-T3).** Entrega temprana compartida (plan §6):
> T1 va en su propia rama corta `feat/EP-03-tokens-un-uso`, fusionada el jueves 22, porque
> la usan tres FEAT. T2–T5 van en `feat/EP-03-recuperacion`, creada desde `develop` ya con T1.

### FEAT-03-T1 — Reutilizar y completar propósitos en `TokensUnUsoPort`
- **Resultado:** reutilizar la emisión, consumo e invalidación existentes para `VERIFICAR_CORREO` y `RECUPERACION`; revisar caducidad y pruebas de cada propósito. Completar el soporte de `CAMBIO_CORREO` vinculando el token con el usuario y la dirección nueva para que los flujos de Eva/Valery puedan consumirlo. Esta tarea no implementa la solicitud, el envío de confirmación ni la actualización del correo. No duplicar repositorios ni mecanismos existentes.
- **Dependencias:** Eva puede avanzar con las capacidades de verificación existentes; el corte T1 completa lo faltante y publica el contrato interno estable, no obliga a rehacer los tres propósitos.
- **Aceptación:** un token de un propósito no sirve para otro; consumo único bajo concurrencia; hash, nunca el valor en tabla.
- **Evidencia:** pruebas PostgreSQL por propósito y aviso al grupo con la firma del puerto.
- **Cierre (25-oct-2026):** integrada en `develop` y utilizada por los flujos consumidores. Eva y Valery confirmaron la firma del puerto. Define TTL de 24 h para `VERIFICAR_CORREO` y `CAMBIO_CORREO`, máximo 30 min para `RECUPERACION`, y conserva el destino pendiente de `CAMBIO_CORREO` en el snapshot redactado. Evidencia: `Modulo-de-Seguridad-Backend/docs/evidencia/FEAT-03-T1.md`; pruebas PostgreSQL 16.6 con Testcontainers, 22/22.

### FEAT-03-T2 — `POST /password/recuperar`
- **Resultado:** respuesta idéntica exista o no la cuenta; si la cuenta es elegible conforme a SPEC-08, incluso con bloqueo automático, token `RECUPERACION` y correo con enlace por el outbox; límite con `solicitud_limitada`.
- **Aceptación:** mismos código, cuerpo y tiempo aproximado para cuenta inexistente; límite de 3 solicitudes por hora y correo, exista o no; cuarta → 429. Incluir una cuenta con bloqueo automático.
- **Evidencia:** pruebas del caso neutro y del límite.
- **Cierre (25-oct-2026):** integrada en `develop`. La reserva de cuota se confirma separada del envío; un fallo al guardar el correo mantiene la respuesta neutra y revierte el token nuevo sin invalidar el anterior. La prueba PostgreSQL compara cuenta existente e inexistente y confirma 202 para las tres primeras solicitudes y 429 en la cuarta. La suite backend completa pasó 829/829 con JDK 21 y PostgreSQL en Docker. Evidencia complementaria: `Modulo-de-Seguridad-Backend/docs/evidencia/FEAT-03-T2.md`.

### FEAT-03-T3 — `POST /password/restablecer`
- **Resultado:** consume el token, aplica la política SPEC-07 e historial, revoca todas las familias de refresh, levanta solo el bloqueo automático y reinicia su contador si existe, audita y envía aviso de cambio.
- **Aceptación:** válido, usado, vencido, inválido y contraseña rechazada; el refresh anterior deja de funcionar; la cuenta con bloqueo automático recupera el acceso. Un bloqueo manual permanece, según SPEC-15. Token máximo 30 minutos e invalidación del anterior al reemitir.
- **Evidencia:** pruebas PostgreSQL; nada sensible en logs.
- **Cierre (25-oct-2026):** integrada en `develop` y validada con T2. Las pruebas PostgreSQL cubren tokens válidos, inválidos, usados y vencidos, política de contraseña, revocación de sesiones y tratamiento de bloqueos. Evidencia: `Modulo-de-Seguridad-Backend/docs/evidencia/FEAT-03-T3.md`.

### FEAT-03-T4 — Pantallas FRONT-05
- **Resultado (frontend):** «¿Olvidaste tu contraseña?» en el login, solicitar y restablecer con medidor; el token se lee del enlace y se retira de la barra de direcciones.
- **Aceptación:** criterios de FRONT-05; mensaje neutro al solicitar.
- **Evidencia:** pruebas Vitest y capturas del despliegue.
- **Cierre (25-oct-2026):** integrada en `develop`, usada en el recorrido y verificada con 98 pruebas Vitest; `npm run build` y `npm run lint` pasaron. Evidencia: `G7-Modulo-de-Seguridad-Frontend/docs/evidencia/FEAT-03-T4.md` y `G7-Modulo-de-Seguridad-Frontend/docs/evidencia/h4/FEAT-03.md`.

### FEAT-03-T5 — Recorrido de punta a punta
- **Resultado:** solicitar → correo → restablecer → login con la nueva, en el despliegue; repetirlo con G1 junto con David (F-004/F-005).
- **Evidencia:** `docs/evidencia/h4/FEAT-03.md` con SHAs y capturas sanitizadas.
- **Cierre (25-oct-2026):** completado el recorrido de recuperación en el entorno desplegado y la prueba conjunta F-004/F-005 con G1. Se verificaron solicitud, recepción del correo, restablecimiento e inicio de sesión con la nueva contraseña; el token se retiró de la URL. Los PR de backend y frontend están integrados en `develop`, y el equipo probó el flujo integrado. Evidencia y capturas sanitizadas: `G7-Modulo-de-Seguridad-Frontend/docs/evidencia/h4/FEAT-03.md`. Verificación local complementaria: backend `clean build`, 829 pruebas aprobadas; frontend, 98 pruebas, build y lint aprobados.
