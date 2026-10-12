# H3 — Eva Lucía Moreno Zevallos

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/eva-moreno/TAREAS.md).
Una tarea lista por vez; no implementar todo el plan en una sola petición.

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueña completa | Eva Lucía Moreno Zevallos |
| Épica | EP-01 — Registro y gestión de usuarios; SPEC-01–04 |
| Incremento / versión | H3-EVA / 1.0.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 y complemento documental local; v1 1.1.0 |
| Evidencia | Por producir al implementar; no se acredita carga/consulta real |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-01](../../../specs/epicas/EP-01-registro-gestion-usuarios.md), [SPEC-03](../../../specs/funcionales/SPEC-03-alta-consulta-cuentas.md); [SPEC-01](../../../specs/funcionales/SPEC-01-registro-clientes.md), [02](../../../specs/funcionales/SPEC-02-verificacion-correo.md) y [04](../../../specs/funcionales/SPEC-04-baja-reactivacion.md) conservan alcance posterior |
| UI/UX | [FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md), [07](../../../specs/front/FRONT-07-admin-listado-usuarios.md) y [08](../../../specs/front/FRONT-08-admin-detalle-usuario.md); [diseño](../../../docs/diseno/sistema-diseno.md) y [mockups](../../../docs/mockups/README.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): GET /usuarios, GET /usuarios/{id}, Usuario y pagina/listado; [errores](../../../specs/catalogo-errores.md) |
| React | [Base](../../../specs/componentes-react/FRONT-00-base-compartida.md), [FRONT-07](../../../specs/componentes-react/FRONT-07-admin-listado-usuarios.md), [FRONT-08](../../../specs/componentes-react/FRONT-08-admin-detalle-usuario.md) |

Complementan [estados](../../../docs/arquitectura/estados-usuario.md),
[datos](../../../docs/integracion/revision-datos.md) y
[composición H3](../ACUERDOS-INTERNOS.md). No hace falta editar el contrato.

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

**Recorrido H3:** ADMIN_SISTEMA autenticado mediante MFA real entra al panel,
consulta cuentas con filtros/paginación y abre el detalle persistido. Un
CLIENTE recibe 403; sin acceso válido, 401. El listado no es una respuesta fija.

Eva implementa pantalla/contenedor, operaciones de lectura, mapping/datos,
autorización consumida, pruebas y demostración. Sergio entrega el bloque de
roles dentro del detalle; Luis entrega estado efectivo; Valery aporta proyección
segura de atributos. Eva integra esas capacidades y conserva su lectura completa.

**Además:** protocolo de fixtures técnicos para los compañeros y Ventas. Preparar
cuentas no sustituye el recorrido académico ni acredita registro público.

**Siguiente incremento:** registro/verificación de correo, altas/activación,
registro asistido, baja/reactivación y búsqueda por documento. Requieren sus
fuentes, términos H-12 y, en activación, la spec UI/React pendiente #36.
No implementar POST /usuarios v1 histórico; alta estricta posterior es v2.

La navegación H3 muestra listado/detalle y el bloque de roles disponible.
Oculta alta/baja/reactivación/bloqueo manual y otras acciones aplazadas.
No emitir eventos de usuario creado por una consulta o un fixture técnico.

## 3. Cobertura y criterios

| Fuente | Operación/capa | Resultado H3 |
|---|---|---|
| RF-03.2; ESC-03.2 | GET /api/v1/usuarios con permiso usuario.ver | Página real; filtros estado/rol/correo, página/tamaño conforme contrato |
| RF-03.2; UI-07.1/07.7 | FiltrosUsuarios + TablaUsuarios | Valores de DB, página vacía correcta, estado de carga/error, debounce prescrito |
| RF-03.2; UI-08.8 | GET /api/v1/usuarios/{id}, AdminDetallePage | Datos del objetivo autorizado; 404 después de autorización si inexistente |
| RF-11.9/11.10; ESC-03.2 | Permiso y proyección | CLIENTE no lista cuentas ajenas; no enumeración sin autorización |
| RF-14.7; RF-15.6 | Lectura del estado efectivo | Vencimiento automático visible como ACTIVO, sin job; bloqueo no revela motivo a servicio |
| TEC-02; plan H3 conjunto | Fixture A/B/V y usuarios independientes | UUID estables, credenciales reales mediante Juan José, roles mediante Sergio, sin consentimiento inventado |

Las acciones RF-03.1/03.3–03.9 y RF-04 no se contabilizan como terminadas por
mostrar botones o datos de fixtures. El objeto Usuario respeta campos requeridos,
condicionales y privacidad; no serializar directamente una entidad JPA.

## 4. Decisiones técnicas

### Interfaz y HTTP

Usar componentes propuestos AdminListadoPage/FiltrosUsuarios/TablaUsuarios/
AdminDetallePage y su cliente HTTP común. Eva compone las rutas del panel,
pero no programa por Sergio su SeccionRoles. Solo se muestran bloques entregados
y probados de este incremento. Retorno al listado conserva filtros y página.

GET /usuarios es búsqueda administrativa autorizada; no se convierte en consulta
pública por correo ni cambia ADR-006. Autorizar antes de buscar objetivo o
construir respuesta. 401/403/404 y códigos existentes permanecen estables.

### Datos y fixtures

V1 contiene usuario/perfiles y V100 semillas. Faltan mappings/repositorios
completos de cuentas del issue #22: UsuarioEstadoEfectivo no los sustituye.
Eva diseña/entrega Usuario y consultas con estado efectivo, sin duplicar
entidades del mismo nombre en otras épicas. Datos de roles provienen de Sergio.

Fixtures aprobadas como diseño técnico: A/B CLIENTE activos sin MFA voluntario, V VENDEDOR sin
CLIENTE, ADM ADMIN_SISTEMA con correo operativo y MFA efectivo. Además M para
MFA, L para bloqueo y C para cambio/perfil si se requiere aislamiento.
Los UUID reales se fijan al cargar y se comunican a Michael; no están definidos
por este plan. A tiene pedidos en G5; B ninguno. Cambios de roles/contraseña o
bloqueos se prueban en cuentas separadas, sin alterar A/B a mitad de la demo.

Usar entorno de prueba e identidades sintéticas. Juan José calcula hashes
compatibles y política; Sergio entrega asociación de roles. No guardar
contraseñas reales en Git ni insertar aceptaTerminos:true con texto inexistente.
H-12 no se declara resuelto por un fixture; su propósito técnico se registra.

### Seguridad y composición

GET autorizado proyecta identidad/atributos mediante las capacidades reales.
Nunca devolver hashes de credencial, OTP o tokens. ADMIN_SISTEMA entra después
del segundo factor, aun cuando la bandera mfa_habilitado de una semilla sea false.
El listado no exige credenciales de servicio en React.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Antes de |
|---|---|---|
| Cuenta/mapping y fixtures TEC-02 | Eva con Juan José/Sergio | Consulta real y prueba conjunta; revisar datos sintéticos y cadena aplicada |
| Login/MFA, sesión y roles | Jose Luis, Luis, Sergio | Prueba de panel con ADMIN_SISTEMA real; un JWT de fixture solo comprueba aislamiento |
| Estado efectivo/proyección | Luis, Valery | Respuestas completas/seguras, incluida expiración de bloqueo |
| FRONT-07/08 y composición aprobadas H3 | Eva y dueños de bloques; UX Valery | Implementar alcance aprobado y verificar cada bloque con su dueño |
| Entorno | Christian | DB/HTTP reales y despliegue |

H-12/UI-01 bloquean flujos aplazados, no esta lectura. Revisar el plan y las
interfaces antes de derivar tareas. Los componentes pueden prepararse con un
doble identificado mientras llegan las dependencias, sin declarar integración.

## 6. Fases y entregables

| Fase | Entrega de Eva | Verificación |
|---|---|---|
| Diseño | Revisión de fuentes, mapping, consultas y protocolo de fixtures | RF/ESC/UI y campos del contrato trazados |
| Persistencia/lectura | Mapping y consultas con filtros/paginación, proyección segura | PostgreSQL real con autorizaciones y estado efectivo |
| Interfaz | Listado/detalle propios y composición del bloque roles | Navegación, vacíos, carga/error y accesibilidad |
| Composición | Panel con MFA real; A/B/V preparados para G5 | Misma identidad entre DB y sub, sin saltar controles |
| Evidencia | Pruebas, defensa del recorrido y contribución/uso de IA | Petición/consulta/pantalla reales en entorno desplegado |

Son fases; las tareas pequeñas aún no se han generado.

## 7. Pruebas y terminado

- Listado filtra y pagina datos persistidos; otro filtro sin coincidencias
  devuelve la página vacía del contrato, sin cambiar total/paginación a mano.
- ADMIN consulta detalle; UUID inexistente produce 404 solo después del permiso.
- CLIENTE autenticado recibe 403 y sin token 401; ocultar menú no es la defensa.
- Estado efectivo cambia al vencer un bloqueo; ninguna respuesta expone secreto.
- A/B/V conservan UUID/roles acordados tras reinicio; las credenciales funcionan
  mediante el verificador real. Fixture técnica no se presenta como registro.
- FRONT-07/08: teclado, foco y estados correctos; acciones aplazadas ausentes.

Terminado H3: lectura real desde pantalla hasta DB y vuelta, casos de aceptación
con evidencia y revisión, más fixtures reproducibles. No cierra SPEC-01–04.
Pruebas de carga/alcance completo posteriores conservan sus requisitos.

## 8. Riesgos y recuperación

Migraciones ya aplicadas se amplían con nuevas, no se reescriben. No reiniciar
una DB compartida para recargar fixtures; aislar usuarios y conservar UUID A/B.
Si otra mutación cambia una cuenta mientras se consulta, recargar su detalle
y no inferir estado del objeto React antiguo.

Si login/MFA aún no están disponibles, los tests de lectura aislada siguen
siendo válidos como tales; la defensa integrada queda pendiente. Términos y
activación se resuelven antes de abordar esos flujos del siguiente incremento.
