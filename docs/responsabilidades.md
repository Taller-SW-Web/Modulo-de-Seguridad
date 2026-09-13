# División de responsabilidades — G7

**Módulo:** Seguridad y autenticación de usuarios (gestor de accesos)
**Curso:** Taller de Construcción de Software Web — UNMSM — Ciclo 2026-II
**Repositorio:** https://github.com/Taller-SW-Web/Modulo-de-Seguridad
**Versión:** 1.0 — Semana 4 (Hito 1)

> Este documento es el entregable del rubro **«Funcionalidades identificadas y
> distribuidas por cada integrante»** del Hito 1 (Componente 2, 25%).

---

## 1. Por qué este módulo se reparte distinto

Este módulo no es un canal de venta: es el **proveedor de identidad** de todo el
marketplace. Según la matriz cruzada del curso, los otros seis módulos dependen
de nosotros y **ninguno puede leer nuestra base de datos**. Eso impone dos
consecuencias sobre el reparto:

1. **El contrato de la API tiene dueño único.** Si cada integrante inventara sus
   propios endpoints, los seis equipos consumidores recibirían un contrato
   inconsistente. Por eso el contrato (SPEC-09) no se reparte: lo lleva el
   Product Owner y todos los demás programan contra él.
2. **Nadie es dueño exclusivo de una capa.** El curso evalúa el desarrollo
   *individual* en cada revisión semanal. Ningún integrante ocupa un rol que sea
   solo de gestión, y todos tienen commits propios en `main`.

---

## 2. Equipo y roles

| # | Integrante | Código | Rol de equipo | Responsabilidad principal |
|---|---|---|---|---|
| 1 | **Sergio Alejandro Osorio Montenegro** | 20130037 | Product Owner y Arquitecto de solución | Arquitectura del módulo, contrato OpenAPI, ADRs, priorización del backlog y coordinación con los otros 6 equipos |
| 2 | **Jose Luis Limachi Sarmiento** | 22200287 | Tech Lead — Backend | Arquitectura interna del servicio, núcleo de autenticación, emisión y rotación de tokens, revisión de código del equipo |
| 3 | **Eva Lucía Moreno Zevallos** | 20200277 | Backend | Dominio de usuario: registro, ciclo de vida de la cuenta, roles, permisos y atributos de perfil |
| 4 | **Juan José Cano Vasquez** | 19200303 | Full Stack | Vertical de credenciales: política de contraseñas, recuperación y cambio (backend + sus pantallas) |
| 5 | **Luis David Morales Brenis** | 23200280 | Full Stack | Vertical de protección: OTP/MFA y bloqueo de cuentas (backend + sus pantallas) |
| 6 | **Valery Cristin Gutierrez Bendezu** | 23200263 | Frontend y Diseño | Sistema de diseño, paleta de colores, wireframes, prototipos en Figma y pantallas públicas de la SPA |
| 7 | **Christian Gabriel Arancivia Salas** | 23200077 | DevOps, QA y Frontend de administración | Contenedores, CI/CD, despliegue en nube, estrategia de pruebas y panel de administración |

### Nota sobre el rol de arquitectura

En la plataforma del curso, Jose Luis figura con el tag *Software Architect*. El
acuerdo del equipo es:

- **Sergio** decide y documenta la **arquitectura de solución**: diagramas de
  contexto y componentes, decisiones de seguridad (RS256 + JWKS), contrato de
  API e integración con los otros seis módulos. Es quien presenta el rubro
  *«Arquitectura del módulo preliminar»*.
- **Jose Luis** es dueño de la **arquitectura de implementación**: estructura de
  paquetes del servicio, capas, configuración de Spring Security, modelo de
  datos físico y estándar de código. Firma como co-autor del documento de
  arquitectura y es el revisor obligatorio de todo PR que toque el núcleo.

Ambos nombres aparecen en `docs/arquitectura/`. Ninguna decisión de arquitectura
entra sin que los dos estén de acuerdo; si no lo están, decide el Product Owner
y se registra el motivo como ADR.

---

## 3. Reparto de las 9 especificaciones (SDD)

Las nueve especificaciones son el **backlog completo del ciclo**. Cada una la
redacta, implementa y prueba su responsable.

| SPEC | Funcionalidad | Responsable backend | Responsable interfaz | Hito objetivo |
|---|---|---|---|---|
| **SPEC-01** | Registro y gestión de usuarios | Eva Lucía | Valery | Hito 3 (Sem. 8) |
| **SPEC-02** | Autenticación usuario/contraseña | Jose Luis | Valery | Hito 3 (Sem. 8) |
| **SPEC-03** | Gestión de credenciales y contraseñas | Juan José | Juan José / Valery | Hito 3 (Sem. 8) |
| **SPEC-04** | Autenticación por OTP y MFA | Luis David | Luis David | Hito 4 (Sem. 11) |
| **SPEC-05** | Gestión de roles y permisos | Eva Lucía | Christian | Hito 4 (Sem. 11) |
| **SPEC-06** | Auditoría y trazabilidad de eventos de seguridad | Christian | Christian | Hito 3 (registro) / Hito 5 (consulta) |
| **SPEC-07** | Bloqueo y desbloqueo de cuentas | Luis David | Christian | Hito 5 (Sem. 14) |
| **SPEC-08** | Gestión de atributos de usuarios | Eva Lucía | Christian | Hito 5 (Sem. 14) |
| **SPEC-09** | API de identidad para los demás módulos | Sergio | — | Hito 1 (contrato) / Hito 4 (implementación) |

### Carga por integrante

| Integrante | SPECs propias | Trabajo transversal permanente |
|---|---|---|
| Sergio | SPEC-09 | Arquitectura, OpenAPI, mock server, ADRs, coordinación intermódulos, aprobación de las 9 specs |
| Jose Luis | SPEC-02 | Revisión de código de todos los PR, estructura del proyecto backend, pruebas del núcleo |
| Eva Lucía | SPEC-01, 05, 08 | Modelo de datos de usuario/rol/permiso y sus migraciones Flyway |
| Juan José | SPEC-03 | Adaptador de correo (outbox) compartido con SPEC-01 y SPEC-04 |
| Luis David | SPEC-04, 07 | Tabla `intento_login`, compartida con SPEC-02 |
| Valery | Interfaz de SPEC-01, 02, 03 | Sistema de diseño, paleta, wireframes, prototipos Figma, 3 propuestas de UX |
| Christian | SPEC-06 + interfaz de SPEC-05, 07, 08 | Docker, Docker Compose, GitHub Actions, despliegue en nube, JMeter, evidencias de prueba |

**Por qué así.** Eva concentra las tres specs del dominio «usuario» porque
comparten tablas (`usuario`, `rol`, `permiso`, `perfil_*`) y partirlas obligaría
a coordinar migraciones entre dos personas. Juan José lleva una sola spec, pero
es la más grande del set —doce requisitos y quince escenarios, el ciclo de vida
completo de una credencial— y además dos pantallas. Luis David recibe una
vertical completa, backend *más* su pantalla, para poder demostrar una
funcionalidad de punta a punta en las revisiones semanales. Jose Luis lleva solo
SPEC-02, pero es la spec más difícil del módulo y además revisa todo lo demás.
Christian pasa de llevar solo interfaz a ser dueño de SPEC-06, que es la spec
que alimenta el panel de administración que ya tenía asignado.

### Dos cambios sobre el reparto inicial (13 de septiembre)

**La política de contraseñas dejó de ser una spec suelta.** El profesor indicó
que no puede sostenerse por sí sola y debe ir dentro de otra. Se fusionó con la
antigua SPEC-06 —recuperación y cambio— en la actual **SPEC-03, Gestión de
credenciales y contraseñas**, que cubre el ciclo de vida completo: política de
robustez, historial, caducidad, recuperación por correo y cambio autenticado.
La fusión evita además que la regla de complejidad se duplicara en los tres
flujos que la invocan (registro, cambio y restablecimiento).

**La auditoría pasó a tener dueño.** Aparecía seis veces como frase suelta en
los no funcionales de SPEC-01, SPEC-05, SPEC-07 y SPEC-08 —«queda registrada en
`auditoria_seguridad`»— sin que ninguna spec dijera qué se registra, quién puede
consultarlo ni cómo se exporta. Dos requisitos ya escritos dependían de esa
tabla (RF-09.14 y RF-09.12) y la pantalla 8 de los wireframes la dibujaba sin
respaldo. Ocupa el número **06**, que liberó la fusión anterior.

El total sigue siendo nueve specs y SPEC-09 no se tocó: está publicada y los
seis equipos consumidores programan contra ella.

---

## 4. Quién entrega qué en el Hito 1 (Semana 4)

El rubro del Hito 1 vale 25% del Componente 2 y tiene cuatro líneas.

| Rubro del profesor | Responsable | Entregable concreto | Ubicación |
|---|---|---|---|
| Arquitectura del módulo preliminar | **Sergio** (con Jose Luis) | Diagramas de contexto, componentes y secuencia + ADRs | `docs/arquitectura/` |
| Funcionalidades identificadas y distribuidas por cada integrante | **Sergio** | Este documento + tablero en GitHub Projects | `docs/responsabilidades.md` |
| Especificaciones de requerimientos en detalle — SDD | **Cada responsable escribe la suya** | 9 archivos con las 7 secciones SDD | `specs/SPEC-0X-*.md` |
| Diseño de wireframes | **Valery** | Wireframes de 8 pantallas en Figma + export PDF | `docs/wireframes/` + enlace Figma |

> **Regla de evidencia individual.** Cada integrante hace *commit de su propia
> especificación* con su propia cuenta de GitHub. El profesor evalúa desarrollo
> individual y en el Hito 2 pide explícitamente «evidencia de uso de todos los
> integrantes» en el repositorio. Un repo donde solo commitea el líder pierde
> ese punto para los siete.

---

## 5. Cómo trabajamos

### 5.1 Flujo SDD por funcionalidad

Ninguna línea de código se escribe antes de que su especificación esté aprobada.

| Paso | Actividad | Responsable |
|---|---|---|
| 1 | Redactar la spec con las 7 secciones obligatorias | Responsable de la funcionalidad |
| 2 | Revisar que no contradiga el contrato publicado, y aprobar | Product Owner |
| 3 | Traducir los escenarios *Dado/Cuando/Entonces* a pruebas automatizadas | Responsable |
| 4 | Implementar, usando la spec como entrada de la herramienta de IA | Responsable |
| 5 | Revisar el PR: cada requisito debe tener su prueba | Otro integrante (el núcleo lo revisa Jose Luis) |
| 6 | Verificar la lista de completitud y cerrar | Product Owner |

### 5.2 Las 7 secciones obligatorias de una spec

| Sección | Pregunta que responde |
|---|---|
| Contexto | ¿Por qué existe esta capacidad? |
| Propósito | ¿Para qué sirve y a quién? |
| Alcance | ¿Hasta dónde llega esta unidad de trabajo? |
| Requisitos | ¿Qué debe hacer? (numerados y verificables) |
| Escenarios | ¿Cómo lo verificamos? (mínimo 2 por requisito clave, uno de ellos caso borde) |
| No funcionales | ¿Con qué condiciones de rendimiento, seguridad y límites? |
| Fuera de alcance | ¿Qué NO hará, para no dejarlo a interpretación? |

Una spec a la que le falte una sección **no se aprueba**. Plantilla en
[`specs/_PLANTILLA.md`](../specs/_PLANTILLA.md).

### 5.3 Ramas y revisión

- `main` protegida: nadie commitea directo.
- Una rama por spec: `spec-01-registro`, `spec-02-login`, …
- Todo PR necesita **una aprobación** de otro integrante. Los que tocan
  autenticación, tokens o permisos necesitan la de Jose Luis.
- El PR enlaza la spec que implementa y marca qué requisitos cubre.

### 5.4 Ceremonias

| Actividad | Frecuencia | Propósito |
|---|---|---|
| Planificación | Lunes | Repartir la semana y decidir qué se demuestra el sábado |
| Sincronización rápida | Miércoles y viernes, 15 min | Detectar bloqueos, no informar avances |
| Revisión con el profesor | Sábado | Validar avance; se evalúa desarrollo individual |
| Sincronización entre módulos | Quincenal | Acordar cambios del contrato con los otros 6 equipos |
| Retrospectiva | Después de cada hito | Ajustar antes del siguiente tramo |

---

## 6. Compromisos con los otros seis equipos

Somos el único módulo del que dependen los seis restantes. Tres compromisos:

1. **Contrato disponible en Semana 4.** OpenAPI publicado más un mock server con
   credenciales de prueba conocidas, para que programen contra nosotros antes de
   que exista la implementación.
2. **Contrato estable.** Ningún cambio incompatible sin aviso de una semana y
   sin convivencia entre versiones. Todo bajo `/api/v1`.
3. **Entorno disponible desde el Hito 3.** El servicio desplegado se mantiene
   accesible de forma continua, no solo durante las demostraciones.

Responsable único de los tres: **Sergio (PO)**.

---

## 7. Riesgos del reparto y cómo los cubrimos

| Riesgo | Mitigación | Dueño |
|---|---|---|
| Un integrante concentra conocimiento crítico | Toda funcionalidad está documentada en su spec; revisión cruzada obligatoria de PR | Sergio |
| Desbalance de carga (el curso evalúa individual) | Revisión del tablero cada lunes; redistribución explícita si alguien acumula retraso | Sergio |
| Los seis equipos quedan bloqueados esperando la API | Contrato + mock en Semana 4, antes de cualquier implementación | Sergio |
| Curva de Spring Security y JWT | Prueba de concepto de login con JWT sin BD; programación en parejas en el núcleo | Jose Luis |
| Pruebas concentradas en Semana 14 | Cada escenario de spec se convierte en prueba al implementar, no después | Christian |
| Frontend bloqueado esperando al backend | La SPA se desarrolla contra el mock del OpenAPI desde Semana 4 | Valery |
