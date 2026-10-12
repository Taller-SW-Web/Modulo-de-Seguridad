# H3 — Jose Luis Limachi Sarmiento

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/jose-limachi/TAREAS.md).
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

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueño completo | Jose Luis Limachi Sarmiento |
| Épica | EP-02 — Autenticación y gestión de sesiones; SPEC-05/06 |
| Incremento / versión | H3-JOSE / 1.1.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 y complemento local; v1 1.1.0 |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-02](../../../specs/epicas/EP-02-autenticacion-sesiones.md), [SPEC-05](../../../specs/funcionales/SPEC-05-inicio-sesion.md), [SPEC-06](../../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md) |
| UI/UX | [FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md), [FRONT-01](../../../specs/front/FRONT-01-inicio-sesion.md), continuación [FRONT-04](../../../specs/front/FRONT-04-desafio-otp.md) de Luis; [diseño](../../../docs/diseno/sistema-diseno.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): login/refresh/logout, SesionIniciada/DesafioMfa y [errores](../../../specs/catalogo-errores.md) |
| React | [Base de sesión](../../../specs/componentes-react/FRONT-00-base-compartida.md), [LoginPage](../../../specs/componentes-react/FRONT-01-inicio-sesion.md) y composición de [OTP](../../../specs/componentes-react/FRONT-04-desafio-otp.md) |

El [plan detallado EP-02](../../funcionalidades/EP-02-autenticacion-sesiones.md)
mantiene el diseño de rotación/concurrencia. Este plan H3 es la entrada del
estudiante; aquel documento se consume como diseño, sin otro tablero.
[Interfaces H3](../ACUERDOS-INTERNOS.md) concreta consumidores adicionales.

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

**Recorrido propio:** pantalla login → comprobaciones reales → familia persistida
y tokens → acceso protegido; renovación rota el refresco; logout cierra esa
familia. Si hay MFA, responder desafío y continuar mediante Luis sin emitir
una sesión antes de verificarlo. Negativos conservan respuesta genérica.

**Aporte a Ventas:** CLIENTE A/B reales sin MFA voluntario, acceso RS256 consumido
por G5; no se añade llamada de G7 a G5. Michael construye su pantalla/pedidos.

El incremento conserva SPEC-05/06, incluida detección de reúso. La aceptación
con otras épicas utiliza sus capacidades, sin convertir a Jose Luis en dueño
de sus reglas. No se promete que el JWT anterior caduque al hacer logout;
el consumidor local respeta exp mientras no implemente otro control acordado.

**Después de H3:** mejoras operativas/UX de sesión, ampliar evidencia de
concurrencia/rendimiento y composición de nuevos recorridos. No aplazar una
regla de rotación/antienumeración para implementar un refresh inseguro.

## 3. Cobertura y criterios

| Fuente | Operación | Resultado H3 |
|---|---|---|
| RF-05.1/05.6; ESC-05.1; UI-01.1 | POST /api/v1/auth/login | 200 SesionIniciada, 900 s/7 días, UUID y familia reales |
| RF-05.3/05.4; ESC-05.2/05.3/05.4 | Login inválido/no disponible | 401 CREDENCIALES_INVALIDAS, sin tokens ni estado revelador |
| RF-05.2; ESC-05.5; RF-09.8 | Login de cuenta con MFA efectivo | 200 DesafioMfa; continuación de Luis, sin acceso directo |
| RF-05.5; RF-07.7 | Caducidad al completar autenticación | 403 PASSWORD_CADUCADA; no emitir tokens ni duplicar regla |
| RF-05.7; RF-06.5 | Hechos de sesión | Auditoría canónica de éxito/fallo/cierre/reúso |
| RF-06.1/06.3/06.4; ESC-06.1/06.2/06.4 | POST /api/v1/auth/refresh | Rotación atómica o 401; reúso revoca solo su familia |
| RF-06.2/06.6; ESC-06.3/06.5 | POST /api/v1/auth/logout | 204 idempotente; no requiere acceso vigente |
| UI-00.1/00.2/00.3/00.4; H-11 | React sesión/rutas | Memoria, renovación serializada, cierre limpio; recarga vuelve a login |
| H3-C01–C12 del plan conjunto | Acceso validado por G5 | Prueba real entre servicios; negativos no dependen de scopes A10 |

## 4. Decisiones técnicas

### HTTP y composición

Jose Luis implementa sus controladores/servicios y coordina filtro de acceso
con Sergio. Consume identidad Eva, verificación/caducidad Juan José, protección
y MFA Luis, roles/firma Sergio y auditoría Christian. No usa un hash fijo o
un permiso enviado por React para sustituir esas capacidades.

Después de OTP correcto, Luis solicita a la capacidad de sesión abrir una
familia; una prueba de OTP consumida no permite dos emisiones. Revalidar
cuenta y caducidad antes de completar la sesión según las fuentes.

### Datos y transacciones

Se conserva TokenRefresco/V3 y se incorporan sesion_familia y acceso_sesion
según DISENO-PERSISTENCIA. La raíz es una fila independiente de familia,
no el primer token. Todos los caminos toman cuenta y luego raíz estable;
reúso confirma revocación antes del 401. Registros usados se conservan mientras
haya un token utilizable en la familia.

Cada acceso emitido registra jti→usuario/familia/expiración. Juan José usa
resolverAcceso para conservar familia actual; no se infiere por sub ni se
admite familiaId enviado por HTTP. Diseño DAT-01 cerrado; migración/prueba
reales permanecen por implementar.

Familia previa a automático puede renovar según SPEC-15; bloqueo manual,
baja, cambio de rol/contraseña tienen sus revocaciones correspondientes.

### React compartido

LoginPage/useLogin y base de sesión conservan su dueño. Acceso/refresco/usuario
en memoria, una familia por pestaña que inicia sesión. No persistir tokens
en URL, localStorage o sessionStorage. Un 401 por contraseña actual incorrecta
en el formulario de Juan José no cierra la sesión ni dispara refresh.

El arranque React/TypeScript, cliente HTTP y router queda fijado en
ENTORNO-Y-REACT. La base todavía no tiene aplicación frontend; se crea al
implementar sus tareas de arranque. No instalar
dependencias durante este bloque documental.

Desconocer el resultado de un refresh no autoriza repetirlo automáticamente:
limpiar sesión y pedir login si no puede recuperarse sin reutilizar el token.
Rutas/enlaces a flujos fuera de H3 no anuncian disponibilidad.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Libera |
|---|---|---|
| Cuenta/estado/verificador/roles | Eva, Luis, Juan José, Sergio | Login real con todas las comprobaciones |
| Desafío/OTP | Luis | Panel de gestión y continuación MFA |
| jti/familia, locks y mapping DAT-01 | Jose Luis con consumidores | Cambio de contraseña y transacciones de sesión |
| RS256/JWKS/issuer | Sergio | Acceso real y comprobación en G5 |
| Auditoría e infraestructura | Christian | Trazabilidad, DB y entrega en nube |
| H-11/INT-05 | Sergio con Michael | Aceptación de memoria, configuración y prueba bilateral |

Los tests de sesión pueden usar dependencias internas simuladas claramente
identificadas. La aceptación de login/mis pedidos requiere proveedores reales.
EP-02 y acuerdos internos están alineados; runtime TEC-04 no está validado
por las pruebas del kit en JDK 23.

## 6. Fases y entregables

| Fase | Entrega de Jose Luis | Verificación |
|---|---|---|
| Diseño | Revisión de interfaces y mapping/familia; base React/runtime | Plan consistente con v1 y dependencias |
| Sesión | Emisión/familia, refresh/logout y relación de sesión actual | PostgreSQL real, rotación/reúso y concurrencia |
| Login | Controlador y pantalla, composición de todas las reglas | Éxito, negativos y desafío sin bypass |
| Conexión | Acceso real entregado a G5 y claves públicas | Login→pedidos en nube, identidad/roles/issuer correctos |
| Evidencia | Casos de sesión y demo conjunta, explicación de IA | Fuente→plan→código→prueba verificable |

## 7. Pruebas y terminado

Cubrir login CLIENTE, contraseña equivocada/inexistente/no disponible con la
misma respuesta; MFA obligatorio sin tokens previos; caducidad real. Comprobar
refresh válido, vencido, repetido y simultáneo; familia independiente permanece
vigente. Logout con acceso vencido o refresco desconocido conserva 204.

Comprobar dos sesiones: cambio de contraseña desde una conserva esa familia
y revoca la otra; cambio de rol revoca ambas. Bloqueo automático mantiene
familias previas y prohíbe login nuevo. Los JWT siguen las reglas de firma,
claims y vigencia, no una lista de respuestas prefabricadas.

Frontend: recarga exige login, solo un refresh en vuelo, 401 semánticos
diferenciados por code, retorno seguro y foco/errores de FRONT-01.
Verificar los casos aplicables del plan G5 real; no presentar mocks como demo.

Terminado H3 exige evidencia API/DB/React real y revisión, además de los casos
de integración. Objetivo de login p95 <800 ms de SPEC-05; no acreditar una
prueba de carga completa por observar una sola petición rápida.

## 8. Riesgos y recuperación

Revocación por reúso se confirma antes del 401, sin rollback accidental.
Si falla crear/persistir familia, no entregar tokens huérfanos. No registrar
secreto de refresco ni contraseña, tampoco en excepciones.

Si caen claves válidas o issuer no coincide, denegar; no aceptar payload sin
verificación. Si se pierde la respuesta de una rotación, no reintentar a ciegas.
La rama/configuración G5 siguen pendientes; continuar pruebas independientes
sin declarar integración concluida.
