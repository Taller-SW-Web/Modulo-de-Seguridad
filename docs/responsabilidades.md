# División de responsabilidades — G7

**Módulo:** Seguridad y autenticación de usuarios (gestor de accesos)
**Curso:** Taller de Construcción de Software Web — UNMSM — Ciclo 2026-II
**Repositorio:** https://github.com/Taller-SW-Web/Modulo-de-Seguridad
**Versión:** 2.0 — Semana 5 (18 specs, tras el feedback del Hito 1)

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
   inconsistente. Por eso el contrato (SPEC-17 y SPEC-18) no se reparte: lo lleva el
   Product Owner y todos los demás programan contra él.
2. **Nadie es dueño exclusivo de una capa.** El curso evalúa el desarrollo
   *individual* en cada revisión semanal. Ningún integrante ocupa un rol que sea
   solo de gestión, y todos deben tener commits propios en `main`.

---

## 2. Equipo y roles

| # | Integrante | Código | Rol de equipo | Responsabilidad principal |
|---|---|---|---|---|
| 1 | **Sergio Alejandro Osorio Montenegro** | 20130037 | Product Owner y Arquitecto de solución | Arquitectura del módulo, contrato OpenAPI, ADRs, priorización del backlog y coordinación con los otros 6 equipos |
| 2 | **Jose Luis Limachi Sarmiento** | 22200287 | Tech Lead — Backend | Arquitectura interna del servicio, núcleo de autenticación, emisión y rotación de tokens, revisión de código del equipo |
| 3 | **Eva Lucía Moreno Zevallos** | 20200277 | Backend | Dominio de usuario: registro, ciclo de vida de la cuenta, roles, permisos y atributos de perfil |
| 4 | **Juan José Cano Vasquez** | 19200303 | Full Stack | Vertical de credenciales: política de contraseñas, recuperación y cambio (backend + sus pantallas) |
| 5 | **Luis David Morales Brenis** | 23200280 | Pasivo / Versatil | Vertical de protección: OTP/MFA y bloqueo de cuentas (backend + sus pantallas) |
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

## 3. Reparto de las 18 especificaciones (SDD)

Las dieciocho especificaciones son el **backlog completo del ciclo**. Cada una
la redacta, implementa y prueba su responsable. Son specs **de backend**: las
pantallas se especifican aparte, en [`specs/front/`](../specs/front/), y las
lleva el responsable de UX (ver §3.2).

| SPEC | Funcionalidad | Responsable | Hito objetivo |
|---|---|---|---|
| **SPEC-01** | Registro de clientes | Eva Lucía | Hito 3 (Sem. 8) |
| **SPEC-02** | Verificación de correo | Juan José | Hito 3 (Sem. 8) |
| **SPEC-03** | Alta y consulta administrativa de cuentas | Christian | Hito 3 (Sem. 8) |
| **SPEC-04** | Baja y reactivación de cuentas | Eva Lucía | Hito 3 (Sem. 8) |
| **SPEC-05** | Inicio de sesión con correo y contraseña | Jose Luis | Hito 3 (Sem. 8) |
| **SPEC-06** | Renovación y cierre de sesión | Jose Luis | Hito 3 (Sem. 8) |
| **SPEC-07** | Política y cambio de contraseña | Juan José | Hito 3 (política) / Hito 4 (cambio) |
| **SPEC-08** | Recuperación de contraseña | Juan José | Hito 4 (Sem. 11) |
| **SPEC-09** | Segundo factor en el inicio de sesión (OTP) | Luis David | Hito 4 (Sem. 11) |
| **SPEC-10** | Activación y desactivación del segundo factor | Luis David | Hito 4 (Sem. 11) |
| **SPEC-11** | Gestión de roles y permisos | Eva Lucía | Hito 4 (Sem. 11) |
| **SPEC-12** | Registro de auditoría | Christian | Hito 3 (Sem. 8) |
| **SPEC-13** | Consulta y exportación de la auditoría | Christian | Hito 5 (Sem. 14) |
| **SPEC-14** | Bloqueo automático por intentos fallidos | Luis David | Hito 5 (Sem. 14) |
| **SPEC-15** | Bloqueo y desbloqueo por un administrador | Luis David | Hito 5 (Sem. 14) |
| **SPEC-16** | Gestión de atributos de usuarios | Eva Lucía | Hito 5 (Sem. 14) |
| **SPEC-17** | Claves públicas, tokens de servicio e introspección | Sergio | Hito 1 (contrato) / Hito 4 (implementación) |
| **SPEC-18** | Consulta de identidad para los demás módulos | Sergio | Hito 1 (contrato) / Hito 4 (implementación) |

### 3.1 Carga por integrante

| Integrante | SPECs propias | Requisitos | Trabajo transversal permanente |
|---|---|---|---|
| Sergio | SPEC-17, 18 | 17 | Arquitectura, OpenAPI, mock server, ADRs, coordinación intermódulos, aprobación de las 18 specs |
| Jose Luis | SPEC-05, 06 | 12 | Revisión de código de todos los PR, estructura del proyecto backend, pruebas del núcleo |
| Eva Lucía | SPEC-01, 04, 11, 16 | 31 | Modelo de datos de usuario/rol/permiso y sus migraciones Flyway |
| Juan José | SPEC-02, 07, 08 | 25 | Adaptador de correo (outbox) que usan también SPEC-09, SPEC-14, SPEC-15 y SPEC-16 |
| Luis David | SPEC-09, 10, 14, 15 | 36 | Tabla `intento_login`, compartida con SPEC-05 |
| Valery | Specs de interfaz (`specs/front/`) | — | Sistema de diseño, paleta, wireframes, prototipos Figma, 3 propuestas de UX |
| Christian | SPEC-03, 12, 13 | 16 | Docker, Docker Compose, GitHub Actions, despliegue en nube, JMeter, evidencias de prueba, pantallas del panel de administración |

**Por qué así.** Al dividir las specs no se movió ninguna función de dueño salvo
dos, y las dos por afinidad con lo que la persona ya construye:

- **La verificación de correo pasa a Juan José**, porque es un enlace de un solo
  uso enviado por correo: el mismo mecanismo y el mismo adaptador (outbox) que ya
  construye para la recuperación de contraseña.
- **El alta y la consulta administrativa de cuentas pasan a Christian**, porque
  son el backend del panel de administración que ya tenía asignado. Así puede
  demostrar el panel de punta a punta.

Eva conserva el resto del dominio «usuario» —registro, baja, roles y atributos—
porque comparten tablas (`usuario`, `rol`, `permiso`, `perfil_*`) y partirlas
obligaría a coordinar migraciones entre dos personas. Luis David conserva sus
dos verticales completas, backend *más* pantalla, ahora en cuatro specs más
pequeñas. Jose Luis lleva solo dos specs, pero son el núcleo del módulo y además
revisa todo lo demás. Christian y Sergio tienen menos requisitos porque cargan
con el trabajo transversal (DevOps y QA, y contrato y coordinación).

### 3.2 Specs de backend y specs de interfaz

El profesor pidió separar las especificaciones de interfaz. Desde el 20 de
septiembre:

- Las **18 specs de `specs/`** describen qué hace el servicio: requisitos,
  escenarios verificables por la API, contrato. No describen pantallas.
- Las **specs de interfaz de [`specs/front/`](../specs/front/)** describen cada
  pantalla: estados, validaciones del lado del cliente, mensajes, accesibilidad,
  y qué spec de backend consume. Las lleva Valery como responsable de UX, con su
  propia plantilla. Por ahora solo existe la plantilla: se redactarán cuando el
  equipo lo decida.

### 3.3 Cambios sobre el reparto

**20 de septiembre — de 9 a 18 specs.** Tras la presentación del Hito 1, el
profesor pidió llegar a más de 10 y menos de 20 specs, con una por función: su
ejemplo fue que «gestión de usuarios» agrupaba demasiadas funciones. Cada spec
se partió por sus endpoints, que ya iban separados, sin cambiar ninguna regla;
dos quedaron enteras porque ya eran una sola función (roles y atributos). El
contrato publicado no cambió. La equivalencia completa entre números antiguos y
nuevos está en [`specs/trazabilidad.md`](../specs/trazabilidad.md) §1.1 y §8.

**13 de septiembre — dos cambios sobre el reparto inicial** *(con la numeración
antigua de 9 specs)*.

*La política de contraseñas dejó de ser una spec suelta.* El profesor indicó
que no puede sostenerse por sí sola y debe ir dentro de otra. Se fusionó con la
entonces SPEC-06 —recuperación y cambio— en la SPEC-03 antigua, «Gestión de
credenciales y contraseñas». Al dividir el 20 de septiembre se mantuvo esa
decisión: la política vive hoy en SPEC-07, junto al cambio de contraseña.

*La auditoría pasó a tener dueño.* Aparecía seis veces como frase suelta en los
no funcionales de cuatro specs —«queda registrada en `auditoria_seguridad`»— sin
que ninguna dijera qué se registra, quién puede consultarlo ni cómo se exporta.
Ocupó el número 06 antiguo, que liberó la fusión anterior. Hoy son SPEC-12
(registro) y SPEC-13 (consulta y exportación).

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
- Una rama por spec: `spec-01-registro-clientes`, `spec-05-inicio-sesion`, …
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
