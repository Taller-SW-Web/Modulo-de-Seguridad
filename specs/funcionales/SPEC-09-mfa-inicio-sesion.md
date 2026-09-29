# SPEC-09 — Segundo factor en el inicio de sesión (OTP)

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 4 (Sem. 11) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-04 «Autenticación por OTP y MFA» el 20 de septiembre, por indicación del profesor: una spec por función. Conserva la generación y verificación de códigos y el desafío del inicio de sesión; la activación y desactivación del segundo factor pasaron a SPEC-10. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

La contraseña por sí sola no ofrece protección suficiente para las cuentas que administran operaciones sensibles del marketplace: una contraseña filtrada bastaría para anular pedidos o cambiar precios.

Este módulo es el proveedor de identidad, por lo que debe generar, enviar y verificar los códigos de un solo uso. Ningún canal debe implementar su propia generación de OTP ni guardar códigos en su base de datos.

---

## Propósito — ¿para qué?

Añadir un segundo factor al inicio de sesión cuando la cuenta lo tiene habilitado: la contraseña correcta produce un desafío, y solo el código correcto lo convierte en sesión.

El resultado observable es un `challengeToken` de corta duración que no concede acceso por sí mismo, un código de 6 dígitos enviado por correo o SMS, y el par de tokens de SPEC-05 cuando el código se verifica dentro de los límites.

---

## Alcance — ¿hasta dónde?

- Generación criptográficamente segura de códigos numéricos de 6 dígitos.
- Envío por correo real (adaptador SMTP) o por SMS simulado en desarrollo y pruebas.
- Verificación con vencimiento, límite de intentos y uso único.
- Límite de solicitudes de código por usuario.
- El `challengeToken` del inicio de sesión y su canje por el par de tokens.
- Comprobación de caducidad de contraseña (SPEC-07) antes de emitir tokens.
- Auditoría de solicitudes y verificaciones mediante SPEC-12.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-09.1 | El sistema debe generar códigos numéricos de exactamente 6 dígitos usando un generador criptográficamente seguro. |
| RF-09.2 | Cada código debe tener una vigencia de 5 minutos desde su generación. |
| RF-09.3 | El sistema debe almacenar únicamente el hash del código y nunca el código en texto plano. |
| RF-09.4 | El sistema debe enviar el código por correo electrónico mediante SMTP o por SMS mediante el adaptador configurado para el entorno. |
| RF-09.5 | Un código debe admitir como máximo 3 intentos de verificación. Al agotarse los intentos, debe quedar invalidado. |
| RF-09.6 | El sistema debe permitir como máximo 3 solicitudes de código por usuario dentro de cualquier ventana de 15 minutos. |
| RF-09.7 | Un código debe quedar invalidado después de su primera verificación correcta. |
| RF-09.8 | Si un usuario con MFA habilitado inicia sesión correctamente con su contraseña, el sistema debe devolver un `challengeToken` en lugar de tokens de acceso. |
| RF-09.9 | El `challengeToken` debe tener corta duración, estar vinculado al usuario y no poder utilizarse para otra cuenta o desafío. |
| RF-09.10 | La verificación correcta del desafío debe emitir el token de acceso de 15 minutos y el token de refresco de 7 días definidos por SPEC-05. |
| RF-09.11 | Al verificar el desafío (`POST /api/v1/auth/otp/verificar`) y antes de emitir tokens, el sistema debe aplicar la comprobación de caducidad de contraseña de SPEC-07: si caducó, responde `403 PASSWORD_CADUCADA` y no emite tokens. SPEC-09 invoca la regla; no la reimplementa. |
| RF-09.12 | El sistema debe registrar mediante SPEC-12 las solicitudes, las verificaciones exitosas y las fallidas (`OTP_SOLICITADO`, `OTP_VERIFICADO`, `OTP_FALLIDO`), sin registrar nunca el código. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-09.1 Generación y solicitud de OTP

- **Dado** que existe un usuario elegible con un canal de contacto registrado y verificado,
- **Cuando** solicita `POST /api/v1/auth/otp/solicitar` indicando un `challengeToken` válido y el canal `EMAIL`,
- **Entonces** el sistema genera un código de 6 dígitos, guarda únicamente su hash, inicia una vigencia de 5 minutos, envía el mensaje de forma asíncrona y responde `202` con el destino enmascarado y `expiraEn: 300`.

### ESC-09.2 Envío por SMS simulado

- **Dado** un entorno de desarrollo o pruebas y un usuario con celular registrado,
- **Cuando** solicita un OTP con canal `SMS`,
- **Entonces** el sistema utiliza `MockSmsSender`, no contacta a un proveedor externo, no devuelve el código en la respuesta y deja el código disponible únicamente por el mecanismo controlado de pruebas definido por el equipo.

### ESC-09.3 Verificación correcta del código

- **Dado** un `challengeToken` válido y un código vigente cuyo hash coincide,
- **Cuando** el usuario envía `POST /api/v1/auth/otp/verificar`,
- **Entonces** el sistema marca el código como usado, registra `OTP_VERIFICADO`, emite el token de acceso y el token de refresco, y responde `200`.

### ESC-09.4 Código incorrecto dentro del límite

- **Dado** un código vigente con 2 intentos disponibles,
- **Cuando** el usuario envía un código incorrecto,
- **Entonces** el sistema incrementa el contador, registra `OTP_FALLIDO`, responde `401 CODIGO_INVALIDO` e informa los intentos restantes sin revelar el código correcto.

### ESC-09.5 Agotamiento de intentos *(caso borde)*

- **Dado** un código que ya acumuló 2 intentos incorrectos,
- **Cuando** el usuario envía un tercer código incorrecto,
- **Entonces** el sistema invalida definitivamente el código, registra `OTP_FALLIDO`, responde `401 OTP_INTENTOS_AGOTADOS` y exige solicitar un nuevo código.

### ESC-09.6 Código expirado *(caso borde)*

- **Dado** un código generado hace más de 5 minutos,
- **Cuando** el usuario envía el código correcto junto con su desafío,
- **Entonces** el sistema lo rechaza, responde `410 OTP_EXPIRADO`, no emite tokens y no modifica el contador de la cuenta.

### ESC-09.7 Exceso de solicitudes *(caso borde)*

- **Dado** un usuario que ya solicitó 3 códigos durante los últimos 15 minutos,
- **Cuando** solicita un cuarto código,
- **Entonces** el sistema responde `429 DEMASIADAS_SOLICITUDES`, no genera ni envía otro código e indica cuándo puede volver a intentarlo sin revelar información innecesaria.

### ESC-09.8 Inicio de sesión con MFA habilitado

- **Dado** un usuario activo con MFA habilitado y contraseña correcta,
- **Cuando** completa `POST /api/v1/auth/login`,
- **Entonces** el sistema responde `200` con `mfaRequerido: true` y un `challengeToken`, pero no entrega token de acceso ni token de refresco.

### ESC-09.9 Código de otro desafío *(caso borde de seguridad)*

- **Dado** que un usuario tiene dos desafíos abiertos, A y B,
- **Cuando** presenta el código generado para B usando el `challengeToken` de A,
- **Entonces** el sistema responde `401 CODIGO_INVALIDO`, no consume el código de B y no emite tokens.

### ESC-09.10 Reutilización de código *(caso borde)*

- **Dado** un código que ya fue verificado correctamente,
- **Cuando** se presenta nuevamente con el mismo desafío,
- **Entonces** el sistema responde `401 CODIGO_INVALIDO` y no emite una segunda sesión.

### ESC-09.11 Desafío inexistente o manipulado *(caso borde de seguridad)*

- **Dado** un `challengeToken` inexistente, vencido o manipulado,
- **Cuando** se solicita o verifica un OTP,
- **Entonces** el sistema responde `401 TOKEN_INVALIDO` o `401 CODIGO_INVALIDO`, no revela si la cuenta existe y no envía mensajes.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La generación del código debe utilizar `SecureRandom` o un mecanismo criptográficamente equivalente.
- El código, el `challengeToken` y los tokens no deben aparecer en logs, trazas, mensajes de error ni respuestas distintas de las definidas por el contrato.
- La verificación debe responder en menos de 300 ms en el percentil 95, sin incluir la latencia del proveedor de correo o SMS.
- El envío de mensajes debe ser asíncrono y contar con reintentos controlados.
- Las comparaciones del código deben ser resistentes a diferencias de tiempo observables.
- Dos verificaciones simultáneas no pueden consumir el mismo código dos veces.
- Los datos de contacto deben mostrarse enmascarados en las respuestas.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- Habilitar o deshabilitar el segundo factor, y la obligatoriedad por rol, que son de SPEC-10.
- La emisión y rotación de los tokens de acceso y de refresco, que pertenecen a SPEC-05 y SPEC-06.
- El bloqueo de cuentas por intentos fallidos, que pertenece a SPEC-14.
- Segundo factor mediante aplicaciones TOTP, llaves FIDO2/WebAuthn, passkeys o notificaciones push.
- Códigos de respaldo para recuperar el segundo factor.
- Integración con un proveedor SMS real durante esta etapa.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/otp/solicitar` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/auth/otp/verificar` | Utiliza contrato existente; puede responder `403 PASSWORD_CADUCADA` (SPEC-07) | ⬜ |
| `OTP_SOLICITADO` · `OTP_VERIFICADO` · `OTP_FALLIDO` | Catálogo de auditoría de SPEC-12 | ⬜ |

Esta spec no publica eventos RabbitMQ.

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
