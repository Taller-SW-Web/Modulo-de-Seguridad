# H3 — persistencia, propiedad y concurrencia

**Versión:** 1.1.0, 12-oct-2026. Diseño técnico cerrado por delegación comunicada
por Luis; SQL, mappings, grants y base remota todavía no modificados.
[Composición y locks](ACUERDOS-INTERNOS.md) gobierna las llamadas.
El proveedor Supabase fue confirmado por Luis. La conexión MCP disponible
no identifica el proyecto G7; no se consultaron tablas de otro proyecto.

El diseño físico se implementa en el **backend nuevo** y sus adaptadores,
según [arquitectura hexagonal](BACKEND-HEXAGONAL.md). Modelos de dominio no son
entidades JPA; los mappings de §2 pertenecen a `adapter.out.persistence.entity`.
No reutilizar el pom, entidades/repositorios ni tests del backend antiguo.

## 1. Un dueño físico por tabla

La autoría histórica de un archivo V1/V2/V3/V4 no asigna todas sus tablas al
autor. Este reparto de persistencia corresponde al reparto vigente de épicas.

| Dueño físico | Tablas / objetos | Consumo permitido desde otra épica |
|---|---|---|
| Eva | `usuario`; mapping/proyección `usuario_estado_efectivo` | CuentasPort y CuentaEscrituraInterna. Ningún consumidor escribe sus columnas con SQL/JPA propio |
| Valery | `perfil_cliente`, `perfil_vendedor`, `direccion` | PerfilPort; direcciones no se implementan en H3 |
| Sergio | `rol`, `permiso`, `rol_permiso`, `usuario_rol`; `cliente_servicio`, `scope`, `cliente_servicio_scope` | AutorizacionPort; provisión de servicio queda posterior |
| Juan José | `credencial`, `password_historial`, `token_un_uso` | CredencialesPort y TokensUnUsoPort; productor Luis decide el desbloqueo |
| Jose Luis | `token_refresco`; nuevas `sesion_familia`, `acceso_sesion` | SesionesPort para apertura/resolución/revocación |
| Luis | `desafio_mfa`, `otp`, `intento_login`, `bloqueo`; nuevas `racha_fallo`, `huella_fallo` | MfaPort y ProteccionPort; contadores en usuario se guardan mediante Eva |
| Christian | `auditoria_seguridad`, `outbox`; worker/grants | AuditoriaPort y MensajeriaPort, dentro del commit del productor |

La vista es una proyección de lectura existente, no una segunda tabla Usuario.
Su mantenimiento lo coordina Eva con Luis; solo Eva expone esa lectura. El
resto no usa un repositorio propio contra la vista para saltarse los ports.
Las consultas de perfiles/composición reciben UUID/DTO y no crean relaciones
JPA navegables con entidades de otro dueño.

La regla se verifica en revisión y con una comprobación de dependencias por
paquete: un puente consume únicamente puertos de entrada y DTO exportados de otro dueño. El único usuario
PostgreSQL runtime del monolito no representa siete permisos por estudiante;
esa frontera se implementa en el código y su revisión, no se afirma que un
GRANT común la haga cumplir por sí solo.

Para filtrar listado por rol, Sergio entrega UUID mediante AutorizacionPort;
Eva combina ese conjunto con filtros de estado/correo y pagina/cuenta en su
consulta de cuentas. Conjunto vacío devuelve página vacía. No paginar primero
y filtrar después ni consultar usuario_rol desde un repositorio de Eva.
Proyección de roles/perfiles de una página se obtiene por lecturas por lote
de sus dueños, evitando un join JPA con entidades ajenas y consultas por fila.

## 2. Convenciones de mappings JPA

| Tipo / caso | Mapping acordado |
|---|---|
| Identificadores UUID | `UUID`, `@Id`, columna explícita; generar UUID de nuevas filas en aplicación. Mantener DEFAULT existentes para cargas controladas |
| Identity de auditoría | `Long`, `GenerationType.IDENTITY`; el DTO público respeta RegistroAuditoria |
| Fecha PostgreSQL timestamptz | `OffsetDateTime` en entidad, UTC; `Instant` en interfaces de dominio; sin LocalDateTime para vencimiento |
| Fecha de nacimiento | `LocalDate` y columna `date`, sin conversión a instante |
| Enum PostgreSQL existente | `@Enumerated(STRING)` y `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`, nombre/type exactos: estado_cuenta, codigo_rol, tipo_documento, canal_origen, resultado, tipo_bloqueo, actor_tipo. No ordinales |
| Canal/motivo/estado almacenados como varchar | Enum STRING o value object validado; no forzar NAMED_ENUM sobre una columna varchar |
| JSONB | `@JdbcTypeCode(SqlTypes.JSON)` y DTO/mapa validado; no concatenar JSON/SQL |
| FK a otro dominio | UUID escalar `usuarioId`/`credencialId`, sin ManyToOne ni CascadeType.ALL sobre entidad ajena |
| PK compuesta | `@EmbeddedId` para usuario_rol, rol_permiso, cliente_servicio_scope y huella_fallo; nombres/longitudes iguales al esquema |
| PK=usuario en perfil/bloqueo/racha | UUID asignado, sin generación ni cascada a Usuario |
| Vista de estado | Mapping inmutable/read-only, sin save/delete ni generación automática |
| PATCH propio | DTO con presencia diferenciada de omitido/null; whitelist antes de invocar escritura Eva. Omitido conserva valor; null solo se acepta donde OpenAPI lo permita |

`ddl-auto=validate`, `open-in-view=false`; Flyway realiza cambios físicos.
No usar update/create/create-drop en Supabase ni modificar migraciones aplicadas.
Los serializers nunca exponen entidades; controllers convierten resultados
tipados a los esquemas y errores existentes.

`@Lock(PESSIMISTIC_WRITE)` o SELECT FOR UPDATE **desde el repositorio dueño**
se ejecuta bajo la transacción coordinada. Repositorios existentes se revisan
porque tener un findByUsuarioId no prueba que bloquee. Guardar explícitamente
el hecho y usar flush antes de entregar resultado cuando la validación de
constraints sea necesaria; el cuerpo HTTP solo sale tras commit.

Para documento existente, Valery mantiene AES-256-GCM, clave por documento_key_id
fuera de DB y envelope versionado nonce/tag; su proyección muestra solo los
últimos tres dígitos según RF-16.4. No reinterpretar un formato histórico ni
fingir null si falta clave: contrastar dato y formato antes de incluirlo en
fixtures. Documento y claves nunca salen hacia Eva/React en claro.

## 3. Familia estable y acceso asociado — Jose Luis

Se elige una **tabla de familia separada**, en lugar de reutilizar la primera
fila de token_refresco como raíz. Así la raíz no caduca/se elimina al rotar
un token, y se preservan los familia_id existentes sin renumerarlos.

| Tabla | Columnas / constraints nuevas de diseño |
|---|---|
| `sesion_familia` | id UUID PK; usuario_id UUID NOT NULL FK usuario; revocada boolean NOT NULL default false; creado_en timestamptz NOT NULL; revocada_en timestamptz nullable; UNIQUE(id,usuario_id); CHECK que revocada_en sea coherente con revocada |
| `token_refresco` existente | Mantener columnas/nombres. Agregar FK compuesta (familia_id,usuario_id)→sesion_familia(id,usuario_id), después de validar datos. hash_token sigue único |
| `acceso_sesion` | jti UUID PK; usuario_id UUID NOT NULL; familia_id UUID NOT NULL; expira_en timestamptz NOT NULL; creado_en timestamptz NOT NULL; FK compuesta→sesion_familia; CHECK(expira_en>creado_en); índices familia_id y expira_en |

SesionesPort bloquea `sesion_familia` con PESSIMISTIC_WRITE. Todos los caminos
que rotan, cierran o revocan usan ese mismo lock después del usuario. Revocar
marca raíz y tokens de esa familia; una familia revocada nunca vuelve a activa.
Crear familia y primer token/acceso asociado comparte el commit del login.

Cada acceso real tiene jti nuevo y fila acceso_sesion, incluso tras refresh.
El JWT público **no recibe familiaId nuevo**. `resolverAcceso` verifica jti,
sub y exp contra esta relación y raíz activa; el navegador no elige la familia.
La fila se conserva mientras el acceso sea vigente. G5 no consulta esta tabla
ni recibe acceso a Supabase: valida firma y claims según el contrato.

Un token de refresco es 32 bytes aleatorios, base64url sin padding; se almacena
SHA-256 hexadecimal, nunca secreto completo. Vigencia 7 días desde su emisión;
cada rotación crea otro con esa vigencia, sin agregar un límite absoluto de
familia inexistente en la spec. Los tokens usados se conservan para detectar
reúso mientras exista algún token vigente de la familia. No limpiar la raíz
por vencer el primer token; limpieza automática no se implementa en H3.

El backfill futuro crea raíces a partir de familia_id existentes y exige que
cada familia pertenezca a un solo usuario. Cualquier revocación histórica de
la familia mantiene la raíz revocada. Datos incoherentes detienen migración;
no se corrigen escogiendo arbitrariamente un usuario. Un acceso histórico sin
asociación no puede demostrar familia actual para cambiar contraseña y debe
reauthenticarse; no se atribuye por sub.

## 4. Credencial e historial — Juan José

Se agrega `credencial.version bigint NOT NULL default 0`, mapping `@Version`.
Es la revisión interna fiable para preverificación y desafío; actualizado_en
queda como fecha del último establecimiento de contraseña, no como versión
de concurrencia. Actualizar password_hash/algoritmo/actualizado_en/version en
la misma transacción. Campos legacy de bloqueo de credencial no se utilizan
como segundo contador: la protección funcional reside en usuario/bloqueo.

Para nuevas contraseñas se fija **Argon2id, memoria 19 456 KiB, iteraciones 2,
paralelismo 1, salt 16 bytes, hash 32 bytes**, mediante PasswordEncoder de Spring
Security y su proveedor criptográfico requerido. Parametrización queda en el
hash codificado. Sin truncar, recortar ni normalizar la contraseña de login.
Para fixtures/hashes existentes se verifica por algoritmo real; BCrypt solo
si costo ≥12. No reinterpretar un hash inválido ni cambiarlo masivamente.

La política publicada conserva longitud mínima 10, complejidad, datos personales
y lista común de SPEC-07. Lista común se carga de archivo externo versionado
en UTF-8, caché con recarga atómica cada 60 s; se conserva última versión válida
si una actualización falla. En primer arranque sin lista válida no se habilita
establecer contraseña. La lista no añade reglas HTTP fuera de la spec.

Historial contiene las últimas cinco contraseñas **utilizadas incluyendo la
actual**, orden estable creado_en/id. La fixture inicial agrega el hash actual
al historial mediante Juan José. Antes de cambiar comparar nueva con esas
cinco; después insertar hash nuevo y conservar cinco. Para datos anteriores
incompletos se incluye la actual en la comprobación y no se inventan hashes.
Caducidad: actualizado_en +90 días, o creado_en si corresponde, instante igual
al límite ya caducado, solo cuando exista cualquier rol de gestión.

## 5. Desafío y OTP — Luis

| Objeto | Diseño adicional |
|---|---|
| `desafio_mfa` | Mantener id/usuario/hash/expiracion/consumido/creado. Añadir credencial_id UUID y credencial_version bigint para revisión de contraseña. Nuevos desafíos exigen ambas; históricos sin vínculo se rechazan como inválidos |
| `otp` | Añadir desafio_id UUID, invalidado_en timestamptz, hash_key_id varchar(50). FK compuesta(desafio_id,usuario_id)→desafio_mfa(id,usuario_id). Propósito LOGIN_MFA exige vínculo en registros nuevos; no convertir OTP históricos de otro propósito en desafíos de login |
| Índices OTP | (usuario_id,creado_en) para cuota móvil; (desafio_id,creado_en) para último código. FK/constraints se validan tras contraste de datos históricos |

Desafío: 32 bytes aleatorios/base64url, SHA-256 en DB, 300 s; EMAIL por defecto.
OTP: SecureRandom de 000000 a 999999, HMAC-SHA-256 con clave externa identificada
por hash_key_id y contexto inequívoco propósito/usuario/desafío/otp. No guardar
código reversible en tabla OTP; comparar en tiempo constante.

`usado` indica consumo exitoso; reenvío y tercer fallo usan invalidado_en.
Ambos impiden consumo posterior. Reenvío invalida anterior del mismo desafío
bajo lock y crea nuevo código por 300 s; desafío no se extiende. Antes de
verificar comprobar desafío, código más reciente, vigencia y límites.
Si cambia la versión de credencial, desafío deja de ser aplicable.

Cuota: COUNT de solicitudes aceptadas del usuario con creado_en > ahora−15 min,
incluyendo códigos usados/invalidados y todos sus desafíos. Cuenta se bloquea
antes de comprobar/insertar para que dos solicitudes no rebasen 3. No se
limpia OTP antes de cumplir esa ventana. Retry-After = ceil del tiempo hasta
que salga de ventana la solicitud más antigua que ocupa cuota, mínimo 1 s.
SMS sin celular no crea fila ni consume cuota. SMTP posterior fallido no
convierte una solicitud confirmada en inexistente.

Dos verificaciones correctas concurrentes se serializan por usuario/desafío;
consumen OTP y desafío y abren **una sola** familia nueva. Rechazo de tercer
intento se confirma antes de su 401. Fallo técnico al abrir familia revierte
consumo; no se retorna token firmado si commit falla.

## 6. Fallos distintos y enlace — Luis con capacidades de Eva/Juan José

| Tabla nueva / campo | Diseño |
|---|---|
| `racha_fallo` — Luis | usuario_id UUID PK/FK usuario; racha_id UUID NOT NULL; hash_key_id varchar(50) NOT NULL; creado_en timestamptz; UNIQUE(usuario_id,racha_id) |
| `huella_fallo` — Luis | usuario_id UUID; racha_id UUID; huella_hmac char(64); creado_en timestamptz; PK(usuario_id,racha_id,huella_hmac); FK compuesta→racha_fallo. Máximo cinco huellas por racha por regla bajo lock |
| `bloqueo` — Luis | Añadir revision UUID para identificar cada bloqueo. Bloqueo posterior genera otra revisión; tipo/hasta/motivo existentes se conservan |
| `token_un_uso` — Juan José | Añadir contexto_id UUID nullable, valor revision del bloqueo para DESBLOQUEO_AUTOMATICO. Otros propósitos mantienen su contexto propio o null |

HMAC con pepper externo y contexto longitud-prefijada de propósito, UUID,
racha_id y bytes exactos de la contraseña incorrecta. Nunca SHA sin clave ni
contraseña en intentos/auditoría/eventos. Clave y racha persisten entre reinicios;
hash_key_id permite usar la misma clave durante toda esa racha. Rotación no
se realiza perdiendo la posibilidad de deduplicar huellas anteriores.

Luis inserta huella única y solicita a Eva guardar el contador en el mismo
commit; duplicada deja contador igual, aunque el intento sí se registra.
Solo incorrecta sobre ACTIVO cuenta, sin ventana ni dependencia de IP.
Inexistente/pendiente/inactivo/bloqueado deja intento seguro sin sumar racha.
Quinto distinto crea un solo bloqueo/correo/evento. No usar contador legacy
credencial.intentos_fallidos ni contar filas intento_login como sustituto.

Reset por login completo, desbloqueo o vencimiento elimina huellas y renueva
racha_id, con contador cero mediante Eva. Solo login completo correcto pone
bloqueos_seguidos en cero. Vencimiento se proyecta desde la vista sin scheduler
ni evento; normalización física se hace al siguiente camino mutante bajo lock.
La racha de cuatro fallos no se purga por antigüedad: no existe ventana temporal.

Juan José emite token de 32 bytes aleatorios/hash SHA-256, propósito específico,
TTL 30 min y contexto revision; Luis lo consume mediante su port. Un nuevo
bloqueo invalida tokens anteriores del propósito en la misma transacción.
Enlace de recuperación/verificación no es válido para desbloquear. Bajo lock
se comprueba revision y bloqueo AUTOMATICO; nunca levanta MANUAL.

## 7. Auditoría y outbox — Christian

Auditoría agrega `registro_id UUID` interno único para deduplicar reintentos;
el id numérico/DTO público permanece. Registros históricos reciben identificador
en su migración mediante ADD COLUMN con DEFAULT gen_random_uuid() y NOT NULL,
sin UPDATE de filas ni deshabilitar el trigger append-only; comprobar el costo
de ese ALTER antes de aplicarlo. No editar contenido del hecho. Crítica se inserta junto al
hecho; no crítica se intenta después del commit en transacción independiente.
Si falla, mensaje seguro en cola privada durable `seguridad.auditoria.pendiente`,
publisher confirm, tres reintentos 10/30/60 s y DLQ privada. Repetición del mismo
registro_id no crea una segunda fila. Si DB y broker fallan, registrar fallo
técnico sin secreto y evidenciar el incidente; RF-12.6 no revierte login.

Runtime dispone solo SELECT/INSERT en auditoría y secuencia necesaria, sin
UPDATE/DELETE/TRUNCATE ni propiedad de tabla. Trigger existente se mantiene.
Separa identidad de migración/operación y futura purga; H3 no concede al runtime
la capacidad de borrar por retención. Conserva mínimo 90 días.

Outbox conserva PENDIENTE/ENVIADO/FALLIDO; agrega `proximo_intento_en`,
`reserva_id UUID`, `reservado_hasta`, `ultimo_error` sanitizado. Reclamar lote
máximo 20 con FOR UPDATE SKIP LOCKED, lease 60 s y commit rápido. Enviar fuera
de transacción de negocio; confirmar resultado en otra transacción condicionada
por reserva_id. Lease vencido permite recuperación tras reinicio. Poll 2 s.

Correo usa envelope AES-256-GCM (nonce aleatorio 12 bytes, tag 128 bits,
key_id y versión); AAD vincula id/topico/version. Clave externa separada de
RSA, huella y documento. Se cifra destino y contenido, incluidos código/enlace.
No se devuelve en GET públicos ni se imprime. Payload válido permanece
cifrado hasta entrega; después se reemplaza por metadatos sin contenido.
Entrega OTP/enlace vencida o invalidada se cancela y purga contenido, conservando
estado/causa segura. Worker pregunta vigencia al productor por port, no a su repo.

Eventos públicos mantienen envelope/payload del catálogo, eventoId estable,
exchange seguridad.usuarios topic durable, mensajes persistentes y publisher
confirm. Una publicación confirmada se marca ENVIADO; caída conserva outbox.
Tres reintentos tras intento inicial, esperas 10/30/60 s; agotado queda FALLIDO
y se intenta DLQ según catálogo sin borrar el hecho. Reejecución usa mismo
eventoId. SMTP usa Message-ID estable: una respuesta de red incierta aún puede
duplicar correo; no se promete entrega exactamente una vez. Un código anterior
recibido tarde sigue inválido en el verificador.

## 8. Migraciones previstas, sin ejecutar

V1–V4 y V100 se conservan como antecedentes en `backend/`; no son
un historial verificado del proyecto nuevo ni de Supabase. **V5 no existe** y V100 puede haber sido
aplicada, por lo que no se introducirán cambios como V5 detrás de esa versión.
Se reservan versiones posteriores en una secuencia única; Christian coordina
orden, pero cada dueño escribe solo la migración de sus tablas.

| Orden reservado | Dueño | Contenido |
|---|---|---|
| V101 | Juan José | credencial.version; revisión disponible para desafíos |
| V102 | Jose Luis | sesion_familia/acceso_sesion, backfill y FK de tokens |
| V103 | Luis | desafío revisión, OTP vínculo/hash_key_id/invalidado_en e índices |
| V104 | Luis | racha_fallo/huella_fallo y revision de bloqueo |
| V105 | Juan José | contexto_id de token de uso único, sin escribir bloqueo |
| V106 | Christian | registro_id de auditoría y campos/índices outbox |

Si una versión ya está ocupada en el historial remoto, se renumera antes de
crear el archivo y se conserva orden/dependencias; no se reemplaza la aplicada.
Grants/usuarios de entorno son operación administrativa separada con privilegios
de infraestructura, no credenciales runtime dentro de una migración del dominio.

Antes de aplicar: identificar proyecto/esquema y pg_version, revisar
flyway_schema_history, constraints/índices/grants y copia de seguridad disponible.
Si la base se creó con DDL manual sin historial, comparar exhaustivamente
V1–V4 antes de cualquier baseline explícito. No usar baseline-on-migrate=true,
repair automático, DROP/reset o V100 como supuesto saneamiento. Semillas H3
son protocolo explícito por capacidades, no ejecución ciega de contraseñas demo.

## 9. Comprobación prevista y fuentes técnicas

En tareas se probará PostgreSQL real/Testcontainers con las migraciones:
locks/reúso, último admin, OTP doble, quinta contraseña concurrente, reinicio
con racha/familia conservadas, rollback de crítica y outbox, grants negativos
y recuperación de lease. H2 no demuestra enum/JSONB/locks de PostgreSQL.
La lectura real de Supabase verifica versión/esquema antes de ejecutar cambios.

Fuentes primarias consultadas para el diseño: [locking de Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html),
[propagación transaccional](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html)
y [almacenamiento de contraseñas OWASP](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).
La parametrización escogida es una decisión local compatible con SPEC-01/07;
el rendimiento se medirá, no se acredita por citar esas fuentes.
