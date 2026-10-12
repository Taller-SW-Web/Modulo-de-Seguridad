# SDD — fuentes primarias y aplicación en G7

**Consulta:** 12-oct-2026. El flujo del curso se contrasta con documentación
oficial de herramientas SDD. Se adopta el método manualmente en la estructura
del repositorio; no se instaló Spec Kit ni Kiro.

## Fuentes consultadas

| Fuente primaria | Qué aporta |
|---|---|
| [GitHub Spec Kit, proceso](https://github.github.com/spec-kit/) | Especificar, planificar, derivar tareas, implementar y comprobar convergencia con la intención |
| [GitHub Spec Kit, guía](https://github.github.com/spec-kit/quickstart.html) | Aclarar ambigüedades; plan contiene diseño técnico; tareas se derivan del diseño; revisar consistencia antes de implementar |
| [GitHub Spec Kit, concepto](https://github.github.com/spec-kit/concepts/sdd.html) | Requisitos antes de decisiones técnicas, refinamiento incremental y obligaciones acordadas entre consumidores de interfaces |
| [Kiro, specs](https://kiro.dev/docs/specs/) | Requisitos/aceptación, diseño de arquitectura/componentes/errores/pruebas y tareas discretas verificables |

Las fuentes son oficiales de sus herramientas. No se presentan sus nombres
de archivos o comandos como una norma universal que obligue a reorganizar G7.
Nuestra adaptación mantiene AGENTS, las siete secciones de spec del curso,
el contrato con dueño único y las ocho épicas con dueño de punta a punta.

## Correspondencia concreta

| Etapa | Artefactos G7 | Qué debe quedar claro |
|---|---|---|
| Especificar / aclarar | specs funcionales, UI/UX, contrato API, componentes React; catálogos/ADR | Comportamiento, errores, estados, aceptación y límites, sin contradicción abierta en el incremento |
| Planificar / diseñar | plan por épica/capacidad con [plantilla](../plan/PLANTILLA.md) | Las cuatro entradas, decisiones FE/BE/datos, interfaces internas, dependencias, fases, pruebas y recuperación |
| Derivar tareas | Seguimiento individual posterior | Resultado pequeño, RF/ESC/UI, dependencia, dueño, comprobación y evidencia |
| Analizar consistencia | Revisión de spec ↔ plan ↔ tareas ↔ contrato | Cada criterio cubierto; cambios funcionales vuelven primero a su spec |
| Implementar / verificar | Código incremental y evidencia | Una tarea lista por vez, escenarios positivos/negativos y entorno identificado |
| Comprobar alcance | Spec frente a implementación y evidencia | Detectar faltantes; ajustar fuentes/tareas; una demo parcial no cierra toda la épica |

En el curso, UI y componentes React ya están especificados antes del plan.
El plan los consume y concreta composición/datos/tecnología; no vuelve a
inventar reglas. Si la capacidad es exclusivamente API, justifica por qué
no tiene pantalla o React propio y traza sus consumidores.

## Punto actual y salida hacia tareas

Se prepararon [flujo SDD](../plan/flujo-sdd.md),
[plan conjunto H3](../plan/PLAN-IMPLEMENTACION.md),
[EP-02](../plan/funcionalidades/EP-02-autenticacion-sesiones.md) y
[TEC-IDENTIDAD](../plan/transversales/TEC-IDENTIDAD.md).
La continuación añade [siete planes H3](../plan/hito-3/README.md), uno por estudiante
para las ocho épicas, y [TEC-INFRA](../plan/transversales/TEC-INFRA.md). Todos
requieren revisión de sus dueños. No se exige diseñar toda la épica para H6
antes de iniciar el incremento H3; lo restante se planifica en futuras entregas.

Todavía no se generan tareas, por instrucción de Luis. Las 48 filas generales
existentes son cobertura inicial, no tareas derivadas de planes terminados.
Antes de desarrollar un incremento: concretar las decisiones/dependencias
necesarias, revisar su plan, derivar tareas y comprobar trazabilidad. Un
pendiente ajeno al incremento no impide los trabajos independientes.

[Preparación por integrante](../plan/preparacion-por-integrante.md) indica
qué falta. [Estado de issues](integracion/estado-issues-20261012.md) separa
definición documental de implementación comprobada.
