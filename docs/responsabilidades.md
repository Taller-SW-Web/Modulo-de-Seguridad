# Responsabilidades por épica — G7

**Actualización:** 11 de octubre de 2026 · reparto acordado para preparar Hito 3.
**Estado:** documentado en la rama de trabajo; pendiente de publicación. No
acredita implementación, pruebas ni aceptación de otros equipos.

## Ocho funcionalidades, siete estudiantes

Una **épica** es una funcionalidad de usuario completa. Una **SPEC** describe
una parte verificable. Dividir las specs no divide la responsabilidad entre
estudiantes o capas.

| Épica | Funcionalidad | Dueño de punta a punta | Código | Specs |
|---|---|---|---|---|
| [EP-01](../specs/epicas/EP-01-registro-gestion-usuarios.md) | Registro y gestión de usuarios | Eva Lucía Moreno Zevallos | 20200277 | 01, 02, 03, 04 |
| [EP-02](../specs/epicas/EP-02-autenticacion-sesiones.md) | Autenticación y sesiones | Jose Luis Limachi Sarmiento | 22200287 | 05, 06 |
| [EP-03](../specs/epicas/EP-03-credenciales.md) | Gestión de credenciales | Juan José Cano Vasquez | 19200303 | 07, 08 |
| [EP-04](../specs/epicas/EP-04-segundo-factor.md) | OTP y segundo factor | Luis David Morales Brenis | 23200280 | 09, 10 |
| [EP-05](../specs/epicas/EP-05-roles-permisos.md) | Roles y permisos | Sergio Alejandro Osorio Montenegro | 20130037 | 11 |
| [EP-06](../specs/epicas/EP-06-auditoria.md) | Auditoría de seguridad | Christian Gabriel Arancivia Salas | 23200077 | 12, 13 |
| [EP-07](../specs/epicas/EP-07-bloqueo-desbloqueo.md) | Bloqueo y desbloqueo | Luis David Morales Brenis | 23200280 | 14, 15 |
| [EP-08](../specs/epicas/EP-08-atributos-perfil.md) | Atributos y perfil | Valery Cristin Gutierrez Bendezu | 23200263 | 16 |

Luis David tiene dos épicas; los otros seis estudiantes tienen una cada uno.
No hay una novena épica académica por el contrato.

## Responsabilidad completa e interfaces

Cada dueño especifica, implementa y demuestra su interfaz, backend,
persistencia, validaciones, integración con dependencias, eventos y pruebas.
Las tareas de frontend, backend y datos mantienen el mismo responsable. Un
revisor comprueba el trabajo sin convertirse en dueño de una capa.

Las dependencias se consumen mediante interfaces del dueño correspondiente:
EP-02 usa protección de EP-07 y desafíos de EP-04; las épicas registran auditoría
mediante EP-06. No se duplican sus reglas. Los adaptadores existentes de correo
y outbox se reutilizan; el dueño conserva su flujo de negocio completo.

### Pantallas con varias funcionalidades

Eva mantiene el contenedor de cuentas de FRONT-07 y FRONT-08. Los bloques de
roles pertenecen a Sergio; bloqueo/desbloqueo, a Luis David; atributos, a Valery;
historial, a Christian. Cada bloque incluye su componente interactivo, API,
persistencia y pruebas, todos a cargo del mismo dueño. Componer bloques
completos en una pantalla no reparte ninguna épica por capas.

FRONT-00 no es otra épica: Jose Luis mantiene sesión y navegación autenticada;
Juan José, política/medidor de contraseña; Sergio, controles de permisos;
Valery, el sistema visual. El [índice de interfaz](../specs/front/README.md)
documenta dueños de pantalla y bloques integrados.

## Responsabilidades técnicas adicionales

| Integrante | Responsabilidad adicional; no sustituye su épica |
|---|---|
| Sergio | Product Owner, arquitectura de solución, edición exclusiva del contrato y capacidades de SPEC-17/18 |
| Jose Luis | Arquitectura de implementación y revisión del núcleo |
| Eva | Coherencia del dominio de cuentas y coordinación de la entidad usuario |
| Juan José | Mecanismos de credenciales y revisión del adaptador de correo existente |
| Luis David | Coherencia de desafíos, intentos fallidos y bloqueos con el login |
| Valery | Sistema de diseño y revisión visual; no implementa todas las pantallas ajenas |
| Christian | Infraestructura, despliegue y estrategia de comprobación; cada dueño implementa y prueba su épica |

SPEC-17/18 también necesitan código: descubrimiento, JWKS, tokens de servicio,
introspección y consultas. Sergio mantiene esa integración técnica. No añade
una épica académica ni incorpora reglas de negocio de los módulos consumidores.

## Cambio de reparto

| Spec | Dueño anterior | Dueño vigente |
|---|---|---|
| SPEC-02 | Juan José | Eva Lucía |
| SPEC-03 | Christian | Eva Lucía |
| SPEC-11 | Eva Lucía | Sergio |
| SPEC-16 | Eva Lucía | Valery |

Las aprobaciones funcionales del 2 de octubre se conservan como antecedentes.
La reasignación no modifica requisitos ni declara cerrados huecos contractuales;
tampoco atribuye al nuevo dueño la autoría histórica. El
[plan de Hito 1](plan-hito-1.md) conserva su reparto histórico.

## Primera versión y seguimiento

Las fichas de épica contienen primera versión, dependencias y escenarios de
demostración. La meta común es Hito 3 / semana 8. Los hitos originales de las
specs describen el alcance completo del ciclo y no sustituyen la primera
versión individual. Ningún documento nuevo declara software terminado.

- [Catálogo de épicas](../specs/epicas/README.md).
- [Plan H3 de cada estudiante](../plan/hito-3/README.md) y
  [condiciones antes de codificar](../plan/hito-3/ANTES-DE-CODIFICAR.md).
- [Plan vigente](../plan/README.md) y [tareas](../plan/seguimiento-epicas.md).
- [Fuentes y revisiones](fuentes-oficiales.md).
- [Registro vigente de inconsistencias](registro-inconsistencias.md).

## Flujo de trabajo

1. El dueño completa sus specs con siete secciones y resuelve bloqueos. Si
   afecta al contrato, prepara la propuesta para Sergio.
2. El PO revisa coherencia y aprueba cambios funcionales o contractuales.
3. El dueño escribe su plan a partir de las specs funcionales/UI/API/React,
   define dependencias y criterios, y lo revisa antes de derivar tareas.
   [Flujo y plantillas SDD](../plan/flujo-sdd.md).
4. El mismo dueño implementa una tarea y comprueba sus criterios antes de
   avanzar a la siguiente dependiente; mantiene todas las capas de su épica.
5. Otro integrante revisa el PR; Jose Luis revisa autenticación, tokens y
   permisos. Revisar no reasigna implementación.
6. Se registra evidencia de escenarios, persistencia e integración real.

Se mantienen revisión cruzada y evidencia individual del curso. Un mock o un
diseño no acredita una funcionalidad implementada. Esta preparación no crea
commits, PRs ni issues. Los cambios incompatibles requieren versionado y la
publicación respeta ADR-003.
