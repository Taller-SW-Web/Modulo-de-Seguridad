# Módulo de Seguridad y Autenticación de Usuarios

**Gestor de accesos del Marketplace Multicanal de Productos Deportivos**
Grupo 7 — Taller de Construcción de Software Web — UNMSM — Ciclo 2026-II

Este módulo es el **proveedor de identidad** del marketplace. Es dueño de la
entidad usuario (cliente, vendedor, administrador) y los otros seis módulos
dependen de él para autenticar, validar tokens y consultar usuarios.

> **Regla de integración del curso:** ningún módulo accede a la base de datos de
> otro. Toda relación con este módulo pasa por su API.

---

## Estado

**Semana 4 — Hito 1.** Fase de especificación. Todavía no hay código de
producción: primero el contrato y las specs, después la implementación.

| Entregable del Hito 1 | Estado |
|---|---|
| Arquitectura preliminar | En curso |
| Funcionalidades distribuidas | ✅ [`docs/responsabilidades.md`](docs/responsabilidades.md) |
| 9 especificaciones SDD | En curso |
| Wireframes | En curso |
| Contrato OpenAPI + mock | En curso |

---

## Por dónde empezar

| Si eres… | Lee esto |
|---|---|
| Integrante del G7 | [`docs/plan-hito-1.md`](docs/plan-hito-1.md) — tu tarea concreta de esta semana |
| Integrante nuevo | [`docs/responsabilidades.md`](docs/responsabilidades.md) — quién hace qué y cómo trabajamos |
| **De otro equipo del curso** | [`specs/openapi.yaml`](specs/openapi.yaml) — el contrato. No necesitas nada más para empezar a programar contra nosotros |
| Vas a escribir una spec | [`specs/_PLANTILLA.md`](specs/_PLANTILLA.md) |

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
| **Validación local del token** con la clave pública de `/.well-known/jwks.json` | En cada petición ordinaria. Es el caso normal | Ninguna llamada de red. No detecta cambios de estado hasta que el token vence (15 min) |
| **Introspección remota** `POST /api/v1/auth/introspeccion` | Antes de operaciones sensibles: anulaciones, reembolsos, cambios de precio | Una llamada de red y dependencia de nuestra disponibilidad |
| **Eventos asíncronos** en RabbitMQ | Para enteraros de bajas, bloqueos y cambios de rol sin preguntar | Ninguna, pero es eventualmente consistente |

El token se firma con **RS256 y no con HS256** precisamente por esto: recibís la
clave **pública** y podéis verificar sin poder firmar. Con una clave simétrica
habría que repartir la clave de firma y cualquiera de los seis equipos podría
emitir un token de administrador.

### Programad contra nosotros antes de que existamos

El contrato se congela antes que el código. Cuando esté publicado:

```bash
npx @stoplight/prism-cli mock specs/openapi.yaml -p 4010
curl http://localhost:4010/.well-known/jwks.json
```

Responde con los ejemplos reales del contrato, incluidos los caminos de error.
Cuando la implementación esté lista, solo cambiáis la URL base.

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
