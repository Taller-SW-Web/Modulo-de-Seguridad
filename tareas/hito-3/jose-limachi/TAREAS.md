# H3 — tareas de Jose Luis Limachi Sarmiento

**Dueño:** Jose Luis · **Épica:** EP-02 · **Incremento:** H3-JOSE.
**Base:** [PLAN aprobado 1.1.0](../../../plan/hito-3/jose-limachi/PLAN.md),
12-oct-2026, y [diseño EP-02](../../../plan/funcionalidades/EP-02-autenticacion-sesiones.md).
Aprobación comunicada por Luis; este es el único tablero H3 de Jose Luis.

**Resultado:** login real con comprobaciones de sus dueños, sesión persistida,
refresh con rotación/reúso y logout. Incluye la base React que consumen las demás
pantallas y el aporte de autenticación a la demo con Ventas.

**Fuentes comunes:** [composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md).

**Límite:** Jose Luis consume MFA, bloqueo, credenciales, roles/firma y auditoría;
no implementa sus reglas ni accede a sus tablas. No desarrolla `/pedidos/me`
de G5 ni agrega endpoints, scopes o claims de familia al contrato.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `sesion` bajo el paquete raíz |
| Dominio | `domain.model`: SesionFamilia / AccesoSesion / TokenRefresco, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: Login / RenovarSesion / CerrarSesion; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: SesionesPort; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-02-T002/T003** y de las tareas de endpoints ya
existentes; no son otra funcionalidad ni duplican IDs. Cada tarea de endpoint
entrega caso de uso, DTO/mapping HTTP y adaptadores necesarios, además de su UI.
Los tests de mappers excluyen secretos y distinguen omitido/null; los tests del
núcleo usan puertos dobles, sin Spring/DB. La aceptación usa PostgreSQL y
proveedores reales. Reglas ArchUnit de TEC-INFRA-T001 verifican los límites.
El esqueleto compartido se prepara en TEC-INFRA-T001; puede adelantarse el
núcleo aislado, pero no cerrar persistencia/HTTP sin las dependencias reales.

## Orden de trabajo

Empezar por T001 (base React) y T002 (persistencia). T003–T006 construyen sesión
y login; pueden probarse con ports controlados. T008 usa la base y T009 compone
la pantalla. T007 verifica las garantías del backend; T010 cierra el recorrido
propio; T011 comprueba el aporte real a G5.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-02-T001 | Arranque React y contratos compartidos | P0 | Diseño de entorno aprobado | Fusionada (F#2) |
| EP-02-T002 | Persistencia de familias y asociación de acceso | P0 | Esquema/historia revisados con Christian | Fusionada (B#24, B#31) |
| EP-02-T003 | Apertura, resolución y revocación de familias | P0 | T002; cuentas Eva, firma Sergio | Fusionada (B#29) |
| EP-02-T004 | Refresh atómico y detección de reúso | P0 | T003; autorización/estado y auditoría de sus dueños | Fusionada (B#29) |
| EP-02-T005 | Logout público e idempotente | P0 | T003; auditoría Christian | Fusionada (B#29) |
| EP-02-T006 | Login y continuación MFA sin bypass | P0 | T003; cuentas, credenciales, protección/MFA, roles/firma y auditoría | Fusionada (B#38, B#39) |
| EP-02-T007 | Pruebas de atomicidad, concurrencia y composición | P0 | T003–T006; providers reales para composición | Fusionada (B#45) |
| EP-02-T008 | Sesión en memoria, HTTP y rutas protegidas | P0 | T001; T004–T005 para aceptación real | Fusionada (F#2) |
| EP-02-T009 | Pantalla de login y transición a OTP | P1 | T006, T008; pantalla OTP de Luis para aceptación real | Fusionada (F#2, F#5) |
| EP-02-T010 | Verificar y defender autenticación desplegada | P1 | T007–T009; proveedores/entorno reales | Pendiente (despliegue) |
| EP-02-T011 | Comprobar login G7 → mis pedidos G5 | P0 | T010; configuración Sergio/Christian, fixtures Eva y servicio Michael | Pendiente (prueba conjunta G5) |

Txxx refiere a EP-02. Una dependencia externa se entrega según el plan de su
dueño; no se asigna aquí su implementación ni un ID futuro. Todos los resultados
son obligatorios dentro del alcance; la prioridad indica orden habilitante.

## Tareas

### EP-02-T001 — Crear la base React/TypeScript compartida

- **Fuentes:** entorno/React §3; [componentes FRONT-00](../../../specs/componentes-react/FRONT-00-base-compartida.md),
  estructura y selección H3; [sistema de diseño](../../../docs/diseno/sistema-diseno.md).
- **Implementar:** raíz del repo frontend independiente: Vite 7, React 19.2, TypeScript 5.9 estricto,
  Router 7 declarativo y Mantine 9.6.2; Node 22 compatible según plan. Fijar
  versiones compatibles/lockfile; scripts dev/build/typecheck/test/preview y
  tema. Respetar `src/pantallas/`, `lib/` y contratos compartidos.
- **Cierre:** instalación reproducible con `npm ci`; build y typecheck pasan;
  un test de arranque comprueba router/tema. Exportar tipos de `Problem`,
  `SesionIniciada` y `DesafioMfa` conforme OpenAPI, sin tokens en código.
- **Comprobar:** instalación limpia, `npm run build`, `npm run typecheck` y
  `npm test`; entregar comandos/versiones a los compañeros.
- **Evidencia:** pendiente. No crear pantallas de los otros dueños, medidor de
  contraseña de Juan José ni otro cliente/store paralelo.

### EP-02-T002 — Persistir familias estables y accesos asociados

- **Fuentes:** persistencia §§2–3/8, DAT-01; SPEC-06 RF-06.1/06.3.
- **Implementar:** en `src/main/java/com/marketplace/auth_service/sesion/` del repo backend,
  entidades/repositorios `sesion_familia`, `acceso_sesion` y adaptación de
  `TokenRefresco`. Preparar migración V102 reservada, después de V100; coordinar
  versión/historia con Christian antes de aplicarla.
- **Cierre:** raíz independiente, FK compuesta familia/usuario, jti de acceso
  único e índices del diseño. El backfill conserva revocaciones y aborta una
  familia con usuarios incoherentes; no inferir familia por `sub`.
- **Comprobar:** migración sobre PostgreSQL de prueba con datos previos coherentes
  e incoherentes, constraints y validación JPA. No resetear Supabase ni editar
  migraciones ya aplicadas. Consultar cuenta solo mediante Eva.
- **Evidencia:** scripts versionados, resultados y revisión del cambio; no
  ejecutar migración remota hasta comprobar la historia/esquema real.

### EP-02-T003 — Entregar SesionesPort y abrir familias reales

- **Fuentes:** [SPEC-05 RF-05.6](../../../specs/funcionales/SPEC-05-inicio-sesion.md);
  composición §§2–4 (`SesionesPort`); persistencia §3. Consume T002 y ports de
  [Eva](../../../plan/hito-3/eva-moreno/PLAN.md) y
  [Sergio](../../../plan/hito-3/sergio-osorio/PLAN.md).
- **Implementar:** interfaz/DTO internos, adaptador y servicio de sesión:
  abrir, resolver acceso por jti verificado, revocar todas/revocar otras.
  Apertura recibe identidad ya resuelta; no depende de LoginService/MfaService.
- **Cierre:** una familia y acceso persistidos; JWT firmado por Sergio de 900 s,
  refresco aleatorio de 32 bytes y vigencia 7 días, hash SHA-256 persistido.
  Mutaciones participan en commit del coordinador; error técnico no deja
  tokens huérfanos. Resolución verifica usuario/familia/expiración de la asociación.
- **Comprobar:** tests DB: apertura, jti desconocido, revocación de una/todas/otras,
  rollback y locks cuenta→familias ordenadas. Un `familiaId` HTTP nunca decide
  sesión actual. Tokens/contraseñas no aparecen en logs.
- **Entrega/evidencia:** contrato probado a Luis/Juan José/Sergio y tests;
  asociar cada acceso nuevo también en refresh. Revocar refrescos no promete
  invalidar inmediatamente un JWT ya emitido en G5.

### EP-02-T004 — Implementar POST /api/v1/auth/refresh

- **Fuentes:** [SPEC-06 RF-06.1/06.3/06.4/06.5, ESC-06.1/06.2/06.4](../../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md);
  composición §§3–4; diseño EP-02. Consume T003, roles/estado reales y
  [auditoría Christian](../../../plan/hito-3/christian-arancivia/PLAN.md).
- **Implementar:** controlador/DTO y rotación transaccional: localizar por hash,
  lock cuenta→raíz, revalidar, marcar usado, emitir siguiente y persistir acceso.
  Retener usados mientras haya un sucesor utilizable; sin TTL absoluto añadido.
- **Cierre:** válido devuelve nuevo par; desconocido/vencido/revocado 401
  `REFRESCO_INVALIDO`. Reúso revoca su familia y confirma el efecto **antes**
  del 401; no revoca otras familias. Automático previo permite renovar, manual
  o baja no; usar reglas de Luis/Eva, no consultas a sus tablas.
- **Comprobar:** tests HTTP/DB y dos renovaciones simultáneas: como máximo una
  rotación, el segundo reúso revoca la familia, incluido el sucesor. Auditar
  `REFRESCO_REUTILIZADO`; fallo técnico revierte el conjunto.
- **Evidencia:** tests con estado DB posterior, respuestas sanitizadas y ausencia
  de secretos en logs. No repetir automáticamente una rotación incierta.

### EP-02-T005 — Implementar POST /api/v1/auth/logout

- **Fuentes:** SPEC-06 RF-06.2/06.5/06.6, ESC-06.3/06.5; OpenAPI logout.
- **Implementar:** controlador público autorizado por refresco del cuerpo;
  cerrar esa familia usando T003 y registrar `SESION_CERRADA` con Christian
  cuando corresponda, sin auditar una identidad inventada por token desconocido.
- **Cierre:** 204 con acceso vencido o ausente; también con refresco desconocido,
  vencido o ya revocado. Validación del cuerpo conserva el error contractual.
  No exigir Bearer ni cerrar otras familias del usuario.
- **Comprobar:** tests HTTP/DB de cierre normal, repetido, desconocido y sesión
  independiente que sigue renovando. Tras logout el refresco no emite tokens.
- **Evidencia:** resultados y efecto persistido; distinguir cierre de familia
  del vencimiento del acceso ya firmado.

### EP-02-T006 — Implementar login y componer el segundo factor

- **Fuentes:** SPEC-05 RF-05.1–05.7/ESC-05.1–05.5; composición §§3–4; OpenAPI
  login, `SesionIniciada` y `DesafioMfa`. Consume T003 y los planes de
  [Juan José](../../../plan/hito-3/juan-cano/PLAN.md),
  [Luis](../../../plan/hito-3/luis-morales/PLAN.md), Eva, Sergio y Christian.
- **Implementar:** controlador/coordinador de login; correo normalizado,
  preverification/revisión de credencial Juan José, cuenta Eva, defensa Luis,
  roles/permisos Sergio. Respetar orden de locks y comparación equivalente para
  inexistentes; repetir revisión cambiada como máximo una vez sin efectos.
- **Cierre:** CLIENTE sin MFA obtiene sesión; credenciales erróneas/inexistente/
  bloqueada/inactiva/pendiente dan el mismo 401 `CREDENCIALES_INVALIDAS` sin
  tokens. Fallo elegible se confirma mediante Luis antes del 401. MFA efectivo
  devuelve solo desafío, sin familia ni reset anticipado de racha.
- **Composición:** Luis implementa OTP y, tras verificar/revalidar cuenta y
  credencial, llama `abrir` en el mismo commit. Caducidad da 403
  `PASSWORD_CADUCADA` al completar autenticación. Usar auditoría canónica;
  no duplicar validación de contraseña, bloqueo ni OTP.
- **Comprobar/evidencia:** tests de todas las ramas, administración con MFA aunque
  bandera voluntaria false y cambio de credencial durante desafío. Guardar
  respuestas/evidencia sanitizadas y diferencias de latencia de negativos.

### EP-02-T007 — Probar garantías del backend y composición entre dueños

- **Fuentes:** SPEC-05/06 escenarios y RNF; PLAN §7; composición §§3–4.
- **Implementar:** pruebas de integración en PostgreSQL para T003–T006 con
  concurrencia controlada, reloj inyectable y providers reales en pruebas conjuntas.
  Cada dueño conserva pruebas de su regla; Jose Luis verifica la composición.
- **Cierre:** reúso persiste revocación pese al 401; error de persistencia/firma
  no entrega sesión; dos completamientos OTP no producen dos familias. Cambio
  de contraseña desde una sesión conserva esa familia y revoca otra; cambio
  de rol revoca ambas. Automático conserva familias previas pero impide nuevo login.
- **Comprobar:** tests backend apropiados vía Gradle wrapper y evidencia DB/HTTP. Medir
  login p95 <800 ms y refresh p95 <300 ms con 50 concurrentes; este último objetivo
  propuesto y cualquier desviación se registran para revisión según el plan.
  No sustituir esa medición por una petición rápida ni pedir cerrar toda la carga H4/H5.
- **Evidencia:** comandos, condiciones, resultados y limitaciones; pruebas con
  dobles se identifican como aisladas y no cierran composición real.

### EP-02-T008 — Implementar sesión en memoria, HTTP y rutas

- **Fuentes:** [FRONT-00 UI-00.1–00.4](../../../specs/front/FRONT-00-comportamiento-comun.md),
  componentes FRONT-00 §§1–2, SPEC-06 H-11; entorno/React §§3–4.
- **Implementar:** sobre T001, `lib/sesion.ts`, `lib/api.ts`, `lib/errores.ts`,
  `useSesion` y router/guards. Tipos/contratos reutilizados por cada pantalla;
  acceso/refresco/identidad/desafío solo en memoria.
- **Cierre:** una Promise de refresh por pestaña; actualizar ambos tokens
  atómicamente. Renovar al acercarse vencimiento o 401 `TOKEN_INVALIDO`
  autenticado, con un solo reintento seguro. 401 por credenciales/código/contraseña
  actual no renueva ni cierra sesión. Timeout de refresh no reusa token incierto.
- **Cierre de UI:** logout limpia memoria incluso sin red; recarga vuelve a
  login; respuestas tardías no restauran sesión cerrada. `volver` solo ruta
  interna segura. Registrar rutas H3 de cada dueño cuando su entrega esté lista;
  guards de React no sustituyen autorización del backend.
- **Comprobar/evidencia:** tests de requests paralelas, renovación fallida,
  logout en carrera, recarga, 401 semántico y retorno externo rechazado;
  comprobar ausencia en URL/localStorage/sessionStorage/cookies persistentes.

### EP-02-T009 — Implementar LoginPage y su transición a OTP

- **Fuentes:** [FRONT-01](../../../specs/front/FRONT-01-inicio-sesion.md) y
  [componentes FRONT-01](../../../specs/componentes-react/FRONT-01-inicio-sesion.md);
  selección H3 de FRONT-00. Consume T006/T008 y OTP de Luis para prueba real.
- **Implementar:** `src/pantallas/login/LoginPage.tsx` y `useLogin.ts`;
  campos, submit, avisos, éxito o desafío en memoria. Navegar a la pantalla OTP
  de Luis sin implementar su hook/verificación ni pasar challenge por URL.
- **Cierre:** validar correo/obligatorios, no política de contraseña al entrar;
  impedir doble submit. Error genérico conserva correo, borra contraseña y
  enfoca; sin red conserva lo escrito. Mostrar PASSWORD_CADUCADA sin ofrecer
  recuperación aún no disponible; ningún enlace promete flujo posterior.
- **Comprobar:** tests de estados/código HTTP, doble clic, destinos/retorno,
  gestión sin sesión antes de OTP; teclado, mostrar contraseña, autocomplete,
  alertas y móvil 390 px. Después comprobar login contra API real.
- **Evidencia:** tests y recorrido/capturas sanitizadas; UI de inexistente,
  bloqueada/inactiva/pendiente indistinguible de contraseña equivocada.

### EP-02-T010 — Verificar y defender autenticación desplegada

- **Fuentes:** PLAN §7 y fuentes de T001–T009; entorno/React y plan conjunto H3.
- **Inicio:** providers reales, SMTP para MFA, DB y despliegue Christian;
  issuer/RS256/filtro de Sergio. Dobles no liberan esta tarea.
- **Cierre:** pantalla→login→DB→tokens→pantalla protegida; usuario de gestión
  completa MFA; refresh rota, logout cierra y recarga exige login. JWT cumple
  firma/claims/exp reales, no ejemplos ilustrativos del kit.
- **Comprobar:** tests backend/React y recorrido desplegado con negativos;
  reinicio conserva claves/familias. Coordinar con Sergio que solo operaciones
  públicas H3 precisas se permitan, sin wildcard `/auth/**` ni errores HTML.
- **Evidencia:** revisión/URL desplegada, resultados y diagrama del recorrido;
  uso de IA trazado a fuentes y tareas. No declarar todas las specs futuras
  terminadas ni revocación instantánea de accesos remotos.

### EP-02-T011 — Comprobar el aporte a la demo conjunta con Ventas

- **Fuentes:** [acuerdo H3 G5](../../../docs/integracion/hito-3-ventas.md) y
  [plan conjunto/casos H3](../../../plan/PLAN-IMPLEMENTACION.md).
- **Inicio:** T010; A/B/V de Eva, issuer/JWKS Sergio, URLs/configuración Christian,
  endpoint/pantalla de Michael. Coordinación confirma H-11 en G5; Jose Luis no
  implementa su pantalla ni consulta su DB.
- **Cierre:** login real en G7 → JWT validado con JWKS en G5 →
  `GET /api/v1/pedidos/me`. A ve solo sus pedidos, B recibe 200 vacío;
  sin token/vencido/alterado 401, válido sin CLIENTE 403 según contrato G5.
  Usuario identificado exclusivamente por `sub`, sin scope de servicio A10.
- **Comprobar:** prueba conjunta en nube prevista para jueves 15-oct, ensayo
  antes del sábado 17; UUID/cuentas reales y pedidos precargados persistidos.
  Si G5/entorno no está listo, registrar bloqueo de T011 y evidencia independiente,
  sin llamar integración a un mock.
- **Evidencia:** revisión de ambos servicios, fecha, casos/respuestas sanitizadas
  y recorrido conjunto; no compartir tokens/contraseñas reales en Git.

## Registro de ejecución y revisión

Cada tarea termina con criterios, evidencia y revisión de otro integrante.
Las pruebas de providers se revisan con sus dueños. Los contratos y planes ya
aprobados no requieren una nueva aprobación rutinaria para comenzar.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | Todas | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
| 16-oct-2026 | EP-02-T001 | Fusionada (F#2). Base React: Mantine, router, cliente HTTP; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T002 | Fusionada (B#24, B#31). Familias y accesos + cascadas V102; Luis | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T003 | Fusionada (B#29). Apertura, resolución y revocación; Luis | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T004 | Fusionada (B#29). Refresh atómico y reúso; Luis | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T005 | Fusionada (B#29). Logout idempotente; Luis | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T006 | Fusionada (B#38, B#39). POST /auth/login + continuación MFA; Sergio. Evidencia docs/evidencia/EP-02-T006.md | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T007 | Fusionada (B#45). Carga 50 concurrentes: login p95 673–693 ms, refresh 269–283 ms; corrige 2 agotamientos del pool; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T008 | Fusionada (F#2). Sesión en memoria, una renovación en vuelo, guards; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
| 16-oct-2026 | EP-02-T009 | Fusionada (F#2, F#5). FRONT-01 login y transición a OTP; Sergio | CI en verde (merge propio, 15-oct) | Fusionada |
