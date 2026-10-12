# H3 — Juan José Cano Vasquez

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/juan-cano/TAREAS.md).
Una tarea lista por vez; no implementar todo el plan en una sola petición.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `credencial y token` bajo el paquete raíz |
| Dominio | `domain.model`: Credencial / PasswordHistorial / TokenUnUso, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: CambiarContrasena / ConsultarPolitica; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: CredencialesPort, TokensUnUsoPort; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-03-T001/T002/T003** y de las tareas de endpoints ya
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
| Dueño completo | Juan José Cano Vasquez |
| Épica | EP-03 — Gestión de credenciales; SPEC-07/08 |
| Incremento / versión | H3-JUAN / 1.1.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 + complemento local; v1 1.1.0 |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-03](../../../specs/epicas/EP-03-credenciales.md), [SPEC-07](../../../specs/funcionales/SPEC-07-politica-cambio-contrasena.md); [SPEC-08](../../../specs/funcionales/SPEC-08-recuperacion-contrasena.md) conserva recuperación posterior |
| UI/UX | [FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md), [FRONT-10](../../../specs/front/FRONT-10-cambio-contrasena.md), enlaces desde [06](../../../specs/front/FRONT-06-mi-cuenta.md)/[13](../../../specs/front/FRONT-13-actividad-propia.md); [diseño](../../../docs/diseno/sistema-diseno.md), [mockups](../../../docs/mockups/README.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): GET /password/politica, POST /password/cambiar y errores de política/credenciales |
| React | [Base/política](../../../specs/componentes-react/FRONT-00-base-compartida.md), [CambiarContrasenaPage/useCambiarContrasena](../../../specs/componentes-react/FRONT-10-cambio-contrasena.md) |

Revisar [datos](../../../docs/integracion/revision-datos.md),
[interfaces H3](../ACUERDOS-INTERNOS.md) y
[sesión de Jose Luis](../jose-limachi/PLAN.md).

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

Usuario con sesión vigente abre el formulario, consulta la política y cambia
su contraseña proporcionando la actual. Se actualizan hash e historial,
se conservan los refrescos de esa familia y se revocan las demás, se audita
el hecho crítico y llega el aviso por correo. Una contraseña actual incorrecta
no modifica datos ni cierra la sesión; una nueva inválida recibe 422.

Juan José implementa interfaz, endpoint, regla/datos, pruebas y defensa.
Su verificador/política se consume desde login y OTP; no implementa esos
controladores ni la revocación propia de Jose Luis.

**Siguiente incremento:** recuperación/restablecimiento por enlace de SPEC-08.
H3 no anuncia /password/recuperar ni /restablecer disponibles. La comprobación
de caducidad de personal sí es obligatoria en la autenticación implementada:
si caducó no se emiten tokens; la recuperación aún pendiente no autoriza
saltarse ese control. Fixtures de gestión tienen fecha real vigente.

## 3. Cobertura y criterios

| Fuente | Operación | Resultado H3 |
|---|---|---|
| RF-07.1/07.2/07.3/07.5/07.11 | Política y validación | Longitud 10, complejidad, datos personales/comunes; backend gobierna reglas y medidor |
| RF-07.4/07.6; ESC-07.5 | Persistencia de hash/historial | Últimas cinco no reutilizables; ningún secreto en texto claro |
| RF-07.8; ESC-07.8/07.9; UI-10.1/10.2/10.3 | POST /api/v1/password/cambiar | 204 real o 401/422 canónico; contraseña actual incorrecta no cierra sesión |
| Descripción OpenAPI /password/cambiar; UI-10.4 | Sesiones | Mantener familia actual y revocar todas las otras del usuario |
| RF-07.9/07.10; RF-12.6 | Notificación/auditoría | Aviso asíncrono real; fallo de auditoría crítica revierte el cambio |
| RF-07.7; ESC-07.7 | Capacidad de caducidad para login/OTP | Cualquier rol de gestión caduca al día 90; sin sesión al completar autenticación |

## 4. Decisiones técnicas

### Política y credencial

Argon2id con parámetros, version de credencial, historial y migración fijados
en DISENO-PERSISTENCIA; BCrypt costo >=12 solo para hashes existentes compatibles.
No retornar hashes a controllers ni afirmar que V100 contiene credenciales válidas.
Juan José también entrega TokensUnUsoPort para el enlace de Luis: solo él
accede a token_un_uso, sin implementar por ello la regla del desbloqueo.

Lista común UTF-8 en /run/config/g7/password-comunes.txt, versión registrada,
recarga 60 s sin nuevo deploy. Para comparación de política: NFKD, quitar
diacríticos, lowercase y espacios normalizados sobre copias; no modificar
el secreto para hash/login. Rechazar coincidencia en lista y presencia de
nombre/apellidos completos, correo completo o su parte local; nombres
compuestos también se comprueban por tokens de al menos tres caracteres.
Los nombres completos cortos siguen protegidos. La API publica esquema actual,
sin hashes/historial ni endpoint de lista. Backend decide incluso sin medidor.

Los cinco hashes recientes permiten comprobar reutilización mediante el
verificador, no comparar hashes salados por igualdad. Contraseña actual cuenta
como la más reciente; conservar ventanas/fechas de historial consistentemente.
Actualización de fecha/hash e historial es transaccional y serializada por
cuenta; dos cambios concurrentes no pueden pisar la nueva contraseña.

### Sesión actual y transacción

Actor del acceso validado, no usuarioId del formulario. Pedir a Jose Luis
resolución jti→familia actual y revocación de otras familias; DAT-01 es
dependencia de H3. No inferir familia por sub ni añadir refreshToken/familiaId
al cuerpo de /password/cambiar.

Cambio + revocación de otras familias + CONTRASENA_CAMBIADA crítica +
solicitud durable de aviso se coordinan en una transacción. Si auditar falla,
no dejar hash nuevo ni refrescos revocados a medias. Si el proveedor SMTP
falla después, mensaje pendiente con reintento, sin deshacer un cambio ya
confirmado ni afirmar correo entregado.

### Interfaz

CambiarContrasenaPage/useCambiarContrasena y medidor de FRONT-00 conservan
responsabilidad de Juan José. Campos actual/nueva/repetir, foco al error y
validación orientativa; no se envía repetir. Datos personales para orientación
proceden de /auth/me de Valery; la comprobación del backend usa datos reales.

401 CREDENCIALES_INVALIDAS aquí significa contraseña actual equivocada:
no disparar refresh/logout. Tras 204 volver a Mi cuenta conservando sesión;
las otras sesiones detectan revocación al renovar, no prometer que sus JWT
ya emitidos desaparezcan de inmediato.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Condición |
|---|---|---|
| Identidad, fixture de credencial vigente | Eva/Juan José | Cuenta real y protocolo de pruebas, separada de A/B |
| Familia actual y revocación transaccional | Jose Luis | DAT-01 concretado y capacidad probada con dos sesiones |
| Roles de gestión | Sergio | Caducidad por cualquier rol, no solo uno |
| Perfil para UI y navegación | Valery | /auth/me y enlace al formulario, sin duplicar consulta pública |
| Auditoría crítica y correo | Christian | Mismo commit de datos para auditoría y entrega durable de aviso |
| UI/backend aprobado H3 | Juan José; UX Valery | Implementar FRONT-10 y mapping cerrado, verificar escenarios en tareas |

Puede preparar/verificar política, hashing y formulario de forma independiente.
La aceptación completa espera sesión/DB/auditoría/correo reales. No generar
tareas de recuperación ni bloquear este cambio por términos de registro.

## 6. Fases y entregables

| Fase | Entrega de Juan José | Verificación |
|---|---|---|
| Diseño | Revisión de reglas, datos, lista común y sesión actual | Correspondencia funcional/UI/API/React |
| Credencial | Política, hashing/verificación, caducidad e historial | Casos positivos/negativos sin HTTP fijo |
| Cambio | Servicio/controlador y transacción con dependencias | PostgreSQL, dos familias y rollback de auditoría |
| Interfaz | Formulario/medidor y navegación de éxito/error | FRONT-10, teclado/foco y sesión conservada |
| Evidencia | Cambio real y aviso; nuevo login acepta nueva y rechaza antigua | Pruebas y defensa de todas las capas |

## 7. Pruebas y terminado

Cubrir nueva válida, corta/incompleta/común/con datos personales, una de las
cinco previas y actual incorrecta. El backend deniega aunque se desactive
validación React. Sin acceso vigente, denegar; no cambiar cuenta ajena porque
la ruta no acepta un identificador.

Dos familias: cambiar desde C1 mantiene C1 y rechaza refresh de C2. Fallo
de auditoría crítica revierte hash/historial/revocación. Concurrencia no deja
hash/historial inconsistentes. Confirmar llegada del aviso sin secreto.

En login/OTP, gestión con 90 días no obtiene tokens; CLIENTE/VENDEDOR sin rol
de gestión no recibe caducidad obligatoria de personal. UI-10.2 conserva sesión.

Terminado: recorrido real de pantalla/HTTP/DB/respuesta y casos con evidencia,
más verificador consumido por autenticación. No cierra SPEC-08 ni toda EP-03.

## 8. Riesgos y recuperación

No cambiar la contraseña de A/B durante la integración G5. Usar cuenta dedicada,
credencial inicial compartida privadamente y reposición controlada en pruebas.
No loguear contraseñas ni sus hashes, ni enviar la nueva contraseña por correo.

Si no se puede identificar familia actual, bloquear la tarea de cambio;
revocar todas o conservar todas no implementa la regla. Recuperación/caducidad
sin interfaz posterior no se disimula con reset manual como demo de SPEC-08.
