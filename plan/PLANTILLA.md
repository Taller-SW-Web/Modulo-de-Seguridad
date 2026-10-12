# Plan de implementación — EP-XX / capacidad técnica

**Plantilla:** copiar al preparar un plan; no acredita una planificación individual terminada.
Reglas en [flujo SDD](flujo-sdd.md). Los enlaces de esta plantilla se ajustan
al copiarla a otra carpeta; no pegar rutas inexistentes como evidencia.

## 1. Metadatos y entradas

| Campo | Completar |
|---|---|
| ID, versión y fecha | EP-XX / 0.1.0 / fecha |
| Dueño único | Nombre completo del dueño de la épica; técnico si corresponde |
| Estado del plan | Borrador / En revisión / Revisado para el incremento indicado |
| Revisor y resultado | Nombre, fecha y observaciones; no inventar aprobación |
| Épica y specs | Enlaces a ficha, RF/ESC funcionales, UI, React y contrato |
| Revisión consumida | Commit publicado o corte local identificado; versión del contrato |
| Seguimiento | Archivo de tareas que se elaborará después de revisar el plan |

## 2. Objetivo y límites

Describir el resultado observable de punta a punta. Separar alcance completo,
incremento H3, aplazado y bloqueado. Conservar al mismo dueño en todas las capas.
No sustituir una dependencia real por una respuesta ficticia para declarar éxito.

## 3. Cobertura y criterios

| Incremento | RF/ESC y UI/React de origen | Operación / dato / evento | Resultado comprobable |
|---|---|---|---|
| Completar | Códigos reales y enlaces | Identificadores canónicos | Éxito y caso borde |

Indicar qué requisitos quedan fuera de este incremento y cuándo se planificarán.

## 4. Decisiones técnicas

| Área | Diseño para el incremento | Fuente / decisión pendiente |
|---|---|---|
| Interfaz y componentes | Estados, navegación, sesión y accesibilidad | UI/React |
| Backend | Operaciones, validaciones y composición de capacidades | Specs y contrato |
| Persistencia | Entidades, restricciones, concurrencia y transacciones | Migraciones reales; contraste DAT-01 |
| Dependencias | Interfaz interna y dueño que la entrega | No duplicar reglas |
| Auditoría y eventos | Hecho, criticidad, fallo/reintento y productor | Catálogos vigentes |
| Configuración | Parámetros por entorno y tratamiento de secretos | Decisiones verificadas |

Los nombres de archivos/clases que todavía no existen se rotulan «propuestos».
Un diseño no cambia un endpoint, claim ni regla funcional por interpretación.

## 5. Dependencias y condiciones de inicio

| Dependencia / bloqueo | Dueño | Qué evidencia lo libera | Trabajo independiente permitido |
|---|---|---|---|
| Completar | Dueño de capacidad | Resultado concreto | Alcance sin depender de ella |

Enlazar el registro vigente. No declarar el conjunto listo si una dependencia
necesaria sigue abierta; sí señalar incrementos que ya pueden implementarse.

## 6. Fases y entregables

| Fase | Resultado | Depende de | Verificación | Dueño |
|---|---|---|---|---|
| Completar | Incremento pequeño demostrable | Fase/capacidad | Prueba y escenario | Mismo dueño |

Las fases son el orden del plan. Las tareas, su estado y ejecución viven en
el seguimiento posterior, sin copiar un segundo tablero aquí.

## 7. Pruebas y terminado

Elegir casos positivos, errores, concurrencia, privacidad y accesibilidad
aplicables. Diferenciar comprobación con fixtures, API real, base persistida
y prueba conjunta en nube. Para cada caso: entrada, resultado y evidencia.
Registrar objetivos no funcionales y cualquier desviación revisada.

El incremento termina con pruebas, revisión, trazabilidad y demostración.
La épica completa solo termina cuando se cumple todo su alcance.

## 8. Riesgos y recuperación

Describir fallos previsibles, comportamiento de recuperación y límites de los
reintentos. Indicar cómo reproducir/restaurar datos de prueba sin borrar datos
ajenos ni editar migraciones aplicadas. Referenciar decisiones pendientes.
