# H3 — tareas de Christian Gabriel Arancivia Salas

**Dueño:** Christian · **Épica académica:** EP-06 · **Incremento:** H3-CHRISTIAN.
**Adicional:** TEC-INFRA; no sustituye su funcionalidad de auditoría.
**Base:** [PLAN aprobado 1.1.0](../../../plan/hito-3/christian-arancivia/PLAN.md) y
[TEC-INFRA 0.1.0](../../../plan/transversales/TEC-INFRA.md), alcance H3 aprobado
según comunicación de Luis; diseño definitivo en documentos comunes 1.1.0.

**Resultado académico:** hecho real→registro seguro en DB→consulta solo del
titular→pantalla de actividad. **Resultado adicional:** entorno compartido,
transporte durable y servicios desplegados para recorridos individuales/G5.

**Fuentes comunes:** [SPEC-12](../../../specs/funcionales/SPEC-12-registro-auditoria.md),
[SPEC-13](../../../specs/funcionales/SPEC-13-consulta-auditoria.md),
[composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md),
[eventos](../../../specs/catalogo-eventos.md).

**Límite:** no auditoría administrativa/HistorialTabs, exportación, purga
automática, pruebas a escala, CI/CD completo, API Management ni observabilidad
de hitos posteriores. En H3 ya se conserva mínimo 90 días y solo-anexado.
Cada productor conserva su lógica; Christian no programa recorridos ajenos.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `auditoria y mensajeria` bajo el paquete raíz |
| Dominio | `domain.model`: RegistroAuditoria / MensajePendiente, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: RegistrarAuditoria / ConsultarActividad / EncolarMensaje; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: AuditoriaPort, MensajeriaPort; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-06-T001/T002; TEC-INFRA-T001/T003** y de las tareas de endpoints ya
existentes; no son otra funcionalidad ni duplican IDs. Cada tarea de endpoint
entrega caso de uso, DTO/mapping HTTP y adaptadores necesarios, además de su UI.
Los tests de mappers excluyen secretos y distinguen omitido/null; los tests del
núcleo usan puertos dobles, sin Spring/DB. La aceptación usa PostgreSQL y
proveedores reales. Reglas ArchUnit de TEC-INFRA-T001 verifican los límites.
El esqueleto compartido se prepara en TEC-INFRA-T001; puede adelantarse el
núcleo aislado, pero no cerrar persistencia/HTTP sin las dependencias reales.

## Orden de trabajo

TEC-INFRA-T001 y revisión inicial T002 identifican entorno/historia antes de
migrar. EP-06-T001 y TEC-INFRA-T003 preparan conjuntamente **una** V106 del
dueño, sin aplicarla hasta completar revisión. EP-06-T002/T003 y transporte
habilitan productores; consulta/UI pueden avanzar aisladas. Los cierres exigen
hechos, dependencias y servicios reales.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-06-T001 | Persistencia y formato seguro de auditoría | P0 | Revisión inicial TEC-INFRA-T002 | Fusionada (B#34) |
| EP-06-T002 | AuditoriaPort crítica en commit del productor | P0 | EP-06-T001; aplicación revisada de V106 | Fusionada (B#41) |
| EP-06-T003 | No crítica después de commit y reintento privado | P0 | EP-06-T001/T002; broker TEC-INFRA-T004 | Fusionada (B#41, B#45) |
| EP-06-T004 | Consulta propia paginada | P1 | EP-06-T002/T003; acceso de Sergio/Jose Luis | Fusionada (B#42) |
| EP-06-T005 | ActividadPage y TablaActividad | P1 | EP-06-T004; EP-02-T001/T008; navegación Valery | Fusionada (F#8) |
| EP-06-T006 | Verificar privacidad, titularidad y atomicidad | P0 | EP-06-T002/T003/T004; productores y grants reales | Pendiente |
| EP-06-T007 | Defender actividad propia desplegada | P1 | EP-06-T005/T006; TEC-INFRA-T005; hechos reales | Pendiente (despliegue) |
| TEC-INFRA-T001 | Identificar/configurar entorno y build | P0 | Accesos privados al proyecto/servicios correctos | Fusionada (B#13) |
| TEC-INFRA-T002 | Revisar DB, coordinar migraciones y privilegios | P0 | TEC-INFRA-T001; migraciones de cada dueño al aplicar | En curso (B#35, B#44) |
| TEC-INFRA-T003 | MensajeriaPort y outbox con reclamo durable | P0 | Revisión DB; V106 conjunta con EP-06-T001 | Fusionada (B#41) |
| TEC-INFRA-T004 | Correo y eventos con reintentos/confirmación | P0 | TEC-INFRA-T003; SMTP/broker y ports de validez reales | En revisión (B#41, B#46) |
| TEC-INFRA-T005 | Desplegar y verificar backend/SPA en nube | P0 | Build de dueños; DB/grants/transporte/claves revisados | En curso (B#36) |
| TEC-INFRA-T006 | Comprobar entorno en prueba conjunta G5 | P0 | TEC-INFRA-T005; login/JWKS G7 y servicio Michael | Pendiente (prueba conjunta G5) |

Revisión inicial y aplicación de TEC-INFRA-T002 son pasos sucesivos, no una
dependencia circular. EP-01/02/03/04/05/07 identifican tareas de sus dueños.
Valery tiene su propio tablero para perfil/navegación; consumir sus entregas.

## EP-06 — auditoría y actividad propia

### EP-06-T001 — Mapear registros y validar el formato seguro

- **Fuentes:** RF-12.1–12.5; persistencia §§2/7/8, V106 reservada.
- **Implementar:** entidad/repositorio propios `auditoria_seguridad`, DTO interno
  tipado y validación de acciones/criticidad/detalle. Añadir `registro_id` UUID
  único interno; mantener id numérico y DTO público. Integrar en una V106 los
  campos outbox de TEC-INFRA-T003 antes de aplicar; si ya aplicada, nueva versión.
- **Cierre:** histórico obtiene UUID con ALTER ADD COLUMN DEFAULT gen_random_uuid()
  NOT NULL, sin UPDATE ni desactivar trigger. Detalle permitido por acción, sin
  contraseña/hash/OTP/token/documento claro; token referenciado solo por jti.
- **Comprobar/evidencia:** migration/mapping en PostgreSQL, coste del ALTER sobre
  datos previos, catálogo inválido/secretos rechazados o saneados y registro_id
  duplicado. No ejecutar cambios remotos sin revisión ni purgar auditoría.

### EP-06-T002 — Entregar registro crítico transaccional

- **Fuentes:** RF-12.6, ESC-12.6; composición §§2–4 `AuditoriaPort`.
- **Implementar:** interfaz/DTO y adaptador `registrarCritica` con MANDATORY,
  misma transacción/DataSource del productor; sin REQUIRES_NEW. Productor decide
  hecho/actor/objetivo/contexto y Christian valida formato/catálogo.
- **Cierre:** crítica y mutación confirman juntas; fallo de insertar auditoría
  revierte negocio/familias/outbox. Actor/contexto vienen del acceso verificado
  o propósito autorizado, nunca identidad libre elegida por el navegador.
- **Comprobar:** rollback con productores reales de contraseña/roles/bloqueos,
  invocación sin transacción rechazada y duplicación identificada sin doble fila.
- **Entrega/evidencia:** tests y contrato a Juan José/Sergio/Luis; no implementar
  sus mutaciones ni abrir transacciones que oculten el fallo crítico.

### EP-06-T003 — Registrar no críticas después del commit y reintentar

- **Fuentes:** RF-12.3/12.5/12.6; persistencia §7; composición §2.
- **Implementar:** `registrarNoCriticaTrasCommit`: intento independiente tras
  commit; fallo persiste mensaje seguro en cola privada durable
  `seguridad.auditoria.pendiente` con confirm, reintentos 10/30/60 s y DLQ privada.
  Deduplicar registro_id; separar adapter/consumer sin ciclo de beans.
- **Cierre:** fallo de auditoría no deja rollback-only en familia ya confirmada;
  no registrar hecho exitoso si productor revirtió. Cola privada no es evento
  público ni suscripción G5. Si DB/broker fallan, incidente técnico sanitizado,
  sin afirmar escritura ni revertir login confirmado.
- **Comprobar/evidencia:** DB caída/restaurada, repetición del mismo mensaje,
  agotamiento y recuperación controlada; broker caído y logs sin contenido
  secreto. No declarar éxito final de registro solo por encolar.

### EP-06-T004 — Implementar GET /api/v1/auth/me/actividad

- **Fuentes:** RF-13.4/13.5, ESC-13.6; OpenAPI actividad/PaginaAuditoria;
  composición §4.
- **Implementar:** consulta SQL en su repositorio, servicio y controlador propios;
  objetivo=sub del actor validado, orden fecha descendente/desempate estable,
  paginación/total antes de proyectar. No cargar todos para filtrar en React.
- **Cierre:** cualquier persona autenticada consulta solo su cuenta sin
  auditoria.ver; objetivoUsuarioId/actor/query manipulados nunca seleccionan otro
  titular. Identidad desconocida de login tiene objetivo null, no se atribuye
  a una cuenta. Parámetros y DTO/campos según contrato, sin detalle secreto.
- **Comprobar:** A/B aislados, sin token 401, servicio válido no aplicable según
  G7, vacío y varias páginas; registrar AUDITORIA_CONSULTADA una sola vez con
  detalle seguro, sin HTTP recursivo ni cadena de lecturas para auditar.
- **Evidencia:** tests DB/HTTP y totales/orden; no añadir consulta administrativa,
  exportación o filtro seleccionable de identidad.

### EP-06-T005 — Construir actividad reciente del titular

- **Fuentes:** [FRONT-13 UI-13.1–13.4](../../../specs/front/FRONT-13-actividad-propia.md)
  y [componentes FRONT-13](../../../specs/componentes-react/FRONT-13-actividad-propia.md).
- **Implementar:** `src/pantallas/actividad/`: `ActividadPage`,
  `TablaActividad`, `etiquetasAccion`, `useActividad`; HTTP/router/sesión de
  Jose Luis, enlace desde perfil de Valery y cambio de contraseña de Juan José.
- **Cierre:** carga/vacío/error/reintento, paginación 20 por defecto, fechas Lima,
  etiquetas canónicas y acción desconocida segura; advertencias con texto/icono.
  No mostrar detalle bruto/actorId ni enlace a configuración MFA pendiente.
- **Comprobar/evidencia:** tests de estados/páginas, acción desconocida, teclado/
  anuncio de página y tarjetas 390 px; recorrido con API real. No exportación,
  selección de otra cuenta o llamada a `/auditoria` administrativo.

### EP-06-T006 — Verificar privacidad, aislamiento y efectos de auditoría

- **Fuentes:** PLAN §7; RF-12.4–12.7 y RF-13.4/13.5.
- **Inicio:** escritor/consulta/providers reales y rol runtime de TEC-INFRA-T002.
- **Cierre:** hechos reales de login/fallo, OTP, contraseña y bloqueo producen
  registros seguros; críticas revierten al fallar, no críticas reintentan sin
  tumbar login. A no ve B; UPDATE/DELETE/TRUNCATE rechazados con runtime real.
- **Comprobar:** tests PostgreSQL/HTTP, grants y reintentos/deduplicación; mínimo
  90 días conservados sin purga. Campos sensibles ausentes en DB/DTO/logs/cola.
  No fabricar filas para decir que productores aún pendientes ya auditan.
- **Evidencia:** comandos/resultados sanitizados, estado posterior a fallos y
  revisión con productores; prueba de un millón de registros queda posterior.

### EP-06-T007 — Defender auditoría y actividad propia desplegadas

- **Fuentes:** PLAN §7; tareas EP-06 anteriores y FRONT-13.
- **Inicio:** UI/API reales, login/proveedores y despliegue Christian.
- **Cierre:** una acción real deja registro DB; titular abre actividad y ve solo
  hechos propios ordenados/paginados. Mostrar segunda identidad aislada y
  rechazo sin token; explicar crítica/no crítica y protección solo-anexado.
- **Comprobar/evidencia:** navegador→HTTP→consulta→DB→pantalla en nube, pruebas,
  revisión/URL, capturas seguras y uso de IA trazado a fuentes/tareas. No borrar
  auditoría para preparar demo vacía; DevOps por sí solo no cierra EP-06.

## TEC-INFRA — capacidad técnica adicional

### TEC-INFRA-T001 — Preparar configuración y build del entorno compartido

- **Fuentes:** TEC-INFRA §§3–4; entorno/React §§1/3/5; TEC-01/TEC-04.
- **Implementar:** configuración de desarrollo/prueba/nube y procedimiento de
  build; Java 21/Boot 3.5.16/Gradle 8.14.3, dependencias Gradle reales, scripts/lockfile React del
  dueño Jose Luis. Integrar starters JOSE/Mail/AMQP con sus dueños, sin versiones
  transitivas improvisadas ni reemplazo por Supabase Auth. Preparar wiring de
  puertos/decoradores y reglas ArchUnit 1.5.1 del diseño hexagonal: núcleo sin
  frameworks, persistencia encapsulada, puentes solo a exports y ausencia de
  ciclos. No producir casos de uso/entidades de otros dueños ni cambiar sus IDs.
- **Cierre:** identificar proyecto G7 real y conexiones privadas, host backend/
  SPA, SMTP/broker, CA y responsables. JDBC session pooler 5432 para host IPv4
  o directa según conectividad del diseño; sslmode=verify-full, Hikari fijado,
  una instancia H3. Nada de defaults locales como credenciales de nube.
- **Comprobar/evidencia:** `gradlew compileJava compileTestJava`, `test` y
  `bootJar`; tests de arquitectura con clases reales. Registrar fallo por Docker
  ausente sin omitir tests ni conectarlos a Supabase. Build/entorno local nuevos,
  versiones/variables documentadas sin valores secretos. Falta de acceso o
  plataforma se registra como bloqueo de ejecución; no crear recursos de pago
  ni adivinar host/región/proyecto para llenar configuración.

### TEC-INFRA-T002 — Revisar DB y coordinar migraciones/grants

- **Fuentes:** persistencia §§1/8; entorno/React §1; RF-12.4/12.7.
- **Paso inicial:** identificar versión/schema/historia Flyway, constraints,
  grants/RLS/vista y copia de seguridad del proyecto correcto. Registrar
  diferencias DAT-01 antes de aplicar; no asumir otra DB conectada como G7.
- **Paso de aplicación:** cada dueño escribe sus migraciones V101–V106; Christian
  coordina orden/versiones y ejecución controlada después de revisión. Renumerar
  si versión ya ocupada; no V5 detrás de V100, repair/reset/DROP/baseline automático
  ni ejecución ciega de seeds. DB manual sin historial exige comparación previa.
- **Cierre:** identidad migrador separada de g7_runtime; runtime sin owner/
  superuser/BYPASSRLS, auditoría SELECT/INSERT+secuencia, no UPDATE/DELETE/TRUNCATE.
  Revocar acceso anon/authenticated/PUBLIC a G7 expuesto; grants/políticas del
  servidor y vista según diseño, sin auth.uid() para JWT propio. Catálogo admite
  lock pero no API de edición. Configuración nube baseline-on-migrate=false.
- **Comprobar/evidencia:** pruebas con runtime real de lectura/escritura autorizada
  y prohibida; mappings validate sin ddl automático. Administración/grants son
  operación infraestructura, no autorización para editar datos de otros dueños.

### TEC-INFRA-T003 — Implementar MensajeriaPort y worker de outbox

- **Fuentes:** persistencia §7; composición §§2–4; catálogo de eventos.
- **Implementar:** DTO/adapter de guardarEvento/guardarCorreo transaccionales
  MANDATORY y repositorio outbox propio. Incorporar a V106 conjunta los campos
  de reintento/lease/errores antes de aplicación; estados existentes conservados.
- **Cierre:** hecho/outbox confirman juntos; reclamo de hasta 20 filas SKIP LOCKED,
  lease 60 s, poll 2 s, commit rápido. Enviar fuera de transacción; confirmar
  resultado en otra por reserva_id, recuperación de lease tras reinicio.
  Productor decide payload/validez, worker no lee sus tablas ni crea hecho nuevo.
- **Privacidad:** correo AES-256-GCM con nonce12/tag128/key_id/versión y AAD
  id/tópico/versión; destino/contenido cifrados. Clave externa distinta de RSA/
  pepper/documento; purgar sensible tras entrega o cancelación por invalidez.
- **Comprobar/evidencia:** rollback productor no entrega, dos workers no reclaman
  misma reserva vigente, caída/reinicio recupera, reserva antigua no sobrescribe
  nueva; secreto ausente en outbox claro/logs/respuestas. No duplicar este worker
  en tableros de productores ni prometer entrega exactamente una vez.

### TEC-INFRA-T004 — Entregar SMTP/Rabbit y reintentos comprobables

- **Fuentes:** TEC-INFRA §4; persistencia §7; SPEC-09/14/07 notificación;
  catálogo de eventos (transporte/reintentos/DLQ).
- **Implementar:** adaptadores JavaMailSender y RabbitTemplate, configuración
  privada real. Evento público en exchange durable topic `seguridad.usuarios`,
  mensaje persistente/confirm/eventoId estable. Cola auditoría privada separada.
- **Cierre:** ENVIADO solo tras confirmación; inicial + tres reintentos 10/30/60 s,
  agotado FALLIDO y DLQ según catálogo conservando evidencia. SMTP Message-ID
  estable; red incierta puede duplicar correo, nunca crear otra autorización.
  Consultar validez de OTP/enlace al productor mediante port, cancelar/purgar
  inválidos; envío tardío no extiende vigencia ni hace válido un código anterior.
- **Comprobar:** SMTP/broker caídos/restaurados, mensaje confirmado, agotamiento,
  reconexión y correo real en buzón controlado. Confirmar roles/bloqueos/
  desbloqueos/atributos H3 con producers reales; no exigir suscripción G5 para
  `/pedidos/me` ni anunciar seis consumidores integrados.
- **Evidencia:** resultados/IDs sanitizados sin cuerpo secreto; aceptación SMTP
  no prueba lectura en bandeja. Coordinar reintento privado con EP-06-T003.

### TEC-INFRA-T005 — Publicar backend/SPA y verificar continuidad

- **Fuentes:** TEC-INFRA §§4/7/8; entorno/React; plan conjunto H3.
- **Inicio:** builds de dueños, migraciones/grants revisados, SMTP/broker,
  claves persistentes Sergio y secretos/custodia del entorno definidos.
- **Implementar:** build/imagen backend y publicación SPA, procedimiento de
  despliegue/reversión identificable. HTTPS, base URL/issuer/JWKS coherentes,
  rutas SPA, CORS para orígenes concretos y Retry-After expuesto. Llamadas G5
  backend→G7 no requieren CORS; no convertirlo en sustituto de autorización.
- **Cierre:** servicios arrancan con runtime real, persisten claves/familias/
  mensajes tras reinicio, no usan secretos en build frontend. Enlaces de correo
  apuntan al entorno y ruta propios, política no-referrer y sin token de retorno
  de login en URL. No anunciar opcionales posteriores como implementados.
- **Comprobar/evidencia:** smoke HTTP/DB/UI, reinicio y procedimiento reproducible;
  conservar revisión anterior para rollback código, sin borrar DB ni fingir
  que revertir imagen revierte esquema. CI/CD completo queda H4.

### TEC-INFRA-T006 — Comprobar entorno y apoyar ensayo conjunto G5

- **Fuentes:** [acuerdo H3](../../../docs/integracion/hito-3-ventas.md) y
  [plan conjunto/casos](../../../plan/PLAN-IMPLEMENTACION.md).
- **Inicio:** TEC-INFRA-T005, login Jose Luis, identidad Sergio, fixtures Eva y
  `/pedidos/me` de Michael. Coordinar la misma prueba de sus tableros, sin crear
  otra implementación ni exigir API/scopes nuevos.
- **Cierre:** G7 y G5 en nube; login real y consulta por sub, A solo sus pedidos,
  B vacío, negativos de token/rol. Configuración y revisiones de ambos
  identificadas; G5 no accede a DB G7. Ready jueves 15-oct, ensayo antes de
  sábado 17; horario exacto coordinado con los equipos.
- **Comprobar/evidencia:** disponibilidad HTTPS/DB/JWKS/SMTP y red durante prueba,
  resultados conjuntos y fallos con dueño; ausencia de endpoint real es bloqueo
  de demo, no algo que sustituya Prism. Evidencia infraestructura separada de
  EP-06; cada dueño conserva validación/defensa de su recorrido.

## Registro de ejecución y revisión

Todas comienzan pendientes; cierre requiere criterios, evidencia y revisión de
otro integrante. EP-06 y TEC-INFRA tienen evidencia separada; un ensayo conjunto
puede aportar a varios cierres sin duplicar su tarea de implementación.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | EP-06-* | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
| 12-oct-2026 | TEC-INFRA-* | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
| 16-oct-2026 | EP-06-T001 | Fusionada (B#34). Núcleo y persistencia de auditoría (correcciones de Sergio) | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-06-T002 | Fusionada (B#41). Crítica en el commit del productor; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-06-T003 | Fusionada (B#41, B#45). No crítica tras commit en ejecutor aparte; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-06-T004 | Fusionada (B#42). GET /auth/me/actividad; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-06-T005 | Fusionada (F#8). FRONT-13 actividad; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | TEC-INFRA-T001 | Fusionada (B#13). Configuración JDBC externa | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | TEC-INFRA-T002 | En curso (B#35, B#44). Runbook y grants ensayados 26/26, V101–V106 en db/migration; falta aplicar la ventana | CI en verde (merge propio, 15-oct) | En curso |
| 16-oct-2026 | TEC-INFRA-T003 | Fusionada (B#41). Outbox con reclamo SKIP LOCKED y correo cifrado; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | TEC-INFRA-T004 | En revisión (B#41, B#46). Worker SMTP + publicador RabbitMQ con confirmaciones; falta broker y SMTP reales | CI en verde (merge propio, 15-oct) | En revisión |
| 16-oct-2026 | TEC-INFRA-T005 | En curso (B#36). Imagen Docker, CORS, salud y guía; falta plataforma | CI en verde (merge propio, 15-oct) | En curso |
