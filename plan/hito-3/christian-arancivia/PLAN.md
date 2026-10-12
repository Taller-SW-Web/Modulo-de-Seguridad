# H3 — Christian Gabriel Arancivia Salas

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/christian-arancivia/TAREAS.md).
Una tarea lista por vez; distinguir EP-06 de TEC-INFRA sin duplicar tableros.

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

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueño completo | Christian Gabriel Arancivia Salas |
| Épica académica | EP-06 — Auditoría; SPEC-12/13 |
| Capacidad adicional | Infraestructura; no reemplaza su funcionalidad académica |
| Incremento / versión | H3-CHRISTIAN / 1.1.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 + complemento local; v1 1.1.0 |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-06](../../../specs/epicas/EP-06-auditoria.md), [SPEC-12](../../../specs/funcionales/SPEC-12-registro-auditoria.md), [SPEC-13](../../../specs/funcionales/SPEC-13-consulta-auditoria.md) |
| UI/UX | [FRONT-13 actividad propia](../../../specs/front/FRONT-13-actividad-propia.md), [FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md), [diseño](../../../docs/diseno/sistema-diseno.md), [mockups](../../../docs/mockups/README.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): GET /auth/me/actividad, RegistroAuditoria/PaginaAuditoria; errores y [acciones](../../../specs/catalogo-errores.md) |
| React | [ActividadPage/TablaActividad](../../../specs/componentes-react/FRONT-13-actividad-propia.md) y [base](../../../specs/componentes-react/FRONT-00-base-compartida.md) |

Infraestructura en [TEC-INFRA](../../transversales/TEC-INFRA.md);
transporte de [eventos](../../../specs/catalogo-eventos.md);
[diseño compartido](../ACUERDOS-INTERNOS.md).

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

**Recorrido académico:** una acción real (login/fallo, cambio de contraseña,
OTP o bloqueo) escribe registro seguro; el titular abre Actividad reciente y
ve únicamente eventos de su cuenta persistidos, ordenados y paginados.
Otro usuario no puede elegir esa identidad en la petición.

Christian entrega escritor/transacciones/repositorios y consulta/pantalla/pruebas.
Los productores conservan la decisión de cuándo ocurrió el hecho y llaman
la capacidad canónica; Christian no implementa sus funciones.

**Adicional:** preparar despliegue, DB/SMTP/RabbitMQ y transporte compartido de
entregas propias. Coordina, sin apropiarse del backend/frontend de los demás.

**Después:** consulta administrativa de auditoría y bloque HistorialTabs,
exportación H-08, evidencia de rendimiento a escala y automatización completa
de retención. En H3 ya rigen solo-anexado, privacidad y conservación mínima
de 90 días; no purgar datos de prueba para simular historial vacío.

Navegación H3 muestra actividad propia, no exportación ni panel administrativo
de auditoría aún no entregados. Conserva los RF posteriores sin declararlos hechos.

## 3. Cobertura y criterios

| Fuente | Capacidad | Resultado H3 |
|---|---|---|
| RF-12.1/12.2/12.3 | Escritor auditado | Catálogo cerrado, formato único, éxito/fallo y actor/objetivo/contexto |
| RF-12.4/12.5; ESC-12.3/12.4 | DB y privacidad | Aplicación sin UPDATE/DELETE de auditoría; ningún secreto/documento claro |
| RF-12.6; ESC-12.6 | Criticalidad | Operación crítica revierte si falta auditoría; no crítica conserva su regla y reintenta |
| RF-12.7 | Retención | Conservar al menos 90 días; purga automatizada posterior con usuario separado |
| RF-13.5; ESC-13.6; UI-13.1–13.4 | GET /api/v1/auth/me/actividad | Solo objetivo igual al sub validado, paginado/ordenado según contrato |
| RF-13.4 | Lectura auditada | Auditar consulta propia con detalle seguro, sin recursión de llamadas |
| TEC-01 y plan conjunto | Capacidad adicional | Servicios reales y prueba reproducible; no sustituye su demo de actividad |

## 4. Decisiones técnicas

### Escritura, lectura y transacciones

V4/AuditoriaSeguridad existen; faltan repositorios/servicios de auditoría.
Campos y acciones provienen de SPEC-12, no de nombres inventados en código.
Validar detalle por acción y rechazar/sanear secretos antes de serializar.
Una referencia a token usa jti, nunca el token completo.

Críticas (cambio de contraseña/roles/bloqueos incluidos) se insertan dentro
de la transacción del productor. Si auditoría falla, esa transacción revierte.
No usar REQUIRES_NEW para una crítica que debe confirmar/revertir con el hecho.

No críticas de sesión/OTP se coordinan después de confirmar el hecho, con
transacción independiente; una excepción de su escritura no puede marcar
rollback-only la apertura de familia. Reintento cerrado: cola privada durable seguridad.auditoria.pendiente,
registro_id deduplicable y tres reintentos 10/30/60 s antes de DLQ privada,
según DISENO-PERSISTENCIA. No es el evento público de usuario ni una suscripción de G5.
Si DB/cola no permiten guardar, registrar fallo técnico sin secreto y no
afirmar que el evento ya quedó auditado; respetar RF-12.6.

La consulta resuelve identidad del sub, sin objetivoUsuarioId seleccionable
para actividad propia. Filtrar y paginar en SQL antes de devolver datos, no
descargar la auditoría completa para filtrar en React. Identidades desconocidas
en intentos de login tienen objetivo null: no atribuirlas a otra cuenta.

Registrar lectura una vez con acción canónica AUDITORIA_CONSULTADA; no volver
a invocar HTTP para escribir ni disparar otra lectura recursiva. Usar fechas
UTC y orden estable con identificador como desempate si hace falta.

### Privilegios y conservación

V4 tiene trigger contra UPDATE, pero no acredita rol de aplicación sin DELETE.
Christian separa credencial de migración de credencial runtime y revisa grants.
La purga futura usa otra identidad y evidencia; su scheduler no se implementa
en este bloque ni se da permiso de borrado al usuario de la aplicación.

### React

ActividadPage/TablaActividad/etiquetasAccion y hook propios. Paginación,
vacío, error, acción desconocida sin romper y fecha en hora local de la UI.
El enlace a cambio de contraseña usa el bloque real de Juan José; el de
configuración MFA futura no se anuncia disponible en H3.

No exportar ni consultar /auditoria desde esta pantalla. La consulta propia
no exige auditoria.ver de administrador, pero sí acceso de usuario válido.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Antes de |
|---|---|---|
| Hechos reales de sesión/OTP/roles/protección/cambio | Jose Luis/Luis/Sergio/Juan José | Evidencia integrada del registro; tests aislados usan fixtures declaradas |
| Identidad/acceso | Eva/Jose Luis/Sergio | Historial del titular con autorización real |
| Críticas y orden de locks | Productores + Christian | Rollback/reintentos correctos sin transferir su lógica de negocio |
| DB/grants/cola | Christian, TEC-INFRA | Append-only y lectura reales; sin permisos de borrado para aplicación |
| FRONT-13 aprobado H3 | Christian; UX Valery | Implementar UI y comprobar criterios trazados |
| Plataforma y configuración G5 | Christian/Sergio/Michael | Entrega conjunta en nube, independiente del diseño de historial |

## 6. Fases y entregables

| Fase | Entrega de Christian | Verificación |
|---|---|---|
| Diseño | DTO seguro, criticidad, lectura y reintento/privilegios | Consistencia con SPEC-12/13 y productores |
| Registro | Repositorio/escritor y coordinación transaccional | DB real, crítica revierte y no crítica no tumba login |
| Consulta/UI | Endpoint propio, ActividadPage y paginación | Solo titular, vacío/acciones/foco y persistencia |
| Infraestructura | Entorno compartido y adaptadores de mensajes | SMTP/broker/despliegue reales según TEC-INFRA |
| Evidencia | Historial de hechos reales y pruebas de privacidad | Defensa académica separada de la entrega DevOps |

## 7. Pruebas y terminado

Login correcto/fallido deja registros reales sin contraseña/hash/token; OTP
sin código; bloqueo con detalle permitido; cambio de contraseña crítico.
Fallo de auditoría crítica revierte el hecho; no crítica respeta continuidad
y evidencia de reintento cuando se restablece la dependencia.

Con runtime DB user, UPDATE/DELETE se rechazan. CLIENTE A consulta solo A;
B solo B; manipular query/identidad no devuelve A. Sin token recibe 401;
token de servicio no se acepta como persona. Orden/paginación coherentes,
acción desconocida se muestra segura, estado vacío no inventa filas.

Terminado: hecho real→registro DB→consulta HTTP→pantalla y casos con evidencia.
DevOps solo no cierra EP-06. Retención/administración/exportación y prueba de
un millón de registros siguen posteriores, sin afirmar sus RNF verificados.

## 8. Riesgos y recuperación

No borrar auditoría para volver a ensayar ni incluir payload secreto en una
captura. Los eventos públicos y auditoría interna son entregas distintas.
No declarar éxito de escritura por haber encolado sin confirmar el registro.

Si una dependencia real aún no produce hechos, usar fixture únicamente para
probar escritor/consulta y registrar pendiente de composición. Configuración
de nube aún no elegida no impide diseño, pero impide acreditar despliegue.
