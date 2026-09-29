# SPEC-12 — Registro de auditoría de eventos de seguridad

| Campo | Valor |
|---|---|
| **Responsable** | Christian Gabriel Arancivia Salas |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-06 «Auditoría y trazabilidad» el 20 de septiembre, por indicación del profesor: una spec por función. Conserva el registro —catálogo, formato, inmutabilidad y retención—; la consulta y la exportación pasaron a SPEC-13, que ya tenía un hito distinto (Hito 5). La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Este módulo concentra las decisiones de acceso de todo el marketplace. Cuando alguien anula un pedido que no debía, cuando una cuenta de vendedor aparece con un rol que nadie recuerda haber concedido, o cuando un cliente afirma que él no cambió su contraseña, la pregunta es siempre la misma: **quién hizo qué, cuándo y desde dónde.**

Los eventos sensibles los producen **casi todas** las specs del módulo: el registro, el inicio de sesión, el cambio de rol, el bloqueo, la consulta de un módulo consumidor. Si cada una definiera su propio formato de bitácora, reconstruir «todo lo que le pasó a esta cuenta» obligaría a unir formatos distintos. Esta spec existe para que haya **un solo formato y una sola tabla**.

Además, el registro de accesos a datos personales no es opcional: la Ley N.º 29733 obliga al titular del banco de datos a poder acreditar quién accedió a ellos. SPEC-18 (RF-18.6) exige registrar cada acceso de un módulo consumidor, y ese requisito escribe en la tabla que define esta spec.

---

## Propósito — ¿para qué?

Registrar de forma centralizada e inmutable todo evento relevante para la seguridad del módulo, con un formato único y sin secretos.

El resultado observable es una tabla `auditoria_seguridad` que ninguna operación sensible puede esquivar ni alterar, y que conserva los hechos 90 días.

---

## Alcance — ¿hasta dónde?

Definición del catálogo de acciones auditables y del formato único de registro;
escritura del registro desde todas las specs del módulo; inmutabilidad; política
ante fallos de escritura; y la retención de 90 días.

### Las tres clases de evento que se registran

| Clase | Ejemplos | Quién los produce |
|---|---|---|
| **Autenticación** | Inicio de sesión correcto o fallido, verificación de OTP, cierre de sesión, reutilización de un token de refresco | SPEC-05, SPEC-06, SPEC-09 |
| **Ciclo de vida y privilegios** | Alta, verificación, baja, cambio y restablecimiento de contraseña, bloqueo, desbloqueo, asignación o revocación de rol, cambio de atributos | SPEC-01 a SPEC-04, SPEC-07, SPEC-08, SPEC-10, SPEC-11, SPEC-14 a SPEC-16 |
| **Acceso entre módulos** | Consulta de un usuario por otro módulo, entrega del documento en claro, intento con scope insuficiente | SPEC-17, SPEC-18 |

### Qué contiene un registro

| Campo | Contenido | Obligatorio |
|---|---|---|
| `id` | Identificador del registro | Sí |
| `fecha` | Instante del evento, en UTC y con milisegundos | Sí |
| `accion` | Código del catálogo de acciones (ver *Requisitos*) | Sí |
| `resultado` | `EXITO` o `FALLO` | Sí |
| `actorTipo` | `USUARIO`, `MODULO` o `SISTEMA` | Sí |
| `actorId` | Identificador del usuario, o el `client_id` del módulo | No — ausente si el actor no llegó a identificarse |
| `objetivoUsuarioId` | La cuenta afectada, cuando el evento recae sobre una cuenta | No |
| `ip` | Dirección de origen de la petición | No — ausente en eventos generados por tareas internas |
| `agente` | Cabecera `User-Agent` truncada a 255 caracteres | No |
| `detalle` | Objeto JSON con contexto específico de la acción | No |

**Un registro de auditoría no guarda el dato, guarda el hecho.** En un cambio de
contraseña se registra que ocurrió, no la contraseña; en una consulta de
documento se registra que se entregó en claro y a qué módulo, no el número.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-12.1 | El sistema debe registrar en `auditoria_seguridad` todo evento del catálogo de acciones auditables, con los campos obligatorios del formato único. |
| RF-12.2 | El catálogo de acciones auditables debe ser cerrado y versionado: una spec que necesite auditar algo nuevo añade su código al catálogo mediante acuerdo con el PO, y no inventa uno en su implementación. |
| RF-12.3 | El sistema debe registrar tanto los intentos exitosos como los fallidos, distinguiéndolos con el campo `resultado`. Un ataque se reconstruye con los fallos, no con los éxitos. |
| RF-12.4 | Los registros de auditoría deben ser **de solo anexado**: ninguna operación de la API puede modificarlos ni eliminarlos, y el usuario de base de datos de la aplicación no debe tener privilegios de `UPDATE` ni `DELETE` sobre la tabla. |
| RF-12.5 | El registro de auditoría no debe contener nunca contraseñas, hashes de contraseña, códigos OTP, tokens completos ni el número de documento en claro. Cuando haga falta referenciar un token se registra su `jti`. |
| RF-12.6 | El fallo al escribir un registro de auditoría no debe impedir que la operación auditada se complete, salvo en las acciones marcadas como **críticas** en el catálogo, donde la operación debe revertirse si no se pudo auditar. |
| RF-12.7 | El sistema debe conservar los registros al menos **90 días** y eliminar automáticamente los anteriores mediante una tarea programada, dejando constancia de la purga como un registro más. |

### Catálogo de acciones auditables

Cerrado y versionado, según RF-12.2. Las marcadas como **crítica** revierten la
operación si no se pudieron auditar (RF-12.6).

| Código | Origen | Crítica |
|---|---|---|
| `SESION_INICIADA` | SPEC-05 | No |
| `SESION_FALLIDA` | SPEC-05 | No |
| `SESION_CERRADA` | SPEC-06 | No |
| `REFRESCO_REUTILIZADO` | SPEC-06 | No |
| `OTP_SOLICITADO` | SPEC-09 | No |
| `OTP_VERIFICADO` | SPEC-09 | No |
| `OTP_FALLIDO` | SPEC-09 | No |
| `MFA_ACTIVADO` / `MFA_DESACTIVADO` | SPEC-10 | Sí |
| `USUARIO_CREADO` | SPEC-01, SPEC-03 | Sí |
| `USUARIO_VERIFICADO` | SPEC-02 | No |
| `USUARIO_DESACTIVADO` | SPEC-04 | Sí |
| `USUARIO_REACTIVADO` | SPEC-04 | Sí |
| `CONTRASENA_CAMBIADA` | SPEC-07 | Sí |
| `CONTRASENA_RESTABLECIDA` | SPEC-08 | Sí |
| `RECUPERACION_SOLICITADA` | SPEC-08 | No |
| `ROL_ASIGNADO` / `ROL_REVOCADO` | SPEC-11 | Sí |
| `CUENTA_BLOQUEADA` / `CUENTA_DESBLOQUEADA` | SPEC-14, SPEC-15 | Sí |
| `ATRIBUTOS_ACTUALIZADOS` | SPEC-16 | No |
| `CORREO_CAMBIADO` | SPEC-16 | Sí |
| `MODULO_CONSULTO_USUARIO` | SPEC-18 | No |
| `MODULO_OBTUVO_DOCUMENTO` | SPEC-18 | Sí |
| `ACCESO_DENEGADO` | SPEC-17 y cualquier endpoint protegido | No |
| `AUDITORIA_CONSULTADA` / `AUDITORIA_EXPORTADA` | SPEC-13 | No |
| `AUDITORIA_PURGADA` | SPEC-12 | No |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-12.1 Un evento sensible deja rastro

- **Dado** que un `ADMIN_SISTEMA` autenticado asigna el rol `VENDEDOR` a una cuenta,
- **Cuando** la asignación se completa,
- **Entonces** existe un registro con `accion: "ROL_ASIGNADO"`, `resultado: "EXITO"`, el identificador del administrador como `actorId`, el de la cuenta afectada como `objetivoUsuarioId`, la IP de origen y el rol concedido en `detalle`.

### ESC-12.2 Los intentos fallidos también se registran

- **Dado** que alguien intenta iniciar sesión cinco veces con la contraseña equivocada,
- **Cuando** ocurre cada intento,
- **Entonces** se registran cinco `SESION_FALLIDA` con `resultado: "FALLO"` y sus IP respectivas, y un sexto registro `CUENTA_BLOQUEADA` producido por SPEC-14, de modo que la secuencia completa del ataque queda reconstruible.

### ESC-12.3 La auditoría no guarda el secreto *(caso borde)*

- **Dado** un cambio de contraseña, una verificación de OTP y una consulta del documento de un cliente,
- **Cuando** se inspecciona la tabla `auditoria_seguridad` tras las tres operaciones,
- **Entonces** ningún registro contiene la contraseña ni su hash, ni el código OTP, ni el número de documento en claro: el de contraseña guarda solo el hecho, el de OTP guarda el identificador del desafío, y el de documento guarda `{ "entregado": "EN_CLARO", "modulo": "despacho" }`.

### ESC-12.4 Un registro no se puede alterar *(caso borde de seguridad)*

- **Dado** un registro de auditoría ya escrito,
- **Cuando** se intenta modificarlo o eliminarlo a través de cualquier endpoint de la API, o mediante `UPDATE` con el usuario de base de datos de la aplicación,
- **Entonces** no existe endpoint que lo permita y la sentencia falla por falta de privilegios: la tabla es de solo anexado.

### ESC-12.5 El acceso de un módulo consumidor queda trazado

- **Dado** que el módulo de Despacho consulta `GET /usuarios/{id}` con su token de servicio y scope `usuarios:leer:documento`,
- **Cuando** el sistema le entrega el documento en claro,
- **Entonces** se escriben dos registros: `MODULO_CONSULTO_USUARIO` y `MODULO_OBTUVO_DOCUMENTO`, ambos con `actorTipo: "MODULO"` y el `client_id` de Despacho como `actorId`, satisfaciendo RF-18.6.

### ESC-12.6 La auditoría no bloquea la operación *(caso borde)*

- **Dado** que la tabla de auditoría no está disponible momentáneamente,
- **Cuando** un usuario inicia sesión correctamente,
- **Entonces** el inicio de sesión se completa y el registro `SESION_INICIADA` se reintenta de forma asíncrona; pero si lo que falla es auditar un `ROL_ASIGNADO`, que está marcado como crítico, la asignación se revierte y responde `503`.

### ESC-12.7 Purga por retención *(caso borde)*

- **Dado** un registro con más de 90 días de antigüedad,
- **Cuando** se ejecuta la tarea programada de retención,
- **Entonces** el registro se elimina y queda un `AUDITORIA_PURGADA` indicando cuántos registros se purgaron y hasta qué fecha, de modo que la desaparición de datos antiguos sea ella misma trazable.

---

## Requisitos no funcionales — ¿con qué condiciones?

- **La escritura del registro no puede degradar el camino crítico.** El inicio de sesión debe seguir respondiendo en menos de 800 ms en el percentil 95 con 50 usuarios concurrentes (SPEC-05) **con la auditoría activada**. Si la escritura síncrona no lo permite, se escribe de forma asíncrona con cola en memoria y reintento.
- La tabla debe soportar el volumen de escritura del módulo entero: en el peor caso previsto, un evento por petición de autenticación más uno por cada consulta de módulo consumidor.
- Ningún registro puede contener contraseñas, códigos OTP, tokens completos ni el número de documento en claro (RF-12.5).
- Las fechas se almacenan en **UTC**; la conversión a hora local es responsabilidad de la interfaz.
- La retención de 90 días es el plazo acordado por el equipo y debe poder ampliarse por configuración sin desplegar código.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- **La consulta, la exportación y el historial propio**, que son de SPEC-13.
- **La detección automática de comportamiento anómalo.** Esta spec registra hechos; interpretarlos es trabajo humano.
- **El bloqueo de cuentas**, que es de SPEC-14 y SPEC-15. Aquí se registra que un bloqueo ocurrió, no se decide cuándo ocurre.
- **La bitácora técnica de la aplicación** (`stdout`, trazas, errores de infraestructura). No comparten formato ni destino ni retención.
- **El envío de la auditoría a un SIEM externo** o a un almacenamiento inmutable de terceros.
- **La auditoría de los módulos consumidores sobre sus propios datos.**

---

## Decisiones de diseño registradas

**Por qué es una spec propia y no parte del bloqueo.** El bloqueo es una reacción
automática sobre *una* cuenta ante un patrón de fallos; la auditoría es un
servicio transversal que consumen casi todas las specs. Separarlas da al
catálogo de acciones un único dueño al que acudir cuando una spec necesita
auditar algo nuevo.

**Solo anexado, y sin privilegios de borrado para la aplicación.** Una auditoría
que la propia aplicación puede reescribir no prueba nada. Por eso RF-12.4 no es
una regla de código sino de permisos de base de datos. La única eliminación
admitida es la purga por retención, que corre con otro usuario y deja constancia.

**Fallar la operación solo en las acciones críticas.** Si auditar fuese siempre
obligatorio, una caída de la tabla de auditoría tumbaría el inicio de sesión de
todo el marketplace. Si no lo fuese nunca, un atacante que sature la auditoría
podría cambiar roles sin dejar rastro. El catálogo distingue las dos listas y
RF-12.6 aplica la regla que corresponde a cada una.

---

## Impacto en el contrato

Esta spec **no añade endpoints**: la tabla se escribe desde las demás specs y se
lee con los endpoints de SPEC-13.

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| Tabla `auditoria_seguridad` y catálogo de acciones | Añade (interno, no forma parte del contrato público) | ⬜ |

No publica ningún evento nuevo en RabbitMQ: la auditoría es consumidora de lo
que ocurre, no productora.

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Los 7 requisitos están implementados
- [ ] Las acciones del catálogo escriben su registro desde la spec que las origina
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Se ha verificado con una prueba de carga que el inicio de sesión sigue bajo 800 ms con la auditoría activa
- [ ] El usuario de base de datos de la aplicación no tiene `UPDATE` ni `DELETE` sobre `auditoria_seguridad`
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
