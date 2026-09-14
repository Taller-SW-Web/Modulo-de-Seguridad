# Domain docs

Cómo deben consumir las skills de ingeniería la documentación de dominio de este
repositorio al explorar el código.

## Antes de explorar, lee esto

- **`CONTEXT.md`** en la raíz del repositorio, si existe.
- **`docs/arquitectura/adr/`**: lee los ADR que toquen el área en la que vas a
  trabajar. Hoy hay cuatro:

  | ADR | Decisión |
  |---|---|
  | [001](../arquitectura/adr/001-rs256-frente-a-hs256.md) | RS256 frente a HS256 para firmar los tokens |
  | [002](../arquitectura/adr/002-validacion-local-frente-a-introspeccion.md) | Validación local frente a introspección |
  | [003](../arquitectura/adr/003-contrato-antes-que-codigo.md) | Contrato antes que código |
  | [004](../arquitectura/adr/004-idioma-del-contrato.md) | Idioma del contrato |

> **Ojo con la ruta.** Los ADR de este repositorio están en
> `docs/arquitectura/adr/`, no en el `docs/adr/` que asumen las plantillas por
> defecto. No crees un `docs/adr/` nuevo: añade los ADR nuevos donde ya están
> los cuatro existentes.

Si alguno de estos archivos no existe, **sigue en silencio**. No señales su
ausencia ni propongas crearlos de entrada. La skill `/domain-modeling` —a la que
se llega desde `/grill-with-docs` y `/improve-codebase-architecture`— los crea
de forma perezosa, cuando de verdad se resuelve un término o una decisión.

## Estructura de archivos

Este repositorio es de **contexto único**: no es un monorepo y no hay
`CONTEXT-MAP.md`.

```
/
├── AGENTS.md
├── CONTEXT.md                      ← todavía no existe; se crea cuando haga falta
├── README.md
├── docs/
│   ├── agents/                     ← esta configuración
│   ├── arquitectura/
│   │   ├── adr/                    ← las decisiones
│   │   └── estados-usuario.md
│   ├── plan-hito-1.md
│   └── responsabilidades.md
└── specs/                          ← las nueve specs SDD y el contrato
    ├── SPEC-0X-*.md
    ├── openapi.yaml
    ├── catalogo-errores.md
    ├── catalogo-eventos.md
    └── trazabilidad.md
```

## Usa el vocabulario del glosario

Cuando lo que produzcas nombre un concepto del dominio —en el título de un
issue, en una propuesta de refactor, en una hipótesis, en el nombre de una
prueba— usa el término tal como esté definido en `CONTEXT.md`. No derives hacia
sinónimos que el glosario evita a propósito.

Si el concepto que necesitas todavía no está en el glosario, eso es una señal: o
estás inventando lenguaje que el proyecto no usa (reconsidéralo), o hay un hueco
real (anótalo para `/domain-modeling`).

Mientras no exista `CONTEXT.md`, el vocabulario de referencia de este módulo son
los códigos ya publicados en el contrato: los seis roles (`CLIENTE`, `VENDEDOR`,
`ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`), los
cuatro estados de cuenta y los códigos de error de
[`specs/catalogo-errores.md`](../../specs/catalogo-errores.md). **Son parte del
contrato: no se renombran unilateralmente.**

## Señala los conflictos con un ADR

Si lo que propones contradice un ADR existente, dilo de forma explícita en vez
de pasarlo por alto en silencio:

> _Contradice el ADR-002 (validación local frente a introspección), pero vale la
> pena reabrirlo porque…_
