# AGENTS.md

Guía para agentes de IA que trabajen en este repositorio. Los humanos empiezan
por el [README](README.md) y por [`docs/plan-hito-1.md`](docs/plan-hito-1.md).

## El repositorio en una línea

Módulo de seguridad y autenticación del Marketplace Multicanal de Productos
Deportivos: es el **proveedor de identidad** del que dependen los otros seis
módulos del curso. Fase de especificación — todavía no hay código de producción.

## Reglas que no se saltan

- **El contrato tiene dueño único.** [`specs/openapi.yaml`](specs/openapi.yaml)
  lo edita solo el Product Owner. Seis equipos externos programan contra él, y
  basta con que dos personas lo toquen en paralelo para romperlo.
- **Las specs se escriben antes que el código** (SDD). Una funcionalidad está
  definida cuando su spec tiene las siete secciones de
  [`specs/_PLANTILLA.md`](specs/_PLANTILLA.md); solo entonces se implementa.
- **Ningún error puede permitir enumerar cuentas.** Antes de inventar un código
  de error, mira [`specs/catalogo-errores.md`](specs/catalogo-errores.md): los
  códigos tienen dueño único y varios estados comparten uno a propósito.
- **Un cambio incompatible del contrato no se aplica, se añade.** Renombrar un
  campo que otros módulos ya consumen obliga a `/api/v2`.

## Antes de tocar nada, orientación rápida

| Si vas a… | Lee |
|---|---|
| Nombrar cualquier cosa del dominio | [`CONTEXT.md`](CONTEXT.md) — el glosario: términos canónicos y los que hay que evitar |
| Escribir o revisar una spec | [`specs/_PLANTILLA.md`](specs/_PLANTILLA.md) y [`specs/trazabilidad.md`](specs/trazabilidad.md) |
| Escribir la spec de una pantalla | [`specs/front/`](specs/front/) — specs de interfaz, aparte de las de backend |
| Devolver un error | [`specs/catalogo-errores.md`](specs/catalogo-errores.md) |
| Publicar un evento | [`specs/catalogo-eventos.md`](specs/catalogo-eventos.md) |
| Cambiar el estado de una cuenta | [`docs/arquitectura/estados-usuario.md`](docs/arquitectura/estados-usuario.md) |
| Entender la integración con los otros módulos | [`specs/SPEC-17-tokens-servicio.md`](specs/SPEC-17-tokens-servicio.md) y [`specs/SPEC-18-consulta-identidad.md`](specs/SPEC-18-consulta-identidad.md) |

## Agent skills

### Issue tracker

Los issues viven en GitHub Issues del repositorio
`Taller-SW-Web/Modulo-de-Seguridad`, gestionados con la CLI `gh`. Ver
[`docs/agents/issue-tracker.md`](docs/agents/issue-tracker.md).

### Domain docs

Contexto único: un `CONTEXT.md` en la raíz y los ADR en
`docs/arquitectura/adr/`. Ver [`docs/agents/domain.md`](docs/agents/domain.md).
