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

El resultado observable es que una cuenta bloqueada no puede iniciar sesión ni
renovar su sesión hasta que venza el bloqueo automático o un administrador lo
levante.

---

## Alcance — ¿hasta dónde?

- Registro persistente de intentos de login fallidos.
- Evaluación de una ventana de 15 minutos por cuenta.
- Bloqueo automático al quinto fallo consecutivo.
- Vencimiento automático después de 30 minutos.
- Reinicio del contador después de un login exitoso.
- Bloqueo manual por `ADMIN_SISTEMA` con motivo obligatorio.
- Desbloqueo manual por `ADMIN_SISTEMA`.
- Revocación de sesiones de refresco cuando corresponde.
- Notificación asíncrona por correo.
- Publicación de `usuario.bloqueado` y `usuario.desbloqueado`.
- Registro de los hechos mediante SPEC-06.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-07.1 | El sistema debe registrar cada intento fallido con usuario o correo intentado, fecha, IP y agente de usuario. |
| RF-07.2 | El sistema debe contar los intentos fallidos por cuenta dentro de una ventana móvil de 15 minutos, sin depender de que provengan de la misma IP. |
| RF-07.3 | El sistema debe bloquear automáticamente una cuenta activa después de 5 intentos fallidos consecutivos dentro de la ventana de 15 minutos. |
| RF-07.4 | El bloqueo automático debe establecer el estado `BLOQUEADO`, durar 30 minutos y establecer `bloqueado_hasta` con la fecha de vencimiento. |
| RF-07.5 | Un inicio de sesión exitoso debe reiniciar el contador de intentos fallidos de la cuenta. |
| RF-07.6 | Mientras una cuenta esté bloqueada, los intentos de login no deben emitir tokens, aunque la contraseña presentada sea correcta. |
| RF-07.7 | Al vencer un bloqueo automático, el siguiente intento válido debe reactivar la cuenta, reiniciar el contador y permitir el login. |
| RF-07.8 | Un `ADMIN_SISTEMA` debe poder bloquear manualmente una cuenta activa indicando obligatoriamente un motivo. |
| RF-07.9 | El bloqueo manual no debe tener vencimiento automático y debe reemplazar cualquier bloqueo automático vigente. |
| RF-07.10 | Un `ADMIN_SISTEMA` debe poder desbloquear manualmente una cuenta bloqueada, reiniciando el contador de fallos. |
| RF-07.11 | Un bloqueo o desbloqueo debe revocar los refresh tokens activos de la cuenta afectada. |
| RF-07.12 | El sistema debe notificar por correo al usuario cuando su cuenta sea bloqueada y cuando sea desbloqueada manualmente, sin revelar datos sensibles. |
| RF-07.13 | El sistema debe publicar `usuario.bloqueado` después de confirmar un bloqueo y `usuario.desbloqueado` después de confirmar un desbloqueo. |
| RF-07.14 | Los bloqueos y desbloqueos deben registrarse mediante el catálogo de auditoría de SPEC-06, incluyendo actor, motivo, fecha, IP y resultado. |
| RF-07.15 | Una cuenta `INACTIVO` o `PENDIENTE_VERIFICACION` no debe entrar en estado `BLOQUEADO`; los módulos consumidores solo pueden leer el estado y no modificarlo. |

---

## Escenarios — ¿cómo verificamos?

### ESC-07.1 Registro de un intento fallido

- **Dado** un usuario activo que envía una contraseña incorrecta,
- **Cuando** SPEC-02 procesa el inicio de sesión,
- **Entonces** se registra un `intento_login` con resultado fallido, fecha, IP y agente de usuario, se incrementa el contador de la ventana vigente y se responde `401 CREDENCIALES_INVALIDAS`.

### ESC-07.2 Bloqueo automático tras cinco fallos

- **Dado** una cuenta `ACTIVO` con 4 intentos fallidos dentro de los últimos 15 minutos,
- **Cuando** ocurre un quinto intento fallido,
- **Entonces** la cuenta pasa a `BLOQUEADO`, `bloqueado_hasta` se establece a 30 minutos después, se revocan sus refresh tokens, se registra `CUENTA_BLOQUEADA`, se envía la notificación asíncrona y se publica `usuario.bloqueado`.

### ESC-07.3 Fallos distribuidos entre varias IP *(caso borde)*

- **Dado** que una misma cuenta recibe cinco intentos fallidos desde cinco IP diferentes dentro de 15 minutos,
- **Cuando** llega el quinto fallo,
- **Entonces** la cuenta se bloquea igualmente, porque el conteo es por cuenta y no por dirección IP, y cada IP queda registrada en `intento_login`.

### ESC-07.4 Intentos fuera de la ventana *(caso borde)*

- **Dado** que una cuenta tuvo cuatro fallos hace más de 15 minutos y no tiene fallos recientes,
- **Cuando** recibe un nuevo intento fallido,
- **Entonces** no se bloquea, el contador vigente queda en 1 y los intentos antiguos no participan en la ventana actual.

### ESC-07.5 Login correcto antes del quinto fallo

- **Dado** un usuario activo con cuatro intentos fallidos recientes,
- **Cuando** inicia sesión con la contraseña correcta antes de acumular el quinto fallo,
- **Entonces** el login se completa, el contador se reinicia a cero y la cuenta permanece `ACTIVO`.

### ESC-07.6 Login correcto durante un bloqueo automático *(caso borde)*

- **Dado** una cuenta `BLOQUEADO` con un bloqueo automático vigente,
- **Cuando** el usuario presenta credenciales correctas antes de `bloqueado_hasta`,
- **Entonces** el sistema responde `403 CUENTA_NO_DISPONIBLE`, no confirma si la contraseña era correcta y no emite tokens.

### ESC-07.7 Desbloqueo automático por vencimiento

- **Dado** una cuenta bloqueada automáticamente cuyo `bloqueado_hasta` ya venció,
- **Cuando** el usuario inicia sesión con credenciales correctas,
- **Entonces** el sistema cambia el estado a `ACTIVO`, reinicia el contador, registra `CUENTA_DESBLOQUEADA`, publica `usuario.desbloqueado` y emite la sesión.

### ESC-07.8 Bloqueo manual por administrador

- **Dado** un `ADMIN_SISTEMA` autenticado y una cuenta `ACTIVO`,
- **Cuando** hace `POST /api/v1/usuarios/{id}/bloquear` con un motivo válido,
- **Entonces** la cuenta pasa a `BLOQUEADO`, `bloqueado_hasta` queda en `null`, se revocan los refresh tokens, se registra la acción, se notifica al usuario y se publica `usuario.bloqueado` con `hasta: null`.

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
- **Entonces** la cuenta pasa a `ACTIVO`, el contador se reinicia, se revocan los tokens afectados, se registra el desbloqueo y se publica `usuario.desbloqueado`.

### ESC-07.12 Desbloqueo por usuario sin privilegios *(caso borde)*

- **Dado** un usuario con rol `CLIENTE` o `VENDEDOR`,
- **Cuando** intenta desbloquear una cuenta mediante la API,
- **Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no cambia el estado y registra el acceso denegado mediante SPEC-06.

### ESC-07.13 Cuenta inexistente o inactiva *(caso borde)*

- **Dado** un administrador que intenta bloquear una cuenta inexistente o una cuenta `INACTIVO`,
- **Cuando** invoca el endpoint de bloqueo,
- **Entonces** el sistema responde `404 NO_ENCONTRADO` para el identificador inexistente o `403 CUENTA_NO_DISPONIBLE` para la cuenta no operativa, sin crear un bloqueo.

### ESC-07.14 Concurrencia en el quinto intento *(caso borde de concurrencia)*

- **Dado** una cuenta con cuatro fallos recientes,
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

---

## Requisitos no funcionales — ¿con qué condiciones?

- El contador y los intentos deben persistirse en PostgreSQL; no pueden vivir solo en memoria, porque el servicio puede ejecutarse en varias instancias.
- La actualización del contador y la transición a `BLOQUEADO` deben ser atómicas y resistentes a concurrencia.
- El login debe responder con `403 CUENTA_NO_DISPONIBLE` para cuentas bloqueadas, inactivas o pendientes de verificación, sin permitir enumerar el motivo exacto.
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
- Desbloqueo realizado por un cliente, vendedor o módulo consumidor.
- Eliminación física de intentos o bloqueos fuera de la política de retención.
- Consulta y exportación de auditoría, que pertenece a SPEC-06.
- Emisión, rotación o validación ordinaria de tokens, que pertenece a SPEC-02 y SPEC-09.

---

## Impacto en el contrato

Esta spec utiliza los endpoints ya publicados y añade efectos de estado, auditoría
y eventos. Cualquier cambio de contrato lo aplica Sergio como Product Owner.

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/login` | Utiliza contrato existente para fallos y cuentas no disponibles | ⬜ |
| `POST /api/v1/usuarios/{id}/bloquear` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/usuarios/{id}/desbloquear` | Utiliza contrato existente | ⬜ |
| `usuario.bloqueado` | Publica según catálogo de eventos | ⬜ |
| `usuario.desbloqueado` | Publica según catálogo de eventos | ⬜ |
| `CUENTA_NO_DISPONIBLE` | Utiliza código publicado; no usar `423 Locked` | ⬜ |
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
