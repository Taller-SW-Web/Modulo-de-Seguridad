# SPEC-07 — Bloqueo y desbloqueo de cuentas

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 5 (Sem. 14) |
| **Estado** | Borrador — pendiente de revisión del equipo |
| **Aprobada por** | — |

---

## Contexto — ¿por qué?

El marketplace está expuesto a intentos automatizados de adivinación de
contraseñas. Sin un mecanismo de protección, un atacante puede probar credenciales
indefinidamente contra cuentas de clientes, vendedores y administradores.

El bloqueo también debe poder ser aplicado manualmente cuando un administrador
detecta actividad sospechosa. Como los otros módulos no pueden modificar la
entidad `usuario`, todas las decisiones de bloqueo, desbloqueo y sus efectos se
resuelven en este módulo.

Esta funcionalidad trabaja junto con SPEC-02, que procesa el login, y SPEC-06,
que registra los eventos de seguridad. SPEC-07 decide cuándo cambia el estado de
la cuenta; SPEC-06 define cómo queda registrado ese cambio.

---

## Propósito — ¿para qué?

Detectar intentos consecutivos de autenticación fallida y limitar los ataques de
adivinación de contraseñas, sin convertir el bloqueo en una forma de dejar fuera
al titular legítimo.

El resultado observable es que una cuenta bloqueada no puede iniciar sesiones
nuevas hasta que venza el bloqueo automático, lo levante su titular con el enlace
del correo o restableciendo la contraseña, o lo levante un administrador.

---

## Alcance — ¿hasta dónde?

- Registro persistente de intentos de login fallidos.
- Contador de intentos fallidos consecutivos por cuenta, sin ventana de tiempo.
- Bloqueo automático al quinto intento fallido consecutivo.
- Bloqueo progresivo: 1, 2 y 4 minutos; desde el cuarto bloqueo seguido, sin vencimiento.
- Reinicio del contador: login correcto, desbloqueo, restablecimiento de contraseña o vencimiento.
- Bloqueo manual por `ADMIN_SISTEMA` con motivo obligatorio.
- Desbloqueo manual por `ADMIN_SISTEMA`.
- Desbloqueo por el titular con un enlace de un solo uso enviado por correo.
- Cierre de sesiones en el bloqueo manual.
- Notificación asíncrona por correo.
- Publicación de `usuario.bloqueado` y `usuario.desbloqueado`.
- Registro de los hechos mediante SPEC-06.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-07.1 | El sistema debe registrar cada intento fallido con usuario o correo intentado, fecha, IP y agente de usuario. |
| RF-07.2 | El sistema debe contar los intentos fallidos **consecutivos** de cada cuenta, sin ventana de tiempo y sin depender de la IP. Solo cuenta una contraseña incorrecta sobre una cuenta `ACTIVO`: presentar otra vez la misma contraseña incorrecta no suma, y los intentos contra una cuenta ya bloqueada no cuentan. |
| RF-07.3 | El sistema debe bloquear automáticamente una cuenta `ACTIVO` al quinto intento fallido consecutivo. |
| RF-07.4 | El bloqueo automático debe establecer el estado `BLOQUEADO` con una duración progresiva según los bloqueos seguidos de la cuenta: 1 minuto el primero, 2 el segundo y 4 el tercero. Desde el cuarto bloqueo seguido no vence (`bloqueado_hasta` = `null`). La cuenta de bloqueos seguidos solo vuelve a cero con un login correcto. |
| RF-07.5 | El contador de intentos fallidos debe volver a cero con un login correcto, con cualquier desbloqueo, al restablecer la contraseña y al vencer un bloqueo. |
| RF-07.6 | Mientras una cuenta esté bloqueada, el login no debe emitir tokens y debe responder `401 CREDENCIALES_INVALIDAS`, exactamente igual que ante una contraseña incorrecta, aunque la contraseña presentada sea correcta. |
| RF-07.7 | Un bloqueo automático con vencimiento debe terminar solo al pasar `bloqueado_hasta`, sin ningún proceso ni intervención: desde ese instante la cuenta se considera `ACTIVO` en toda la API. El vencimiento no publica ningún evento, porque los módulos consumidores ya conocen `hasta` por `usuario.bloqueado`. |
| RF-07.8 | Un `ADMIN_SISTEMA` debe poder bloquear manualmente una cuenta activa indicando obligatoriamente un motivo. Nadie puede bloquearse a sí mismo, y no se puede bloquear al último `ADMIN_SISTEMA` activo (`422 ADMINISTRADOR_PROTEGIDO`, la misma regla que aplican SPEC-01 a la baja y SPEC-05 al revocar el rol). |
| RF-07.9 | El bloqueo manual no debe tener vencimiento automático y debe reemplazar cualquier bloqueo automático vigente. |
| RF-07.10 | Un `ADMIN_SISTEMA` debe poder desbloquear manualmente una cuenta bloqueada, reiniciando el contador de fallos. |
| RF-07.11 | Solo el bloqueo manual debe cerrar las sesiones abiertas de la cuenta, revocando sus tokens de refresco. El bloqueo automático no las cierra: las sesiones abiertas antes del bloqueo pueden seguir renovándose, y solo se impide iniciar sesiones nuevas. |
| RF-07.12 | El sistema debe notificar por correo al usuario cuando su cuenta sea bloqueada y cuando sea desbloqueada manualmente, sin revelar datos sensibles. El correo de un bloqueo automático incluye un enlace de desbloqueo; el de un bloqueo manual, no. |
| RF-07.13 | El sistema debe publicar `usuario.bloqueado` después de confirmar un bloqueo, y `usuario.desbloqueado` después de un desbloqueo explícito —por un administrador, con el enlace o restableciendo la contraseña—, indicando la vía. |
| RF-07.14 | Los bloqueos y desbloqueos deben registrarse mediante el catálogo de auditoría de SPEC-06, incluyendo actor, motivo, fecha, IP y resultado. |
| RF-07.15 | Una cuenta `INACTIVO` o `PENDIENTE_VERIFICACION` no debe entrar en estado `BLOQUEADO`; los módulos consumidores solo pueden leer el estado y no modificarlo. |
| RF-07.16 | El enlace de desbloqueo debe ser de un solo uso, expirar a los 30 minutos y quedar invalidado por cualquier bloqueo posterior. Consumirlo con `POST /api/v1/auth/desbloquear` levanta un bloqueo automático, tenga o no vencimiento. |
| RF-07.17 | Restablecer la contraseña (SPEC-03) debe levantar un bloqueo automático, tenga o no vencimiento. Ni el enlace ni el restablecimiento levantan un bloqueo manual. |

---

## Escenarios — ¿cómo verificamos?

### ESC-07.1 Registro de un intento fallido

- **Dado** un usuario activo que envía una contraseña incorrecta,
- **Cuando** SPEC-02 procesa el inicio de sesión,
- **Entonces** se registra un `intento_login` con resultado fallido, fecha, IP y agente de usuario, se incrementa el contador de intentos fallidos consecutivos y se responde `401 CREDENCIALES_INVALIDAS`.
### ESC-07.2 Bloqueo automático tras cinco fallos

- **Dado** una cuenta `ACTIVO` con 4 intentos fallidos consecutivos y ningún bloqueo previo seguido,
- **Cuando** ocurre un quinto intento fallido,
- **Entonces** la cuenta pasa a `BLOQUEADO`, `bloqueado_hasta` se establece a 1 minuto después, las sesiones abiertas **no** se cierran, se registra `CUENTA_BLOQUEADA`, se envía la notificación asíncrona con el enlace de desbloqueo y se publica `usuario.bloqueado`.
### ESC-07.3 Fallos distribuidos entre varias IP *(caso borde)*

- **Dado** que una misma cuenta recibe cinco intentos fallidos consecutivos desde cinco IP diferentes,
- **Cuando** llega el quinto fallo,
- **Entonces** la cuenta se bloquea igualmente, porque el conteo es por cuenta y no por dirección IP, y cada IP queda registrada en `intento_login`.
### ESC-07.4 Fallos separados en el tiempo *(caso borde)*

- **Dado** que una cuenta tuvo cuatro intentos fallidos hace varios días y ningún login correcto desde entonces,
- **Cuando** recibe un nuevo intento fallido,
- **Entonces** la cuenta se bloquea: el contador no caduca con el tiempo, y solo vuelve a cero por las causas de RF-07.5.
### ESC-07.5 Login correcto antes del quinto fallo

- **Dado** un usuario activo con cuatro intentos fallidos consecutivos,
- **Cuando** inicia sesión con la contraseña correcta antes de acumular el quinto fallo,
- **Entonces** el login se completa, el contador se reinicia a cero y la cuenta permanece `ACTIVO`.
### ESC-07.6 Login correcto durante un bloqueo automático *(caso borde)*

- **Dado** una cuenta `BLOQUEADO` con un bloqueo automático vigente,
- **Cuando** el usuario presenta credenciales correctas antes de `bloqueado_hasta`,
- **Entonces** el sistema responde `401 CREDENCIALES_INVALIDAS`, exactamente igual que con una contraseña incorrecta, no emite tokens y el intento no cuenta como fallo.
### ESC-07.7 Vencimiento de un bloqueo automático

- **Dado** una cuenta bloqueada automáticamente cuyo `bloqueado_hasta` ya pasó,
- **Cuando** se consulta su estado o el usuario inicia sesión con credenciales correctas,
- **Entonces** la cuenta figura `ACTIVO` en toda la API sin que ningún proceso la haya cambiado, el contador está a cero, el login emite la sesión y no se publica ningún evento.
### ESC-07.8 Bloqueo manual por administrador

- **Dado** un `ADMIN_SISTEMA` autenticado y una cuenta `ACTIVO`,
- **Cuando** hace `POST /api/v1/usuarios/{id}/bloquear` con un motivo válido,
- **Entonces** la cuenta pasa a `BLOQUEADO`, `bloqueado_hasta` queda en `null`, se revocan los tokens de refresco, se registra la acción, se notifica al usuario sin enlace de desbloqueo y se publica `usuario.bloqueado` con `hasta: null`.
### ESC-07.9 Bloqueo manual sin motivo *(caso borde)*

- **Dado** un `ADMIN_SISTEMA` autenticado,
- **Cuando** intenta bloquear una cuenta sin enviar `motivo` o enviando un motivo vacío,
- **Entonces** el sistema responde `400 VALIDACION`, no cambia el estado y no publica ningún evento.

### ESC-07.10 Bloqueo manual sobre bloqueo automático

- **Dado** una cuenta `BLOQUEADO` por bloqueo automático con tiempo restante,
- **Cuando** un administrador la bloquea manualmente con un motivo,
- **Entonces** el bloqueo manual reemplaza al automático, `bloqueado_hasta` pasa a `null` y la cuenta permanece bloqueada después del vencimiento original.

### ESC-07.11 Desbloqueo manual

- **Dado** una cuenta bloqueada y un `ADMIN_SISTEMA` autorizado,
- **Cuando** hace `POST /api/v1/usuarios/{id}/desbloquear`,
- **Entonces** la cuenta pasa a `ACTIVO`, el contador se reinicia, se registra el desbloqueo y se publica `usuario.desbloqueado` con `via: "ADMINISTRADOR"`.
### ESC-07.12 Desbloqueo por usuario sin privilegios *(caso borde)*

- **Dado** un usuario con rol `CLIENTE` o `VENDEDOR`,
- **Cuando** intenta desbloquear una cuenta mediante la API,
- **Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no cambia el estado y registra el acceso denegado mediante SPEC-06.

### ESC-07.13 Cuenta inexistente o inactiva *(caso borde)*

- **Dado** un administrador que intenta bloquear una cuenta inexistente o una cuenta `INACTIVO`,
- **Cuando** invoca el endpoint de bloqueo,
- **Entonces** el sistema responde `404 NO_ENCONTRADO` para el identificador inexistente o `403 CUENTA_NO_DISPONIBLE` para la cuenta no operativa, sin crear un bloqueo.

### ESC-07.14 Concurrencia en el quinto intento *(caso borde de concurrencia)*

- **Dado** una cuenta con cuatro intentos fallidos consecutivos,
- **Cuando** dos peticiones fallidas concurrentes llegan casi simultáneamente,
- **Entonces** la actualización se serializa de forma atómica: la cuenta se bloquea una sola vez, no se crean bloqueos duplicados y se publica un único `usuario.bloqueado`.
### ESC-07.15 Evento publicado después de confirmar el cambio

- **Dado** un bloqueo automático o manual válido,
- **Cuando** la transacción que cambia la cuenta se confirma,
- **Entonces** el evento se publica después de la confirmación; si la transacción falla, no se publica ningún `usuario.bloqueado` falso.

### ESC-07.16 Falla de auditoría en una acción crítica *(caso borde)*

- **Dado** que la auditoría no está disponible al ejecutar un bloqueo manual,
- **Cuando** el sistema intenta completar la operación crítica,
- **Entonces** revierte el cambio y responde `503 NO_DISPONIBLE`, conforme a la política de acciones críticas de SPEC-06.

### ESC-07.17 Repetir la misma contraseña incorrecta *(caso borde)*

- **Dado** un usuario activo con un intento fallido,
- **Cuando** vuelve a presentar exactamente la misma contraseña incorrecta,
- **Entonces** el intento se registra en `intento_login`, pero el contador no sube.

### ESC-07.18 Bloqueo sin vencimiento tras bloqueos seguidos *(caso borde)*

- **Dado** una cuenta que ya se bloqueó tres veces seguidas, sin ningún login correcto entre medias,
- **Cuando** acumula otros cinco intentos fallidos consecutivos,
- **Entonces** se bloquea con `bloqueado_hasta = null`: no vence sola, y solo se levanta con el enlace, restableciendo la contraseña o por un administrador.

### ESC-07.19 Desbloqueo con el enlace del correo

- **Dado** una cuenta con bloqueo automático y el enlace recibido por correo hace menos de 30 minutos,
- **Cuando** el titular hace `POST /api/v1/auth/desbloquear` con su token,
- **Entonces** la cuenta pasa a `ACTIVO`, el contador vuelve a cero, se registra `CUENTA_DESBLOQUEADA` y se publica `usuario.desbloqueado` con `via: "ENLACE"`.

### ESC-07.20 Enlace vencido o reemplazado *(caso borde)*

- **Dado** un enlace de desbloqueo de más de 30 minutos, o de un bloqueo al que siguió otro,
- **Cuando** el titular lo usa,
- **Entonces** el sistema responde `410 TOKEN_DESBLOQUEO_EXPIRADO` o `401 TOKEN_DESBLOQUEO_INVALIDO` y la cuenta sigue bloqueada.

### ESC-07.21 El enlace no levanta un bloqueo manual *(caso borde de seguridad)*

- **Dado** una cuenta con bloqueo automático cuyo titular recibió un enlace, y que después un administrador bloquea manualmente,
- **Cuando** el titular usa el enlace,
- **Entonces** el sistema responde `401 TOKEN_DESBLOQUEO_INVALIDO` y el bloqueo manual se mantiene.

### ESC-07.22 Restablecer la contraseña levanta el bloqueo automático

- **Dado** una cuenta con bloqueo automático, con o sin vencimiento,
- **Cuando** el titular restablece su contraseña mediante SPEC-03,
- **Entonces** el bloqueo se levanta, el contador vuelve a cero y se publica `usuario.desbloqueado` con `via: "RESTABLECIMIENTO"`. Si el bloqueo fuera manual, se mantendría.

### ESC-07.23 Las sesiones abiertas sobreviven a un bloqueo automático *(caso borde de seguridad)*

- **Dado** un titular con una sesión abierta en su móvil,
- **Cuando** un tercero provoca un bloqueo automático fallando cinco veces seguidas,
- **Entonces** el titular puede seguir renovando su sesión con `POST /api/v1/auth/refresh`; solo se rechazan los inicios de sesión nuevos.

### ESC-07.24 Autobloqueo o bloqueo del último administrador *(caso borde)*

- **Dado** un `ADMIN_SISTEMA` autenticado,
- **Cuando** intenta bloquearse a sí mismo, o bloquear al último `ADMIN_SISTEMA` activo,
- **Entonces** el sistema responde `422 ADMINISTRADOR_PROTEGIDO` y no cambia nada.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El contador y los intentos deben persistirse en PostgreSQL; no pueden vivir solo en memoria, porque el servicio puede ejecutarse en varias instancias.
- La actualización del contador y la transición a `BLOQUEADO` deben ser atómicas y resistentes a concurrencia.
- El login debe responder `401 CREDENCIALES_INVALIDAS` para cuentas bloqueadas, inactivas o pendientes de verificación, idéntico a una contraseña incorrecta y con la misma latencia.
- El historial de intentos debe conservarse al menos 90 días.
- La consulta del historial de intentos administrativo debe responder en menos de 500 ms en el percentil 95 para hasta 1000 registros de una cuenta.
- El bloqueo no debe depender de que los intentos provengan de la misma IP.
- Las notificaciones de bloqueo/desbloqueo deben ser asíncronas y no bloquear el camino crítico del login.
- Los eventos RabbitMQ deben publicarse al menos una vez, ser deduplicables mediante `eventoId` y seguir el catálogo de eventos existente.
- No se deben guardar contraseñas, códigos OTP ni tokens completos en los registros de auditoría.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733.

---

## Fuera de alcance — ¿qué NO hará?

- Detección de anomalías por geolocalización, dispositivo o aprendizaje automático.
- CAPTCHA como condición previa al bloqueo.
- Bloqueo global por dirección IP a nivel de firewall o WAF.
- Análisis de botnets o correlación avanzada entre cuentas.
- Desbloqueo por un cliente, vendedor o módulo consumidor sobre una cuenta ajena, o sobre un bloqueo manual. El titular sí puede levantar su propio bloqueo automático (RF-07.16 y RF-07.17).
- Bloqueo por combinación de cuenta e IP, o por ubicación conocida del titular. Queda propuesto como mejora para el Hito 4.
- Eliminación física de intentos o bloqueos fuera de la política de retención.
- Consulta y exportación de auditoría, que pertenece a SPEC-06.
- Emisión, rotación o validación ordinaria de tokens, que pertenece a SPEC-02 y SPEC-09.

---

## Impacto en el contrato

Esta spec utiliza los endpoints ya publicados y añade efectos de estado, auditoría
y eventos. Cualquier cambio de contrato lo aplica Sergio como Product Owner.

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/login` | Responde `401 CREDENCIALES_INVALIDAS` también con la cuenta bloqueada | ⬜ |
| `POST /api/v1/usuarios/{id}/bloquear` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/usuarios/{id}/desbloquear` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/auth/desbloquear` | Añade — desbloqueo por el titular con el enlace del correo | ⬜ |
| `usuario.bloqueado` | Publica según catálogo de eventos | ⬜ |
| `usuario.desbloqueado` | Publica según catálogo de eventos | ⬜ |
| `CUENTA_NO_DISPONIBLE` | Solo al intentar bloquear una cuenta no operativa; el login ya no lo devuelve | ⬜ |
| `TOKEN_DESBLOQUEO_INVALIDO` · `TOKEN_DESBLOQUEO_EXPIRADO` · `ADMINISTRADOR_PROTEGIDO` | Añade | ⬜ |
| `CUENTA_BLOQUEADA` / `CUENTA_DESBLOQUEADA` | Acciones del catálogo de auditoría de SPEC-06 | ⬜ |

Los módulos consumidores no modifican estados de cuenta. Solo reciben los eventos
o consultan el estado mediante la API de identidad de SPEC-09.

---

## Lista de completitud

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en `specs/openapi.yaml`
- [ ] Los errores usan `specs/catalogo-errores.md` y no permiten enumerar cuentas
- [ ] Los eventos y sus payloads coinciden con `specs/catalogo-eventos.md`
- [ ] Las acciones de bloqueo y desbloqueo están cubiertas por SPEC-06
- [ ] Las transiciones coinciden con `docs/arquitectura/estados-usuario.md`
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
