# H3 — tareas de Luis David Morales Brenis

**Dueño:** Luis David · **Incremento:** H3-LUIS.
**Épicas:** EP-04 (OTP/MFA) y EP-07 (bloqueo/desbloqueo).
**Base:** [PLAN aprobado 1.0.0](../../../plan/hito-3/luis-morales/PLAN.md),
12-oct-2026; aprobación comunicada por Luis.

Dos recorridos completos del mismo dueño, con IDs y evidencia separados:
**contraseña→OTP EMAIL→sesión** y **cinco fallos distintos→bloqueo→correo→
desbloqueo→login**. Ninguno se da por terminado porque el otro funcione.

**Fuentes comunes:** [composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md),
[auditoría](../../../specs/funcionales/SPEC-12-registro-auditoria.md),
[eventos](../../../specs/catalogo-eventos.md).

**Límite:** configuración MFA SPEC-10/v2, proveedor SMS real y acciones manuales
SPEC-15 posteriores. Gestión sigue obligando MFA; un bloqueo manual existente
se respeta y jamás se levanta mediante enlace. No sustituir correo por código
visible en React ni respuesta fija.

## Orden de trabajo

Empezar EP-04-T001 y EP-07-T001 (datos). Entregar los ports T002 de ambas épicas
a Jose Luis/Eva/Valery. Avanzar reglas/HTTP y pantallas con dependencias
controladas; los cierres EP-04-T006 y EP-07-T007 requieren composición real.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-04-T001 | Persistencia de desafío y OTP vinculados | P0 | Esquema/historia revisados con Christian | Pendiente |
| EP-04-T002 | MfaPort y desafío sin sesión | P0 | EP-04-T001; cuentas Eva, revisión Juan José, roles Sergio | Pendiente |
| EP-04-T003 | Solicitar/reenvíar OTP con cuota persistida | P0 | EP-04-T002; correo/outbox/auditoría Christian | Pendiente |
| EP-04-T004 | Verificar OTP y abrir una sola sesión | P0 | EP-04-T003; EP-02-T003; caducidad Juan José, protección/roles reales | Pendiente |
| EP-04-T005 | OtpPage y manejo del desafío | P1 | EP-04-T003/T004; EP-02-T001/T008/T009 | Pendiente |
| EP-04-T006 | Probar y defender MFA real | P1 | EP-04-T004/T005; login/correo/despliegue reales | Pendiente |
| EP-07-T001 | Persistencia de racha, huellas y bloqueo | P0 | Esquema/historia revisados con Christian | Pendiente |
| EP-07-T002 | ProteccionPort y fallos distintos | P0 | EP-07-T001; EP-01-T002; guard de roles Sergio | Pendiente |
| EP-07-T003 | Quinto fallo, progresión y correo/evento | P0 | EP-07-T002; EP-03-T004; auditoría/outbox Christian | Pendiente |
| EP-07-T004 | Desbloqueo por enlace atómico | P1 | EP-07-T003; EP-03-T004; guard Sergio | Pendiente |
| EP-07-T005 | Pantalla de enlace y lectura de bloqueo | P1 | EP-07-T004; base React Jose Luis; detalle Eva para composición | Pendiente |
| EP-07-T006 | Probar concurrencia y composición de defensa | P0 | EP-07-T003/T004; login/refresh y providers reales | Pendiente |
| EP-07-T007 | Defender bloqueo y desbloqueo desplegados | P1 | EP-07-T005/T006; correo/despliegue reales | Pendiente |

EP-01/02/03 apuntan a tableros de Eva/Jose Luis/Juan José. Sergio, Christian y
Valery entregan sus capacidades según sus planes; no se les asignan aquí tareas.

## EP-04 — segundo factor

### EP-04-T001 — Vincular desafío, credencial y OTP en persistencia

- **Fuentes:** [SPEC-09 RF-09.1–09.3/09.9](../../../specs/funcionales/SPEC-09-mfa-inicio-sesion.md);
  persistencia §§5/8, V103 reservada.
- **Implementar:** entidades/repositorios propios en
  `backend/src/main/java/com/marketplace/auth/`; migración para revisión de
  credencial del desafío, OTP/desafío/usuario, invalidación, key id e índices.
  UUID ajenos son valores, sin entidades/repositorios de Juan José/Eva.
- **Cierre:** nuevos LOGIN_MFA vinculados coherentemente; históricos sin revisión
  no autorizan login. Desafío aleatorio 32 bytes/SHA-256/300 s; OTP SecureRandom
  de seis dígitos, incluidos ceros iniciales, HMAC contextual con clave externa
  y comparación constante. No guardar código reversible en tabla OTP.
- **Comprobar/evidencia:** migración/mapping en PostgreSQL de prueba, constraints,
  claves/contextos distintos y ausencia de secretos. No reescribir migraciones
  aplicadas ni convertir OTP de otro propósito en desafío de login.

### EP-04-T002 — Entregar MfaPort y crear desafíos desde login

- **Fuentes:** SPEC-09 RF-09.8/09.9/09.13/09.17; SPEC-10 RF-10.1;
  composición §2 `MfaPort` y §4. Consume T001 de esta épica.
- **Implementar:** interfaz/DTO y servicio propios: MFA efectivo, creación de
  desafío y lectura de estado para Valery. Recibir revisión de credencial de
  Juan José; roles actuales de Sergio; cuenta de Eva.
- **Cierre:** gestión obliga MFA aunque bandera voluntaria false; EMAIL por
  defecto, sin canal preferido inventado. Login devuelve desafío sin familia,
  acceso ni reset de racha; crear desafío no envía OTP por sí mismo. No usarlo
  como acceso, cambio MFA o verificación de contacto.
- **Comprobar/evidencia:** tests de roles múltiples, CLIENTE voluntario, sin MFA,
  desafío vencido/manipulado/credencial cambiada; entrega de contrato a Jose
  Luis/Valery. No implementar habilitar/deshabilitar MFA de v1 histórico o v2.

### EP-04-T003 — Implementar POST /api/v1/auth/otp/solicitar

- **Fuentes:** SPEC-09 RF-09.4/09.6/09.12–09.16; ESC-09.1/09.2/09.7/09.12–09.14;
  OpenAPI solicitar; composición §§3–4. Consume EP-04-T002 y Christian.
- **Implementar:** controlador/servicio propios; validar desafío y cuenta antes
  de canal/cuota. Lock cuenta→desafío/códigos, invalidar anterior, generar OTP
  y encolar correo sensible cifrado mediante MensajeriaPort en el mismo commit.
- **Cierre:** 202 con campos contractuales, sin código. Máximo tres solicitudes
  aceptadas/usuario en ventana móvil 15 min, todos sus desafíos y códigos
  usados/invalidados incluidos; 429 `DEMASIADAS_SOLICITUDES` con Retry-After
  positivo. Reenvío no prorroga desafío; fallo SMTP posterior no devuelve cuota.
- **Canales:** EMAIL real; adaptador SMS simulado solo desarrollo/tests, captura
  controlada sin logs públicos. SMS sin celular: 400 VALIDACION sin fila/cuota;
  desafío inválido primero: 401 TOKEN_INVALIDO. No llamar transporte bajo lock.
- **Comprobar/evidencia:** concurrencia en tercera/cuarta solicitud, ventana,
  reenvío invalida anterior, destino/canal y auditoría OTP_SOLICITADO; tests
  DB/HTTP y entrega real en cierre, sin secretos en evidencia.

### EP-04-T004 — Implementar POST /api/v1/auth/otp/verificar

- **Fuentes:** SPEC-09 RF-09.5/09.7/09.10–09.12; ESC-09.3–09.6/09.9–09.11;
  OpenAPI verificar; composición §4. Consume EP-04-T003, SesionesPort Jose Luis
  y CredencialesPort Juan José; roles/cuenta/protección revalidados.
- **Implementar:** validar desafío/código más reciente, vigencia y revisión;
  incorrecto incrementa intentos, tercero invalida. Correcto consume OTP/desafío,
  abre familia por Jose Luis y completa/reset de login en el mismo commit.
- **Cierre:** 401 CODIGO_INVALIDO/OTP_INTENTOS_AGOTADOS/TOKEN_INVALIDO y 410
  OTP_EXPIRADO conforme contrato. Fallos OTP no suman racha de contraseña;
  defensas se confirman antes del HTTP. Caducidad al completar da 403
  PASSWORD_CADUCADA sin sesión; ninguna prueba puede emitir dos familias.
- **Comprobar:** dos verificaciones correctas simultáneas, tercero incorrecto,
  usado/reenviado/otro desafío, cuenta bloqueada o credencial cambiada; error
  técnico al abrir familia revierte consumo y no entrega tokens huérfanos.
- **Evidencia:** tests HTTP/DB, sesión real y auditoría canónica; Luis no firma
  tokens ni escribe tablas de sesión/credencial por su cuenta.

### EP-04-T005 — Construir OtpPage y useDesafioOtp

- **Fuentes:** [FRONT-04 UI-04.1–04.8](../../../specs/front/FRONT-04-desafio-otp.md)
  y [componentes FRONT-04](../../../specs/componentes-react/FRONT-04-desafio-otp.md).
- **Implementar:** `frontend/src/pantallas/otp/`: `OtpPage`, `useDesafioOtp`,
  `CuentaRegresiva`; consumir sesión/HTTP/router de Jose Luis. Al abrir solicitar
  una sola vez, también ante rerender de desarrollo; no reenviar por accidente.
- **Cierre:** desafío en memoria, recarga/sin desafío vuelve a login; PinInput,
  pegar seis dígitos, foco y envío sin doble submit. Reloj usa menor vigencia
  código/desafío, nunca se reinicia desafío al reenviar. Retry-After restringe
  reenvío pero permite código aún válido; vencido el desafío vuelve a login.
- **Comprobar:** tests de estados/códigos, tercer error, reenvío, cuota y éxito
  guardando sesión compartida; caducidad no ofrece recuperación aún pendiente.
  Si se prueba SMS sin celular, permitir EMAIL; sin selector adicional no
  introducirlo. Teclado, aria-live, one-time-code y móvil 390 px.
- **Evidencia:** tests y recorrido real; no challenge/OTP en URL, consola o
  almacenamiento persistente. Mantener captura SMS exclusivamente en tests.

### EP-04-T006 — Probar y defender segundo factor real

- **Fuentes:** PLAN §7, escenarios SPEC-09 y FRONT-04 aplicables; EP-04-T001–T005.
- **Inicio:** providers reales de Eva/Juan José/Jose Luis/Sergio/Christian y
  cuentas M/ADM separadas de A/B. SMTP y despliegue funcionando.
- **Cierre:** contraseña correcta→desafío→correo real→OTP→sesión; gestión nunca
  obtiene sesión antes del segundo factor. Demostrar errores, expiración,
  reenvío, cuota, uso único y fallo técnico sin consumo parcial.
- **Comprobar/evidencia:** tests backend/React y concurrencia DB, recorrido en
  nube, versión/URL, capturas sanitizadas y uso de IA trazado a fuentes.
  Registrar evidencia **EP-04** propia; no declarar configuración SPEC-10 ni
  SMS real implementados, ni usar mocks como correo de demo.

## EP-07 — bloqueo y desbloqueo automático

### EP-07-T001 — Preparar racha, huellas y revisión del bloqueo

- **Fuentes:** [SPEC-14 RF-14.1–14.4](../../../specs/funcionales/SPEC-14-bloqueo-automatico.md);
  persistencia §§6/8, V104 reservada.
- **Implementar:** entidades/repositorios propios de intentos/bloqueos y nuevas
  racha_fallo/huella_fallo; migración revisión del bloqueo e integridad del diseño.
  HMAC con pepper externo/contexto longitud-prefijada y bytes exactos del secreto.
- **Cierre:** racha y hash_key_id persistidos; reinicio conserva deduplicación.
  No contraseña en intentos/DB/auditoría, ni huella en eventos. No usar contador
  legacy de credencial ni número de filas intento_login como contador funcional.
- **Comprobar/evidencia:** PostgreSQL, constraints/huella única y claves por racha,
  reinicio y revisión nueva del bloqueo. Datos de usuario solo mediante Eva;
  no añadir una ventana temporal de fallos ni purgar una racha por antigüedad.

### EP-07-T002 — Entregar ProteccionPort y contar fallos distintos

- **Fuentes:** SPEC-14 RF-14.1/14.2/14.5/14.6/14.7; ESC-14.1/14.3–14.7/14.10;
  composición §§2–4. Consume EP-07-T001 y CuentaEscrituraInterna Eva.
- **Implementar:** evaluación del estado, registro de fallo y completarLogin;
  pedir guard ADMIN_SISTEMA de Sergio y lock de cuenta Eva en orden aprobado.
  Insertar huella única/registrar intento y guardar contadores mediante Eva.
- **Cierre:** solo contraseña incorrecta distinta sobre ACTIVO suma; repetirla
  deja contador igual, pero registra intento. IP/tiempo no fragmentan racha.
  Inexistente/pendiente/inactivo/bloqueado no suma; no revela estado en login.
  Completar login resetea racha y bloqueos seguidos; desafío aún no lo hace.
- **Estado:** bloqueo vencido se proyecta ACTIVO sin job/evento; siguiente
  mutación normaliza contador/huellas. Desbloqueo/vencimiento reinician racha,
  no bloqueos seguidos. Exponer lectura segura a Eva/Valery sin entidad ajena.
- **Comprobar/evidencia:** tests de repetidos/distintos, otra IP, cuatro fallos
  separados, expiración y login completo/MFA pendiente; contrato a Jose Luis.

### EP-07-T003 — Bloquear al quinto fallo con efectos atómicos

- **Fuentes:** SPEC-14 RF-14.3/14.4/14.8–14.11; ESC-14.2/14.8/14.9/14.11/14.16;
  composición §4. Consume EP-07-T002, TokensUnUsoPort Juan José y Christian.
- **Implementar:** transición automática bajo locks, escalera 1/2/4 min y
  cuarto sin vencimiento. Generar revisión nueva, invalidar enlaces previos,
  solicitar token propósito/contexto/30 min; crítica + evento `usuario.bloqueado`
  + correo cifrado durable en el mismo commit mediante sus ports.
- **Cierre:** usuario base permanece ACTIVO, estado efectivo BLOQUEADO. Quinta
  entrada concurrente produce un solo bloqueo/enlace/correo/evento; login sigue
  401 genérico. No revocar familias previas ni enviar correo/broker bajo lock.
- **Comprobar:** progresión conservada al vencer/desbloquear, correo enlazado,
  familias previas renovables, fallo de auditoría/outbox revierte transición.
  El envío tras commit usa worker Christian; producer ofrece validez para
  no entregar enlaces reemplazados sin leer tablas ajenas.
- **Evidencia:** pruebas DB/HTTP y outbox/eventos sanitizados; no afirmar entrega
  exacta una vez ni correo real hasta comprobarlo en cierre.

### EP-07-T004 — Implementar POST /api/v1/auth/desbloquear

- **Fuentes:** SPEC-14 RF-14.11, ESC-14.12–14.14; OpenAPI desbloquear;
  composición §4. Consume EP-07-T003, token de Juan José y guard Sergio.
- **Implementar:** controlador/servicio propio; localizar sin consumir, guard→
  cuenta→bloqueo/token. Revalidar usuario/propósito/revisión/vigencia y tipo
  AUTOMATICO; consumir prueba y levantar bloqueo en commit con crítica/evento.
- **Cierre:** 204 válido, también automático sin vencimiento; token inexistente/
  usado/reemplazado 401 TOKEN_DESBLOQUEO_INVALIDO; vencido 410
  TOKEN_DESBLOQUEO_EXPIRADO. No levantar MANUAL ni aceptar token de recuperación.
  Reiniciar intentos/huellas por Eva; bloqueos seguidos solo los resetea login.
- **Comprobar/evidencia:** doble consumo, reemplazo y bloqueo manual entre emisión
  y uso; error técnico revierte consumo/estado/evento. No consultar enlace con
  un endpoint previo ni inventar código de error/estado que enumere cuentas.

### EP-07-T005 — Construir pantalla de enlace y lectura administrativa

- **Fuentes:** [FRONT-12 UI-12.1–12.5](../../../specs/front/FRONT-12-desbloqueo-con-enlace.md),
  [componentes FRONT-12](../../../specs/componentes-react/FRONT-12-desbloqueo-con-enlace.md)
  y selección H3 de [FRONT-08](../../../specs/front/FRONT-08-admin-detalle-usuario.md).
- **Implementar:** `frontend/src/pantallas/desbloqueo/DesbloqueoPage.tsx` y
  `useDesbloqueo`; lectura `SeccionBloqueo` para el contenedor Eva, sin acciones
  manuales. Usar base HTTP/router de Jose Luis y proyección contractual autorizada.
- **Cierre:** token pasa a memoria y se elimina URL al cargar; política no-referrer
  coordinada con entorno. Abrir/previsualizar correo **no** consume ni llama API;
  solo botón explícito, doble clic una petición. Sin token mostrar error local.
- **Comprobar:** éxito/401/410/error red, foco al resultado, móvil y retorno login.
  No revelar MANUAL ni habilitar recuperación pendiente; indicar alternativa
  de administrador según H-14 sin inventar contacto. Lectura de bloqueo muestra
  vencimiento real, sin cálculo de acciones no implementadas.
- **Evidencia:** tests UI/recorrido; token ausente en URL/logs/storage persistente,
  ausencia de consumo al montar página. Eva integra bloque, Luis lo implementa.

### EP-07-T006 — Probar concurrencia y composición de protección

- **Fuentes:** PLAN §7; ESC-14.3–14.14/14.16 y composición §§3–4.
- **Inicio:** providers reales de Eva/Juan José/Sergio/Christian; login/refresh
  Jose Luis. Dobles solo acreditan aislamiento; usar cuenta L, no A/B.
- **Cierre:** quinto fallo simultáneo no duplica efectos; huellas persisten tras
  reinicio; misma contraseña no suma; login/MFA completo resetea correctamente.
  Automático mantiene refresco previo y niega nuevo login; manual no admite
  login, refresh ni desbloqueo por enlace. No programar operaciones manuales.
- **Comprobar:** bloqueo en límite exacto de vencimiento, cuatro bloqueos seguidos,
  doble consumo, reemplazo, rollback crítico y SMTP fallido con reintento sin
  deshacer bloqueo. JWT previo remoto puede seguir vigente hasta exp.
- **Evidencia:** tests PostgreSQL/HTTP con reloj/concurrencia controlados,
  resultados y estado posterior, sin secretos ni promesas de revocación remota.

### EP-07-T007 — Defender bloqueo y desbloqueo desplegados

- **Fuentes:** PLAN §7; EP-07-T001–T006, SPEC-14 y FRONT-12 aplicables.
- **Inicio:** UI/API/providers reales y correo/despliegue Christian.
- **Cierre:** cinco contraseñas erróneas distintas→bloqueo real→correo→pantalla
  de enlace→submit→204→login correcto. Mostrar negativo con enlace usado y
  login genérico mientras bloqueada; lectura Eva refleja el estado efectivo.
- **Comprobar/evidencia:** recorrido navegador/HTTP/DB y vuelta, resultados,
  URL/revisión desplegada, capturas sin enlaces secretos y explicación de IA.
  Registrar evidencia **EP-07** aparte de EP-04; no declarar bloqueo manual,
  soporte real ni restablecimiento SPEC-08 implementados.

## Registro de ejecución y revisión

Todas las tareas comienzan pendientes. Cada cierre necesita criterios, evidencia
y revisión de otro integrante; ports compartidos se revisan con sus consumidores.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | EP-04-* | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
| 12-oct-2026 | EP-07-* | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
