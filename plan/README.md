# Plan de preparación e implementación — G7

**Corte inicial:** 11 de octubre; actualización 12-oct-2026. Este plan no acredita
código, despliegue ni integración terminados. La preparación documental se publicó mediante PR #45/#46;
main comprobado: 5796622f7c039021e03dd25b2338c6a208320e05.

## Repositorios de ejecución

[Backend hexagonal H3](hito-3/BACKEND-HEXAGONAL.md) gobierna el backend nuevo
independiente y las rutas de los siete tableros. Boot 3.5.16, Gradle 8.14.3;
la base Maven histórica queda descartada. Frontend en un repo separado cuya
URL aún debe comunicarse. Abrir los clones de código y documentación a la IA.

## Lectura

1. [Épicas](../specs/epicas/README.md) y [responsabilidades](../docs/responsabilidades.md).
2. [Fuentes y revisiones](../docs/fuentes-oficiales.md).
3. [Registro vigente](../docs/registro-inconsistencias.md).
4. [Tareas H3 por estudiante](../tareas/hito-3/README.md) y [cobertura histórica](seguimiento-epicas.md).
5. [Commits y publicación de esta preparación](commits-publicacion.md).
6. [Flujo SDD](flujo-sdd.md), [plan técnico H3](PLAN-IMPLEMENTACION.md)
   y [condiciones de inicio](DECISIONES-Y-BLOQUEOS.md).
7. [Hito 3 por estudiante](hito-3/README.md) y [antes de codificar](hito-3/ANTES-DE-CODIFICAR.md).
8. [Hitos y responsabilidad individual](../docs/hitos-y-alcance-individual.md):
   fuentes de evaluación y confirmación de avance por incrementos.

Specs en `specs/`; planes en `plan/` y tableros de ejecución en `tareas/hito-3/`. Una tarea de frontend
o backend no es una funcionalidad nueva ni cambia el dueño de la épica.

## Etapa SDD siguiente — dos puntos por bloque

El bloque del 12-oct prepara **SDD-1**, estructura y plantillas de planes/tareas,
y **SDD-2**, plan técnico de login/mis pedidos con Ventas. No reemplaza los
nueve puntos de preparación ni acredita su cierre completo.

La continuación del 12-oct prepara [siete planes H3 por estudiante](hito-3/README.md),
cubriendo ocho épicas y enlazando TEC-IDENTIDAD/TEC-INFRA. Luis confirma que H3
es avance por incrementos; todos deben tener evidencia propia. Los siete
planes H3 tienen aprobación formal comunicada por Luis y diseño interno cerrado
por delegación. [Cierre y paso a tareas](hito-3/CIERRE-PLAN.md); no acredita disponibilidad. Las
[tareas derivadas](../tareas/hito-3/README.md) ya están disponibles para los siete estudiantes. Las ocho épicas conservan dueño y alcance, sin programar
todas de una vez. Las 48 filas previas son cobertura general, no tareas pequeñas
listas para ejecutar. [Reglas y plantilla](flujo-sdd.md).

## Preparación por bloques

| Punto | Resultado | Estado de este bloque |
|---|---|---|
| 1 | Ocho épicas, dueños y trazabilidad completa | Publicado por PR #45/#46; aprobación de los siete incrementos H3 comunicada por Luis |
| 2 | Referencia comprobada, enlaces reparados e inventario | Documentado; revisión por épica y revisiones locales v1/v2 enlazadas |
| 3 | Resolver MFA: H-01/H-02/H-05/H-06 | Definición publicada, FRONT-09 especificada; DAT-01 y servidor pendientes |
| 4 | Corregir kit, issuer, refresco y sesiones | Kit publicado; validación previa de ejemplos y 88 comprobaciones JWT; servidor pendiente |
| 5 | Resolver o aplazar otros huecos | Tratamientos documentados; pendientes explícitos de contrato, activación, términos y soporte |
| 6 | Ordenar scopes, A9/A10 y límites | Matriz y fuentes G2/G6 contrastadas; A10 confirmado; A9/suscripciones/provisión pendientes |
| 7 | Acuerdo técnico con Ventas para Hito 3 | Recorrido, Michael y entrega G5 jueves 15 confirmados en #2; G7 adopta objetivo 15; rama, URLs, hora y H-11 pendientes, INT-05 |
| 8 | Comunicaciones a los seis equipos | Fuentes contrastadas y seis mensajes preparados; envío/respuestas pendientes |
| 9 | Validación contractual/criptográfica y publicación | Validación previa pasó: 178 ejemplos, 68 peticiones mock y 88 comprobaciones JWT; base publicada por PR #45/#46; complemento local y prueba en nube pendientes |

Presupuesto inicial: una jornada. Agotarlo no autoriza a inventar confirmaciones
ni a implementar un subflujo cuyo contrato sigue bloqueado. La preparación
interna de los puntos 3 a 6 está documentada; el recorrido de Ventas del punto 7
ya tiene confirmación externa. Continuación documental del 12-oct: puntos 8/9,
[mensajes preparados](../docs/integracion/correcciones-modulos.md)
y [validación local](../docs/integracion/validacion-hito-3.md). [Revisión por épica](../docs/integracion/revision-por-epica.md) y [ADR-008](../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md) registran los dos bloques de esta continuación. Sin commits, PRs, mensajes
externos ni trabajo con Obsidian por el agente. Los cuatro commits iniciales
ya aparecen localmente durante SDD-1/2; el agente conserva ese historial y
deja el complemento pendiente según la [guía](commits-publicacion.md).

## Recorrido confirmado para Hito 3

Login real en Seguridad → JWT RS256 → backend de Ventas valida mediante JWKS
→ `GET /api/v1/pedidos/me` filtra exclusivamente por UUID `sub` y rol CLIENTE
→ pantalla de mis pedidos. Datos de prueba persistidos en Ventas; llamadas,
firma y autorización reales. No necesita scopes ni credenciales de A10.

**Estado:** recorrido aceptado por Ventas en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2) y elegido por G7.
Michael está confirmado para F1 y coordinación JWT. Ventas compromete
disponibilidad como máximo jueves 15/10; Luis adopta esa fecha como objetivo
de Seguridad, para presentar el sábado 17/10. Rama de /pedidos/me, URLs,
hora de prueba y aceptación de sesión en memoria aún pendientes.
No acredita demo implementada. [Ficha y coordinación](../docs/integracion/hito-3-ventas.md).

Marketplace y los otros módulos conservan el alcance general de la matriz.
Su recorrido de registro/verificación/wishlist se coordinará para otro hito;
no se exige su integración para esta demo. No se detiene el trabajo interno
ya definido: la espera de la aceptación del recorrido por Joseph terminó; la configuración y prueba conjunta siguen pendientes.

[Preparación interna](../docs/integracion/preparacion-interna.md) y
[matriz de scopes](../docs/integracion/matriz-scopes.md) contienen los cambios.

## Salida de preparación

- Dueños de punta a punta y [incremento H3 individual definido](hito-3/README.md).
- Bloqueos aplicables resueltos o subflujos expresamente bloqueados.
- Sergio publica cambios con versionado y aviso; no se supone aprobación.
- Acuerdo bilateral, revisión y configuración de Ventas identificadas.
- Casos y tareas individuales enlazados a esas fuentes.

GOV-01 registra cinco días hasta la presentación frente a una semana de aviso
contractual. Este documento no decide una excepción ni promete despliegues
dependientes de cambios sin aprobación.

## Calendario objetivo — actualización 12-oct

- Hasta jueves 15/10: preparar y comprobar entregables internos; ambos
  servicios accesibles en nube y prueba conjunta como objetivo.
- Viernes 16/10: corregir fallos y ensayar; guardar evidencia.
- Sábado 17/10: presentación. La hora no está indicada, por lo que el margen
  aproximado de dos días o día y medio no se afirma como 48 horas exactas.

Ventas sí confirmó su responsable y fecha. La fecha de G7 es el objetivo
indicado por Luis, no disponibilidad acreditada. Luis publicó el acuse de G7 en el issue. A10 se ajusta por #44 retirando CSAT;
A11 registra solo esta demo. Definición publicada; configuración, aviso/adopción y ejecución pendientes.
