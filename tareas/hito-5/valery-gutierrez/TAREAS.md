# H5 — tareas de Valery Cristin Gutierrez Bendezu

**Épica:** EP-08 Atributos y perfil; pruebas de rendimiento.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Ramas:** `feat/TEC-INFRA-rendimiento` (backend) y la ya abierta `feat/EP-08-T007-efectos-perfil`.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama. Revisión cruzada con Eva.

**Situación de partida:** la única prueba de carga es la de H3 y se hizo en local. La rúbrica de H5 pide evidencia de pruebas de performance.

**Fuentes comunes:** evidencia de carga de H3 en el repo backend (`docs/evidencia/EP-02-T007-carga.md` y `docs/evidencia/carga/carga.py`),
[plan de identidad](../../../specs/funcionales/SPEC-17-18-api-identidad.plan.md),
[SPEC-16](../../../specs/funcionales/SPEC-16-atributos.md),
[tablero H4](../../hito-4/valery-gutierrez/TAREAS.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-17-T1 | Plan de carga: escenarios, umbrales y datos | P1 | David OPS-T2 (mié 11-nov) | Pendiente |
| FEAT-17-T2 | Ejecución contra el despliegue | P1 | T1 | Pendiente |
| FEAT-17-T3 | Informe de resultados | P1 | T2 | Pendiente |
| FEAT-04 | Cierre de direcciones, cambio de correo y edición admin | P2 | — | En PR |

## FEAT-17 — Pruebas de rendimiento (P1)

Umbrales ya escritos en la documentación: ingreso p95 menor a 800 ms y renovación p95 menor a 300 ms, con 50 usuarios concurrentes; introspección p95 menor a 200 ms con 50 concurrentes.

### FEAT-17-T1 — Plan
- **Resultado:** escenarios de ingreso, renovación, emisión de token de servicio e introspección; duración, concurrencia y datos.
- **Primer paso:** reutilizar el script de carga de H3 y pedir a David las cuentas dedicadas y el cliente de prueba.
- **Aceptación:** plan revisado. Las cuentas de carga no son las de la demo, porque el bloqueo automático puede bloquearlas.

### FEAT-17-T2 — Ejecución
- **Resultado:** rondas contra el entorno desplegado, en una ventana avisada al equipo.
- **Primer paso:** una ronda corta para comprobar que nada se bloquea.
- **Aceptación:** p50, p95, p99 y errores de cada escenario, con la versión desplegada.

### FEAT-17-T3 — Informe
- **Resultado:** comparación contra los umbrales y causas probables de lo que no se cumpla.
- **Aceptación:** informe publicado. Un umbral incumplido se declara, no se oculta.
- **Evidencia:** `docs/evidencia/h5/FEAT-17.md`. Nivel: propia desplegada.

## FEAT-04 — Cierre (P2)
- **Primer paso:** cerrar sin fusionar el PR de confirmación de cambio de correo que quedó duplicado, y actualizar sobre `develop` el PR de efectos de perfil, cuyo CI falla.
- **Aceptación:** recorridos desplegados de direcciones, cambio de correo y edición administrativa; tablero H4 actualizado.
- **Evidencia:** `docs/evidencia/h5/FEAT-04.md`.
