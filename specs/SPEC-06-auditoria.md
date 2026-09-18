# SPEC-06 — Auditoría y trazabilidad de eventos de seguridad

| Campo | Valor |
|---|---|
| **Responsable** | Christian Gabriel Arancivia Salas |
| **Hito objetivo** | Hito 3 (Sem. 8) — registro · Hito 5 (Sem. 14) — consulta y exportación |
| **Estado** | Borrador — pendiente de revisión del equipo |
| **Aprobada por** | — |

> **Origen de esta spec.** La propuesta de reorganización del backlog que
> circuló el 13 de septiembre detectó que la auditoría no tenía dueño: aparecía
> seis veces como frase suelta en los no funcionales de SPEC-01, SPEC-05,
> SPEC-07 y SPEC-08 —«queda registrada en `auditoria_seguridad`»— sin que
> ninguna spec definiera qué se registra, quién puede leerlo ni cómo se exporta.
> El hallazgo se adoptó; la decisión de levantarlo como spec propia en vez de
> fundirlo con SPEC-07 está justificada en *Decisiones de diseño registradas*.
>
> **Por qué ocupa el número 06.** Ese número lo dejó libre la fusión de la
> política de contraseñas dentro de SPEC-03, hecha por indicación del profesor.
> Renumerar el mismo día en que se reparten las specs, y antes de que nadie haya
> escrito la suya, era más barato que dejar un hueco permanente en un set que
> vive hasta la semana 16. SPEC-09 no se tocó: está publicada y seis equipos
> programan contra ella.

---

## Contexto — ¿por qué?

Este módulo concentra las decisiones de acceso de todo el marketplace. Cuando
alguien anula un pedido que no debía, cuando una cuenta de vendedor aparece con
un rol que nadie recuerda haber concedido, o cuando un cliente afirma que él no
cambió su contraseña, la pregunta es siempre la misma: **quién hizo qué, cuándo
y desde dónde.** Sin un registro que lo responda, la respuesta es una
conversación de memoria entre siete equipos.

El problema no es solo forense. Tres piezas ya construidas dependen de que este
registro exista y hoy se apoyan en el vacío:

- **SPEC-09 (RF-09.14)** exige registrar cada acceso de un módulo consumidor con
  el módulo solicitante, el endpoint, el identificador consultado y si el dato
  sensible se entregó en claro o enmascarado. Ese requisito escribe en una tabla
  que nadie especificó.
- **SPEC-09 (ESC-09.4 y RF-09.12)** exige registrar como evento de seguridad el
  intento de un módulo de suplantar a una persona y el acceso con scope
  insuficiente.
- **El wireframe 8** del Hito 1 —*Panel admin: detalle de usuario, roles,
  bloqueos e historial de accesos*— dibuja una pantalla que ninguna spec
  respaldaba.

Además, el registro de accesos a datos personales no es opcional: la Ley N.º
29733 obliga al titular del banco de datos a poder acreditar quién accedió a
ellos y con qué finalidad. El número de documento de nuestros clientes es
exactamente ese tipo de dato.

Por último, hay un motivo de diseño interno. Los eventos sensibles los producen
**todas** las specs del módulo: el registro los genera, el login los genera, el
cambio de rol los genera. Si cada una define su propio formato de bitácora,
consultar «todo lo que le pasó a esta cuenta» obliga a unir seis formatos
distintos. Esta spec existe para que haya **un solo formato y una sola tabla**.

---

## Propósito — ¿para qué?

Registrar de forma centralizada, inmutable y consultable todo evento relevante
para la seguridad del módulo, de modo que un administrador pueda reconstruir qué
le ocurrió a una cuenta, qué hizo un administrador con sus privilegios y qué
datos personales consultó cada módulo consumidor.

El resultado observable es doble: una tabla que ninguna operación sensible puede
esquivar, y una consulta filtrable y exportable que convierte esa tabla en una
respuesta cuando alguien pregunta qué pasó.

---

## Alcance — ¿hasta dónde?

Definición del catálogo de acciones auditables y del formato único de registro;
escritura del registro desde todas las specs del módulo; consulta filtrada y
paginada por un administrador; exportación del resultado de un filtro;
protección del acceso a la propia auditoría; y la política de retención.

### Las tres clases de evento que se registran

| Clase | Ejemplos | Quién los produce |
|---|---|---|
| **Autenticación** | Inicio de sesión correcto o fallido, verificación de OTP, cierre de sesión, reutilización de un token de refresco | SPEC-02, SPEC-04 |
| **Ciclo de vida y privilegios** | Alta de usuario, baja lógica, cambio de contraseña, restablecimiento, bloqueo, desbloqueo, asignación o revocación de rol, cambio de atributos | SPEC-01, SPEC-03, SPEC-05, SPEC-07, SPEC-08 |
| **Acceso entre módulos** | Consulta de un usuario por otro módulo, entrega del documento en claro, intento con scope insuficiente | SPEC-09 |

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
| RF-06.1 | El sistema debe registrar en `auditoria_seguridad` todo evento del catálogo de acciones auditables, con los campos obligatorios del formato único. |
| RF-06.2 | El catálogo de acciones auditables debe ser cerrado y versionado: una spec que necesite auditar algo nuevo añade su código al catálogo mediante acuerdo con el PO, y no inventa uno en su implementación. |
| RF-06.3 | El sistema debe registrar tanto los intentos exitosos como los fallidos, distinguiéndolos con el campo `resultado`. Un ataque se reconstruye con los fallos, no con los éxitos. |
| RF-06.4 | Los registros de auditoría deben ser **de solo anexado**: ninguna operación de la API puede modificarlos ni eliminarlos, y el usuario de base de datos de la aplicación no debe tener privilegios de `UPDATE` ni `DELETE` sobre la tabla. |
| RF-06.5 | El registro de auditoría no debe contener nunca contraseñas, hashes de contraseña, códigos OTP, tokens completos ni el número de documento en claro. Cuando haga falta referenciar un token se registra su `jti`. |
| RF-06.6 | El fallo al escribir un registro de auditoría no debe impedir que la operación auditada se complete, salvo en las acciones marcadas como **críticas** en el catálogo, donde la operación debe revertirse si no se pudo auditar. |
| RF-06.7 | Un administrador debe poder consultar la auditoría filtrando por usuario afectado, actor, acción, resultado, rango de fechas y dirección IP, con resultados paginados y ordenados de más reciente a más antiguo. |
| RF-06.8 | Un administrador debe poder exportar el resultado de un filtro en formato CSV o JSON, con el mismo criterio de filtrado que la consulta. |
| RF-06.9 | El acceso a la consulta y a la exportación de la auditoría debe exigir el permiso `auditoria.ver`, que solo posee el rol `ADMIN_SISTEMA`. Cualquier otro rol recibe `403`. |
| RF-06.10 | La consulta y la exportación de la auditoría deben, a su vez, quedar registradas en la auditoría: quién consultó los registros de quién también es un hecho auditable. |
| RF-06.11 | El sistema debe conservar los registros al menos **90 días** y eliminar automáticamente los anteriores mediante una tarea programada, dejando constancia de la purga como un registro más. |
| RF-06.12 | Un usuario debe poder consultar los eventos de **su propia** cuenta —inicios de sesión, cambios de contraseña, bloqueos— sin permiso de administrador y sin ver los de nadie más. |

### Catálogo de acciones auditables

Cerrado y versionado, según RF-06.2. Las marcadas como **crítica** revierten la
operación si no se pudieron auditar (RF-06.6).

| Código | Origen | Crítica |
|---|---|---|
| `SESION_INICIADA` | SPEC-02 | No |
| `SESION_FALLIDA` | SPEC-02 | No |
| `SESION_CERRADA` | SPEC-02 | No |
| `REFRESCO_REUTILIZADO` | SPEC-02 | No |
| `OTP_SOLICITADO` | SPEC-04 | No |
| `OTP_VERIFICADO` | SPEC-04 | No |
| `OTP_FALLIDO` | SPEC-04 | No |
| `MFA_ACTIVADO` / `MFA_DESACTIVADO` | SPEC-04 | Sí |
| `USUARIO_CREADO` | SPEC-01 | Sí |
| `USUARIO_VERIFICADO` | SPEC-01 | No |
| `USUARIO_DESACTIVADO` | SPEC-01 | Sí |
| `USUARIO_REACTIVADO` | SPEC-01 | Sí |
| `CONTRASENA_CAMBIADA` | SPEC-03 | Sí |
| `CONTRASENA_RESTABLECIDA` | SPEC-03 | Sí |
| `RECUPERACION_SOLICITADA` | SPEC-03 | No |
| `ROL_ASIGNADO` / `ROL_REVOCADO` | SPEC-05 | Sí |
| `CUENTA_BLOQUEADA` / `CUENTA_DESBLOQUEADA` | SPEC-07 | Sí |
| `ATRIBUTOS_ACTUALIZADOS` | SPEC-08 | No |
| `CORREO_CAMBIADO` | SPEC-08 | Sí |
| `MODULO_CONSULTO_USUARIO` | SPEC-09 | No |
| `MODULO_OBTUVO_DOCUMENTO` | SPEC-09 | Sí |
| `ACCESO_DENEGADO` | SPEC-09, SPEC-06 | No |
| `AUDITORIA_CONSULTADA` / `AUDITORIA_EXPORTADA` | SPEC-06 | No |
| `AUDITORIA_PURGADA` | SPEC-06 | No |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-06.1 Un evento sensible deja rastro
- **Dado** que un `ADMIN_SISTEMA` autenticado asigna el rol `VENDEDOR` a una cuenta,
- **Cuando** la asignación se completa,
- **Entonces** existe un registro con `accion: "ROL_ASIGNADO"`, `resultado: "EXITO"`, el identificador del administrador como `actorId`, el de la cuenta afectada como `objetivoUsuarioId`, la IP de origen y el rol concedido en `detalle`.

### ESC-06.2 Los intentos fallidos también se registran
- **Dado** que alguien intenta iniciar sesión cinco veces con la contraseña equivocada,
- **Cuando** ocurre cada intento,
- **Entonces** se registran cinco `SESION_FALLIDA` con `resultado: "FALLO"` y sus IP respectivas, y un sexto registro `CUENTA_BLOQUEADA` producido por SPEC-07, de modo que la secuencia completa del ataque queda reconstruible.

### ESC-06.3 La auditoría no guarda el secreto *(caso borde)*
- **Dado** un cambio de contraseña, una verificación de OTP y una consulta del documento de un cliente,
- **Cuando** se inspecciona la tabla `auditoria_seguridad` tras las tres operaciones,
- **Entonces** ningún registro contiene la contraseña ni su hash, ni el código OTP, ni el número de documento en claro: el de contraseña guarda solo el hecho, el de OTP guarda el identificador del desafío, y el de documento guarda `{ "entregado": "EN_CLARO", "modulo": "despacho" }`.

### ESC-06.4 Un registro no se puede alterar *(caso borde de seguridad)*
- **Dado** un registro de auditoría ya escrito,
- **Cuando** se intenta modificarlo o eliminarlo a través de cualquier endpoint de la API, o mediante `UPDATE` con el usuario de base de datos de la aplicación,
- **Entonces** no existe endpoint que lo permita y la sentencia falla por falta de privilegios: la tabla es de solo anexado.

### ESC-06.5 Una consulta filtrada
- **Dado** un `ADMIN_SISTEMA` investigando una cuenta concreta,
- **Cuando** envía `GET /auditoria?objetivoUsuarioId={id}&accion=SESION_FALLIDA&desde=2026-09-01&hasta=2026-09-13&pagina=1&tamano=50`,
- **Entonces** el sistema responde `200` con los registros que coinciden, ordenados de más reciente a más antiguo, paginados, y con el total de coincidencias para que la interfaz pueda paginar.

### ESC-06.6 Un rol sin privilegios no entra *(caso borde)*
- **Dado** un usuario autenticado con rol `CLIENTE` o `VENDEDOR`,
- **Cuando** intenta `GET /auditoria`,
- **Entonces** el sistema responde `403` con `code: SCOPE_INSUFICIENTE`, no devuelve ningún registro, y **deja un registro `ACCESO_DENEGADO`**: el intento de leer la auditoría sin permiso es, él mismo, un evento de seguridad.

### ESC-06.7 Consultar la auditoría es auditable
- **Dado** un `ADMIN_SISTEMA` con permiso `auditoria.ver`,
- **Cuando** consulta los registros de una cuenta ajena,
- **Entonces** además de recibir los resultados se escribe un registro `AUDITORIA_CONSULTADA` con el filtro aplicado en `detalle`, de modo que el uso del privilegio de auditoría queda sujeto a la propia auditoría.

### ESC-06.8 Exportación del resultado de un filtro
- **Dado** un `ADMIN_SISTEMA` que acaba de aplicar un filtro,
- **Cuando** envía `GET /auditoria/exportar?formato=csv` con los mismos parámetros de filtro,
- **Entonces** el sistema responde `200` con `Content-Type: text/csv`, una cabecera `Content-Disposition` con nombre de archivo fechado, y **exactamente los mismos registros** que devolvería la consulta con ese filtro, sin el límite de paginación.

### ESC-06.9 Exportación de un filtro demasiado amplio *(caso borde)*
- **Dado** un filtro que abarca más de 100 000 registros,
- **Cuando** se solicita su exportación,
- **Entonces** el sistema responde `413` con `code: EXPORTACION_DEMASIADO_GRANDE` e indica en `detail` que se acote el rango de fechas, en lugar de intentar materializar el archivo completo en memoria.

### ESC-06.10 El acceso de un módulo consumidor queda trazado
- **Dado** que el módulo de Despacho consulta `GET /usuarios/{id}` con su token de servicio y scope `usuarios:leer:documento`,
- **Cuando** el sistema le entrega el documento en claro,
- **Entonces** se escriben dos registros: `MODULO_CONSULTO_USUARIO` y `MODULO_OBTUVO_DOCUMENTO`, ambos con `actorTipo: "MODULO"` y el `client_id` de Despacho como `actorId`, satisfaciendo RF-09.14.

### ESC-06.11 La auditoría no bloquea la operación *(caso borde)*
- **Dado** que la tabla de auditoría no está disponible momentáneamente,
- **Cuando** un usuario inicia sesión correctamente,
- **Entonces** el inicio de sesión se completa y el registro `SESION_INICIADA` se reintenta de forma asíncrona; pero si lo que falla es auditar un `ROL_ASIGNADO`, que está marcado como crítico, la asignación se revierte y responde `503`.

### ESC-06.12 Un usuario consulta su propio historial
- **Dado** un usuario autenticado sin rol de administración,
- **Cuando** envía `GET /auth/me/actividad`,
- **Entonces** recibe únicamente los eventos de su propia cuenta —inicios de sesión con su IP y fecha, cambios de contraseña, bloqueos— y ningún registro cuyo `objetivoUsuarioId` sea otro.

### ESC-06.13 Purga por retención *(caso borde)*
- **Dado** un registro con más de 90 días de antigüedad,
- **Cuando** se ejecuta la tarea programada de retención,
- **Entonces** el registro se elimina y queda un `AUDITORIA_PURGADA` indicando cuántos registros se purgaron y hasta qué fecha, de modo que la desaparición de datos antiguos sea ella misma trazable.

---

## Requisitos no funcionales — ¿con qué condiciones?

- **La escritura del registro no puede degradar el camino crítico.** El inicio de sesión debe seguir respondiendo en menos de 800 ms en el percentil 95 con 50 usuarios concurrentes (SPEC-02) **con la auditoría activada**. Si la escritura síncrona no lo permite, se escribe de forma asíncrona con cola en memoria y reintento.
- La consulta filtrada debe responder en menos de **300 ms** en el percentil 95 sobre una tabla de un millón de registros. Exige índices sobre `objetivoUsuarioId`, `actorId`, `accion` y `fecha`.
- La tabla debe soportar el volumen de escritura del módulo entero: en el peor caso previsto, un evento por petición de autenticación más uno por cada consulta de módulo consumidor.
- Ningún registro puede contener contraseñas, códigos OTP, tokens completos ni el número de documento en claro (RF-06.5).
- Las fechas se almacenan en **UTC**; la conversión a hora local es responsabilidad de la interfaz. Un incidente entre equipos no se investiga con seis zonas horarias.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales; la retención de 90 días es el plazo acordado por el equipo y debe poder ampliarse por configuración sin desplegar código.

---

## Fuera de alcance — ¿qué NO hará?

- **La detección automática de comportamiento anómalo.** Botnets, análisis por aprendizaje automático y correlación entre cuentas. Esta spec registra hechos; interpretarlos es trabajo humano.
- **El bloqueo de cuentas**, que es SPEC-07. Aquí se registra que un bloqueo ocurrió, no se decide cuándo ocurre.
- **La bitácora técnica de la aplicación** (`stdout`, trazas, errores de infraestructura). Son dos cosas distintas: esa la lee un desarrollador depurando, esta la lee un administrador investigando. No comparten formato ni destino ni retención.
- **El envío de la auditoría a un SIEM externo** o a un almacenamiento inmutable de terceros.
- **Las alertas en tiempo real** ante patrones sospechosos: no se notifica a nadie automáticamente, salvo la notificación de bloqueo que ya define SPEC-07.
- **El análisis forense a largo plazo.** La retención es de 90 días; quien necesite más, exporta.
- **La auditoría de los módulos consumidores sobre sus propios datos.** Registramos que Despacho consultó a un usuario; qué hizo Despacho después con ese dato lo audita Despacho.

---

## Dependencias conocidas

| Dependencia | Estado | Cómo se resuelve mientras tanto |
|---|---|---|
| El permiso `auditoria.ver` en el catálogo de permisos de **SPEC-05** | Definido | Solo lo tiene `ADMIN_SISTEMA` |
| Las acciones del catálogo las producen **todas** las demás specs | En redacción | Cada responsable añade la llamada de auditoría al implementar su spec; el catálogo de acciones se congela antes del Hito 3 para que nadie invente códigos |
| Un scope de auditoría para módulos consumidores | No previsto | **Ningún módulo consumidor accede a la auditoría.** Si alguno lo pide, se evalúa como cambio de contrato |

---

## Decisiones de diseño registradas

**Por qué es una spec propia y no parte de SPEC-07.** La propuesta original
fundía bloqueo de cuentas y auditoría en una sola funcionalidad. Son cosas
distintas: el bloqueo es una reacción automática sobre *una* cuenta ante un
patrón de fallos; la auditoría es un servicio transversal que las **nueve** specs
consumen. Fundirlas produciría una spec con dos propósitos —la plantilla pide
uno— y dejaría la auditoría del acceso entre módulos (SPEC-09) colgando de una
spec que no tiene nada que ver con ella. Separarlas tiene además un efecto
práctico: el catálogo de acciones tiene un único dueño al que acudir cuando una
spec necesita auditar algo nuevo.

**Solo anexado, y sin privilegios de borrado para la aplicación.** Una auditoría
que la propia aplicación puede reescribir no prueba nada: quien comprometa la
aplicación borra su rastro. Por eso RF-06.4 no es una regla de código sino de
permisos de base de datos. La única eliminación admitida es la purga por
retención, que corre con otro usuario y deja constancia.

**Auditar la consulta de la auditoría.** Puede sonar recursivo, pero el rol
`ADMIN_SISTEMA` es el más poderoso del sistema y es precisamente el que nadie
más vigila. RF-06.10 es la única forma de que el uso de ese privilegio deje
rastro.

**Fallar la operación solo en las acciones críticas.** Si auditar fuese siempre
obligatorio, una caída de la tabla de auditoría tumbaría el inicio de sesión de
todo el marketplace. Si no lo fuese nunca, un atacante que sature la auditoría
podría cambiar roles sin dejar rastro. El catálogo distingue las dos listas y
RF-06.6 aplica la regla que corresponde a cada una.

**El historial propio del usuario (RF-06.12) no es un capricho.** Es la forma
más barata de que un cliente detecte un acceso que no reconoce, y convierte la
auditoría en algo que también sirve a quien no es administrador.

---

## Impacto en el contrato

Esta spec **añade** endpoints a `specs/openapi.yaml`. Ninguno de ellos está
disponible para los módulos consumidores: son de administración y de usuario
final.

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v1/auditoria` | Añade | ⬜ |
| `GET /api/v1/auditoria/exportar` | Añade | ⬜ |
| `GET /api/v1/auth/me/actividad` | Añade | ⬜ |
| Permiso `auditoria.ver` en el catálogo de SPEC-05 | Añade | ⬜ |
| Código de error `EXPORTACION_DEMASIADO_GRANDE` | Añade | ⬜ |

No publica ningún evento nuevo en RabbitMQ: la auditoría es consumidora de lo
que ocurre, no productora.

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Los 12 requisitos están implementados
- [ ] Las 28 acciones del catálogo escriben su registro desde la spec que las origina
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Se ha verificado con una prueba de carga que el inicio de sesión sigue bajo 800 ms con la auditoría activa
- [ ] La consulta filtrada responde bajo 300 ms sobre un millón de registros sembrados
- [ ] El usuario de base de datos de la aplicación no tiene `UPDATE` ni `DELETE` sobre `auditoria_seguridad`
- [ ] Los tres endpoints figuran en `specs/openapi.yaml` publicado
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
