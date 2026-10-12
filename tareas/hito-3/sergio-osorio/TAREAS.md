# H3 — tareas de Sergio Alejandro Osorio Montenegro

**Dueño:** Sergio · **Épica académica:** EP-05 · **Incremento:** H3-SERGIO.
**Adicional:** TEC-IDENTIDAD; no es una novena épica ni sustituye roles/permisos.
**Base:** [PLAN aprobado 1.1.0](../../../plan/hito-3/sergio-osorio/PLAN.md) y
[TEC-IDENTIDAD 1.1.0](../../../plan/transversales/TEC-IDENTIDAD.md), 12-oct-2026;
aprobación comunicada por Luis. Un único tablero, con evidencias separadas.

**Resultado académico:** administrador con MFA asigna/revoca un rol desde el
detalle; asociaciones, revocación de refrescos, auditoría y evento reales.
**Resultado técnico:** JWT de acceso RS256 verificable por G5 mediante JWKS.

**Fuentes comunes:** [SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md),
[composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md),
[eventos](../../../specs/catalogo-eventos.md).

**Límite:** no tokens/clientes/scopes de servicio, introspección, consultas de
servicio SPEC-18 ni rotación completa de claves. A9/A10 mantienen sus condiciones;
`/pedidos/me` no requiere aprovisionarlos. No cambiar el contrato por implementar.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `autorizacion e identidad` bajo el paquete raíz |
| Dominio | `domain.model`: Rol / Permiso / UsuarioRol, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: AsignarRol / RevocarRol / FirmarAcceso; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: AutorizacionPort, FirmaPort; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-05-T001/T002; TEC-IDENTIDAD-T001** y de las tareas de endpoints ya
existentes; no son otra funcionalidad ni duplican IDs. Cada tarea de endpoint
entrega caso de uso, DTO/mapping HTTP y adaptadores necesarios, además de su UI.
Los tests de mappers excluyen secretos y distinguen omitido/null; los tests del
núcleo usan puertos dobles, sin Spring/DB. La aceptación usa PostgreSQL y
proveedores reales. Reglas ArchUnit de TEC-INFRA-T001 verifican los límites.
El esqueleto compartido se prepara en TEC-INFRA-T001; puede adelantarse el
núcleo aislado, pero no cerrar persistencia/HTTP sin las dependencias reales.

## Orden de trabajo

EP-05-T001/T002 y TEC-IDENTIDAD-T001/T002/T003 habilitan a los compañeros.
El bloque UI puede probarse con respuestas controladas mientras llegan providers;
EP-05-T006/T007 y TEC-IDENTIDAD-T005 exigen composición real.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-05-T001 | Mappings y catálogo cerrado de roles/permisos | P0 | Esquema/historia revisados con Christian | Pendiente |
| EP-05-T002 | AutorizacionPort y guard de administrador | P0 | EP-05-T001; cuenta Eva para comprobaciones conjuntas | Pendiente |
| EP-05-T003 | Asignación individual con efectos atómicos | P1 | EP-05-T002; EP-01-T002; EP-02-T003; auditoría/outbox Christian | Pendiente |
| EP-05-T004 | Revocación individual y protección de administrador | P1 | EP-05-T003; mismos providers reales | Pendiente |
| EP-05-T005 | SeccionRoles y composición con detalle | P1 | EP-05-T003/T004; EP-02-T001/T008; EP-01-T004 y contrato de contenedor Eva | Pendiente |
| EP-05-T006 | Verificar concurrencia, permisos y efectos | P0 | EP-05-T003/T004; login/MFA y providers reales | Pendiente |
| EP-05-T007 | Defender roles/permisos desplegados | P1 | EP-05-T005/T006; despliegue Christian | Pendiente |
| TEC-IDENTIDAD-T001 | Claves persistentes y FirmaPort | P0 | Configuración privada del entorno con Christian | Pendiente |
| TEC-IDENTIDAD-T002 | Validación JWT y filtro de acceso G7 | P0 | TEC-IDENTIDAD-T001; EP-05-T002 | Pendiente |
| TEC-IDENTIDAD-T003 | Descubrimiento y JWKS públicos | P0 | TEC-IDENTIDAD-T001; base URL/issuer del entorno | Pendiente |
| TEC-IDENTIDAD-T004 | Pruebas criptográficas en runtime del backend | P0 | TEC-IDENTIDAD-T001/T002/T003 | Pendiente |
| TEC-IDENTIDAD-T005 | Configuración y validación conjunta con G5 | P0 | TEC-IDENTIDAD-T004; EP-02-T010/T011; entorno y servicio G5 | Pendiente |

EP-01/02 identifican tareas de Eva/Jose Luis. Los demás dueños entregan sus
ports según su plan; no se les asigna aquí implementar las reglas de Sergio.

## EP-05 — roles y permisos

### EP-05-T001 — Mapear el catálogo y las asociaciones

- **Fuentes:** RF-11.1/11.2/11.4/11.9; persistencia §§1–2; catálogo de CONTEXT.
- **Implementar:** entidades/repositorios propios `rol`, `permiso`, `rol_permiso`
  y `usuario_rol` en `src/main/java/com/marketplace/auth_service/` del repo backend (autorizacion e identidad); UUID de
  usuario como valor, sin entidad/repo Eva ni cascadas entre dominios.
- **Cierre:** seis roles exactos y permisos G7; asociaciones únicas, roles
  múltiples y unión sin duplicados, incluidos arrays vacíos. ADMIN_SISTEMA+
  VENDEDOR produce exactamente diez permisos; sin permisos de otras APIs.
- **Comprobar:** PostgreSQL y cálculo de roles/permisos, vacío, duplicados y
  códigos inválidos. Comprobar esquema existente antes de cambios físicos;
  no crear roles REPARTIDOR, CAJERO, SUPERVISOR o de servicio.
- **Evidencia:** tests/mapping y contribución de roles al protocolo de fixtures
  de Eva. No reescribir migraciones aplicadas ni insertar credenciales ajenas.

### EP-05-T002 — Entregar autorización actual y guard transaccional

- **Fuentes:** RF-11.7/11.9/11.10; composición §§2–3 `AutorizacionPort`.
- **Implementar:** interfaz/DTO y adaptador: resolver roles/permisos, exigir
  permiso/titularidad, UUID de usuarios con rol y lock de catálogo ADMIN_SISTEMA.
  Usar actor de JWT validado; catálogo estático en backend y etiquetas canónicas
  en UI, sin `client_secret` ni GET /roles de servicio desde React.
- **Cierre:** permisos actuales se revalidan en servidor; titularidad no permite
  editar documento/campos privilegiados. Guard bajo transacción existente antes
  de cuentas/familias; consulta de activos mediante Eva, sin SQL a usuario.
- **Comprobar:** ADMIN_VENTAS/CLIENTE sin rol.asignar, permisos cambiados tras
  emitir JWT y titular vs otro usuario. Guard compartido serializa operaciones
  de disponibilidad de Luis; no altera sus reglas ni habilita ciclos de beans.
- **Entrega/evidencia:** tests y contratos a consumidores. Ocultar menú o tener
  un scope técnico no concede permiso de usuario.

### EP-05-T003 — Implementar POST /api/v1/usuarios/{id}/roles

- **Fuentes:** RF-11.3/11.5/11.6/11.8; ESC-11.1–11.3; OpenAPI asignación;
  composición §4 y catálogo de eventos.
- **Implementar:** controlador/servicio/DTO propios para un rol por petición;
  autorizar antes de buscar objetivo, guard→cuentas ordenadas→familias→asociación.
  Reautorizar bajo lock; guardar asociación, revocar todas las familias del
  objetivo por Jose Luis y crítica ROL_ASIGNADO/evento por Christian en un commit.
- **Cierre:** 200 contractual, sin reemplazar array completo; rol inválido
  400 VALIDACION, sin permiso 403 SCOPE_INSUFICIENTE. Registrar ACCESO_DENEGADO
  sin secretos conforme catálogo; unicidad evita asociaciones duplicadas.
- **Comprobar:** dos asignaciones distintas conservan ambas; error de crítica/
  outbox revierte asociación y revocación. Evento `usuario.roles_cambiados`
  contiene lista completa resultante y eventoId, publicado solo tras commit.
- **Evidencia:** tests DB/HTTP sanitizados. No prometer invalidación instantánea
  de JWT remotos; nuevo login debe calcular roles actuales y MFA efectivo.

### EP-05-T004 — Implementar DELETE /usuarios/{id}/roles/{rol}

- **Fuentes:** RF-11.3/11.5–11.8; ESC-11.5/11.6; OpenAPI revocación.
- **Implementar:** revocar un rol, preservando otros, con mismo coordinador de
  transacción/locks; crítica ROL_REVOCADO y evento completo por sus providers.
  Proteger propio ADMIN_SISTEMA y último administrador efectivamente ACTIVO.
- **Cierre:** 204 válido; protegido 422 ADMINISTRADOR_PROTEGIDO sin cambios;
  catálogo/autorización y errores restantes conforme contrato. Conteo de activos
  toma UUID de Sergio y estado efectivo de Eva, sin repositorio ajeno.
- **Comprobar:** propio/último, administrador bloqueado/vencido, dos revocaciones
  cruzadas simultáneas y usuario con varios roles; nunca dejar cero admins
  activos por carrera. Fallo técnico revierte efectos completos.
- **Evidencia:** tests y estado DB posterior; estas protecciones no asignan a
  Sergio desarrollar baja o bloqueo manual posteriores.

### EP-05-T005 — Construir SeccionRoles e integrarla con Eva

- **Fuentes:** [FRONT-08 UI-08.3/08.4](../../../specs/front/FRONT-08-admin-detalle-usuario.md)
  y [componentes FRONT-08](../../../specs/componentes-react/FRONT-08-admin-detalle-usuario.md);
  entorno/React §4.
- **Implementar:** `SeccionRoles` y hook/diálogos propios en
  `src/pantallas/admin-detalle/`; usar HTTP/sesión compartidos y props
  acordadas con Eva. Sergio implementa su bloque completo; Eva el contenedor.
  Entregar/probar el bloque sin esperar el cierre EP-01-T006; la aceptación
  del panel compuesto se comprueba juntos en EP-05-T007 y EP-01-T008.
- **Cierre:** mostrar asignados/disponibles con etiquetas canónicas; confirmar
  cierre de sesiones y MFA obligatorio al asignar gestión. No ofrecer revocar
  propio ADMIN; mensajes de 400/403/422 y éxito; callback
  `onUsuarioActualizado` recarga detalle. Impedir doble envío.
- **Comprobar/evidencia:** tests componente/callback y API real, teclado/foco/
  móvil; sin secretos de servicio en navegador ni edición de lista completa.

### EP-05-T006 — Comprobar permisos, concurrencia y efectos reales

- **Fuentes:** PLAN §7; ESC-11.1–11.7; composición §§3–4.
- **Inicio:** T003/T004, providers reales de Eva/Jose Luis/Christian y login/MFA
  Luis; usar cuentas dedicadas, no A/B de G5.
- **Cierre:** dos familias del objetivo dejan de renovar tras cambio; nueva
  autenticación lleva roles actuales y cualquier rol de gestión exige MFA.
  Fallo crítico conserva asociación/familias; rol inválido/no autorizado no muta.
  Revocaciones concurrentes no eliminan ambos administradores.
- **Comprobar:** pruebas PostgreSQL/HTTP concurrentes y publicación Rabbit real;
  cálculo de permisos objetivo <15 ms y acceso <4 KB con medición registrada,
  no supuesto. Verificar lista resultante/eventoId y ausencia de secretos.
- **Evidencia:** comandos/resultados/condiciones; separar prueba aislada de
  aceptación real y revocación de refrescos de vigencia del JWT previo.

### EP-05-T007 — Defender el recorrido académico desplegado

- **Fuentes:** PLAN §7; FRONT-08 y tareas EP-05 anteriores.
- **Inicio:** bloque real en detalle Eva, login/MFA reales y despliegue Christian.
- **Cierre:** ADMIN entra con MFA→asigna rol→DB/efectos→pantalla actualizada→
  revoca rol; mostrar protegido/sin permiso y nuevo login del objetivo.
- **Comprobar/evidencia:** recorrido UI/HTTP/DB completo en nube, pruebas,
  revisión/URL, capturas sanitizadas y uso de IA trazado a spec/plan/tarea.
  Firmar JWT o mostrar JWKS por sí solo no cierra esta épica.

## TEC-IDENTIDAD — capacidad técnica adicional

### TEC-IDENTIDAD-T001 — Implementar claves persistentes y FirmaPort

- **Fuentes:** [SPEC-17 RF-17.1/17.2/17.14](../../../specs/funcionales/SPEC-17-tokens-servicio.md),
  RF-05.6; entorno/React §2, TEC-IDENTIDAD §4 y ADR-001.
- **Implementar:** proveedor de claves/FirmaPort con NimbusJwtEncoder, starter
  JOSE compatible con Java 21/Boot 3.5.16. RSA 3072 PKCS8 externo y persistente;
  kid thumbprint SHA-256 de JWK pública. Christian custodia configuración privada.
- **Cierre:** firmar claims recibidos, sin verificar contraseña/calcular roles/
  abrir familia. RS256, sub UUID, email, tipo acceso, roles/permisos arrays,
  issuer exacto, iat/exp/jti; 900 s, Clock inyectable. Sin aud de servicio,
  familiaId/customerId inventados ni clave regenerada al arrancar.
- **Comprobar/evidencia:** firma verificable, reinicio conserva key/kid, secreto
  ausente/configuración inválida deniega emisión; tests runtime real y dependencia
  resuelta. Privada/tokens completos nunca en Git, logs o frontend.

### TEC-IDENTIDAD-T002 — Validar acceso y crear el contexto del actor

- **Fuentes:** entorno/React §2; catálogos de errores; composición §2; ADR-002.
- **Implementar:** NimbusJwtDecoder con pública local, RS256/issuer/tiempo/perfil
  estrictos, converter de ActorUsuario y SecurityFilterChain stateless con
  Problems canónicos. Coordinar filtros/rutas con Jose Luis y los dueños HTTP.
- **Cierre:** skew=0, exp<=ahora inválido; rechazar firma/issuer/kid/perfil
  incorrectos. Token de servicio firmado válido en operación de persona da
  403 TOKEN_NO_APLICABLE según G7; no transformar scopes en roles/permisos.
  Revalidación de permisos sensibles queda en el servicio dueño.
- **Comprobar:** sin Bearer/vencido/alterado 401 TOKEN_INVALIDO; públicos H3
  específicos funcionan, protegidos deniegan. No wildcard `/auth/**`, HTTP
  a JWKS propio, cookie Bearer ni errores HTML por defecto.
- **Evidencia:** tests de filtro/perfil y configuración stateless/CSRF coherente
  con transporte. No habilitar endpoints posteriores por aparecer en OpenAPI.

### TEC-IDENTIDAD-T003 — Publicar descubrimiento y JWKS

- **Fuentes:** SPEC-17 RF-17.1/17.2; OpenAPI operaciones .well-known;
  TEC-IDENTIDAD §4.
- **Implementar:** GET `/api/v1/auth/.well-known/openid-configuration` y
  `/api/v1/auth/.well-known/jwks.json` con la misma clave del firmador.
- **Cierre:** issuer idéntico al iss y jwks_uri absoluto HTTPS del entorno;
  JWKS solo kty/use/alg/kid/n/e, sin privados. Descubrimiento y JWKS son URLs
  distintas; omitir metadatos opcionales de servicios todavía no implementados.
- **Comprobar/evidencia:** HTTP público, consistencia de URLs detrás de proxy,
  token real validado con n/e/kid publicado y reinicio estable. No literal
  auth-service, localhost en nube ni rotación completa declarada por continuidad.

### TEC-IDENTIDAD-T004 — Probar validadores con firmas reales

- **Fuentes:** PLAN §7; TEC-IDENTIDAD §7; kit como referencia, no evidencia de app.
- **Implementar:** tests con claves de prueba y tokens realmente firmados en el
  runtime Java 21 del backend para T001–T003; no reutilizar JWT ilustrativos.
- **Cierre:** válido aceptado; vencido, firma alterada, issuer incorrecto, kid/
  algoritmo no admitidos y token de servicio rechazados según su caso. Claim
  sub/perfil/arrays y TTL válidos; no conceder privilegios por payload decodificado.
- **Comprobar/evidencia:** tests backend/HTTP reproducibles, versión de JOSE y
  resultados; límites de pruebas locales separados de verificación remota G5.

### TEC-IDENTIDAD-T005 — Entregar configuración y comprobar JWT con G5

- **Fuentes:** [acuerdo H3](../../../docs/integracion/hito-3-ventas.md),
  [plan conjunto](../../../plan/PLAN-IMPLEMENTACION.md), TEC-IDENTIDAD §7.
- **Inicio:** login real, descubrimiento/JWKS y despliegue; Michael entrega
  `/pedidos/me` y su validador. Coordinar con Christian/Jose Luis, sin duplicar
  sus tareas de despliegue o recorrido.
- **Cierre:** ficha con revisiones, bases URL, issuer/JWKS, orígenes, UUID A/B/V,
  aceptación H-11 y horario de prueba. G5 valida el JWT real y autoriza CLIENTE/
  sub; no customerId alternativo ni scope ventas:pedidos:leer.
- **Comprobar/evidencia:** prueba conjunta prevista jueves 15-oct y ensayo antes
  del sábado 17, positivos/negativos compartidos. Sergio acredita identidad,
  Jose Luis login, Michael pedidos; configuraciones privadas fuera de Git.
  No declarar integración hecha si falta servicio/entorno real de G5.

## Registro de ejecución y revisión

Cada tarea necesita criterios, evidencia y revisión de otro integrante.
Evidencia EP-05 y TEC-IDENTIDAD se registra por separado.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | EP-05-* | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
| 12-oct-2026 | TEC-IDENTIDAD-* | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
