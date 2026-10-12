# H3 — Luis David Morales Brenis

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/luis-morales/TAREAS.md).
Una tarea lista por vez; EP-04 y EP-07 conservan criterios/evidencia separados.

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueño completo | Luis David Morales Brenis |
| Épicas | EP-04 segundo factor (SPEC-09/10) y EP-07 protección (SPEC-14/15) |
| Incremento / versión | H3-LUIS / 1.0.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 + complemento local; v1 1.1.0 |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-04](../../../specs/epicas/EP-04-segundo-factor.md), [SPEC-09](../../../specs/funcionales/SPEC-09-mfa-inicio-sesion.md), [10](../../../specs/funcionales/SPEC-10-activacion-mfa.md); [EP-07](../../../specs/epicas/EP-07-bloqueo-desbloqueo.md), [SPEC-14](../../../specs/funcionales/SPEC-14-bloqueo-automatico.md), [15](../../../specs/funcionales/SPEC-15-bloqueo-manual.md) |
| UI/UX | [FRONT-04 OTP](../../../specs/front/FRONT-04-desafio-otp.md), [FRONT-12 desbloqueo](../../../specs/front/FRONT-12-desbloqueo-con-enlace.md), composición de [01](../../../specs/front/FRONT-01-inicio-sesion.md)/[08](../../../specs/front/FRONT-08-admin-detalle-usuario.md); [diseño](../../../docs/diseno/sistema-diseno.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): /auth/otp/solicitar, /auth/otp/verificar, /auth/desbloquear; [v2 parcial](../../../specs/contrato-api/openapi-v2.yaml) solo para el cambio MFA posterior; [errores](../../../specs/catalogo-errores.md) |
| React | [OtpPage/useDesafioOtp](../../../specs/componentes-react/FRONT-04-desafio-otp.md), [DesbloqueoPage/useDesbloqueo](../../../specs/componentes-react/FRONT-12-desbloqueo-con-enlace.md), composición de [login](../../../specs/componentes-react/FRONT-01-inicio-sesion.md)/[detalle](../../../specs/componentes-react/FRONT-08-admin-detalle-usuario.md) |

Revisar [estados](../../../docs/arquitectura/estados-usuario.md),
[DAT-01](../../../docs/integracion/revision-datos.md) e
[interfaces H3](../ACUERDOS-INTERNOS.md). Las dos épicas tienen evidencia separada.

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

### EP-04 — recorrido H3

Contraseña correcta de cuenta con MFA efectivo → desafío, sin sesión →
OTP por EMAIL real → verificación → sesión real mediante Jose Luis.
Reenvío, uso único, vencimiento y límites forman parte del mismo recorrido.
La gestión obliga MFA aunque una bandera histórica esté false.

**Posterior:** activación/desactivación confirmada mediante v2/SPEC-10/FRONT-09
y proveedor SMS real cuando se acuerde. No implementar las rutas v1 históricas.
La demo muestra EMAIL; el adaptador SMS simulado de desarrollo/pruebas conserva
la rama del contrato existente, sin proveedor real ni código en la respuesta.
SMS sin celular mantiene el error definido; no se inventa canal preferido
ni verificación externa de contactos.

### EP-07 — recorrido H3

Cinco contraseñas equivocadas distintas y consecutivas sobre cuenta ACTIVO →
bloqueo automático y correo → enlace de uso único → pantalla propia →
desbloqueo → login permitido. Progresión y vencimiento efectivos conservan
la regla; nunca responder «cuenta bloqueada» en login.

**Posterior:** bloqueo/desbloqueo manual de SPEC-15, panel de sus acciones y
canal real de soporte H-14. El login sigue protegiendo una cuenta manualmente
bloqueada si existe como dato; desbloqueo por enlace jamás la levanta.

Luis implementa sus dos interfaces/controladores/servicios/datos/pruebas.
Jose Luis conserva login/sesión, Juan José credencial y Christian auditoría/
transporte. No repartir OTP frontend a otro alumno.

## 3. Cobertura y criterios

| Épica / fuente | Superficie | Resultado H3 |
|---|---|---|
| EP-04, RF-09.1–09.7; ESC-09.1/09.3/09.4/09.5/09.6 | Solicitar/verificar código | 6 dígitos, hash seguro, 300 s, tres intentos, uso único, errores canónicos |
| EP-04, RF-09.8/09.9/09.10/09.11; RF-10.1 | Desafío y continuación | Cuenta/desafío vinculados; rol de gestión obliga; caducidad revisada antes de sesión |
| EP-04, RF-09.12/09.14/09.16; ESC-09.12/09.14 | Auditoría, reenvío y cuota | OTP nuevo invalida anterior; desafío no se extiende; 3 solicitudes/15 min por usuario; Retry-After |
| EP-04, UI-04.1–04.8 | OtpPage | PinInput, pegar/foco, temporizador mínimo, error de intento y recarga sin desafío |
| EP-07, RF-14.1–14.7; ESC-14.2/14.5/14.7/14.8/14.10/14.11 | Login/protección | Contador independiente de IP; quinta entrada distinta bloquea; escalera 1/2/4 min y cuarto sin vencimiento |
| EP-07, RF-14.8/14.9/14.10/14.11; ESC-14.12/14.13/14.14 | POST /api/v1/auth/desbloquear | Correo real, 30 min/uso único/reemplazo, auditoría/evento y estado restaurado |
| EP-07, RF-15.4; ESC-14.16 | Familia previa a bloqueo automático | No revocarla; permite refresh según regla, pero prohíbe nuevo login |
| EP-07, UI-12.1–12.5; UI-08.1/08.2 | Pantalla y detalle | Abrir enlace no desbloquea; submit explícito; estados y expiración seguros |

## 4. Decisiones técnicas

### Desafío y OTP

Tokens opacos aleatorios con representación protegida en servidor. Login
produce desafío de 300 s según contrato; no se usa como acceso ni cambio MFA.
OTP generado mediante SecureRandom, hash HMAC-SHA-256 con clave externa y
contexto de desafío para evitar almacenar código recuperable en tabla otp.

DAT-01: V2 no tiene relación desafio_id coherente con el DDL descriptivo.
Luis implementa vínculo compuesto OTP/desafío y revisión de credencial,
invalidado_en/hash_key_id y locks cerrados en DISENO-PERSISTENCIA. Al verificar, revalidar
estado/caducidad/MFA y abrir familia real con Jose Luis; consumo atómico evita
dos sesiones. Fallos de OTP no incrementan el contador de contraseña.

Reenvío aceptado invalida código previo, sin extender desafío. Verificar exige
ambos vigentes; UI usa el menor tiempo. Cuota por usuario en ventana móvil,
no solo por IP. Desafío inválido da 401 TOKEN_INVALIDO sin envío; equivocado
401 CODIGO_INVALIDO, tercer fallo 401 OTP_INTENTOS_AGOTADOS, código expirado
410 OTP_EXPIRADO, cuota 429 con espera. Usar códigos del catálogo.

SMTP real y entrega asíncrona con Christian; MockSmsSender para la rama SMS
explícita de pruebas, con captura controlada solo en tests y sin logs públicos.
Outbox sensible cifrado y purgado
tras entrega, no código en claro ni logs. H3 no sustituye correo por mostrar
el código en React o por una respuesta fija.

### Protección y estado

V1 guarda contadores; V3 registra intentos/bloqueo y vista de estado efectivo.
Mapping existente no prueba deduplicación de contraseñas equivocadas:
[HMAC por racha](../DISENO-PERSISTENCIA.md) queda definido con tablas racha_fallo/huella_fallo.
Eva guarda contadores mediante CuentaEscrituraInterna; Luis no escribe usuario.
Una misma contraseña equivocada ya vista en la racha no suma otra vez.
No persistir contraseña en claro ni incluir huella en auditoría.

Bloqueo vive en su tabla y usuario.estado base ACTIVO; la API proyecta BLOQUEADO.
Vencido el automático, ya se considera ACTIVO sin job ni evento de vencimiento;
al siguiente acceso mutante normalizar contador/huellas de la racha vencida.
Contador de bloqueos seguidos solo se reinicia tras login completo correcto,
no después de emitir desafío o de desbloquear con enlace.

Serializar contador/transición por usuario. Quinto fallo no crea varios
bloqueos/correos/eventos ante concurrencia. No revocar familias existentes
en automático. Enlace aleatorio hasheado emitido/consumido por TokensUnUsoPort de Juan José,
con propósito y revision del bloqueo específicos,
30 min y revocación por bloqueo posterior; no aceptar token de recuperación.

### Interfaces

OtpPage/useDesafioOtp pertenecen a Luis y continúan LoginPage de Jose Luis.
DesbloqueoPage/useDesbloqueo leen token en memoria y lo eliminan de la URL;
solo consumen al pulsar, no al abrir ni por previsualización de correo.
Detalle administrativo muestra SeccionBloqueo para lectura, sin acciones
manuales H3. Mantener foco, doble submit y mensajes de FRONT-04/12.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Antes de |
|---|---|---|
| OTP/desafío/huella, locks DAT-01 | Luis con Jose Luis/Eva | Tareas de datos y verificación concurrente |
| Login/sesión y caducidad | Jose Luis, Juan José | Completar MFA sin tokens huérfanos ni eludir caducidad |
| Roles/MFA efectivo | Sergio/Luis | Gestión y proyección de perfil; seed false no evita obligatoriedad |
| Fixtures M/L/ADM separadas de A/B | Eva, Juan José y Sergio | Ensayo que no altere integración G5 |
| Auditoría crítica/outbox/correo | Christian con Luis productor | Bloqueo/desbloqueo real y rollback/reintento coherente |
| UI FRONT-04/12 aprobada H3 | Luis; UX Valery | Implementación y comprobación visual trazadas en tareas |

H-14 y cambio MFA v2 son posteriores. DAT-01 tiene diseño H3 cerrado;
sus migraciones y proveedores reales se verifican en implementación. Desafío/pantallas aisladas pueden probarse antes; no declarar
MFA integrado ni emisión segura por esas pruebas.

## 6. Fases y entregables

| Fase | Entrega de Luis | Verificación |
|---|---|---|
| Diseño común | Mapping/locks/huella y propósito de tokens | Revisión de dos dominios con fuentes estables |
| Segundo factor | Desafío, correo, solicitud/verificación y OtpPage | Recorrido real completo, expiración/cuota/reúso |
| Protección | Contador/bloqueo/vencimiento y notificación/token | Concurrencia, escalera y sesiones previas |
| Desbloqueo | Endpoint/pantalla y composición de login/detalle | Correo→submit→ACTIVO→login, sin bypass manual |
| Evidencia | Dos recorridos y casos negativos separados | Defiende cada épica con UI/HTTP/DB/pruebas propias |

## 7. Pruebas y terminado

**EP-04:** código correcto; incorrecto tres veces; expirado; otro desafío;
reutilizado; reenvío invalida anterior; cuarto envío/15 min; doble verificación.
Gestión con mfa_habilitado:false sigue requiriendo OTP. Cambiar estado o
caducar credencial entre desafío y confirmación no entrega sesión.
Recargar pierde challengeToken; mostrar nuevo login sin OTP en logs.

**EP-07:** fallos distintos bloquean al quinto, repetidos no suman, IP/tiempo
entre fallos no reinician. Login correcto reinicia racha/bloqueos; automático
vence en 1/2/4 min y cuarto no vence. No cambiar vigencia de producción para
acelerar ensayo: usar reloj de test en pruebas aisladas. Quinto concurrente
produce una transición y auditoría; falta de auditoría crítica revierte.

Enlace correcto, vencido, usado o sustituido; abrir página no lo consume.
Enlace no levanta manual. Familia previa al automático sigue renovando y
nuevo login se rechaza genéricamente. Probar correo/evento reales de su dominio.

Terminado exige evidencia separada de ambas épicas en el entorno real.
Verificación OTP objetivo p95 <300 ms, sin latencia SMTP. Los alcances
posteriores y carga completa no se declaran verificados por esta demo.

## 8. Riesgos y recuperación

No bloquear ni cambiar credenciales A/B. El público no conoce si una cuenta
está bloqueada; el titular se entera por correo. Fallo SMTP posterior al commit
deja reintento, no deshace bloqueo ni entrega éxito falso de correo.

No reutilizar el desafío de login para activar MFA/verificar celular.
No permitir consumo paralelo ni restauración de enlaces viejos. Si el mapping
no soporta propósito/vínculo/huella, resolverlo antes de programar su parte;
un lock Java local no cubre dos instancias del servicio.
