# Módulo de Seguridad y Autenticación de Usuarios

**Gestor de accesos del Marketplace Multicanal de Productos Deportivos**
Grupo 7 — Taller de Construcción de Software Web — UNMSM — Ciclo 2026-II

## Integrantes y roles

| Integrante | Código | Rol |
|---|---|---|
| Sergio Alejandro Osorio Montenegro | 20130037 | PO; roles/permisos e integración técnica |
| Jose Luis Limachi Sarmiento | 22200287 | Tech Lead; autenticación y sesiones |
| Eva Lucía Moreno Zevallos | 20200277 | Registro y gestión de usuarios |
| Juan José Cano Vasquez | 19200303 | Gestión de credenciales |
| Luis David Morales Brenis | 23200280 | OTP/MFA y bloqueo/desbloqueo |
| Valery Cristin Gutierrez Bendezu | 23200263 | Atributos/perfil; revisión UX |
| Christian Gabriel Arancivia Salas | 23200077 | Auditoría; DevOps y QA |

[Ocho épicas completas y specs](specs/epicas/README.md). Quién lleva cada una: [`docs/responsabilidades.md`](docs/responsabilidades.md).

---

Este módulo es el **proveedor de identidad** del marketplace. Es dueño de la
entidad usuario —y de sus seis roles: cliente, vendedor y cuatro de gestión— y
los otros seis módulos
dependen de él para autenticar, validar tokens y consultar usuarios.

> **Regla de integración del curso:** ningún módulo accede a la base de datos de
> otro. Toda relación con este módulo pasa por su API.

---

## Cómo encaja en el marketplace

Los diagramas se construyen solos al cargar la página y se quedan quietos para
que puedas leerlos. Se adaptan al tema claro u oscuro de GitHub.

![Diagrama de contexto: el módulo de seguridad y los seis módulos que lo consumen](docs/arquitectura/contexto.svg)

Somos el proveedor de identidad. Los otros seis módulos se relacionan con
nosotros por tres vías: **verificando el token en local** con la clave pública
del JWKS —el caso normal, sin llamarnos—, **preguntando por introspección**
antes de operaciones sensibles, y **suscribiéndose a eventos** para enterarse de
bajas y bloqueos sin preguntar.

### Cómo nos llaman los demás módulos

El recorrido completo de las tres vías, con los endpoints reales del contrato:
token de servicio y JWKS al arrancar, validación local en cada petición,
consultas e introspección cuando hacen falta, y eventos por RabbitMQ.

![Secuencia de integración de los demás módulos con Seguridad](docs/arquitectura/comunicacion-modulos.svg)

### Qué hay dentro

Seis bloques de dominio, cada uno dueño de sus tablas y de un grupo de specs afines.

![Estructura del módulo: API, seis bloques de dominio e infraestructura](docs/arquitectura/estructura-modulo.svg)

La vista por tecnologías y capas técnicas (Spring, JPA, outbox):

![Componentes internos del AUTH-SERVICE](docs/arquitectura/componentes.svg)

### Cómo se inicia sesión

Una contraseña correcta no basta si la cuenta tiene segundo factor —obligatorio
para los cuatro roles de gestión—: el servicio responde con un `challengeToken`,
no con una sesión. La SPA pide el código con `/auth/otp/solicitar` y el par de
tokens se firma solo después de verificarlo.

![Secuencia de inicio de sesión con segundo factor](docs/arquitectura/secuencia-login.svg)

### Cómo se renueva la sesión, y qué pasa si roban un token

El token de refresco se usa **una sola vez**. Si aparece uno ya rotado, o lo
robaron o se duplicó la sesión: no hay forma de saber cuál es el legítimo, así
que se revoca la familia entera de esa sesión. Las demás sesiones del usuario
siguen vivas.

![Secuencia de rotación del token de refresco y detección de reúso](docs/arquitectura/secuencia-refresco.svg)

> **¿Necesitas explorarlos?** Los mismos diagramas en versión interactiva —con
> zoom, búsqueda, recorridos guiados y exportación— están en
> [`docs/arquitectura/`](docs/arquitectura/). Descarga el `.html` y ábrelo: no
> necesita servidor ni conexión.

---

## Estado

**12-oct-2026 — preparación del Hito 3 / semana 8.** Reparto por ocho épicas
y consolidación de fuentes/inconsistencias. Existe una base de persistencia;
este seguimiento no acredita integración funcional desplegada. La aprobación
original de specs no cierra huecos de contrato/interfaz.

Preparación documental publicada por PR #45/#46; referencia de main
`5796622f7c039021e03dd25b2338c6a208320e05`.
[Fuentes primarias de SDD](docs/sdd-fuentes-y-aplicacion.md),
[preparación por integrante](plan/preparacion-por-integrante.md) y
[estado de los 14 issues consultados](docs/integracion/estado-issues-20261012.md).
Los [siete planes H3 por estudiante](plan/hito-3/README.md) están aprobados,
cubriendo las ocho épicas, y sus [tableros de tareas](tareas/hito-3/README.md)
ya están disponibles. [Antes de codificar](plan/hito-3/ANTES-DE-CODIFICAR.md)
distingue diseño cerrado de dependencias/configuración aún por comprobar.

- [Plan actual](plan/README.md) y [tareas H3 por estudiante](tareas/hito-3/README.md).
- [Cobertura histórica por épica](plan/seguimiento-epicas.md): 48 filas generales, no el tablero de ejecución.
- [Revisión pública y fuentes](docs/fuentes-oficiales.md).
- [Registro único de inconsistencias](docs/registro-inconsistencias.md).
- [Preparación de commits y publicación](plan/commits-publicacion.md).

**Hito 3:** recorrido de login real en Seguridad → consulta de mis pedidos en
Ventas confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2).
Michael coordina F1/JWT; G5 entrega como máximo el jueves 15/10 y G7 adopta
ese día como objetivo para probar antes del sábado 17/10. Configuración,
rama y prueba conjunta siguen pendientes. Marketplace y los otros módulos
siguen en el alcance general. [Ficha de coordinación](docs/integracion/hito-3-ventas.md)
y [preparación interna](docs/integracion/preparacion-interna.md).

| Base del Hito 1 | Dónde |
|---|---|
| Arquitectura | [`docs/arquitectura/`](docs/arquitectura/) — diagramas y decisiones ADR |
| Funcionalidades distribuidas | [`docs/responsabilidades.md`](docs/responsabilidades.md) |
| Especificaciones SDD | ✅ 18 specs de backend **aprobadas** el 2-oct — índice en [`specs/trazabilidad.md`](specs/trazabilidad.md) —, 15 de interfaz en [`specs/front/`](specs/front/) y 15 de componentes React en [`specs/componentes-react/`](specs/componentes-react/) |
| Contrato OpenAPI + mock | [`specs/contrato-api/openapi.yaml`](specs/contrato-api/openapi.yaml) — valida sin errores y el mock de Prism responde |
| Acuerdos con los otros módulos | [`docs/integracion/acuerdos.md`](docs/integracion/acuerdos.md) — decisiones y concesiones; comprobar estado |

---

## Por dónde empezar

| Si eres… | Lee esto |
|---|---|
| Integrante del G7 | [Tu plan H3](plan/hito-3/README.md) y [qué falta antes de codificar](plan/hito-3/ANTES-DE-CODIFICAR.md) |
| Integrante nuevo | [`docs/responsabilidades.md`](docs/responsabilidades.md) — quién hace qué y cómo trabajamos |
| **De otro equipo del curso** | [`specs/contrato-api/openapi.yaml`](specs/contrato-api/openapi.yaml) — el contrato; consultar [kit](specs/kit-integracion.md) y [revisión de referencia](docs/fuentes-oficiales.md) |
| Quieres la vista del usuario | [`specs/historias-usuario.md`](specs/historias-usuario.md) — 41 historias, cada una enlazada a sus RF y escenarios |
| Vas a escribir una spec | [`specs/_PLANTILLA.md`](specs/_PLANTILLA.md), y después [`specs/trazabilidad.md`](specs/trazabilidad.md) para ver qué endpoints, eventos y pantallas te tocan |
| Vas a especificar una pantalla | [`specs/front/`](specs/front/) — las specs de interfaz van aparte, con su propia plantilla |
| Vas a programar una pantalla | [`specs/componentes-react/`](specs/componentes-react/) — archivos, componentes y hooks de cada pantalla, y [`docs/diseno/sistema-diseno.md`](docs/diseno/sistema-diseno.md) para el sistema de diseño |
| Vas a devolver un error | [`specs/catalogo-errores.md`](specs/catalogo-errores.md) — los códigos tienen dueño único |
| Vas a publicar un evento | [`specs/catalogo-eventos.md`](specs/catalogo-eventos.md) |
| Vas a cambiar el estado de una cuenta | [`docs/arquitectura/estados-usuario.md`](docs/arquitectura/estados-usuario.md) |

---

## Metodología: SDD

La especificación se escribe y se aprueba **antes** que el código. Una
funcionalidad se considera definida cuando su spec está completa con sus siete
secciones; solo entonces se implementa. Si el código se desvía de la spec, se
corrige el código o se actualiza la spec de forma explícita — nunca a posteriori
y en silencio.

El orden es **specs funcionales/UI/API/React → plan → tareas → código
incremental y verificación**. La estructura y el plan técnico de la demo con
Ventas están preparados; los planes individuales y su desglose de tareas
se escribirán por bloques. [Flujo SDD](plan/flujo-sdd.md),
[plan H3](plan/PLAN-IMPLEMENTACION.md) y [planes por épica](plan/funcionalidades/README.md).

Los escenarios *Dado / Cuando / Entonces* de cada spec son, literalmente, el
plan de pruebas: se traducen a pruebas automatizadas antes de implementar.

---

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Java 21 · Spring Boot 3 · Spring Security |
| Persistencia | Spring Data JPA · PostgreSQL 16 · Flyway |
| Tokens | JWT firmado con RS256 (Nimbus) · JWKS público |
| Frontend | React 18 · Vite · React Router |
| Mensajería | RabbitMQ (eventos `usuario.*`) |
| Contenedores | Docker · Docker Compose |
| Pruebas | JUnit 5 · Mockito · Testcontainers · JMeter |
| CI/CD | GitHub Actions |

---

## Para los otros seis equipos

Tienen tres formas de relacionarse con nosotros. Ninguna incluye tocar nuestra
base de datos.

| Vía | Cuándo usarla | Coste |
|---|---|---|
| **Validación local del token** con la clave pública de `/api/v1/auth/.well-known/jwks.json` | En cada petición ordinaria. Es el caso normal | Ninguna llamada de red. No detecta cambios de estado hasta que el token vence (15 min) |
| **Comprobación remota** | Según el acuerdo: introspección con su scope o consulta de cuenta para Ventas/Postventa (A10) | Una llamada de red; cuenta y sesión son comprobaciones diferentes |
| **Eventos asíncronos** en RabbitMQ | Para enterarse de bajas, bloqueos y cambios de rol sin preguntar | Ninguna, pero es eventualmente consistente |

El token se firma con **RS256 y no con HS256** precisamente por esto: reciben la
clave **pública** y pueden verificar sin poder firmar. Con una clave simétrica
habría que repartir la clave de firma y cualquiera de los seis equipos podría
emitir un token de administrador.

### Programen contra nosotros antes de que existamos

La base contractual se publicó antes que el código. Esta revisión local
actualiza sus rutas y separa v1 de la propuesta parcial v2; consultar
[fuentes oficiales](docs/fuentes-oficiales.md) antes de fijar una revisión.
Para probar los formatos de v1, levanten el mock:

```bash
npx --yes @stoplight/prism-cli@5.16.0 mock specs/contrato-api/openapi.yaml -p 4010 --errors true
curl http://localhost:4010/auth/.well-known/jwks.json
```

Responde con ejemplos ilustrativos del contrato, incluidos los caminos de error, y
valida sus peticiones. Pueden forzar cualquier respuesta con la cabecera
`Prefer` —`Prefer: code=401`— para probar sus caminos de fallo.

> **Ojo con el prefijo.** Prism sirve las rutas sin `/api/v1`; el backend real
> sí lo lleva. Parametricen la URL base y no tocarán código al cambiar.

La guía completa está en [`specs/kit-integracion.md`](specs/kit-integracion.md).

El comando se comprobó con Node 24.19.0. Los ejemplos de JWT/JWKS del mock
son ilustrativos; para verificar firmas se necesitan tokens RS256 firmados
y las claves públicas correspondientes.

## Cierre de planificación H3

Los siete planes del incremento H3 están aprobados según comunicación de Luis,
con [diseño técnico cerrado y paso a tareas](plan/hito-3/CIERRE-PLAN.md).
Cada dueño conserva su recorrido completo; tablas de escritor único y consumo
por interfaces internas. PostgreSQL está en Supabase; acceso/configuración y
servicios reales se comprueban al implementar. Los siete tableros están en
[tareas/hito-3](tareas/hito-3/README.md); ejecutar una tarea lista por vez,
con sus fuentes, dependencias y comprobaciones. No hay demo acreditada.
