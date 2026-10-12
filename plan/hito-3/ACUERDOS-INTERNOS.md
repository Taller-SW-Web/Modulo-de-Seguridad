# H3 — composición interna cerrada

**Versión:** 1.0.0, 12-oct-2026. **Estado:** diseño cerrado para derivar tareas.
Luis comunica la aprobación formal de los siete dueños sobre alcance, requisitos,
UI y composición, y delega los detalles técnicos restantes. La aprobación se
registra como comunicación en esta conversación; no como siete comentarios
de GitHub ni como evidencia de implementación. [Planes individuales](README.md).

## 1. Límites y autoridad

Un monolito Spring Boot, una base PostgreSQL en Supabase y una SPA React/TypeScript.
Las capacidades se llaman mediante interfaces Java dentro del mismo proceso.
No hay HTTP entre épicas, bases por estudiante ni siete microservicios.
Cada endpoint, regla, pantalla y prueba conserva el dueño de su épica.

Cada tabla tiene **un dueño de entidad, repositorio y escritura**. Un consumidor
no importa repositorios/entidades ajenos, no ejecuta SQL sobre sus tablas y no
recibe entidades JPA. Consume DTO inmutables y operaciones del dueño. Una FK
no autoriza cascadas JPA entre dominios. Las transacciones conjuntas invocan a
los proveedores reales; no copian su persistencia.

La tabla `usuario` ya reúne identidad, datos personales, bandera MFA y contadores.
Se conserva para H3 con Eva como única dueña física. Eva expone operaciones
internas estrechas de persistencia; **Valery decide y valida la edición propia;
Luis decide MFA/protección**. Ellos implementan sus servicios, HTTP, UI y pruebas,
y consumen esas operaciones igual que consumen auditoría o sesiones.
No se crean repositorios Usuario alternativos ni un PATCH interno genérico.

La propiedad física completa está en [persistencia](DISENO-PERSISTENCIA.md).
Los paquetes existentes permanecen; cada capacidad exporta un subpaquete `api`
con sus interfaces y DTO. El resto es interno. El filtro de acceso crea el actor
desde un token verificado; ningún cuerpo HTTP decide actor, permiso o familia.

Separar beans de consulta/persistencia de coordinadores HTTP: los adapters
de Cuentas, Credenciales, Sesiones, Autorización y Auditoría dependen de sus
repositorios; los coordinadores Login/MFA/Roles/Perfil componen esos ports.
SesionesPort recibe identidad ya resuelta y no inyecta LoginService o MfaService;
el adaptador outbox no inyecta al worker que consume productores. Así se evita
una dependencia circular Spring. No habilitar allow-circular-references ni
usar @Lazy para ocultar un ciclo. FirmaPort tampoco depende de sesiones.

## 2. Interfaces y resultados acordados

Estas son firmas de diseño, no archivos Java generados ni nuevos endpoints.
Los métodos mutantes participan en la transacción del caso de uso mediante
`Propagation.MANDATORY`; las consultas no abren una escritura oculta.
UUID y tiempos se tipan, y los resultados de negocio se modelan como variantes
cerradas. Ningún DTO de esta tabla se serializa automáticamente como respuesta pública.

| Interfaz / dueño | Operaciones internas | Resultado / restricción |
|---|---|---|
| `CuentasPort` — Eva | `buscarPorCorreoNormalizado(String)`, `leer(UUID)`, `bloquear(UUID)`, `contarActivos(Set<UUID>)`, `consultar(FiltroCuenta, Paginacion)` | Optional/CuentaSnapshot/página; snapshot contiene identidad, estado base/efectivo, verificación, bandera MFA y contadores, sin credencial ni documento claro. `bloquear` toma lock de fila y devuelve copia inmutable |
| `CuentaEscrituraInterna` — Eva | `actualizarBasicos(UUID, CambioBasicos)`, `guardarContadores(UUID, ContadoresProteccion)`, `guardarMfaVoluntario(UUID, boolean)` | Actualización acotada en cuenta bloqueada; no agrega roles ni modifica estado por edición de perfil. Última operación se reserva para el incremento SPEC-10 |
| `CredencialesPort` — Juan José | `preverificar(UUID opcional, SecretoEfimero)`, `confirmarRevision(RevisionCredencial)`, `evaluarCaducidad(UUID, Set<Rol>, Instant)`, `validarNueva(UUID, SecretoEfimero)` | Coincide/no coincide y revisión identificada por id/version; inexistente hace comparación equivalente. Nunca entrega hash, contraseña ni entidad. Caducidad se comprueba al completar autenticación |
| `TokensUnUsoPort` — Juan José | `localizar(hash, Proposito)`, `emitir(UUID, Proposito, Duration)`, `consumir(id, UUID, Proposito, Instant)`, `invalidarPrevios(UUID, Proposito)` | Prueba de propósito, usuario, vigencia y uso único; token recién generado solo llega al productor y al adaptador de correo, sin logging. H3 usa `DESBLOQUEO_AUTOMATICO` |
| `ProteccionPort` — Luis | `evaluar(CuentaSnapshot, Instant)`, `registrarFallo(CuentaSnapshot, HuellaFallo, ContextoPeticion)`, `completarLogin(UUID)`, `desbloquear(PruebaEnlace, ContextoPeticion)` | Estado y transición tipados; Luis posee bloqueos/intentos/huellas y consume escritura de contadores de Eva. Automático conserva familias; enlace no levanta manual |
| `MfaPort` — Luis | `esEfectivo(boolean, Set<Rol>)`, `crearDesafio(UUID, RevisionCredencial)`, `solicitar(PruebaDesafio, Canal)`, `verificar(PruebaDesafio, CodigoEfimero)`, `leerEstado(UUID, Set<Rol>)` | Desafío/entrega/prueba de MFA o resultado canónico. EMAIL por defecto. La prueba de éxito es interna, no construible desde un DTO HTTP ni reutilizable para otro propósito |
| `SesionesPort` — Jose Luis | `abrir(UUID, IdentidadAutorizada, ContextoPeticion)`, `renovar(SecretoRefresco)`, `cerrar(SecretoRefresco)`, `resolverAcceso(AccesoValidado)`, `revocarTodas(UUID)`, `revocarOtras(UUID, UUID familiaActual)` | SesionIniciada/resultado o ContextoSesion; `resolverAcceso` usa el jti verificado y relación persistida. Reúso solo revoca su familia; apertura desde MFA comparte el commit del consumo |
| `AutorizacionPort` — Sergio | `resolver(UUID)`, `exigirPermiso(ActorUsuario, Permiso)`, `exigirTitularOPrivilegio(ActorUsuario, UUID, Permiso)`, `bloquearCatalogoAdministrador()`, `usuariosConRol(Rol)` | Roles/permisos actuales G7 y conjunto UUID; permiso protegido se revalida en servidor. Sin scopes de servicio como permisos ni catálogo de roles consultado desde React con secretos |
| `FirmaPort` — Sergio | `firmar(ClaimsAcceso)`, `validar(String token)`, `jwks()`, `descubrimiento()` | RS256, kid público, issuer del entorno, UUID sub, tipo acceso y 900 s. No calcula contraseña/roles ni abre familias; no introduce aud de servicio en acceso de persona |
| `PerfilPort` — Valery | `leerPropio(ActorUsuario)`, `proyectarPara(ActorUsuario, UUID)`, `actualizarPropio(ActorUsuario, CambioBasicos)` | Proyección contractual y validación de campos; consume cuentas/roles/MFA y sus perfiles propios. Documento se enmascara desde descifrado autorizado, nunca desde texto cifrado |
| `AuditoriaPort` — Christian | `registrarCritica(RegistroSeguro)`, `registrarNoCriticaTrasCommit(RegistroSeguro)`, `actividadPropia(UUID, Paginacion)` | Catálogo, detalle permitido e identificador interno para deduplicar. Crítica comparte commit; no crítica tiene intento/reintento independiente. No recibe secretos |
| `MensajeriaPort` — Christian | `guardarEvento(EventoUsuario)`, `guardarCorreo(EntregaCorreo)` | UUID outbox; ambas participan en commit del productor. Cifra entrega sensible, publica/envía fuera del lock y conserva reintentos. Productor decide hecho, propósito y contenido |

Tipos comunes: `ActorUsuario(sub, jti, roles, permisos)`, `ContextoPeticion(ip,
agente, correlacionId)`, `ContextoSesion(usuarioId, familiaId, accessJti,
expiraEn)`, `CambioBasicos` con presencia explícita de campos y
`RegistroSeguro(registroId, accion, resultado, actor, objetivo, fecha, detalle)`.
El actor y ContextoSesion solo proceden de acceso validado y resolución interna.

`SecretoEfimero`, códigos y tokens tienen vida de petición, representación
redactada y prohibición de `toString`/logs. No son campos del contexto de auditoría.
Los ports no devuelven un mapa libre que permita sobreescribir un agregado.

## 3. Transacción, locks y errores

Un coordinador del dueño del endpoint abre una transacción `REQUIRED`,
aislamiento `READ_COMMITTED`, un solo DataSource/JpaTransactionManager.
Los providers mutantes se invocan mediante beans Spring distintos; no confiar
en una anotación sobre una llamada a sí mismo. Críticas y outbox se insertan
en esa misma transacción. No usar REQUIRES_NEW para revocación, OTP, contador
o auditoría crítica; una falla técnica revierte el conjunto.

**Orden único de adquisición:**

1. Sergio bloquea la fila fija `rol.ADMIN_SISTEMA` cuando la operación afecta
   roles o disponibilidad: asignación/revocación, registro de fallo de login,
   desbloqueo explícito, y futuras baja/reactivación/bloqueo manual.
2. Eva bloquea filas `usuario` necesarias, actor y objetivo cuando corresponda,
   por UUID ordenado. Un UUID se bloquea una sola vez.
3. Jose Luis bloquea las raíces existentes de familia, por UUID ordenado.
4. Dueño bloquea filas internas: credencial; protección/huellas; desafío,
   OTP y token de uso único, siempre en orden estable dentro de cada conjunto.
5. Se insertan auditoría/outbox, sin reclamar mensajes para envío.

No todas las operaciones necesitan todos los niveles. Una familia nueva tiene
UUID nuevo y ninguna raíz previa que bloquear; su inserción en OTP correcto
no adquiere locks sobre familias existentes después del desafío. Ningún port
invocado posteriormente vuelve a un nivel anterior. Lecturas para localizar
usuario/familia antes del lock son orientativas; se revalidan bajo lock.

El catálogo de administrador serializa revocaciones sobre usuarios distintos
y transiciones de disponibilidad. La comprobación del último administrador usa
UUID de asociaciones obtenidos de Sergio y estado efectivo obtenido de Eva,
sin un repositorio de Sergio que escriba/lea directamente `usuario`. El bloqueo
automático sigue permitido por sus reglas; el guard serializa, no añade una
excepción funcional para administradores.

Para evitar hashing costoso bajo el guard global, Juan José preverifica la
contraseña antes de la transacción y devuelve una revisión opaca. Bajo el lock
de usuario confirma esa revisión. Si cambió, se abandona sin efectos y se
recalcula antes de tomar locks, como máximo una repetición interna; no se
entregan tokens sobre una credencial anterior. No repetir una operación cuyo
commit o entrega de respuesta resulte incierto.

Los rechazos que **deben persistir una defensa** se devuelven como resultado
de negocio de la transacción: fallo de contraseña, intento OTP agotado y
reúso del refresco. El controlador convierte ese resultado en HTTP **después
del commit**. Lanzar una excepción rollback para esos 401 borraría la defensa.
Los errores técnicos sí revierten. No se inventan códigos públicos para locks;
se respeta el catálogo y las respuestas técnicas ya previstas en OpenAPI.

No se retienen locks durante SMTP, RabbitMQ, HTTP a módulos o trabajo React.
Un conflicto transaccional vuelve a ejecutar solo trabajo no confirmado,
con límite; pruebas de concurrencia verifican efectos, no solo respuestas.

## 4. Composición de los recorridos

| Recorrido / coordinador | Secuencia y atomicidad |
|---|---|
| Login sin MFA / Jose Luis | Normalizar correo; comparación real/equivalente Juan José; si fallo elegible, guard→cuenta y registro Luis, revalidando revisión. Si correcto, cuenta→roles/protección→caducidad; abrir familia, reset por login completo y firmar. Entregar resultado tras commit; audit no crítica independiente |
| Login con MFA / Jose Luis | Contraseña válida y cuenta disponible→Luis crea desafío persistido; no familia, acceso ni reset de racha todavía. No revelar caducidad de gestión antes de completar MFA |
| Solicitar OTP / Luis | Localizar desafío→cuenta→desafío/códigos; revalidar vigencia, destino y cuota; invalidar anterior, crear OTP y correo cifrado en outbox. Cuota se consume al aceptar la solicitud persistida; falla de transporte no devuelve cuota ni extiende desafío |
| Verificar OTP / Luis | Cuenta→desafío→OTP; revalidar cuenta y revisión de credencial del desafío, roles/MFA y caducidad. Incorrecto incrementa intentos y confirma el rechazo. Correcto consume pruebas y abre una familia nueva con Jose Luis en el mismo commit; luego reset de login completo y resultado. Nunca dos familias por un OTP |
| Refresh/logout / Jose Luis | Hash localiza usuario/familia→cuenta→raíz; reread token. Rotación marca usado, crea siguiente y relación de acceso. Reúso revoca familia y confirma antes del 401; logout desconocido conserva 204. Automático previo no revoca familia |
| Cambio contraseña / Juan José | Actor validado→cuenta→familias ordenadas→credencial/historial; resolver familia actual por jti, verificar actual/nueva, guardar hash/historial, revocar otras, crítica y correo outbox. Conservar familia actual; sin familia activa resoluble, no cambiar |
| Cambio rol / Sergio | Guard→actor/objetivo→familias; reautorizar, proteger propio/último ADMIN, cambiar asociación, revocar refrescos, crítica y evento completo. Error no deja asociación ni revocación parcial |
| Quinto fallo / Luis, consumido por login | Guard→cuenta→protección/huellas/token; incremento distinto, bloqueo progresivo, invalidar enlace anterior, emitir nuevo, crítica+correo+evento outbox. No revocar familias |
| Desbloqueo enlace / Luis | Localizar token sin consumir→guard→cuenta→bloqueo/token; propósito/usuario/version del bloqueo/vigencia; no manual. Consume enlace, quita automático, reset de intentos/huellas, crítica+evento, sin reset de bloqueos seguidos |
| Perfil propio / Valery | Actor→cuenta; validar titular/campos; llamar escritura acotada Eva, actualizar perfiles propios si aplica; celular cambiado pierde verificación; auditoría según criticidad del catálogo y evento con nombres de campos, sin valores |
| Actividad / Christian | Validar actor, filtrar objetivo=sub y paginar antes de proyectar. Lectura auditada una vez con detalle seguro; sin recursión. No acepta identidad elegida por query |

Un desafío incluye revisión interna de la credencial: cambiar contraseña tras
su emisión lo invalida aunque el código sea correcto. Se responde con el error
existente de desafío inválido, sin añadir campos públicos. La política de
credencial caducada conserva 403 únicamente al completar autenticación.

Los accesos emitidos pueden seguir válidos en G5 hasta exp. Revocar refrescos
no elimina accesos remotos ya firmados. G7 revalida permisos actuales de sus
operaciones privilegiadas; la UI no decide autorización. No se impone a G5
una consulta de familia ni introspección adicional en `/pedidos/me`.

## 5. Composición React y entrega entre dueños

Jose Luis mantiene router, sesión en memoria y cliente HTTP; Eva el contenedor
administrativo. Juan José política/medidor y cambio, Luis OTP/desbloqueo,
Sergio SeccionRoles, Christian Actividad y Valery MiCuenta. Cada uno implementa
su componente y su contrato de props/hooks; Valery revisa UX sin desarrollar
por los otros. [Entorno y React concretos](ENTORNO-Y-REACT.md).

| Orden de composición | Entrega habilitante | Aceptación real que libera |
|---|---|---|
| Bases | Cuenta/locks Eva; credencial/tokens Juan José; roles/firma Sergio; auditoría/mensajes Christian | Dependencias probadas por sus dueños; los dobles solo acreditan aislamiento |
| Acceso | Jose Luis compone proveedores; Luis entrega protección/desafío/OTP | CLIENTE accede, gestión completa MFA, refresco rota y sesión existe |
| Recorridos | Cada estudiante integra su pantalla, endpoints y efectos | Siete defensas propias; Luis presenta EP-04/EP-07 separadas |
| Nube y G5 | Configuración Christian/Sergio, login Jose Luis, pedidos Michael | Login real→JWT/JWKS→pedidos por sub, servicios desplegados |

El diseño de estas interfaces y locks ya no requiere otra aprobación rutinaria
antes de tareas. Las dependencias implementadas y la configuración del entorno
sí se comprueban durante ejecución. Una nueva regla funcional cambia primero
su spec; una discrepancia pública se resuelve con el PO antes de programarla.
