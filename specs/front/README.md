# Specs de interfaz

| Campo | Valor |
|---|---|
| **Dueño** | Valery Cristin Gutierrez Bendezu — responsable de UX |
| **Estado** | Solo la plantilla. Las specs se redactan cuando el equipo lo decida |
| **Plantilla** | [`_PLANTILLA-FRONT.md`](_PLANTILLA-FRONT.md) |

Las 18 specs de `specs/` son **de backend**: dicen qué hace el servicio y se
verifican contra su API. Esta carpeta guarda las **specs de interfaz**: cómo se
ve y cómo se comporta cada pantalla de la SPA. Van aparte por indicación del
profesor, y las lleva la persona responsable de UX.

## Qué va aquí y qué no

| Va en una spec de interfaz | Va en la spec de backend |
|---|---|
| Estados de la pantalla: vacío, cargando, error, éxito | Qué responde el endpoint en cada caso |
| Validaciones del lado del cliente, para ayudar al usuario | Las reglas de verdad, que el backend aplica siempre |
| Textos y mensajes que ve el usuario | Los códigos de error (`catalogo-errores.md`) |
| Accesibilidad, diseño adaptable, navegación | Rendimiento, seguridad, datos personales |
| Qué endpoint llama la pantalla y cuándo | El contrato de ese endpoint (`openapi.yaml`) |

**Una spec de interfaz no inventa reglas.** Si una pantalla necesita algo que su
spec de backend no tiene, se cambia primero la spec de backend.

## Numeración

`FRONT-NN-nombre-corto.md`, un archivo por pantalla. Las ocho pantallas del
Hito 1 y la spec de backend que consume cada una están en
[`../trazabilidad.md`](../trazabilidad.md) §3; ese orden es un buen punto de
partida para numerar.
