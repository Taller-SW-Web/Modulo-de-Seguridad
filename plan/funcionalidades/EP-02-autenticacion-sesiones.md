# Plan EP-02 — Autenticación y gestión de sesiones

**Entrada del incremento H3:** [plan de Jose Luis](../hito-3/jose-limachi/PLAN.md).
Este diseño se consume allí; no se mantiene un segundo tablero.

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| ID / versión / fecha | EP-02 / 1.0.0 / 12-oct-2026 |
| Dueño completo | Jose Luis Limachi Sarmiento: interfaz, backend, datos, pruebas y demostración |
| Estado / revisión | Alcance H3 aprobado según comunicación de Luis; diseño técnico delegado cerrado; ejecución pendiente |
| Revisión | Aprobación formal del incremento comunicada por Luis el 12-oct-2026 |
| Base publicada | main `5796622f7c039021e03dd25b2338c6a208320e05`, tras PR #46; este plan es un complemento de trabajo posterior |
| Contrato / seguimiento | v1 1.1.0; [tablero H3 de Jose Luis](../../tareas/hito-3/jose-limachi/TAREAS.md) disponible, sin otro tablero |

Las cuatro entradas se consumen juntas:

| Entrada | Fuente que gobierna |
|---|---|
| Funcional | [EP-02](../../specs/epicas/EP-02-autenticacion-sesiones.md), [SPEC-05](../../specs/funcionales/SPEC-05-inicio-sesion.md) y [SPEC-06](../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md) |
| UI/UX | [FRONT-01](../../specs/front/FRONT-01-inicio-sesion.md), sesión de [FRONT-00](../../specs/front/FRONT-00-comportamiento-comun.md), [sistema de diseño](../../docs/diseno/sistema-diseno.md) y [mockups](../../docs/mockups/README.md) |
| API | [OpenAPI v1](../../specs/contrato-api/openapi.yaml): /auth/login, /auth/refresh, /auth/logout, SesionIniciada, DesafioMfa y [errores](../../specs/catalogo-errores.md) |
| React | [FRONT-01 React](../../specs/componentes-react/FRONT-01-inicio-sesion.md) y bloque de sesión de [FRONT-00 React](../../specs/componentes-react/FRONT-00-base-compartida.md) |

Complementan las entradas el [glosario](../../CONTEXT.md),
[estados de cuenta](../../docs/arquitectura/estados-usuario.md),
[contraste de datos](../../docs/integracion/revision-datos.md),
[plan H3](../PLAN-IMPLEMENTACION.md) y
[plan técnico de identidad](../transversales/TEC-IDENTIDAD.md).
Mockups aportan diseño; las specs gobiernan estados y reglas de seguridad.
El incremento visual H3 de FRONT-00/01 está aprobado según comunicación de Luis.

**Diseño H3 definitivo:** [composición](../hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../hito-3/DISENO-PERSISTENCIA.md) y [entorno/React](../hito-3/ENTORNO-Y-REACT.md).
Concretan los detalles internos bajo delegación de Luis; su aprobación H3
no se extiende a capacidades de hitos posteriores.

## 2. Objetivo y límites

Una persona inicia sesión y recibe tokens solo al completar los controles
aplicables; puede renovar y cerrar esa sesión. Reutilizar un refresco rotado
revoca su familia, conservando las sesiones independientes.

**Demo H3:** login real de CLIENTE activo sin MFA habilitado, familia persistida
y acceso verificable por Ventas. Ventas construye su pantalla, validador y
/pedidos/me. La pantalla React de G7 sigue siendo parte de EP-02 de Jose Luis.

**Primera versión individual:** login, renovación, cierre, sesión y rutas de
FRONT-00/01, más composición real de caducidad, MFA, bloqueo y auditoría con
sus dueños. La demo mínima no completa la épica. Una dependencia incompleta
puede probarse con una fixture identificada, pero su escenario de integración
sigue pendiente. No se omite MFA de gestión ni se emiten tokens por una rama
de seguridad que todavía no funciona.

## 3. Cobertura y criterios

| Origen | Comportamiento / operación | Resultado comprobable |
|---|---|---|
| RF-05.1; ESC-05.1; UI-01.1 | POST /api/v1/auth/login, correo/contrasena | 200 SesionIniciada para cuenta persistida, sin respuesta fija |
| RF-05.3/05.4; ESC-05.2–05.4; UI-01.2–01.4/01.9 | Contraseña incorrecta, inexistente, bloqueada, inactiva o pendiente | Mismo 401 CREDENCIALES_INVALIDAS y UI; sin tokens; EP-07 decide qué intento cuenta |
| RF-05.2; ESC-05.5; UI-01.5 | MFA voluntario o efectivo por cualquier rol de gestión | 200 DesafioMfa; no sesión completa, desafío en memoria; continuación de EP-04 |
| RF-05.5; UI-01.6; SPEC-07 | Caducidad al completar autenticación | 403 PASSWORD_CADUCADA sin tokens; recuperación de EP-03 |
| RF-05.6; ESC-05.1 | Emisión y apertura de familia | RS256, 900 s, refresco de 7 días y roles/permisos calculados por EP-05 |
| RF-05.7 | Registro de inicio y fallo | SESION_INICIADA / SESION_FALLIDA mediante EP-06, sin secretos |
| RF-06.1/06.4; ESC-06.1/06.4; UI-00.1/00.2 | POST /api/v1/auth/refresh | Rotación atómica; nuevo par o 401 REFRESCO_INVALIDO |
| RF-06.3; ESC-06.2 | Reúso de refresco | Familia completa revocada; otra familia del usuario sigue utilizable |
| RF-06.2/06.6; ESC-06.3/06.5; UI-00.4 | POST /api/v1/auth/logout, refresco en cuerpo | 204 sin exigir acceso vigente; también si refresco vencido/revocado/desconocido; memoria borrada |
| RF-06.5 | Auditoría de sesión | SESION_CERRADA / REFRESCO_REUTILIZADO con referencias internas |
| UI-01.7/01.8; UI-00.3/00.5/00.6 | Envío, red, retorno, rutas | Sin doble submit; foco/mensajes prescritos; volver interno; UI no sustituye autorización de backend |

H3-C01–C04 y C11/C12 del plan conjunto verifican el aporte G7.
H3-C05–C10 requieren también Ventas real. No se añaden scopes de negocio
al token del usuario para la demo.

## 4. Decisiones técnicas

### Interfaz y sesión React

Se mantienen LoginPage/useLogin y los archivos propuestos de FRONT-00.
Acceso, refresco e identidad se guardan en memoria. Cada pestaña inicia sin
sesión y obtiene su propia familia; esta versión no comparte tokens entre
pestañas. Recargar exige login. Si se comparte en otra versión, se revisa la
coordinación de renovación entre pestañas. H-11/INT-05 conserva pendiente
la aceptación del comportamiento en G5.

Solo SesionIniciada abre sesión; DesafioMfa deriva a FRONT-04 sin token en
URL. Login no valida la política de contraseña. Un 401 conserva correo,
borra contraseña y mueve foco; error de red conserva el formulario; caducidad
deriva a recuperación. Se verifica teclado, role=alert, autocomplete y móvil
de 390 px según las fuentes UI/React.

La renovación usa una única operación compartida dentro de la sesión:
vencimiento próximo según FRONT-00 o 401 TOKEN_INVALIDO de petición
autenticada. Tras renovación confirmada se reintenta la petición original
una vez. Una respuesta perdida no permite reenviar ciegamente el refresco
posiblemente consumido: se limpia sesión y se pide login. Logout limpia
memoria incluso si falla la red, sin afirmar que el servidor recibió el cierre.

### Backend y capacidades internas

Controladores/adaptadores de login, renovación y cierre serán nuevos en
com.marketplace.auth_service; aún no existen en la base revisada. Los tres endpoints
son públicos. Logout se autoriza con el refresco del cuerpo y es idempotente.

| Capacidad interna propuesta | Dueño | Consumo de EP-02 |
|---|---|---|
| Cuenta y estado efectivo | Eva; Luis David para bloqueos | UUID, estado y datos mínimos; no duplicar cálculo de bloqueo |
| Credencial/caducidad | Juan José | Hash compatible, comprobación segura y caducidad; no copiar política |
| Intentos/bloqueo | Luis David | Resultado del intento y transición según SPEC-14; no contador de EP-02 |
| MFA efectivo/desafío | Luis David, roles de Sergio | Desafío con canal/tiempos; no usar token de cambio MFA |
| Roles/permisos efectivos | Sergio | Seis roles y unión de permisos de Seguridad, sin permisos de APIs ajenas |
| Firma de acceso | Sergio, TEC-IDENTIDAD | Claims calculados, UUID y tiempo; mismo issuer/clave que JWKS |
| Auditoría | Christian | Hechos y tratamiento de fallo/reintento de SPEC-12 |

Son límites y datos de interfaz para revisión entre dueños, no endpoints
externos nuevos. Caducidad se comprueba al completar autenticación, incluido
canje OTP. Cuenta inexistente realiza trabajo criptográfico equivalente a
través de EP-03; la equivalencia de latencia se mide, no se supone.

### Persistencia y transacciones

Se implementan modelos, puertos y adaptadores nuevos de sesión, sin reutilizar
TokenRefresco/JpaRepository del backend Maven. [Arquitectura H3](../hito-3/BACKEND-HEXAGONAL.md)
gobierna ubicaciones, DTO y mappers. Esquema e historial reales se revisan antes
de preparar migraciones; las clases antiguas no acreditan rotación atómica.

**Diseño definitivo H3:** refresco opaco de 32 bytes/hash SHA-256;
raíz independiente sesion_familia y asociación acceso_sesion por jti.
Se preservan familia_id existentes, con migración/backfill validado antes
de constraints. Todos los caminos toman usuario y raíz, releen token y
rotan/revocan en la misma transacción. No mutex solo en memoria ni primera
fila de token como raíz. Retener tokens usados mientras exista sucesor
utilizable; no agregar límite absoluto de familia. Detalle exacto en
[persistencia H3](../hito-3/DISENO-PERSISTENCIA.md).

Ante reúso, la revocación se confirma antes de responder 401: una excepción
no debe deshacerla. Firma o persistencia fallida no dejan una rotación a
medias. Dos solicitudes del mismo refresco no emiten dos sucesores válidos;
la segunda detectada como reúso también revoca el sucesor de la primera.

DAT-01 requiere además resolver jti de acceso → usuario/familia/vigencia.
acceso_sesion persiste esa asociación; esquema/interfaces están cerrados
y su migración/prueba se implementan con consumidores Juan José/Luis/Sergio. No se añade familiaId al token ni se infiere familia solo de sub.
Migraciones aplicadas se conservan; la ampliación utiliza una nueva acordada.

### Auditoría y configuración

Los cuatro hechos de sesión se registran mediante EP-06. El evento de bloqueo
pertenece a EP-07; no se añade un evento de negocio de login/refresh.
Fallo/reintento respetan criticidad de SPEC-12, sin almacén de logs paralelo.

API, issuer/JWKS, orígenes y reloj son configuración de entorno. Backend
nuevo declara Java 21/Boot 3.5.16/Gradle 8.14.3; biblioteca JOSE, dependencias efectivas y frontend
React/Mantine se fijan al preparar el proyecto. El kit ejecutado en otro
runtime no sustituye TEC-04 ni acredita este build.

## 5. Dependencias y condiciones de inicio

| Condición | Evidencia necesaria | Parte afectada |
|---|---|---|
| Revisión de plan y FRONT-00/01 | Jose Luis revisa trazabilidad, diseño interno y estados; cambios en fuentes | Derivar tareas EP-02 |
| EP-01/03/05/07 | Interfaces/datos acordados y luego capacidades reales | Login completo; composición/UI pueden prepararse con fixtures identificadas |
| EP-04 | Desafío y canje reales | Login MFA; no emitir acceso por rama pendiente |
| EP-06 | Registro y política de fallo/reintento | Auditoría real |
| TEC-02/03/04 | Cuentas/hash, firma/JWKS y build reales | Login/validación real local |
| DAT-01 | Persistencia acceso/familia y OTP/desafío cuando corresponde | Cambio de contraseña H3, composición OTP; introspección/cambio MFA posteriores |
| TEC-01 / INT-05 | Entorno, URLs, rama, origen/transporte y prueba bilateral | Integración en nube |
| H-11 | Aceptación de sesión en memoria por G5 | Declarar acordada su sesión de frontend |

Consultar [registro](../../docs/registro-inconsistencias.md) y
[condiciones H3](../DECISIONES-Y-BLOQUEOS.md). H-12 bloquea registro/activación
con aceptación de términos, no el diseño de login. Las cuentas demo requieren
protocolo explícito, sin consentimiento inventado.

## 6. Fases y entregables

| Fase | Resultado de Jose Luis | Dependencia / verificación prevista |
|---|---|---|
| Diseño | Plan revisado, límites internos y persistencia concretos | Fuentes y revisión con dueños; RF/ESC/UI trazados |
| Login | API real, familia persistida y FRONT-01 | Cuenta/credencial/roles/bloqueo/firma; ESC-05/UI-01, ramas pendientes identificadas |
| Sesión | Renovación/cierre y sesión React | Diseño transaccional; ESC-06/UI-00, concurrencia y familia independiente |
| Composición | MFA, caducidad y auditoría reales | Entregables EP-03/04/06/07; sin tokens antes de controles |
| Demostración | EP-02 y aporte G5 comprobados | Entorno y G5; evidencia real local y conjunta en nube |

Son fases del plan, no tareas ejecutables ni un segundo tablero.

## 7. Pruebas y terminado

- Login activo/correcto; contraseña errónea, inexistente, bloqueado, inactivo
  y pendiente: cuerpo/UI genéricos y medición de latencia.
- MFA voluntario/obligatorio por roles múltiples: desafío sin tokens.
  Caducidad en autenticación completa, incluido OTP.
- Refresco válido/vencido/revocado/desconocido/reutilizado; carrera de dos
  renovaciones, logout/reúso y revocación de cuenta. PostgreSQL real verifica
  transacciones; otra familia conserva su estado conforme a SPEC-06.
- Logout sin acceso y con refresco ya invalidado: 204; sin red limpia cliente.
- UI-01.1–01.9 y sesión/rutas UI-00.1–00.6: foco, teclado, doble clic,
  renovación compartida, recarga y retorno externo rechazado.
- JWT real conforme al plan de identidad; auditoría sin contraseñas, OTP
  ni tokens completos.

Objetivos: login p95 <800 ms a 50 concurrentes; refresh p95 <300 ms a 50
(valor propuesto en SPEC-06 que Jose Luis confirma antes de revisión final).
Registrar entorno, comandos, resultados y desviaciones revisadas.
Validación previa del kit no es prueba de esta implementación.

EP-02 completa exige RF-05.1–05.7 y RF-06.1–06.6, UI, composición real,
pruebas, revisión y despliegue. La operación /pedidos/me es aceptación de G5.

## 8. Riesgos y recuperación

Respuesta perdida de refresh exige login; reintento ciego causa reúso.
Rollback mal organizado puede restaurar una familia que debía revocarse:
se comprueba persistencia tras el 401. Reiniciar conserva claves/familias.
Fixtures usan base de prueba y UUID acordados, sin reescribir migraciones
aplicadas ni borrar datos ajenos.

Si la revisión encuentra ambigüedad funcional, se ajusta primero la spec
y este plan. Las tareas ya derivadas se ejecutan en el tablero H3; este documento no modifica wire,
claims ni reglas públicas.
