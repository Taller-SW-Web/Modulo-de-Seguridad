# Arquitectura del módulo — Hito 1 (preliminar)

**Responsable de la arquitectura de solución:** Sergio Alejandro Osorio Montenegro (Product Owner)
**Responsable de la arquitectura de implementación:** Jose Luis Limachi Sarmiento (Tech Lead)

Este documento es el entregable del rubro *«Arquitectura del módulo preliminar»*
del Hito 1. Es preliminar a propósito: se actualiza al cierre de cada hito con
las decisiones tomadas y las desviaciones respecto de lo planificado.

---

## Los cuatro diagramas

Son HTML autocontenidos: se abren con doble clic, sin servidor y sin internet.
Traen tema claro y oscuro, zoom, búsqueda, recorridos guiados y exportación a
PNG, SVG y WebM para la PPT.

| Diagrama | Qué responde | Archivo |
|---|---|---|
| **Contexto** | Quién nos consume y por qué vía | [`contexto.html`](contexto.html) |
| **Componentes** | Qué hay dentro del servicio y quién es responsable de cada capa | [`componentes.html`](componentes.html) |
| **Secuencia — login con MFA** | Cómo se autentica un usuario y cuándo se emiten los tokens | [`secuencia-login.html`](secuencia-login.html) |
| **Secuencia — rotación del refresco** | Cómo se renueva la sesión y qué pasa si roban un token | [`secuencia-refresco.html`](secuencia-refresco.html) |

Cada `.html` tiene su `.json` al lado: esa es la fuente. Para modificar un
diagrama se edita el JSON y se vuelve a generar, nunca se toca el HTML.

### Las versiones animadas del README

Los cuatro `.svg` de esta carpeta son las versiones que se incrustan en el
README. GitHub no renderiza HTML, pero **sí ejecuta animación CSS dentro de un
SVG referenciado como imagen**, así que el diagrama se construye solo al cargar
la página, sin subir nada a ningún servicio.

Se generan con:

```bash
node docs/arquitectura/generar-svg-animado.mjs
```

El script parte de `_export-<nombre>.svg` —el export del visor, que ya es
autocontenido y de doble tema— y le inyecta los keyframes; el **orden** de
revelado sale del `.json`. Si cambias un diagrama: edita el JSON, regenera el
HTML con archify, reexporta el SVG desde el visor (`Export → SVG`) y corre el
script.

### Cómo usarlos en la presentación

Cada diagrama trae **recorridos guiados** en la barra superior. En lugar de
enseñar el diagrama entero y hablar sobre él, se pulsa un recorrido y el
diagrama resalta solo esa parte. Para el sábado:

| Momento | Diagrama | Recorrido |
|---|---|---|
| «Qué construimos» | Contexto | Nuestro alcance |
| «Por qué los seis dependen de nosotros» | Contexto | Vías 1, 2 y 3, una tras otra |
| «Qué hay dentro» | Componentes | Capas de dominio |
| «Cómo funciona el login» | Secuencia login | Los dos recorridos |
| «El caso borde del que estamos orgullosos» | Secuencia refresco | Reúso detectado |

El botón **Present** entra en modo presentación a pantalla completa.

---

## Decisiones registradas (ADR)

| ADR | Decisión | Estado |
|---|---|---|
| [ADR-001](adr/001-rs256-frente-a-hs256.md) | Firmar los tokens con RS256 y no con HS256 | Aceptada |
| [ADR-002](adr/002-validacion-local-frente-a-introspeccion.md) | Ofrecer dos mecanismos de validación y dejar elegir al consumidor | Aceptada |
| [ADR-003](adr/003-contrato-antes-que-codigo.md) | Publicar el contrato en la semana 4, antes de implementar | Aceptada |
| [ADR-004](adr/004-idioma-del-contrato.md) | Rutas, roles y scopes del contrato en español | Aceptada |

---

## Regenerar los diagramas

Requiere Node 18 o superior y la skill `archify`:

```bash
node bin/archify.mjs deliver architecture docs/arquitectura/contexto.json      docs/arquitectura/contexto.html      --quality showcase
node bin/archify.mjs deliver architecture docs/arquitectura/componentes.json   docs/arquitectura/componentes.html   --quality showcase
node bin/archify.mjs deliver sequence     docs/arquitectura/secuencia-login.json    docs/arquitectura/secuencia-login.html    --quality showcase
node bin/archify.mjs deliver sequence     docs/arquitectura/secuencia-refresco.json docs/arquitectura/secuencia-refresco.html --quality showcase
```

---

## Lo que todavía falta para cerrar el Hito 1

- [ ] `modelo-datos.md` — diagrama entidad-relación de las 15 tablas (**Eva Lucía**)
- [ ] `implementacion.md` — estructura de paquetes y capas del backend (**Jose Luis**)
- [ ] `despliegue.md` — topología de despliegue y contenedores (**Christian**)
- [ ] `../../specs/openapi.yaml` — contrato publicado y mock levantado (**Sergio**)
