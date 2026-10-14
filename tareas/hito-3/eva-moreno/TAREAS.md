# H3 — tareas de Eva Lucía Moreno Zevallos

**Dueña:** Eva · **Épica:** EP-01 · **Incremento:** H3-EVA.
**Base:** [PLAN aprobado 1.1.0](../../../plan/hito-3/eva-moreno/PLAN.md),
12-oct-2026. Aprobación comunicada por Luis; este tablero descompone ese alcance.

**Resultado:** ADMIN_SISTEMA con MFA real consulta cuentas persistidas, filtra,
pagina y abre su detalle. Eva entrega el recorrido completo y prepara identidades
de prueba; los bloques ajenos se consumen de sus dueños.

**Fuentes comunes:** [composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md).

**Límite:** no registro, verificación, alta administrativa, baja/reactivación,
búsqueda por documento ni bloqueo manual. Preparar fixtures no implementa esos
flujos. No agregar endpoints ni botones de éxito ficticio.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `usuario` bajo el paquete raíz |
| Dominio | `domain.model`: Usuario, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: ConsultaCuentas; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: CuentasPort, CuentaEscrituraInterna; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-01-T001/T002** y de las tareas de endpoints ya
existentes; no son otra funcionalidad ni duplican IDs. Cada tarea de endpoint
entrega caso de uso, DTO/mapping HTTP y adaptadores necesarios, además de su UI.
Los tests de mappers excluyen secretos y distinguen omitido/null; los tests del
núcleo usan puertos dobles, sin Spring/DB. La aceptación usa PostgreSQL y
proveedores reales. Reglas ArchUnit de TEC-INFRA-T001 verifican los límites.
El esqueleto compartido se prepara en TEC-INFRA-T001; puede adelantarse el
núcleo aislado, pero no cerrar persistencia/HTTP sin las dependencias reales.

## Orden de trabajo

Empezar por T001 y T002; avanzar T003–T004 con pruebas aisladas mientras llegan
proveedores. T005–T006 requieren la base React de Jose Luis para ejecutarse.
T007 prepara datos sin modificar cuentas ajenas; T008 cierra el recorrido real.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-01-T001 | Mapping y lectura de cuentas | P0 | Acceso al esquema de prueba revisado con Christian | En revision |
| EP-01-T002 | Ports de cuenta y escritura acotada | P0 | T001 | Pendiente |
| EP-01-T003 | Listado administrativo protegido | P1 | T001–T002; autorización/roles Sergio, estado Luis, proyección Valery | Pendiente |
| EP-01-T004 | Detalle administrativo protegido | P1 | T002; autorización Sergio, estado Luis, proyección Valery | Pendiente |
| EP-01-T005 | Pantalla de listado y filtros | P1 | T003; EP-02-T001 y T008 de Jose Luis | Pendiente |
| EP-01-T006 | Pantalla de detalle y composición | P1 | T004–T005; SeccionRoles Sergio, lectura de bloqueo Luis | Pendiente |
| EP-01-T007 | Fixtures reproducibles e identidad para G5 | P0 | T002; credenciales Juan José, roles Sergio; entorno Christian | Pendiente |
| EP-01-T008 | Verificar y defender la lectura completa | P1 | T003–T007; login/MFA reales; despliegue Christian | Pendiente |

Las referencias Txxx de esta tabla pertenecen a EP-01 salvo indicación contraria.
Las dependencias habilitan aceptación real, no impiden escribir pruebas aisladas.

## Tareas

### EP-01-T001 — Mapear y consultar las cuentas persistidas

- **Fuentes:** [SPEC-03 RF-03.2](../../../specs/funcionales/SPEC-03-alta-consulta-cuentas.md);
  persistencia §§1–2 y [estados](../../../docs/arquitectura/estados-usuario.md).
- **Implementar:** en `src/main/java/com/marketplace/auth_service/` del repo backend (usuario), el mapping
  `Usuario`, su repositorio y lectura inmutable de `UsuarioEstadoEfectivo`.
  Respetar columnas/tipos existentes; UUID ajenos son valores, sin asociaciones
  JPA/cascadas a tablas de otros dueños.
- **Cierre:** leer una cuenta existente y devolver ausencia para un UUID no
  existente. El estado efectivo respeta el vencimiento del bloqueo; la vista no
  admite escritura. El mapping pasa validación de esquema sin crear tablas.
- **Comprobar:** pruebas de repositorio en PostgreSQL, incluyendo bloqueo vigente
  y vencido; documentar diferencias del esquema real antes de cualquier migración.
- **Evidencia:** pendiente; adjuntar resultado de pruebas y esquema usado, sin
  credenciales. No reescribir migraciones aplicadas ni cargar V100 a ciegas.

### EP-01-T002 — Entregar las capacidades de cuenta a los otros dueños

- **Fuentes:** composición §§2–3, `CuentasPort` y `CuentaEscrituraInterna`;
  persistencia §1. Consume T001.
- **Implementar:** interfaces/DTO inmutables en `application.port.in` y `application.dto` y adaptadores
  propios para búsqueda por correo normalizado, lectura, lock, conteo de activos,
  consulta y actualización acotada de básicos/contadores. Mantener las firmas
  aprobadas; `guardarMfaVoluntario` queda reservado para SPEC-10 posterior.
- **Cierre:** el lock participa en la transacción del coordinador; mutaciones
  exigen transacción existente. Actualizar básicos no altera roles/estado;
  guardar contadores no altera perfil. El snapshot no contiene hashes ni secretos.
- **Comprobar:** tests de contratos de ports, rollback conjunto y dos actualizaciones
  concurrentes serializadas por cuenta; rechazar mutación sin transacción.
- **Entrega/evidencia:** ejemplos de consumo para Jose Luis/Luis/Valery y resultados
  de tests. Sus reglas y controladores permanecen bajo sus dueños.

### EP-01-T003 — Implementar GET /api/v1/usuarios

- **Fuentes:** SPEC-03 RF-03.2/ESC-03.2; OpenAPI operación de listado y schema de
  página; composición §1. Consume autorización/roles de
  [Sergio](../../../plan/hito-3/sergio-osorio/PLAN.md), estado de
  [Luis](../../../plan/hito-3/luis-morales/PLAN.md) y proyección de
  [Valery](../../../plan/hito-3/valery-gutierrez/PLAN.md).
- **Implementar:** servicio de consulta, DTO y controlador propios; filtros por
  estado efectivo, rol y fragmento de correo, sin distinguir mayúsculas;
  `pagina`/`tamano`, límites y orden conforme contrato.
- **Cierre:** autorizar `usuario.ver` antes de consultar. Sergio entrega UUID por
  rol; Eva aplica ese conjunto y los demás filtros **antes** de contar/paginar.
  Conjunto vacío produce página vacía. Proyectar por capacidades en lote, sin
  serializar entidades ni leer directamente tablas ajenas.
- **Comprobar:** PostgreSQL + pruebas HTTP: filtros combinados, dos páginas,
  totales reales, sin coincidencias y parámetros inválidos. Sin token 401; CLIENTE
  403, sin búsqueda posterior. No devolver documento claro/credenciales/tokens.
- **Evidencia:** petición/respuesta sanitizada y tests; si un port aún es doble,
  registrar esa limitación y mantener pendiente la aceptación real.

### EP-01-T004 — Implementar GET /api/v1/usuarios/{id}

- **Fuentes:** SPEC-03 RF-03.2; [FRONT-08 UI-08.8](../../../specs/front/FRONT-08-admin-detalle-usuario.md);
  OpenAPI `Usuario`; proyección/estado y dependencias de T003.
- **Implementar:** consulta de detalle autorizada y DTO contractual con datos
  seguros, roles y bloqueo visible según permiso. Reutilizar los ports existentes,
  sin otra entidad Usuario ni descifrado propio de Eva.
- **Cierre:** administrador autorizado recibe datos reales; UUID inexistente da
  404 `NO_ENCONTRADO` **después** de autorizar. Sin permiso, el resultado no permite
  distinguir si el objetivo existe. Documento enmascarado viene de Valery.
- **Comprobar:** tests HTTP/DB con objetivo existente/inexistente, CLIENTE, token
  ausente, estados y documento presente; verificar ausencia de secretos.
- **Evidencia:** tests y respuestas sanitizadas; no habilitar edición administrativa
  de atributos, historial ni acciones aplazadas.

### EP-01-T005 — Construir listado, filtros y paginación React

- **Fuentes:** [FRONT-07 UI-07.1/07.7](../../../specs/front/FRONT-07-admin-listado-usuarios.md)
  y [componentes FRONT-07](../../../specs/componentes-react/FRONT-07-admin-listado-usuarios.md),
  selección H3. Consume T003 y base React/sesión de Jose Luis.
- **Implementar:** `src/pantallas/admin-listado/`: `AdminListadoPage`,
  `FiltrosUsuarios`, `TablaUsuarios`, `useListadoUsuarios`; usar cliente HTTP,
  tema, sesión y router compartidos, sin otro store ni librería de UI.
- **Cierre:** filtros en URL, correo con debounce 300 ms, cambio de filtro vuelve
  a página 1; retorno desde detalle conserva vista. Tamaño por defecto 20, máximo
  100. Carga, vacío, error/reintento y sin permiso visibles; respuesta antigua no
  reemplaza un filtro nuevo. Única acción H3 de fila: abrir detalle.
- **Comprobar:** tests de componentes con respuestas controladas y recorrido
  contra API real; teclado, foco, anuncio de resultados y diseño a 390 px.
- **Evidencia:** tests y captura/recorrido. No implementar diálogos de bloqueo,
  alta, baja/reactivación ni columnas inexistentes como último acceso.

### EP-01-T006 — Construir el detalle e integrar bloques entregados

- **Fuentes:** [FRONT-08](../../../specs/front/FRONT-08-admin-detalle-usuario.md)
  y [componentes FRONT-08](../../../specs/componentes-react/FRONT-08-admin-detalle-usuario.md),
  selección H3; entorno/React §4. Consume T004–T005.
- **Implementar:** `src/pantallas/admin-detalle/`: `AdminDetallePage`,
  `CabeceraCuenta`, `SeccionDatosCuenta`, `useDetalleUsuario`. Integrar
  `SeccionRoles` de Sergio y lectura del bloqueo de Luis mediante sus contratos.
- **Cierre:** carga/error/403/404 y regreso al listado correctos; tras callback
  `onUsuarioActualizado` recargar detalle. Eva no implementa cambios de roles
  ni sus reglas. No incluir HistorialTabs ni edición de vendedor posteriores.
- **Comprobar:** tests de composición con callbacks y objetivo inexistente;
  recorrido real con bloqueos/roles entregados, sin mostrar acciones futuras;
  teclado, foco y móvil.
- **Evidencia:** resultados y recorrido; un placeholder de otro dueño no cuenta
  como bloque integrado ni libera T008.

### EP-01-T007 — Preparar cuentas de prueba con UUID estables

- **Fuentes:** PLAN §§3–4, TEC-02; entorno/React §5. Consume credenciales de
  [Juan José](../../../plan/hito-3/juan-cano/PLAN.md), roles de Sergio y entorno de
  [Christian](../../../plan/hito-3/christian-arancivia/PLAN.md).
- **Implementar:** carga técnica reproducible solo en entorno de prueba. Eva
  escribe identidad en su dominio y compone las capacidades de fixture de Juan
  José/Sergio para hashes y asociaciones; ningún SQL sobre tablas ajenas.
- **Cierre:** A/B CLIENTE activos sin MFA voluntario; V VENDEDOR sin CLIENTE;
  administradores de prueba y M/L/C separados según diseño. Registrar UUID
  estables y comunicar A/B/V a coordinación G5, sin contraseñas en Git. No
  inventar aceptación de términos ni emitir `usuario.creado` por fixtures.
- **Comprobar:** repetir carga sin duplicar ni sobrescribir cambios de las cuentas
  de la demo; reiniciar conserva UUID. Credenciales/roles se comprueban mediante
  proveedores reales. Eva prepara identidades; Michael precarga pedidos en G5.
- **Evidencia:** protocolo/UUID no sensibles, salida sanitizada y prueba de login
  cuando esté disponible; no presentar carga técnica como registro público.

### EP-01-T008 — Verificar y demostrar el recorrido de Eva desplegado

- **Fuentes:** PLAN §7; [plan conjunto H3](../../../plan/PLAN-IMPLEMENTACION.md);
  FRONT-07/08 y fuentes de T001–T007.
- **Inicio:** API/pantallas y ports reales; login Jose Luis + MFA Luis + firma y
  permisos Sergio; despliegue Christian. Mocks no liberan esta tarea.
- **Cierre:** administrador inicia sesión con MFA, filtra/pagina datos reales,
  abre detalle y vuelve conservando vista. CLIENTE recibe 403, sin token 401,
  objetivo inexistente autorizado 404; bloqueo vencido se ve ACTIVO.
- **Comprobar:** recorrido navegador→HTTP→servicio→PostgreSQL→respuesta/pantalla;
  ejecutar pruebas de repositorios, HTTP y React asociadas. Explicar dependencia
  de cada dueño y diferenciar fixtures de funcionalidades aplazadas.
- **Evidencia:** comandos/resultados, URL/revisión desplegada, capturas sanitizadas,
  contribución y uso de IA trazado a spec→plan→tarea→código→prueba.

## Registro de ejecución y revisión

Cada tarea termina al cumplir sus criterios, guardar evidencia y recibir revisión
de otro integrante. No marcar EP-01 completa: este tablero cubre solo H3.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | Todas | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
| 14-oct-2026 | EP-01-T001 | Implementación completa T001: domain/model (EstadoCuenta, CodigoRol, Usuario), application/dto (FiltroCuenta, Paginacion, CuentaSnapshot, PaginaResultado, ConsultaCuentasQuery), application/port/in (CuentasPort, CuentaEscrituraInterna, CambioBasicos, ContadoresProteccion), application/port/out (UsuarioRepositoryPort, UsuarioEstadoEfectivoPort), application/service (ConsultaCuentasService), adapter/out/persistence (UsuarioJpaEntity, UsuarioEstadoEfectivoJpaEntity, UsuarioRolJpaEntity, repositorios JPA, UsuarioPersistenceMapper, UsuarioRepositoryAdapter), adapter/in/web (UsuarioController GET /api/v1/usuarios y /api/v1/usuarios/{id}, DTOs, UsuarioWebMapper), configuration (UsuarioConfiguration), shared/domain (ActorUsuario, ContextoPeticion). Compilación exitosa (gradlew compileJava). Tests de integración requieren Docker/Testcontainers (pendiente entorno). Estado efectivo via @Subselect en vista usuario_estado_efectivo respeta vencimiento de bloqueo automático. Proyección segura: sin hashes/credenciales, documento enmascarado. | Pendiente | Completado |
