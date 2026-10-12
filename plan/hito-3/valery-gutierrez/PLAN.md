# H3 — Valery Cristin Gutierrez Bendezu

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/valery-gutierrez/TAREAS.md).
Una tarea lista por vez; no implementar todo el plan en una sola petición.

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueña completa | Valery Cristin Gutierrez Bendezu |
| Épica | EP-08 — Atributos y perfil; SPEC-16 |
| Incremento / versión | H3-VALERY / 1.0.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 + complemento local; v1 1.1.0 |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-08](../../../specs/epicas/EP-08-atributos-perfil.md), [SPEC-16](../../../specs/funcionales/SPEC-16-atributos.md), autorización [SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md) |
| UI/UX | [FRONT-06](../../../specs/front/FRONT-06-mi-cuenta.md), [FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md), [diseño](../../../docs/diseno/sistema-diseno.md), [mockups](../../../docs/mockups/README.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): GET /auth/me, PATCH /usuarios/{id}/atributos, PerfilUsuario/AtributosUsuario y [errores](../../../specs/catalogo-errores.md) |
| React | [MiCuentaPage/useMiCuenta](../../../specs/componentes-react/FRONT-06-mi-cuenta.md), SeccionDatosPersonales/Contacto/Documento/Seguridad y [base](../../../specs/componentes-react/FRONT-00-base-compartida.md) |

Revisar [composición](../ACUERDOS-INTERNOS.md),
[datos](../../../docs/integracion/revision-datos.md) y
[ADR-008](../../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md).

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

Titular autenticado ve su perfil persistido y modifica nombres/apellidos/
celular propios. Guarda un PATCH con solo campos modificados; vuelve la
entidad actualizada y al recargar se conservan los datos. Otra cuenta,
documento, rol, estado o atributos privilegiados no pueden modificarse
por titularidad.

Valery implementa página, controlador/servicio, persistencia, proyección,
eventos/auditoría y pruebas de su incremento. La revisión UX adicional no
sustituye su función end-to-end ni la convierte en frontend de todo el grupo.

**Después:** cambio de correo con verificación de Eva, listar/crear direcciones,
edición administrativa de atributos/documento y datos específicos de rol.
Edición/borrado de direcciones siguen fuera de la primera versión contractual.

La página H3 muestra datos/contacto editables y documento de solo lectura
si existe; no anuncia cambio de correo, direcciones o configuración MFA
como disponibles. SeccionSeguridad enlaza cambio de contraseña, actividad
y logout cuando estén realmente compuestos.

## 3. Cobertura y criterios

| Fuente | Operación/capa | Resultado H3 |
|---|---|---|
| RF-16.1; ESC-16.1; UI-06.1 | GET /api/v1/auth/me | Perfil del sub, con mfaHabilitado efectivo obligatorio y datos reales |
| RF-16.2; ESC-16.2; UI-06.2/06.3 | PATCH /api/v1/usuarios/{id}/atributos | Solo nombres/apellidos/celular propios; 200 entidad actualizada |
| RF-11.10; ESC-16.5/16.11 | Autorización backend | Otro id o documento propio sin atributos.editar: 403 sin mutación |
| RF-16.3; ESC-16.6 | Atributos de rol | 422 ATRIBUTO_NO_APLICABLE cuando corresponde; no mass assignment |
| RF-16.4; ESC-16.7 | Proyección de documento | Enmascarado, nunca devolver dato cifrado ni claro sin autorización |
| RF-16.7; ESC-16.12 | Cambio de celular | Estado no verificado; no inventar flujo OTP de contacto |
| RF-16.9/16.10 | Evento/auditoría | usuario.atributos_actualizados con nombres de campos, sin valores; ATRIBUTOS_ACTUALIZADOS |
| UI-06.8 | Interfaz | Un envío en vuelo, carga/error/foco y cancelación correctos |

## 4. Decisiones técnicas

### Perfil y protección

Resolver actor del acceso validado; GET /auth/me no acepta usuarioId alternativo.
Sergio provee permisos y Luis calcula MFA efectivo según cualquier rol de gestión.
Eva provee identidad base. PerfilUsuario requiere mfaHabilitado incluso sin
MFA voluntario; no exponer ese atributo privado a un consumidor con servicio.

Usar DTO explícito, sin devolver entity JPA con campos internos. La ausencia
de fechaNacimiento/codigoVendedor/tienda/fechaIngreso no se inventa: null
condicionado por roles/actor conforme contrato. Correo es solo lectura en H3.

En PATCH validar titularidad/permiso y cada campo antes de escribir. No aceptar
rol, estado, mfaHabilitado ni documento solo porque llegan en JSON.
Titularidad permite únicamente nombres/apellidos/celular. Documento propio y
atributos de rol necesitan atributos.editar; la edición administrativa queda
posterior, sin habilitarla por accidente en la rama propia.

### Datos y cambio

V1 contiene usuario/perfil_cliente/perfil_vendedor. Eva mantiene el único
mapping/repositorio Usuario; Valery posee los perfiles y la lógica de edición.
Valery valida titularidad/campos, orquesta el commit y consume actualizarBasicos
mediante CuentaEscrituraInterna de Eva; no importa ni escribe su repositorio.
Esta dependencia conserva el recorrido completo de Valery, sin repartir capas.

PATCH aplica solo campos presentes permitidos, en una transacción; no reemplazar
todo el perfil ni dejar a null campos no enviados. Un cambio de celular
marca verificación false, respetando A2. Evento incluye únicamente lista de
campos cambiados; no valores de nombres/celular/documento. Crear outbox en el
commit de la mutación y solicitar ATRIBUTOS_ACTUALIZADOS después del commit:
es no crítica según SPEC-12, con reintento sin revertir el cambio confirmado.

Fixtures iniciales pueden carecer de documento, sin inventarlo para rellenar
la UI. Si hay un documento existente, su lectura exige clave/proyección segura
del esquema real: no obtener últimos dígitos del texto cifrado, devolver claro
o fingir que no existe para ocultar falta de implementación. Revisar esa tarea
con Valery antes de incluir datos de documento en el entorno H3.

### React

MiCuentaPage/useMiCuenta y secciones propias de FRONT-06. La página H3 no
carga direcciones automáticamente si ese flujo se aplazó, ni deja botones que
llaman endpoints no entregados. SeccionDatosPersonales/Contacto conservan
Guardar/Cancelar; validar en cliente orienta, backend decide.

Tras 200 actualizar estado local desde respuesta y cerrar edición; comprobar
nuevo GET para persistencia. Celular muestra estado Sin verificar tras el
cambio, sin botón de prueba OTP. Logout consume Jose Luis; cambiar contraseña
y actividad abren las páginas reales de Juan José/Christian.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Antes de |
|---|---|---|
| Identidad/mapping base y fixtures | Eva | CuentasPort/CuentaEscrituraInterna reales, sin acceso directo a usuario |
| Acceso y base de UI | Jose Luis/Sergio | Rutas propias autenticadas; memoria y renovación correctas |
| MFA efectivo/permisos | Luis/Sergio | Perfil y denegación segura, sin alterar rol/estado |
| Auditoría/outbox | Christian; Valery productora | Mutación trazada y evento de campos sin valores |
| FRONT-06/mapping cerrado H3 | Valery | Implementar alcance/campos aprobados y mappings del dueño |
| Clave/proyección de documento existente | Valery/Christian | Cualquier prueba con documento real o sintético cifrado |

Correo/direcciones/atributos administrativos son posteriores. No bloquear edición
propia por activación o términos ajenos. Los tests aislados de servicio/React no
acreditan login integrado ni evento publicado.

## 6. Fases y entregables

| Fase | Entrega de Valery | Verificación |
|---|---|---|
| Diseño | Mapping/proyección/campos permitidos y alcance H3 | Fuentes y permisos coherentes |
| Backend/datos | GET propio y PATCH parcial seguros | PostgreSQL real, titularidad/privacidad y estados |
| Interfaz | MiCuentaPage y edición propia | Carga/vacío/error, Guardar/Cancelar y teclado |
| Composición | Sesión/MFA/acciones de seguridad y evento/auditoría | Capacidades reales, sin formulario falso |
| Evidencia | Cambio persistido y denegación de edición ajena | Pantalla→HTTP→DB→respuesta con revisión |

## 7. Pruebas y terminado

A ve A; cambiar ruta de PATCH a B da 403 y no modifica B. Modificar nombre/
apellido/celular válido se conserva tras nuevo GET; omitir un campo no lo borra.
Documento/rol/estado/atributo de otro rol se deniegan según catálogo y no
producen efectos parciales. JWT en payload no sustituye Authorization validado.

MFA efectivo true para gestión aunque bandera false. Documento enmascarado/
sin registrar según datos reales, ningún hash/cifrado/OTP en respuesta.
Cambio de celular queda false sin verificación externa. Evento contiene campos
y eventoId, nunca valores; auditoría respeta catálogo de criticidad.

FRONT-06: doble guardado impedido, errores visibles/foco, cancelar no modifica
DB y recarga requiere login porque sesión está en memoria. Acciones posteriores
ausentes y enlaces entregados funcionan.

Terminado: recorrido propio persistido desde pantalla hasta DB y respuesta,
negativos y evidencia en el entorno real. No cierra correo/direcciones/
edición administrativa ni toda SPEC-16.

## 8. Riesgos y recuperación

No sobreescribir atributos por mandar la entidad completa de React. El id
en URL no concede titularidad y decodificar JWT en UI no autoriza backend.
No registrar valores personales en eventos ni capturas de debug.

Usar usuario dedicado para cambios de perfil si la demo necesita un nombre
estable en G5. Si cae SMTP o una parte posterior no existe, no bloquear este
PATCH independiente ni mostrar éxito de una operación distinta.
