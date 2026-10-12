# Flujo SDD — specs, plan, tareas y código

**Corte:** 12-oct-2026. Estructura documental preparada; no inicia implementación.
El [plan de nueve puntos](README.md) sigue siendo la preparación previa.
Este documento organiza la etapa siguiente y mantiene el
[reparto de ocho épicas](../specs/epicas/README.md).

## Los dos puntos de este bloque

| Punto SDD | Entregable | Alcance de esta entrega |
|---|---|---|
| SDD-1 | Estructura, plantillas y reglas de planes/tareas | Preparado documentalmente |
| SDD-2 | Plan técnico del recorrido H3 con Ventas | Preparado con dependencias y decisiones pendientes |

No son los puntos 1/2 del plan anterior. A continuación se elaborarán los
planes individuales de EP-02 y de identidad técnica, y después sus tareas.
Las otras épicas se planificarán en bloques posteriores; no se han escrito
sus ocho planes ni generado todas las tareas de una vez.

## Secuencia de trabajo

1. **Leer las entradas.** Ficha de épica, specs funcionales con sus siete
   secciones, UI, componentes React, contrato, errores/eventos y ADR aplicables.
2. **Escribir el plan.** Elegir el incremento, trazar RF/ESC, identificar datos,
   dependencias, decisiones técnicas, fases y pruebas. Una decisión funcional
   nueva vuelve primero a su spec; una contractual pasa por Sergio.
3. **Derivar tareas del plan revisado.** Cada tarea tiene un resultado pequeño,
   dueño, dependencia, criterios observables y evidencia prevista. No basta
   con títulos como «hacer backend» o «hacer frontend».
4. **Implementar una tarea y verificarla.** Leer sus fuentes vigentes, comprobar
   que está lista, hacer el cambio y ejecutar los casos aplicables. Un bloqueo
   detiene esa tarea, sin convertir un mock en una conexión real.
5. **Revisar y registrar evidencia.** Código, comando/entorno, resultado y
   revisión permiten cerrar la tarea. Se actualiza el mismo seguimiento.

## Estructura

```text
specs/                              Reglas, escenarios, UI, API y React
plan/
  README.md                         Preparación de nueve puntos y navegación
  flujo-sdd.md                      Estas reglas
  PLAN-IMPLEMENTACION.md            Recorrido H3, dependencias y aceptación
  DECISIONES-Y-BLOQUEOS.md           Vista de los pendientes del recorrido
  PLANTILLA.md                      Plantilla de plan individual
  funcionalidades/README.md         Índice de los ocho planes por preparar
  seguimiento/README.md             Reglas de tareas y evidencia
  seguimiento/PLANTILLA.md          Plantilla de seguimiento individual
  seguimiento-epicas.md             48 filas previas de cobertura general
  commits-publicacion.md            Publicación documental ya preparada
```

Los futuros planes se llamarán `funcionalidades/EP-XX-nombre.md` y tendrán
un seguimiento `seguimiento/EP-XX-nombre.md`. Las capacidades adicionales
de identidad e infraestructura tendrán planes técnicos identificados como
`TEC-IDENTIDAD` y `TEC-INFRA`; no se convierten en épicas académicas.
Sus carpetas/archivos se crean cuando se redacte cada plan.

## Dueño y alcance

El mismo estudiante realiza todas las capas de su épica. Los bloques de una
pantalla compuesta conservan sus dueños; consumir una capacidad no transfiere
su implementación. Sergio mantiene SPEC-17/18 y Christian infraestructura.
Un revisor comprueba el trabajo y no se convierte en dueño del frontend o
backend ajeno. [Detalle](../docs/responsabilidades.md).

Cada plan separa: alcance completo, incremento H3 y partes aplazadas. Terminar
una demo no cierra una épica completa. La demo conjunta con Ventas tampoco
sustituye la primera versión individual de los siete estudiantes.

## Listo para implementar y terminado

Una tarea está **lista** cuando su plan fue revisado, tiene RF/ESC y operación
contractual identificados, sus datos e interfaces están definidos y sus
dependencias necesarias están disponibles. Los resultados previstos y su
comprobación están escritos antes de generar código. Un pendiente no aplicable
al incremento se deja justificado; no se exige cerrar todo el curso para avanzar.

Una tarea está **terminada** cuando sus criterios pasan en el entorno indicado,
hay evidencia y revisión, y no queda un bloqueo aplicable. «Spec escrita»,
«mock responde», «prueba con JWT sintético» e «integración en nube» son evidencias
distintas. No se sustituyen entre sí.

El [registro único](../docs/registro-inconsistencias.md) conserva los problemas.
[Decisiones del recorrido](DECISIONES-Y-BLOQUEOS.md) enlaza ese registro: no
crea un segundo tablero de problemas con estados independientes.

## Las 48 filas existentes

[seguimiento-epicas.md](seguimiento-epicas.md) es una matriz inicial de cobertura,
no el desglose ejecutable. Se preservan sus IDs y estados como antecedente.
Al preparar tareas concretas, se enlaza cada fila con los IDs derivados del
plan, sin contabilizarla como otra tarea de implementación.

IDs nuevos: `EP-02-T001`, por ejemplo; técnicos: `TEC-IDENTIDAD-T001`.
No se reutilizan los IDs anteriores `EP-02-01`. Una tarea cubre uno o varios
RF/ESC explícitos; ni cada spec ni cada capa se convierte en una épica nueva.

## Referencia consultada

Marketplace, revisión `64cc824ec47ad2758bd657f01e5881165189e1f1`:
[plan](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/funcionalidades/F-002-iniciar-sesion.md)
y [tareas](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/seguimiento/F-002-iniciar-sesion.md).
Se toma su separación de entradas, decisiones, fases y evidencia. G7 conserva
los dueños ya acordados y sus propias reglas de identidad/sesión.
