# Tareas derivadas de planes

**Estado:** estructura preparada; aún no hay tareas incrementales emitidas.
[Flujo SDD](../flujo-sdd.md), [planes individuales](../funcionalidades/README.md)
y [plantilla de seguimiento](PLANTILLA.md).

## Una tarea por resultado verificable

Las tareas se escriben después de revisar el plan del incremento. Cada una
identifica qué cambiar, su dueño completo, los RF/ESC de origen, dependencias,
criterios de aceptación y cómo se comprobarán. Un criterio vago como «funciona»
no permite que la IA ni el revisor determinen cuándo termina.

IDs: `EP-XX-T001`, `EP-XX-T002`…; técnicos: `TEC-IDENTIDAD-T001` o
`TEC-INFRA-T001`. El ID no incorpora una persona o capa distinta para repartir
la épica. Si una tarea depende de otro dueño, consume su capacidad y referencia
la tarea/dependencia que la provee. El revisor puede ser otro integrante.

## Estados y evidencia

| Estado | Condición |
|---|---|
| Pendiente | Definida, sin implementación comprobada |
| Bloqueada | Dependencia/decisión aplicable y criterio de liberación escritos |
| Lista | Plan revisado y condiciones de inicio satisfechas |
| En curso | Implementación de esta tarea en progreso |
| En revisión | Criterios comprobados con evidencia; revisión pendiente |
| Terminada | Criterios y revisión aprobados; evidencia reproducible |

Leer las fuentes vigentes al comenzar. Implementar y comprobar una tarea
antes de tomar la siguiente dependiente. Si cambia una regla, actualizar su
spec/plan/tarea antes de continuar. No pasar estados solo por escribir un documento.

Evidencia mínima: revisión de código o commit cuando exista, fecha, comando o
recorrido, entorno/versiones, casos y resultado, observaciones/retest y revisor.
No registrar contraseñas, tokens completos, OTP ni datos personales reales.

Separar `documental`, `mock/fixture`, `servicio real local` y `conjunta en nube`.
El tipo de evidencia depende del criterio: una prueba local puede cerrar una
regla interna, pero no el criterio de conexión con Ventas en nube.

## Conservación del seguimiento previo

Las 48 filas de [seguimiento-epicas.md](../seguimiento-epicas.md) describen
cobertura general. Al redactar tareas concretas, vincular sus IDs a esas filas.
No copiarlas como 48 tareas adicionales ni marcar «Backend» terminado porque
pasó una sola operación. La tabla nueva es el estado de ejecución del incremento;
la anterior conserva contexto y cobertura.

Por ahora se mantiene pendiente: no se ha creado un tablero operativo completo,
asignado trabajo a una capa ajena ni acreditado implementación.
