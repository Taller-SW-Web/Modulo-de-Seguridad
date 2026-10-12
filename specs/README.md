# Especificaciones — G7

## Lectura

1. [Ocho épicas](epicas/README.md) y [responsabilidades](../docs/responsabilidades.md).
2. [Trazabilidad](trazabilidad.md): specs, endpoints, pantallas, eventos y datos.
3. [Fuentes oficiales](../docs/fuentes-oficiales.md).
4. [Plan y seguimiento](../plan/README.md), separados de las especificaciones.

## Organización

| Fuente | Contenido |
|---|---|
| [epicas/](epicas/) | Ocho funcionalidades de usuario con dueño de punta a punta |
| [funcionales/](funcionales/) | Dieciocho specs SDD; 01–16 pertenecen a las épicas y 17–18 son integración técnica |
| [front/](front/) | Comportamiento de interfaz por pantalla, a cargo del dueño funcional |
| [componentes-react/](componentes-react/) | Componentes y hooks de las mismas funcionalidades |
| [Contratos](contrato-api/README.md) | Base pública 1.0.0; revisiones locales v1/v2 no publicadas; Sergio conserva propiedad, edición delegada en este corte |
| [catalogo-errores.md](catalogo-errores.md) | Códigos y reglas contra enumeración |
| [catalogo-eventos.md](catalogo-eventos.md) | Eventos y cargas útiles; no acredita suscripciones externas |
| [kit-integracion.md](kit-integracion.md) | Guía de consumo; ejemplos subordinados a las reglas y contrato |
| [historias-usuario.md](historias-usuario.md) | Historias, requisitos y escenarios |

Una épica no sustituye las siete secciones de una spec ni crea endpoints.
Las tareas y evidencia viven en `plan/`. Los conflictos se registran en el
[inventario vigente](../docs/registro-inconsistencias.md) y se llevan al PO;
no se resuelven suponiendo que una fuente invalida a otra.
