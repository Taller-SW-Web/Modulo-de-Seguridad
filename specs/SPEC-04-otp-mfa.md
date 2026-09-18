# SPEC-04 — Autenticación por OTP y MFA

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 4 (Sem. 11) |
| **Estado** | Borrador — pendiente de revisión del equipo |
| **Aprobada por** | — |

---

## Contexto — ¿por qué?

La contraseña por sí sola no ofrece protección suficiente para las cuentas que
administran operaciones sensibles del marketplace. Además, el canal Chatbot
necesita poder verificar el correo y el número celular de un cliente antes de
continuar determinados flujos.

Este módulo es el proveedor de identidad del marketplace, por lo que debe
generar, enviar y verificar los códigos de un solo uso. Ningún canal debe
implementar su propia generación de OTP ni guardar códigos en su base de datos.
Las cuentas administrativas requieren una protección adicional; para clientes y
vendedores el segundo factor es opcional.

---

## Propósito — ¿para qué?

Permitir que un usuario demuestre que controla un correo electrónico o un número
celular mediante un código de un solo uso, y añadir un segundo factor al inicio
de sesión cuando la cuenta tenga MFA habilitado.

El resultado observable es un desafío de corta duración que no concede acceso por
sí mismo y que solo se convierte en una sesión cuando el código correcto se
verifica dentro de los límites establecidos.

---

## Alcance — ¿hasta dónde?

- Generación criptográficamente segura de códigos numéricos de 6 dígitos.
- Envío por correo electrónico real mediante el adaptador SMTP del módulo.
- Envío por SMS mediante un adaptador simulado durante desarrollo y pruebas.
- Verificación de códigos con vencimiento, límite de intentos y uso único.
- Límite de solicitudes de código por usuario.
- Desafío MFA durante el inicio de sesión.
- Habilitación y deshabilitación de MFA para usuarios permitidos.
- Auditoría de solicitudes, verificaciones, fallos y cambios de MFA mediante
  SPEC-06.
- Validación de correo y celular cuando el contrato de integración lo solicite.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-04.1 | El sistema debe generar códigos numéricos de exactamente 6 dígitos usando un generador criptográficamente seguro. |
| RF-04.2 | Cada código debe tener una vigencia de 5 minutos desde su generación. |
| RF-04.3 | El sistema debe almacenar únicamente el hash del código y nunca el código en texto plano. |
| RF-04.4 | El sistema debe enviar el código por correo electrónico mediante SMTP o por SMS mediante el adaptador configurado para el entorno. |
| RF-04.5 | Un código debe admitir como máximo 3 intentos de verificación. Al agotarse los intentos, debe quedar invalidado. |
| RF-04.6 | El sistema debe permitir como máximo 3 solicitudes de código por usuario dentro de cualquier ventana de 15 minutos. |
| RF-04.7 | Un código debe quedar invalidado después de su primera verificación correcta. |
| RF-04.8 | Si un usuario con MFA habilitado inicia sesión correctamente con su contraseña, el sistema debe devolver un `challengeToken` en lugar de tokens de acceso. |
| RF-04.9 | El `challengeToken` debe tener corta duración, estar vinculado al usuario y no poder utilizarse para otra cuenta o desafío. |
| RF-04.10 | La verificación correcta del desafío debe emitir el token de acceso de 15 minutos y el token de refresco de 7 días definidos por SPEC-02. |
| RF-04.11 | El segundo factor debe ser obligatorio para quien tenga algún rol de gestión (`ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`) y opcional para `CLIENTE` y `VENDEDOR`. Basta **un** rol de gestión para que sea obligatorio. |
| RF-04.12 | Un usuario autenticado debe poder solicitar la habilitación de MFA y confirmar su activación mediante un código de prueba. |
| RF-04.13 | Un usuario permitido debe poder deshabilitar MFA después de autenticarse y verificar un código de confirmación. Quien tenga algún rol de gestión no puede deshabilitarlo (`422 MFA_OBLIGATORIO`). |
| RF-04.14 | El sistema debe permitir validar un correo o celular mediante este mecanismo cuando exista una solicitud autorizada del canal consumidor. |
| RF-04.15 | El sistema debe registrar mediante SPEC-06 las solicitudes, verificaciones exitosas, verificaciones fallidas, activaciones y desactivaciones de MFA, sin registrar el código. |
| RF-04.16 | Al verificar el desafío (`POST /api/v1/auth/otp/verificar`) y antes de emitir tokens, el sistema debe aplicar la comprobación de caducidad de contraseña de SPEC-03: si caducó, responde `403 PASSWORD_CADUCADA` y no emite tokens. SPEC-04 invoca la regla; no la reimplementa. |

---

## Escenarios — ¿cómo verificamos?

### ESC-04.1 Generación y solicitud de OTP

- **Dado** que existe un usuario elegible con un canal de contacto registrado y verificado,
- **Cuando** solicita `POST /api/v1/auth/otp/solicitar` indicando un `challengeToken` válido y el canal `EMAIL`,
- **Entonces** el sistema genera un código de 6 dígitos, guarda únicamente su hash, inicia una vigencia de 5 minutos, envía el mensaje de forma asíncrona y responde `202` con el destino enmascarado y `expiraEn: 300`.

### ESC-04.2 Envío por SMS simulado

- **Dado** un entorno de desarrollo o pruebas y un usuario con celular registrado,
- **Cuando** solicita un OTP con canal `SMS`,
- **Entonces** el sistema utiliza `MockSmsSender`, no contacta a un proveedor externo, no devuelve el código en la respuesta y deja el código disponible únicamente por el mecanismo controlado de pruebas definido por el equipo.

### ESC-04.3 Verificación correcta del código

- **Dado** un `challengeToken` válido y un código vigente cuyo hash coincide,
- **Cuando** el usuario envía `POST /api/v1/auth/otp/verificar`,
- **Entonces** el sistema marca el código como usado, registra `OTP_VERIFICADO`, emite el token de acceso y el token de refresco, y responde `200`.

### ESC-04.4 Código incorrecto dentro del límite

- **Dado** un código vigente con 2 intentos disponibles,
- **Cuando** el usuario envía un código incorrecto,
- **Entonces** el sistema incrementa el contador, registra `OTP_FALLIDO`, responde `401 CODIGO_INVALIDO` e informa los intentos restantes sin revelar el código correcto.

### ESC-04.5 Agotamiento de intentos *(caso borde)*

- **Dado** un código que ya acumuló 2 intentos incorrectos,
- **Cuando** el usuario envía un tercer código incorrecto,
- **Entonces** el sistema invalida definitivamente el código, registra `OTP_FALLIDO`, responde `401 OTP_INTENTOS_AGOTADOS` y exige solicitar un nuevo código.

### ESC-04.6 Código expirado *(caso borde)*

- **Dado** un código generado hace más de 5 minutos,
- **Cuando** el usuario envía el código correcto junto con su desafío,
- **Entonces** el sistema lo rechaza, responde `410 OTP_EXPIRADO`, no emite tokens y no modifica el contador de la cuenta.

### ESC-04.7 Exceso de solicitudes *(caso borde)*

- **Dado** un usuario que ya solicitó 3 códigos durante los últimos 15 minutos,
- **Cuando** solicita un cuarto código,
- **Entonces** el sistema responde `429 DEMASIADAS_SOLICITUDES`, no genera ni envía otro código e indica cuándo puede volver a intentarlo sin revelar información innecesaria.

### ESC-04.8 Inicio de sesión con MFA habilitado

- **Dado** un usuario activo con MFA habilitado y contraseña correcta,
- **Cuando** completa `POST /api/v1/auth/login`,
- **Entonces** el sistema responde `200` con `mfaRequerido: true` y un `challengeToken`, pero no entrega token de acceso ni token de refresco.

### ESC-04.9 Código de otro desafío *(caso borde de seguridad)*

- **Dado** que un usuario tiene dos desafíos abiertos, A y B,
- **Cuando** presenta el código generado para B usando el `challengeToken` de A,
- **Entonces** el sistema responde `401 CODIGO_INVALIDO`, no consume el código de B y no emite tokens.

### ESC-04.10 Reutilización de código *(caso borde)*

- **Dado** un código que ya fue verificado correctamente,
- **Cuando** se presenta nuevamente con el mismo desafío,
- **Entonces** el sistema responde `401 CODIGO_INVALIDO` y no emite una segunda sesión.

### ESC-04.11 Habilitación voluntaria de MFA

- **Dado** un cliente autenticado con MFA deshabilitado,
- **Cuando** solicita `POST /api/v1/auth/otp/habilitar`,
- **Entonces** el sistema envía un código de confirmación y, después de su verificación correcta, marca `mfa_habilitado` como verdadero y registra `MFA_ACTIVADO`.

### ESC-04.12 Deshabilitación permitida de MFA

- **Dado** un vendedor autenticado con MFA habilitado,
- **Cuando** solicita `POST /api/v1/auth/otp/deshabilitar` y verifica correctamente el código de confirmación,
- **Entonces** el sistema deshabilita MFA, registra `MFA_DESACTIVADO` y exige contraseña solamente en el siguiente inicio de sesión.

### ESC-04.13 Deshabilitación del segundo factor con un rol de gestión *(caso borde)*

- **Dado** un usuario con un rol de gestión —por ejemplo `ADMIN_VENTAS`— y MFA habilitado,
- **Cuando** intenta deshabilitar MFA,
- **Entonces** el sistema responde `422 MFA_OBLIGATORIO`, mantiene MFA activo y registra el intento rechazado.

### ESC-04.14 Desafío inexistente o manipulado *(caso borde de seguridad)*

- **Dado** un `challengeToken` inexistente, vencido o manipulado,
- **Cuando** se solicita o verifica un OTP,
- **Entonces** el sistema responde `401 TOKEN_INVALIDO` o `401 CODIGO_INVALIDO`, no revela si la cuenta existe y no envía mensajes.

### ESC-04.15 Validación de contacto solicitada por un canal autorizado

- **Dado** una solicitud autorizada para verificar el correo o celular de un cliente,
- **Cuando** el usuario recibe y verifica correctamente el código,
- **Entonces** el sistema marca el canal correspondiente como verificado, registra `OTP_VERIFICADO` y devuelve el resultado al consumidor autorizado.

### ESC-04.16 Basta un rol de gestión *(caso borde)*

- **Dado** un usuario con los roles `VENDEDOR` y `GESTOR_COMERCIAL`,
- **Cuando** intenta deshabilitar su segundo factor,
- **Entonces** el sistema responde `422 MFA_OBLIGATORIO`: aunque `VENDEDOR` lo permitiría, `GESTOR_COMERCIAL` lo exige, y manda el más estricto.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La generación del código debe utilizar `SecureRandom` o un mecanismo criptográficamente equivalente.
- El código, el `challengeToken` y los tokens no deben aparecer en logs, trazas, mensajes de error ni respuestas distintas de las definidas por el contrato.
- La verificación debe responder en menos de 300 ms en el percentil 95, sin incluir la latencia del proveedor de correo o SMS.
- El envío de mensajes debe ser asíncrono y contar con reintentos controlados.
- Las comparaciones del código deben ejecutarse de forma resistente a diferencias de tiempo observables.
- El SMS real queda reemplazado por `MockSmsSender` en desarrollo y pruebas.
- Los datos de contacto deben mostrarse enmascarados en respuestas.
- Las solicitudes deben ser seguras frente a concurrencia: dos verificaciones simultáneas no pueden consumir el mismo código dos veces.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733.

---

## Fuera de alcance — ¿qué NO hará?

- Segundo factor mediante aplicaciones TOTP como Google Authenticator.
- Llaves físicas FIDO2/WebAuthn o passkeys.
- Notificaciones push.
- Códigos de respaldo para recuperar MFA.
- Integración con un proveedor SMS real durante esta etapa.
- La emisión o rotación de los tokens de acceso y de refresco, que pertenece a SPEC-02.
- El bloqueo de cuentas por intentos fallidos, que pertenece a SPEC-07.
- La consulta y exportación de auditoría, que pertenece a SPEC-06.

---

## Impacto en el contrato

Esta spec utiliza endpoints ya definidos en `specs/openapi.yaml`. Cualquier ajuste
de contrato lo aplica Sergio como Product Owner.

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/otp/solicitar` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/auth/otp/verificar` | Utiliza contrato existente; puede responder `403 PASSWORD_CADUCADA` (SPEC-03) | ⬜ |
| `POST /api/v1/auth/otp/habilitar` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/auth/otp/deshabilitar` | Utiliza contrato existente | ⬜ |
| `OTP_SOLICITADO`, `OTP_VERIFICADO`, `OTP_FALLIDO` | Catálogo de auditoría de SPEC-06 | ⬜ |
| `MFA_ACTIVADO`, `MFA_DESACTIVADO` | Catálogo de auditoría de SPEC-06 | ⬜ |
| Validación de correo/celular por consumidor autorizado | Requiere confirmar payload y scope con el PO | ⬜ |

Esta spec no publica eventos RabbitMQ de usuario. Los eventos de auditoría son
internos de SPEC-06; los eventos `usuario.*` se publican cuando cambian estados o
atributos definidos por otras specs.

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints y códigos de error figuran en `specs/openapi.yaml` y `specs/catalogo-errores.md`
- [ ] Los eventos de auditoría figuran en `specs/SPEC-06-auditoria.md`
- [ ] No se almacenan códigos OTP ni desafíos en texto plano
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
