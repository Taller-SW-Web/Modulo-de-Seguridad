# Módulo de Seguridad y Autenticación de Usuarios

**Gestor de accesos del Marketplace Multicanal de Productos Deportivos**
Grupo 7 — Taller de Construcción de Software Web — UNMSM — Ciclo 2026-II

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

Seis bloques de dominio, uno por grupo de specs, cada uno dueño de sus tablas.

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

**Semana 4 — Hito 1.** Fase de especificación. Todavía no hay código de
producción: primero el contrato y las specs, después la implementación.

| Entregable del Hito 1 | Estado |
|---|---|
| Arquitectura preliminar | ✅ [`docs/arquitectura/`](docs/arquitectura/) — 6 diagramas y 4 ADR |
| Funcionalidades distribuidas | ✅ [`docs/responsabilidades.md`](docs/responsabilidades.md) |
| 9 especificaciones SDD | 🔶 9 de 9 en `main`, en borrador y pendientes de aprobación — índice en [`specs/trazabilidad.md`](specs/trazabilidad.md) |
| Wireframes | 🔶 primera versión en Stitch (47 pantallas, SPEC-01 a SPEC-08); falta exportarla a `docs/wireframes/` y pasarla a Figma |
| Contrato OpenAPI + mock | 🔶 [`specs/openapi.yaml`](specs/openapi.yaml) — 39 operaciones, valida sin errores y el mock de Prism responde |

---

## Por dónde empezar

| Si eres… | Lee esto |
|---|---|
| Integrante del G7 | [`docs/plan-hito-1.md`](docs/plan-hito-1.md) — tu tarea concreta de esta semana |
| Integrante nuevo | [`docs/responsabilidades.md`](docs/responsabilidades.md) — quién hace qué y cómo trabajamos |
| **De otro equipo del curso** | [`specs/openapi.yaml`](specs/openapi.yaml) — el contrato. No necesitas nada más para empezar a programar contra nosotros |
| Vas a escribir una spec | [`specs/_PLANTILLA.md`](specs/_PLANTILLA.md), y después [`specs/trazabilidad.md`](specs/trazabilidad.md) para ver qué endpoints, eventos y pantallas te tocan |
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

Tenéis tres formas de relacionaros con nosotros. Ninguna incluye tocar nuestra
base de datos.

| Vía | Cuándo usarla | Coste |
|---|---|---|
| **Validación local del token** con la clave pública de `/api/v1/auth/.well-known/jwks.json` | En cada petición ordinaria. Es el caso normal | Ninguna llamada de red. No detecta cambios de estado hasta que el token vence (15 min) |
| **Introspección remota** `POST /api/v1/auth/introspeccion` | Antes de operaciones sensibles: anulaciones, reembolsos, cambios de precio | Una llamada de red y dependencia de nuestra disponibilidad |
| **Eventos asíncronos** en RabbitMQ | Para enteraros de bajas, bloqueos y cambios de rol sin preguntar | Ninguna, pero es eventualmente consistente |

El token se firma con **RS256 y no con HS256** precisamente por esto: recibís la
clave **pública** y podéis verificar sin poder firmar. Con una clave simétrica
habría que repartir la clave de firma y cualquiera de los seis equipos podría
emitir un token de administrador.

### Programad contra nosotros antes de que existamos

El contrato está publicado antes que el código. Levantad el mock:

```bash
npx @stoplight/prism-cli mock specs/openapi.yaml -p 4010
curl http://localhost:4010/auth/.well-known/jwks.json
```

Responde con los ejemplos reales del contrato, incluidos los caminos de error, y
valida vuestras peticiones. Podéis forzar cualquier respuesta con la cabecera
`Prefer` —`Prefer: code=401`— para probar vuestros caminos de fallo.

> **Ojo con el prefijo.** Prism sirve las rutas sin `/api/v1`; el backend real
> sí lo lleva. Parametrizad la URL base y no tocaréis código al cambiar.

La guía completa está en [`specs/kit-integracion.md`](specs/kit-integracion.md).

---

## Equipo

| Integrante | Rol |
|---|---|
| Sergio Alejandro Osorio Montenegro | Product Owner y Arquitecto de solución |
| Jose Luis Limachi Sarmiento | Tech Lead — Backend |
| Eva Lucía Moreno Zevallos | Backend |
| Juan José Cano Vasquez | Full Stack |
| Luis David Morales Brenis | Full Stack |
| Valery Cristin Gutierrez Bendezu | Frontend y Diseño |
| Christian Gabriel Arancivia Salas | DevOps, QA y Frontend de administración |

Detalle del reparto en [`docs/responsabilidades.md`](docs/responsabilidades.md).
