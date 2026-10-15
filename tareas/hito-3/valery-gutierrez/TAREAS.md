# H3 — tareas de Valery Cristin Gutierrez Bendezu

**Dueña:** Valery · **Épica:** EP-08 · **Incremento:** H3-VALERY.
**Base:** [PLAN aprobado 1.1.0](../../../plan/hito-3/valery-gutierrez/PLAN.md),
12-oct-2026; aprobación comunicada por Luis.

**Resultado:** titular autenticado consulta su perfil persistido, edita solo
nombres/apellidos/celular y ve cambios al consultar de nuevo. Valery implementa
su recorrido completo y entrega proyección segura al detalle de Eva.

**Fuentes comunes:** [SPEC-16](../../../specs/funcionales/SPEC-16-atributos.md),
[SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md),
[composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md),
[eventos](../../../specs/catalogo-eventos.md).

**Límite:** cambio de correo, direcciones, edición administrativa/documento/
atributos de rol y configuración MFA posteriores. Correo/documento y atributos
de rol aplicables se leen; no añadir formularios, botones o peticiones de esos
flujos. Revisión UX adicional no sustituye su función ni la hace frontend ajeno.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `perfil` bajo el paquete raíz |
| Dominio | `domain.model`: PerfilCliente / PerfilVendedor, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: ConsultarPerfil / EditarPerfilPropio; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: PerfilPort; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-08-T001/T002** y de las tareas de endpoints ya
existentes; no son otra funcionalidad ni duplican IDs. Cada tarea de endpoint
entrega caso de uso, DTO/mapping HTTP y adaptadores necesarios, además de su UI.
Los tests de mappers excluyen secretos y distinguen omitido/null; los tests del
núcleo usan puertos dobles, sin Spring/DB. La aceptación usa PostgreSQL y
proveedores reales. Reglas ArchUnit de TEC-INFRA-T001 verifican los límites.
El esqueleto compartido se prepara en TEC-INFRA-T001; puede adelantarse el
núcleo aislado, pero no cerrar persistencia/HTTP sin las dependencias reales.

## Orden de trabajo

T001–T002 entregan datos/proyección al resto. T003–T004 construyen API propia;
T005 puede prepararse con respuestas controladas cuando esté la base React.
T006 compone enlaces reales; T007–T008 comprueban efectos y recorrido desplegado.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-08-T001 | Mappings de perfiles y documento protegido | P0 | Esquema revisado; EP-01-T002; clave privada Christian si existe documento | En curso |
| EP-08-T002 | PerfilPort y proyección segura | P0 | T001; EP-01-T002; EP-05-T002; EP-04-T002 | En revisión |
| EP-08-T003 | Consulta GET /auth/me | P1 | T002; TEC-IDENTIDAD-T002 y acceso Jose Luis | En curso |
| EP-08-T004 | PATCH propio con campos presentes | P1 | T002; EP-01-T002; EP-06-T003; TEC-INFRA-T003 | En curso |
| EP-08-T005 | MiCuentaPage y edición propia React | P1 | T003–T004; EP-02-T001/T008 | En curso |
| EP-08-T006 | Componer acciones de seguridad disponibles | P1 | T005; EP-03-T007; EP-06-T005; EP-02-T008 | Pendiente |
| EP-08-T007 | Verificar privacidad, autorización y efectos | P1 | T003–T004; providers/transporte reales | Pendiente |
| EP-08-T008 | Defender perfil propio desplegado | P1 | T005–T007; login/MFA y TEC-INFRA-T005 | Pendiente |

Txxx pertenece a EP-08 salvo prefijo explícito. Los otros IDs apuntan a sus
tableros, sin asignar a Valery implementar las capacidades de sus dueños.
Dobles identificados permiten pruebas aisladas; no cierran aceptación conjunta.

## Tareas

### EP-08-T001 — Mapear perfiles y proteger documento existente

- **Fuentes:** RF-16.3/16.4; persistencia §§1–2; PLAN §4.
- **Implementar:** mappings/repositorios propios `perfil_cliente` y
  `perfil_vendedor` en `src/main/java/com/marketplace/auth_service/` del repo backend (perfil), UUID de
  usuario como valor sin entidad/repo/cascadas ajenos. Leer identidad/dato cifrado
  necesario mediante capacidad segura de Eva; Valery no consulta su tabla.
- **Cierre:** atributos aplicables se leen sin inventar valores. Documento
  AES-256-GCM con clave externa/documento_key_id y envelope versionado validado;
  descifrar solo dentro de proyección autorizada, devolver últimos tres dígitos
  enmascarados. Documento claro/cifrado/clave nunca salen hacia Eva/React.
- **Comprobar:** PostgreSQL/mappings, roles CLIENTE/VENDEDOR/ambos, documento
  ausente y sintético cifrado presente, clave/tag/formato inválidos. No tomar
  últimos dígitos del ciphertext ni fingir null ante clave ausente. Datos legacy
  incompatibles se registran antes de incluirlos en fixtures; sin conversión ciega.
- **Evidencia:** tests y contrato de proyección, sin documento/clave. Cargar dato
  técnico mediante dueño físico, no implementar edición administrativa futura.

### EP-08-T002 — Entregar PerfilPort y DTO privados/públicos seguros

- **Fuentes:** RF-16.1/16.3/16.4; RF-11.10; composición §2 `PerfilPort`;
  OpenAPI `Usuario`/`PerfilUsuario`.
- **Implementar:** interfaz/DTO/adaptador para leerPropio, proyectarPara y
  actualizarPropio; consumir cuentas Eva, autorización Sergio y MFA Luis.
  Separar coordinador de adapters para evitar ciclos de beans con consulta Eva.
- **Cierre:** titular obtiene perfil privado con mfaHabilitado efectivo requerido;
  cualquier rol de gestión implica true aunque bandera voluntaria false.
  Proyección administrativa de Eva revalida permiso y solo contiene campos
  permitidos; sin convertir DTO interno/entidad JPA en respuesta libre.
- **Comprobar:** roles múltiples, perfil incompleto/null según esquema, actor
  sin permiso, documento enmascarado y ausencia de hashes/OTP/tokens. Proyección
  en lote para listado evita N+1; no exponer atributo privado a servicio.
- **Entrega/evidencia:** tests y contrato a Eva/Juan José; no exigir cerrar sus
  pantallas para probar el port ni implementar rutas de servicio SPEC-18.

### EP-08-T003 — Implementar GET /api/v1/auth/me

- **Fuentes:** RF-16.1, ESC-16.1; OpenAPI operación/PerfilUsuario;
  [FRONT-06 UI-06.1](../../../specs/front/FRONT-06-mi-cuenta.md).
- **Implementar:** controlador/servicio propios que resuelven titular por sub
  validado y llaman T002. No aceptar usuarioId alternativo por query/cuerpo ni
  añadir otra ruta para consulta propia.
- **Cierre:** 200 perfil real con campos requeridos/condicionales y MFA efectivo;
  sin token 401, token de servicio válido no aplicable según catálogo G7.
  ID elegido por navegador no permite consultar a otra cuenta.
- **Comprobar/evidencia:** tests HTTP/DB A/B y roles, perfil existente, campos
  ausentes/presentes y secretos excluidos. Datos para medidor Juan José y UI
  proceden de esta respuesta; no duplicar consulta pública de identidad.

### EP-08-T004 — Implementar PATCH propio de atributos

- **Fuentes:** RF-16.2/16.7/16.9/16.10; ESC-16.2/16.5/16.11/16.12;
  RF-11.10; OpenAPI PATCH /usuarios/{id}/atributos y AtributosUsuario.
- **Implementar:** DTO con presencia explícita y whitelist, controlador/servicio
  Valery; autorizar titular/campos antes de escribir, lock cuenta Eva y
  actualizarBasicos mediante su port. Validar nombres/apellidos/celular según
  esquema/reglas; null solo donde permitido, omitido conserva dato anterior.
- **Cierre:** 200 Usuario actualizado real; otro id/documento sin privilegio da
  403 SCOPE_INSUFICIENTE sin efecto parcial. Rol/estado/mfaHabilitado/correo
  no se modifican por enviar JSON. Aplicabilidad/errores respetan catálogo;
  no habilitar rama de edición administrativa/atributos de rol aplazada.
- **Efectos:** celular diferente deja celularVerificado false, sin OTP externo.
  Evento usuario.atributos_actualizados solo nombres de campos realmente
  cambiados; outbox mismo commit. ATRIBUTOS_ACTUALIZADOS es no crítica:
  registrar mediante EP-06-T003 después del commit, sin revertir negocio por
  fallo de auditoría; no usar un insert crítico para cambiar esa regla.
- **Comprobar/evidencia:** PATCH parcial conserva omitidos, inválido no muta,
  campos mezclados privilegiados no permiten aplicar parte válida. Pruebas
  DB/HTTP, fallo outbox rollback y auditoría no crítica con reintento; sin SQL
  ni repositorio Usuario ajenos y sin valores personales en evento/auditoría.

### EP-08-T005 — Construir MiCuentaPage y formularios propios

- **Fuentes:** FRONT-06 UI-06.1/06.2/06.3/06.7/06.8 y
  [componentes FRONT-06](../../../specs/componentes-react/FRONT-06-mi-cuenta.md),
  selección H3; sistema de diseño y base React de Jose Luis.
- **Implementar:** `src/pantallas/mi-cuenta/`: `MiCuentaPage`,
  `useMiCuenta`, `SeccionDatosPersonales`, `SeccionContacto`, `SeccionDocumento`;
  cliente HTTP/tema/sesión compartidos, sin otro store/librería de UI.
- **Cierre:** carga/error/reintento, lectura y una sección editable a la vez.
  Guardar envía solo modificados; cancelar no muta. Tras 200 usar respuesta y
  cerrar edición; celular cambiado muestra Sin verificar. Correo solo lectura;
  documento anuncia últimos tres dígitos/Sin registrar si realmente ausente.
  Atributos aplicables de rol se leen, nunca editan en este incremento.
- **Comprobar:** tests guardar/cancelar/doble clic/errores y persistencia con
  nuevo GET; teclado/foco/etiquetas y 390 px. Sin dirección cargada automáticamente,
  cambio de correo, botones verificar celular o configuración MFA futura.
- **Evidencia:** tests/capturas sanitizados y API real; no preparar éxito ficticio
  por actualización solo del estado React.

### EP-08-T006 — Componer SeccionSeguridad con entregas reales

- **Fuentes:** FRONT-06 sección Seguridad/selección H3, FRONT-00;
  entorno/React §4; tareas EP-03-T007/EP-06-T005/EP-02-T008.
- **Implementar:** `SeccionSeguridad`: enlaces a cambio de contraseña y actividad
  real, logout de Jose Luis y estado MFA informativo obtenido del perfil.
  Valery mantiene composición/navegación; cada dueño conserva su página/hook.
- **Cierre:** enlaces solo disponibles tras integrar esas entregas; no bloquear
  consulta/edición independiente porque falte una página. Logout limpia memoria
  aunque falle red; recarga exige login. No interruptor MFA histórico ni botón
  conectado a flujo posterior. No duplicar renovación/cierre de sesión.
- **Comprobar/evidencia:** tests de enlaces/retorno/cierre, composición con páginas
  reales y guard; evitar dependencia circular: Valery puede entregar perfil T003
  antes de completar navegación T006.

### EP-08-T007 — Comprobar autorización, privacidad y efectos persistidos

- **Fuentes:** PLAN §7; escenarios RF-16/FRONT-06 aplicables.
- **Inicio:** T003/T004, ports reales y transporte Christian; cuenta dedicada C
  para edición, sin alterar nombres de A/B de la demo G5.
- **Cierre:** A ve solo A; PATCH a B deniega sin modificar B. Campos omitidos
  permanecen, inválidos/privilegiados no dejan cambios parciales; rol/estado/MFA
  no se asignan masivamente. Documento protegido, MFA gestión efectivo y celular
  cambiado no verificado. EventoId/campos reales, nunca valores personales.
- **Comprobar:** HTTP/PostgreSQL, mutaciones concurrentes serializadas por cuenta,
  rollback outbox y no crítica reintentada; evento Rabbit publicado solo tras
  commit. Desactivar validación React no evita backend. Sin secretos en evidencia.
- **Evidencia:** comandos/resultados/estado DB y revisión con proveedores;
  mocks no acreditan firma/login/evento real ni edición administrativa futura.

### EP-08-T008 — Defender perfil propio desplegado

- **Fuentes:** PLAN §7; tareas anteriores y FRONT-06 selección H3.
- **Inicio:** login/MFA/base React/providers y despliegue reales; páginas
  enlazadas T006 disponibles para aceptación completa.
- **Cierre:** login→Mi cuenta→editar nombre/celular→PATCH→DB→respuesta/pantalla;
  nuevo GET confirma persistencia. Mostrar intento ajeno denegado, cancelar sin
  efecto y celular Sin verificar. Navegación de seguridad real, sin flujos futuros.
- **Comprobar/evidencia:** tests backend/React y recorrido en nube, versión/URL,
  capturas seguras y uso de IA trazado spec→plan→tarea→código→prueba. Recarga
  requiere login y datos permanecen en DB. No declarar toda SPEC-16 completada.

## Registro de ejecución y revisión

Cada tarea termina con criterios, evidencia y revisión de otro integrante.
La revisión UX transversal se registra aparte, sin transferir frontend ajeno.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | Todas | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
