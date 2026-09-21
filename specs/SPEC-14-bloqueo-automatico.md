# SPEC-14 — Bloqueo automático por intentos fallidos

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 5 (Sem. 14) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-07 «Bloqueo y desbloqueo de cuentas» el 20 de septiembre, por indicación del profesor: una spec por función. Conserva el bloqueo automático, su vencimiento y el desbloqueo por el titular; el bloqueo y el desbloqueo por un administrador pasaron a SPEC-15. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El marketplace está expuesto a intentos automatizados de adivinación de contraseñas. Sin un mecanismo de protección, un atacante puede probar credenciales indefinidamente contra cuentas de clientes, vendedores y administradores.

El riesgo opuesto es igual de real: un bloqueo mal diseñado permite a cualquiera dejar fuera al titular legítimo con solo fallar cinco veces. Por eso el bloqueo es corto y progresivo, no cierra las sesiones abiertas, y el titular puede levantarlo él mismo.

Esta funcionalidad trabaja junto con SPEC-05, que procesa el login, y SPEC-12, que registra los eventos. SPEC-14 decide cuándo cambia el estado de la cuenta.

---

## Propósito — ¿para qué?

Detectar intentos consecutivos de autenticación fallida y frenar la adivinación de contraseñas, sin convertir el bloqueo en una forma de dejar fuera al titular.

El resultado observable es que una cuenta bloqueada no puede iniciar sesiones nuevas hasta que venza el bloqueo o lo levante su titular con el enlace del correo o restableciendo la contraseña.

---

## Alcance — ¿hasta dónde?

- Registro persistente de intentos de login fallidos.
- Contador de intentos fallidos consecutivos por cuenta, sin ventana de tiempo.
- Bloqueo automático al quinto intento fallido consecutivo.
- Bloqueo progresivo: 1, 2 y 4 minutos; desde el cuarto bloqueo seguido, sin vencimiento.
- Reinicio del contador: login correcto, desbloqueo, restablecimiento de contraseña o vencimiento.
- Desbloqueo por el titular con un enlace de un solo uso enviado por correo.
- Desbloqueo al restablecer la contraseña (SPEC-08).
- Notificación asíncrona por correo, publicación de `usuario.bloqueado` y `usuario.desbloqueado`, y registro en SPEC-12.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-14.1 | El sistema debe registrar cada intento fallido con usuario o correo intentado, fecha, IP y agente de usuario. |
| RF-14.2 | El sistema debe contar los intentos fallidos **consecutivos** de cada cuenta, sin ventana de tiempo y sin depender de la IP. Solo cuenta una contraseña incorrecta sobre una cuenta `ACTIVO`: presentar otra vez la misma contraseña incorrecta no suma, y los intentos contra una cuenta ya bloqueada no cuentan. |
| RF-14.3 | El sistema debe bloquear automáticamente una cuenta `ACTIVO` al quinto intento fallido consecutivo. |
| RF-14.4 | El bloqueo automático debe establecer el estado `BLOQUEADO` con una duración progresiva según los bloqueos seguidos de la cuenta: 1 minuto el primero, 2 el segundo y 4 el tercero. Desde el cuarto bloqueo seguido no vence (`bloqueado_hasta` = `null`). La cuenta de bloqueos seguidos solo vuelve a cero con un login correcto. |
| RF-14.5 | El contador de intentos fallidos debe volver a cero con un login correcto, con cualquier desbloqueo, al restablecer la contraseña y al vencer un bloqueo. |
| RF-14.6 | Mientras una cuenta esté bloqueada, el login no debe emitir tokens y debe responder `401 CREDENCIALES_INVALIDAS`, exactamente igual que ante una contraseña incorrecta, aunque la contraseña presentada sea correcta. |
| RF-14.7 | Un bloqueo automático con vencimiento debe terminar solo al pasar `bloqueado_hasta`, sin ningún proceso ni intervención: desde ese instante la cuenta se considera `ACTIVO` en toda la API. El vencimiento no publica ningún evento, porque los módulos consumidores ya conocen `hasta` por `usuario.bloqueado`. |
| RF-14.8 | El sistema debe notificar por correo al usuario cuando su cuenta sea bloqueada y cuando sea desbloqueada manualmente, sin revelar datos sensibles. El correo de un bloqueo automático incluye un enlace de desbloqueo; el de un bloqueo manual, no. |
| RF-14.9 | El sistema debe publicar `usuario.bloqueado` después de confirmar un bloqueo, y `usuario.desbloqueado` después de un desbloqueo explícito —por un administrador, con el enlace o restableciendo la contraseña—, indicando la vía. |
| RF-14.10 | Los bloqueos y desbloqueos deben registrarse mediante el catálogo de auditoría de SPEC-12, incluyendo actor, motivo, fecha, IP y resultado. |
| RF-14.11 | El enlace de desbloqueo debe ser de un solo uso, expirar a los 30 minutos y quedar invalidado por cualquier bloqueo posterior. Consumirlo con `POST /api/v1/auth/desbloquear` levanta un bloqueo automático, tenga o no vencimiento. |
| RF-14.12 | Restablecer la contraseña (SPEC-08) debe levantar un bloqueo automático, tenga o no vencimiento. Ni el enlace ni el restablecimiento levantan un bloqueo manual. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-14.1 Registro de un intento fallido

- **Dado** un usuario activo que envía una contraseña incorrecta,
- **Cuando** SPEC-05 procesa el inicio de sesión,
- **Entonces** se registra un `intento_login` con resultado fallido, fecha, IP y agente de usuario, se incrementa el contador de intentos fallidos consecutivos y se responde `401 CREDENCIALES_INVALIDAS`.

### ESC-14.2 Bloqueo automático tras cinco fallos

- **Dado** una cuenta `ACTIVO` con 4 intentos fallidos consecutivos y ningún bloqueo previo seguido,
- **Cuando** ocurre un quinto intento fallido,
- **Entonces** la cuenta pasa a `BLOQUEADO`, `bloqueado_hasta` se establece a 1 minuto después, las sesiones abiertas **no** se cierran, se registra `CUENTA_BLOQUEADA`, se envía la notificación asíncrona con el enlace de desbloqueo y se publica `usuario.bloqueado`.

### ESC-14.3 Fallos distribuidos entre varias IP *(caso borde)*

- **Dado** que una misma cuenta recibe cinco intentos fallidos consecutivos desde cinco IP diferentes,
- **Cuando** llega el quinto fallo,
- **Entonces** la cuenta se bloquea igualmente, porque el conteo es por cuenta y no por dirección IP, y cada IP queda registrada en `intento_login`.

### ESC-14.4 Fallos separados en el tiempo *(caso borde)*

- **Dado** que una cuenta tuvo cuatro intentos fallidos hace varios días y ningún login correcto desde entonces,
- **Cuando** recibe un nuevo intento fallido,
- **Entonces** la cuenta se bloquea: el contador no caduca con el tiempo, y solo vuelve a cero por las causas de RF-14.5.

### ESC-14.5 Login correcto antes del quinto fallo

- **Dado** un usuario activo con cuatro intentos fallidos consecutivos,
- **Cuando** inicia sesión con la contraseña correcta antes de acumular el quinto fallo,
- **Entonces** el login se completa, el contador se reinicia a cero y la cuenta permanece `ACTIVO`.

### ESC-14.6 Login correcto durante un bloqueo automático *(caso borde)*

- **Dado** una cuenta `BLOQUEADO` con un bloqueo automático vigente,
- **Cuando** el usuario presenta credenciales correctas antes de `bloqueado_hasta`,
- **Entonces** el sistema responde `401 CREDENCIALES_INVALIDAS`, exactamente igual que con una contraseña incorrecta, no emite tokens y el intento no cuenta como fallo.

### ESC-14.7 Vencimiento de un bloqueo automático

- **Dado** una cuenta bloqueada automáticamente cuyo `bloqueado_hasta` ya pasó,
- **Cuando** se consulta su estado o el usuario inicia sesión con credenciales correctas,
- **Entonces** la cuenta figura `ACTIVO` en toda la API sin que ningún proceso la haya cambiado, el contador está a cero, el login emite la sesión y no se publica ningún evento.

### ESC-14.8 Concurrencia en el quinto intento *(caso borde de concurrencia)*

- **Dado** una cuenta con cuatro intentos fallidos consecutivos,
- **Cuando** dos peticiones fallidas concurrentes llegan casi simultáneamente,
- **Entonces** la actualización se serializa de forma atómica: la cuenta se bloquea una sola vez, no se crean bloqueos duplicados y se publica un único `usuario.bloqueado`.

### ESC-14.9 Evento publicado después de confirmar el cambio

- **Dado** un bloqueo automático o manual válido,
- **Cuando** la transacción que cambia la cuenta se confirma,
- **Entonces** el evento se publica después de la confirmación; si la transacción falla, no se publica ningún `usuario.bloqueado` falso.

### ESC-14.10 Repetir la misma contraseña incorrecta *(caso borde)*

- **Dado** un usuario activo con un intento fallido,
- **Cuando** vuelve a presentar exactamente la misma contraseña incorrecta,
- **Entonces** el intento se registra en `intento_login`, pero el contador no sube.

### ESC-14.11 Bloqueo sin vencimiento tras bloqueos seguidos *(caso borde)*

- **Dado** una cuenta que ya se bloqueó tres veces seguidas, sin ningún login correcto entre medias,
- **Cuando** acumula otros cinco intentos fallidos consecutivos,
- **Entonces** se bloquea con `bloqueado_hasta = null`: no vence sola, y solo se levanta con el enlace, restableciendo la contraseña o por un administrador.

### ESC-14.12 Desbloqueo con el enlace del correo

- **Dado** una cuenta con bloqueo automático y el enlace recibido por correo hace menos de 30 minutos,
- **Cuando** el titular hace `POST /api/v1/auth/desbloquear` con su token,
- **Entonces** la cuenta pasa a `ACTIVO`, el contador vuelve a cero, se registra `CUENTA_DESBLOQUEADA` y se publica `usuario.desbloqueado` con `via: "ENLACE"`.

### ESC-14.13 Enlace vencido o reemplazado *(caso borde)*

- **Dado** un enlace de desbloqueo de más de 30 minutos, o de un bloqueo al que siguió otro,
- **Cuando** el titular lo usa,
- **Entonces** el sistema responde `410 TOKEN_DESBLOQUEO_EXPIRADO` o `401 TOKEN_DESBLOQUEO_INVALIDO` y la cuenta sigue bloqueada.

### ESC-14.14 El enlace no levanta un bloqueo manual *(caso borde de seguridad)*

- **Dado** una cuenta con bloqueo automático cuyo titular recibió un enlace, y que después un administrador bloquea manualmente,
- **Cuando** el titular usa el enlace,
- **Entonces** el sistema responde `401 TOKEN_DESBLOQUEO_INVALIDO` y el bloqueo manual se mantiene.

### ESC-14.15 Restablecer la contraseña levanta el bloqueo automático

- **Dado** una cuenta con bloqueo automático, con o sin vencimiento,
- **Cuando** el titular restablece su contraseña mediante SPEC-08,
- **Entonces** el bloqueo se levanta, el contador vuelve a cero y se publica `usuario.desbloqueado` con `via: "RESTABLECIMIENTO"`. Si el bloqueo fuera manual, se mantendría.

### ESC-14.16 Las sesiones abiertas sobreviven a un bloqueo automático *(caso borde de seguridad)*

- **Dado** un titular con una sesión abierta en su móvil,
- **Cuando** un tercero provoca un bloqueo automático fallando cinco veces seguidas,
- **Entonces** el titular puede seguir renovando su sesión con `POST /api/v1/auth/refresh`; solo se rechazan los inicios de sesión nuevos.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El contador y los intentos deben persistirse en PostgreSQL; no pueden vivir solo en memoria, porque el servicio puede ejecutarse en varias instancias.
- La actualización del contador y la transición a `BLOQUEADO` deben ser atómicas y resistentes a concurrencia.
- El login debe responder `401 CREDENCIALES_INVALIDAS` para cuentas bloqueadas, idéntico a una contraseña incorrecta y con la misma latencia.
- El historial de intentos debe conservarse al menos 90 días.
- El bloqueo no debe depender de que los intentos provengan de la misma IP.
- Las notificaciones deben ser asíncronas y no bloquear el camino crítico del login.
- Los eventos RabbitMQ deben publicarse al menos una vez, ser deduplicables mediante `eventoId` y seguir el catálogo de eventos existente.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El bloqueo y el desbloqueo por un administrador, que son de SPEC-15.
- Detección de anomalías por geolocalización, dispositivo o aprendizaje automático.
- CAPTCHA como condición previa al bloqueo.
- Bloqueo global por dirección IP a nivel de firewall o WAF, o por combinación de cuenta e IP (propuesto como mejora para el Hito 4).
- Eliminación física de intentos o bloqueos fuera de la política de retención.
- Emisión, rotación o validación ordinaria de tokens, que pertenece a SPEC-05, SPEC-06 y SPEC-17.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/login` | Responde `401 CREDENCIALES_INVALIDAS` también con la cuenta bloqueada | ⬜ |
| `POST /api/v1/auth/desbloquear` | Añade — desbloqueo por el titular con el enlace del correo | ⬜ |
| `usuario.bloqueado` · `usuario.desbloqueado` | Publica según catálogo de eventos | ⬜ |
| `TOKEN_DESBLOQUEO_INVALIDO` · `TOKEN_DESBLOQUEO_EXPIRADO` | Añade | ⬜ |
| `CUENTA_BLOQUEADA` / `CUENTA_DESBLOQUEADA` | Acciones del catálogo de auditoría de SPEC-12 | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
