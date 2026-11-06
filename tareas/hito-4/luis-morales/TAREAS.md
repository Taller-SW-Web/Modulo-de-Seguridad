# H4 — tareas de Luis David Morales Brenis

**Épicas:** integración G1, EP-04 Segundo factor, EP-07 Bloqueo.
**Plan:** [H4 v1.2 §3](../../../plan/hito-4/README.md#luis-david--integración-g1-ep-04-segundo-factor-y-ep-07-bloqueo).
**Ramas:** `feat/INT-G1-marketplace` (primero), `feat/EP-04-mfa-v2` y `feat/EP-07-bloqueo-manual`.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama.

**Fuentes comunes:** [paquete y coordinación G1–G7 H4](../../../docs/integracion/hito-4-marketplace.md) (19-oct),
[plan §5](../../../plan/hito-4/README.md#5-integración-con-g1-marketplace),
[matriz de scopes](../../../docs/integracion/matriz-scopes.md),
[SPEC-10](../../../specs/funcionales/SPEC-10-activacion-mfa.md),
[SPEC-15](../../../specs/funcionales/SPEC-15-bloqueo-manual.md),
[FRONT-09](../../../specs/front/FRONT-09-segundo-factor.md),
[FRONT-08](../../../specs/front/FRONT-08-admin-detalle-usuario.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml), [OpenAPI v2](../../../specs/contrato-api/openapi-v2.yaml).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-11-T1 | Origen y URL de retorno de G1 por entorno | P1 | T3 | Cerrada para H4; retorno probado |
| FEAT-11-T2 | **Prueba temprana con G1** | P1 | T3 y despliegue | Cerrada para H4; aislamiento A/B desplegado pasa a H5 |
| FEAT-11-T3 | Paquete para G1 | P1 | — | Cerrada para H4; seguimiento en evidencia |
| FEAT-11-T4 | Seguimiento por operación | P1 | FEAT-01/03/04/05/06 | Cerrada para H4 con límites documentados |
| FEAT-11-T5 | Prueba conjunta final con G1 | P1 | T4 | Cerrada para H4 con alcance ajustado; favoritos A/B en H5 |
| FEAT-07-T1 | `POST /auth/mfa/cambios` (v2) | P2 | — | Implementada y fusionada (#57) |
| FEAT-07-T2 | Reenviar y confirmar (v2) | P2 | T1 | Implementada y fusionada (#57) |
| FEAT-07-T3 | Adaptador SMS simulado | P2 | T1 | Implementada y fusionada (#57); solo simulado |
| FEAT-07-T4 | Pantalla FRONT-09 y evidencia | P2 | T1–T3 | Fusionada (#11); recorrido EMAIL desplegado comprobado |
| FEAT-08-T1 | Bloquear y desbloquear por ADMIN | P2 | Backend #55 fusionado | Implementada y fusionada (#65); recorrido principal desplegado comprobado |
| FEAT-08-T2 | Acciones en FRONT-08 y evidencia | P2 | T1 | Fusionada (#14); recorrido desplegado y evidencia registrados |

## FEAT-11 — Integración con G1 Marketplace (P1)

David es el contacto técnico con G1. Cambios de alcance o del contrato pasan por Sergio (PO).

**Cierre H4, 6-nov:** Sergio autorizó a David a resolver el cambio de alcance.
Identidad y recuperación G1–G7 se probaron en despliegue; la evidencia y sus
límites están en [FEAT-11](../../../docs/evidencia/h4/FEAT-11.md). G1 reporta
30/30 tests de aislamiento (commit `3ec390c`), pero no se probó A/B en despliegue.
Favoritos A/B y sus denegaciones reales pasan a H5: dependen de G1 #239, G6 y
configuración de G1. El cierre H4 no acredita esos casos ni toda integración G6.

### FEAT-11-T1 — Configuración para G1
- **Resultado:** acordar primero navegador/BFF y sesión con G1. Para llamadas directas del navegador, Christian carga el origen exacto en `G7_CORS_ORIGENES`; las llamadas servidor a servidor del BFF no requieren CORS. Registrar URL de login/retorno permitida por entorno para Eva (FEAT-01-T4), sin URL arbitraria enviada por el usuario.
- **Aceptación:** cuando exista llamada directa desde navegador, preflight desde el origen autorizado permitido y desde otro sin autorización CORS; para BFF, documentar el flujo servidor a servidor y su protección de sesión. El retorno debe coincidir con una URL de la lista.
- **Evidencia:** captura del preflight cuando corresponda, o comprobación del recorrido BFF; registrar orígenes/retorno sin secretos.

### FEAT-11-T2 — Prueba temprana (S1)
- **Resultado y aceptación H4:** registrar login, sesión, renovación y cierre reportados por G1 y los recorridos de identidad probados por David. El aislamiento A/B y las denegaciones de favoritos en despliegue se trasladan a H5, sin afirmarlos como comprobados.
- **Evidencia:** `docs/evidencia/h4/FEAT-11-temprana.md` con fecha, SHA desplegado y capturas sanitizadas.

### FEAT-11-T3 — Paquete para G1
- **Resultado:** antes de T1/T2, preparar la [matriz por operación y solicitud de datos](../../../docs/integracion/hito-4-marketplace.md): versión, estado, SHA de código/despliegue y URL; acordar transporte, origen/retorno y operación G1. Coordinar cuentas A/B y escenarios pendiente, bloqueado y MFA según disponibilidad por canal seguro. No crear cuentas ni afirmar su entrega solo por escribir este documento.
- **Evidencia:** paquete publicado y seguimiento posterior en [cierre H4](../../../docs/evidencia/h4/FEAT-11.md), sin secretos.

**Registro histórico del 26-oct (superado por el cierre H4 anterior):** se reutiliza el paquete publicado en #64/#65 y se incorpora el
mensaje nuevo de G1. Frontend y BFF recibidos; G1 confirma uso de las cuentas CLIENTE
A/B dedicadas y reporta login, MFA EMAIL para A, sesión, renovación/cierre y favoritos
sin sesión → 401/con B → 200. **No acredita aislamiento A/B**: faltan productos/datos.
T3 sigue **En curso**: faltan SHAs efectivos de las pruebas G1/G7 y referencia de
despliegue, retorno exacto y detalles de sesión implementada, evidencia, ejecutores
y horario para los casos restantes. No se declara pendiente la implementación de
toda la sesión: su detalle técnico sigue por confirmar. No se enviaron mensajes.

[Evidencia temprana y límites](../../../docs/evidencia/h4/FEAT-11-temprana.md):
backend #61 fusionado; frontend #16 abierto; recuperación #64/#13 fusionada sin
confirmación actual de despliegue en este corte. Registro de Sergio del 25-oct
reporta G7 c90037b, anterior a #61. Se registra además el 503/timeout reportado por G1
del 25-oct, 12:28–12:29 de Lima, sin causa confirmada. Continúan T1–T4; T5 pendiente.

### FEAT-11-T4 — Seguimiento por operación
- **Resultado:** registrar por operación código fusionado, SHA desplegado, resultado comprobado y aviso a G1. Una fusión no acredita disponibilidad. Coordinar credenciales de `modulo-marketplace` solo después de aprobación de scopes, provisión segura y comprobación del servicio; nunca publicar secretos ni afirmar que el grant existe por estar planificado.

### FEAT-11-T5 — Cierre conjunto H4
- **Resultado y aceptación ajustados:** registrar los recorridos efectivamente probados con G1 y la emisión del cliente técnico. Los casos A/B con datos de favoritos y denegaciones reales se prueban en H5.
- **Evidencia:** `docs/evidencia/h4/FEAT-11.md`.

## FEAT-07 — Activación de MFA v2 (P2)

**Corte 23-oct:** backend #57 y frontend #11 fusionados y desplegados; David comprobó
habilitación, login con código por correo y deshabilitación con su cuenta C.
[Evidencia y límites](../../../docs/evidencia/h4/FEAT-07.md): quedan casos negativos
de aceptación desplegada, sin marcar toda la FEAT/RF como Verificada. SMS es simulado.

### FEAT-07-T1 — `POST /auth/mfa/cambios`
- **Resultado:** inicia el cambio de MFA (activar, cambiar canal o desactivar según SPEC-10) con reautenticación; envía código al canal nuevo.
- **Aceptación:** gestión (ADMIN) no puede desactivar MFA; no se implementan `/auth/otp/habilitar|deshabilitar` de v1.

### FEAT-07-T2 — `/auth/mfa/cambios/reenviar` y `/confirmar`
- **Resultado:** reenvío con cuota y confirmación que aplica el cambio, audita y avisa por correo.
- **Aceptación:** código vencido, usado o erróneo → error del catálogo.

### FEAT-07-T3 — SMS simulado
- **Resultado:** adaptador de salida que registra el envío en el receptor de pruebas (sin proveedor real).

### FEAT-07-T4 — FRONT-09
- **Resultado (frontend):** pantalla de segundo factor en Mi cuenta.
- **Evidencia:** `docs/evidencia/h4/FEAT-07.md`.

## FEAT-08 — Bloqueo manual (P2)

**Corte 23-oct:** backend #65 y frontend #14 fusionados; David confirmó ambos
despliegues y probó bloqueo manual, login rechazado, desbloqueo, nuevo ingreso,
correos y auditoría con su cuenta C, restaurada operativa.
[Evidencia y límites](../../../docs/evidencia/h4/FEAT-08.md): revocación probada en
PostgreSQL local; F5 no la acredita en nube. Se conservan los casos no ejecutados
y la confirmación independiente de SHAs efectivos pendientes, sin marcar toda
la FEAT/RF como Verificada. El estado de FEAT-11 corresponde al cierre H4 con alcance ajustado descrito arriba.

### FEAT-08-T1 — `POST /usuarios/{id}/bloquear` y `/desbloquear`
- **Resultado:** ADMIN_SISTEMA bloquea con motivo obligatorio y sin vencimiento automático (RF-15.2); revoca sesiones; desbloqueo manual; auditoría y eventos. El objeto de bloqueo del detalle depende de fusionar y comprobar backend #55.
- **Aceptación:** un bloqueo manual no se levanta con el enlace automático ni al restablecer contraseña; sin permiso → 403; autobloqueo o último administrador → `422 ADMINISTRADOR_PROTEGIDO`, según SPEC-15.

### FEAT-08-T2 — Acciones en FRONT-08
- **Resultado (frontend):** botones Bloquear/Desbloquear con motivo en el detalle admin.
- **Evidencia:** `docs/evidencia/h4/FEAT-08.md`.
