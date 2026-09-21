# SPEC-15 — Bloqueo y desbloqueo por un administrador

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 5 (Sem. 14) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-07 «Bloqueo y desbloqueo de cuentas» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El bloqueo automático de SPEC-14 reacciona a un patrón de fallos, pero hay situaciones que solo detecta una persona: una cuenta de vendedor que opera fuera de horario, un reclamo de suplantación, una alerta de otro módulo. Para esos casos un `ADMIN_SISTEMA` necesita cortar el acceso de inmediato, dejar constancia del motivo y decidir él cuándo se restituye.

Como los otros módulos no pueden modificar la entidad `usuario`, todas las decisiones de bloqueo y sus efectos se resuelven en este módulo.

---

## Propósito — ¿para qué?

Permitir que un `ADMIN_SISTEMA` bloquee una cuenta con un motivo y la desbloquee cuando corresponda, y que el detalle de la cuenta muestre por qué y hasta cuándo está bloqueada.

El resultado observable es una cuenta `BLOQUEADO` sin vencimiento, con todas sus sesiones cerradas, que ni el enlace del correo ni el restablecimiento de contraseña pueden levantar; y su vuelta a `ACTIVO` cuando un administrador la desbloquea.

---

## Alcance — ¿hasta dónde?

- Bloqueo manual con `POST /api/v1/usuarios/{id}/bloquear` y motivo obligatorio.
- Reemplazo de cualquier bloqueo automático vigente.
- Cierre de las sesiones abiertas de la cuenta.
- Desbloqueo con `POST /api/v1/usuarios/{id}/desbloquear`.
- Protección del propio administrador y del último `ADMIN_SISTEMA` activo.
- Objeto `bloqueo` en el detalle de la cuenta.
- Notificación, eventos y auditoría con las mismas reglas de SPEC-14.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-15.1 | Un `ADMIN_SISTEMA` debe poder bloquear manualmente una cuenta activa indicando obligatoriamente un motivo. Nadie puede bloquearse a sí mismo, y no se puede bloquear al último `ADMIN_SISTEMA` activo (`422 ADMINISTRADOR_PROTEGIDO`, la misma regla que aplican SPEC-04 a la baja y SPEC-11 al revocar el rol). |
| RF-15.2 | El bloqueo manual no debe tener vencimiento automático y debe reemplazar cualquier bloqueo automático vigente. |
| RF-15.3 | Un `ADMIN_SISTEMA` debe poder desbloquear manualmente una cuenta bloqueada, reiniciando el contador de fallos. |
| RF-15.4 | Solo el bloqueo manual debe cerrar las sesiones abiertas de la cuenta, revocando sus tokens de refresco. El bloqueo automático no las cierra: las sesiones abiertas antes del bloqueo pueden seguir renovándose, y solo se impide iniciar sesiones nuevas. |
| RF-15.5 | Una cuenta `INACTIVO` o `PENDIENTE_VERIFICACION` no debe entrar en estado `BLOQUEADO`; los módulos consumidores solo pueden leer el estado y no modificarlo. |
| RF-15.6 | Mientras la cuenta está `BLOQUEADO`, `GET /api/v1/usuarios/{id}` debe incluir el objeto `bloqueo` con `tipo` (`AUTOMATICO` o `MANUAL`) y `hasta` (`null` si no vence). El `motivo` del bloqueo manual solo se incluye para un token de usuario con el permiso `usuario.ver`; un token de servicio nunca lo recibe. |
| RF-15.7 | El bloqueo y el desbloqueo manual deben notificar al titular por correo —sin enlace de desbloqueo—, publicar `usuario.bloqueado` o `usuario.desbloqueado` con `via: "ADMINISTRADOR"` y registrarse en SPEC-12, con las mismas reglas de RF-14.8 a RF-14.10. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-15.1 Bloqueo manual por administrador

- **Dado** un `ADMIN_SISTEMA` autenticado y una cuenta `ACTIVO`,
- **Cuando** hace `POST /api/v1/usuarios/{id}/bloquear` con un motivo válido,
- **Entonces** la cuenta pasa a `BLOQUEADO`, `bloqueado_hasta` queda en `null`, se revocan los tokens de refresco, se registra la acción, se notifica al usuario sin enlace de desbloqueo y se publica `usuario.bloqueado` con `hasta: null`.

### ESC-15.2 Bloqueo manual sin motivo *(caso borde)*

- **Dado** un `ADMIN_SISTEMA` autenticado,
- **Cuando** intenta bloquear una cuenta sin enviar `motivo` o enviando un motivo vacío,
- **Entonces** el sistema responde `400 VALIDACION`, no cambia el estado y no publica ningún evento.

### ESC-15.3 Bloqueo manual sobre bloqueo automático

- **Dado** una cuenta `BLOQUEADO` por bloqueo automático con tiempo restante,
- **Cuando** un administrador la bloquea manualmente con un motivo,
- **Entonces** el bloqueo manual reemplaza al automático, `bloqueado_hasta` pasa a `null` y la cuenta permanece bloqueada después del vencimiento original.

### ESC-15.4 Desbloqueo manual

- **Dado** una cuenta bloqueada y un `ADMIN_SISTEMA` autorizado,
- **Cuando** hace `POST /api/v1/usuarios/{id}/desbloquear`,
- **Entonces** la cuenta pasa a `ACTIVO`, el contador se reinicia, se registra el desbloqueo y se publica `usuario.desbloqueado` con `via: "ADMINISTRADOR"`.

### ESC-15.5 Desbloqueo por usuario sin privilegios *(caso borde)*

- **Dado** un usuario con rol `CLIENTE` o `VENDEDOR`,
- **Cuando** intenta desbloquear una cuenta mediante la API,
- **Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no cambia el estado y registra el acceso denegado mediante SPEC-12.

### ESC-15.6 Cuenta inexistente o inactiva *(caso borde)*

- **Dado** un administrador que intenta bloquear una cuenta inexistente o una cuenta `INACTIVO`,
- **Cuando** invoca el endpoint de bloqueo,
- **Entonces** el sistema responde `404 NO_ENCONTRADO` para el identificador inexistente o `403 CUENTA_NO_DISPONIBLE` para la cuenta no operativa, sin crear un bloqueo.

### ESC-15.7 Falla de auditoría en una acción crítica *(caso borde)*

- **Dado** que la auditoría no está disponible al ejecutar un bloqueo manual,
- **Cuando** el sistema intenta completar la operación crítica,
- **Entonces** revierte el cambio y responde `503 NO_DISPONIBLE`, conforme a la política de acciones críticas de SPEC-12.

### ESC-15.8 Autobloqueo o bloqueo del último administrador *(caso borde)*

- **Dado** un `ADMIN_SISTEMA` autenticado,
- **Cuando** intenta bloquearse a sí mismo, o bloquear al último `ADMIN_SISTEMA` activo,
- **Entonces** el sistema responde `422 ADMINISTRADOR_PROTEGIDO` y no cambia nada.

### ESC-15.9 El detalle de una cuenta bloqueada muestra el bloqueo *(caso borde de privacidad)*

- **Dado** una cuenta con bloqueo manual y motivo «Actividad sospechosa»,
- **Cuando** un `ADMIN_SISTEMA` consulta `GET /api/v1/usuarios/{id}` y, por separado, un módulo lo consulta con un token de servicio,
- **Entonces** el administrador recibe `bloqueo: { tipo: MANUAL, hasta: null, motivo: "Actividad sospechosa" }` y el módulo recibe `bloqueo: { tipo: MANUAL, hasta: null }`, sin el motivo.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El bloqueo manual y la revocación de las sesiones deben ocurrir en la misma transacción.
- El bloqueo manual es una acción **crítica** de auditoría: si no se puede auditar, se revierte (SPEC-12).
- El `motivo` nunca se entrega a un token de servicio.
- Los eventos se publican solo después de confirmar la transacción.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El bloqueo automático, su vencimiento y el desbloqueo por el titular, que son de SPEC-14.
- Desbloqueo por un cliente, vendedor o módulo consumidor sobre una cuenta ajena, o sobre un bloqueo manual.
- Bloqueos temporales con fecha de fin elegida por el administrador.
- La consulta del historial de bloqueos en la auditoría, que es de SPEC-13.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/usuarios/{id}/bloquear` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/usuarios/{id}/desbloquear` | Utiliza contrato existente | ⬜ |
| `GET /api/v1/usuarios/{id}` | Amplía: objeto `bloqueo` con `tipo`, `hasta` y, solo para administradores, `motivo` (RF-15.6) | ⬜ |
| `CUENTA_NO_DISPONIBLE` | Solo al intentar bloquear una cuenta no operativa | ⬜ |
| `ADMINISTRADOR_PROTEGIDO` | Añade | ⬜ |
| `usuario.bloqueado` · `usuario.desbloqueado` | Publica según catálogo de eventos | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
