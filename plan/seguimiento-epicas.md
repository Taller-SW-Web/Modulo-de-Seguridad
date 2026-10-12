# Seguimiento de implementación por épica

**Preparado:** 11-oct-2026. Revisión documental por épica realizada; filas de implementación no acreditan ejecución. Mismo dueño en todas las capas. RF/ESC se toman de las specs de cada épica; las pruebas/demos se registrarán al ejecutarse.

**Estados:** Pendiente → En curso → En revisión → Demostrada. Bloqueada exige indicar decisión/dependencia; un mock no la cierra.

## EP-01

[Registro y gestión de usuarios](../specs/epicas/EP-01-registro-gestion-usuarios.md). **Dueño de las seis tareas:** Eva Lucía Moreno Zevallos.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-01-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-01-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-01-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-01-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-01-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-01-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** UI-01/H-10; **pendiente:** H-12, activación A6, publicación, INT-05. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-02

[Autenticación y gestión de sesiones](../specs/epicas/EP-02-autenticacion-sesiones.md). **Dueño de las seis tareas:** Jose Luis Limachi Sarmiento.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-02-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-02-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-02-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-02-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-02-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-02-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** KIT-01/02/03, JWT-01/H-11; **comprobado:** fixtures firmadas del kit; **pendiente:** servidor, vínculo DAT-01 y configuración/prueba INT-05. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-03

[Gestión de credenciales](../specs/epicas/EP-03-credenciales.md). **Dueño de las seis tareas:** Juan José Cano Vasquez.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-03-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-03-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-03-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-03-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-03-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-03-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** H-15; **pendiente:** implementar y verificar. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-04

[Segundo factor: OTP y MFA](../specs/epicas/EP-04-segundo-factor.md). **Dueño de las seis tareas:** Luis David Morales Brenis.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-04-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-04-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-04-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-04-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-04-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-04-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** H-01/02/03/05/06; **pendiente:** DAT-01, wireframe, servicios/publicación. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-05

[Roles y permisos](../specs/epicas/EP-05-roles-permisos.md). **Dueño de las seis tareas:** Sergio Alejandro Osorio Montenegro.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-05-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-05-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-05-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-05-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-05-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-05-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** H-09/H-10; **pendiente:** implementación/publicación. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-06

[Auditoría de seguridad](../specs/epicas/EP-06-auditoria.md). **Dueño de las seis tareas:** Christian Gabriel Arancivia Salas.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-06-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-06-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-06-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-06-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-06-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-06-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Decisiones pendientes:** H-07, H-08, EVT-01. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-07

[Bloqueo y desbloqueo de cuentas](../specs/epicas/EP-07-bloqueo-desbloqueo.md). **Dueño de las seis tareas:** Luis David Morales Brenis.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-07-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-07-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-07-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-07-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-07-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-07-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** H-07; **pendiente:** H-14 soporte. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## EP-08

[Atributos y perfil de usuario](../specs/epicas/EP-08-atributos-perfil.md). **Dueño de las seis tareas:** Valery Cristin Gutierrez Bendezu.

| Tarea | Trabajo | Estado | Criterio de aceptación |
|---|---|---|---|
| EP-08-01 · Definición | Revisar RF/ESC y cerrar bloqueos de la primera versión. | Pendiente | Decisión y checklist de RF/ESC seleccionados. |
| EP-08-02 · Interfaz | Implementar pantallas/bloques con estados y accesibilidad. | Pendiente | Pantalla conectada y estados verificables. |
| EP-08-03 · Backend | Implementar operaciones y validaciones de la épica. | Pendiente | Respuestas/denegaciones conformes al contrato aprobado. |
| EP-08-04 · Persistencia | Implementar repositorios y transiciones de dominio. | Pendiente | Estado e invariantes verificadas sin editar migraciones aplicadas. |
| EP-08-05 · Dependencias | Consumir capacidades y emitir auditoría/eventos aplicables. | Pendiente | Resultado trazable entre capacidades y fallos controlados. |
| EP-08-06 · Pruebas y demo | Verificar RF/ESC elegidos y demostrar punta a punta. | Pendiente | Evidencia reproducible, revisión y pendientes del alcance completo. |

**Versión/dependencias:** ficha de épica. **Definido local:** H-01/H-03/H-13; **aplazado:** H-04 edición/borrado; **pendiente:** publicación/servicios. **Evidencia:** pendiente; adjuntar PR, pruebas y demo cuando existan. No se asigna frontend/backend a personas diferentes.

## Integración técnica adicional

Sergio mantiene SPEC-17/SPEC-18 y prepara descubrimiento/JWKS y validación según necesidades del recorrido. Christian mantiene infraestructura de comprobación. La colaboración no sustituye el trabajo vertical ni añade una funcionalidad académica.

## Condición del recorrido con Ventas

Antes de programar una parte deben definirse sus interfaces, RF/ESC y bloqueos.
El recorrido y Michael ya están confirmados en G5 Backend #2; antes de la
prueba conjunta deben completarse los pendientes de INT-05, revisión consumida
y configuración de entornos. El acuerdo bilateral pertenece al punto 7.
Una jornada es presupuesto de preparación, no permiso para saltar decisiones del PO.


**Actualización documental 11-oct:** los tratamientos H-01–H-15 y KIT/JWT se
consultan en el registro vigente. Estas 48 tareas siguen pendientes de
implementación/evidencia; una propuesta escrita no las completa. Para la demo
mínima, Jose Luis entrega login/sesión, Sergio identidad/JWKS, Eva las cuentas
de prueba y Christian infraestructura; cada uno conserva su épica completa.
