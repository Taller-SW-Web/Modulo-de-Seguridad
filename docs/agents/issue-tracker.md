# Issue tracker: GitHub

Los issues y las specs de este repositorio viven como **GitHub Issues**. Usa la
CLI `gh` para todas las operaciones.

Repositorio: **`Taller-SW-Web/Modulo-de-Seguridad`**.

> **Ojo con el directorio de trabajo.** El repositorio está en
> `Modulo-de-Seguridad/`, dentro de una carpeta padre (`ProyectoTallerSW`) que
> **no** es un repo git. `gh` infiere el repositorio del clon en el que se
> ejecuta, así que corre los comandos desde dentro del repositorio, o pasa
> `--repo Taller-SW-Web/Modulo-de-Seguridad` explícitamente.

## Convenciones

- **Crear un issue**: `gh issue create --title "..." --body "..."`. Para cuerpos
  de varias líneas, usa `--body-file -` con un heredoc.
- **Leer un issue**: `gh issue view <número> --comments`, filtrando los
  comentarios con `jq` y trayendo también las etiquetas.
- **Listar issues**:
  `gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'`
  con los filtros `--label` y `--state` que correspondan.
- **Comentar**: `gh issue comment <número> --body "..."`
- **Etiquetar / quitar etiqueta**: `gh issue edit <número> --add-label "..."` /
  `--remove-label "..."`
- **Cerrar**: `gh issue close <número> --comment "..."`

## Los pull requests como superficie de peticiones

**PRs como superficie de peticiones: no.** _(Ponlo en `sí` si este repositorio
trata los PR externos como peticiones de funcionalidad; la skill `/triage` lee
esta bandera.)_

Está en `no` a propósito: los PR de este repositorio los abren los siete
integrantes del grupo, no colaboradores externos. Aquí un PR es trabajo en
curso, no una petición que haya que triar.

Si algún día se pusiera en `sí`, los PR pasarían por las mismas etiquetas y
estados que los issues, con los equivalentes `gh pr`:

- **Leer un PR**: `gh pr view <número> --comments` y `gh pr diff <número>`.
- **Listar PR externos**:
  `gh pr list --state open --json number,title,body,labels,author,authorAssociation,comments`
  y quedarse solo con `authorAssociation` igual a `CONTRIBUTOR`,
  `FIRST_TIME_CONTRIBUTOR` o `NONE` (descartando `OWNER`, `MEMBER` y
  `COLLABORATOR`).
- **Comentar / etiquetar / cerrar**: `gh pr comment`, `gh pr edit --add-label` /
  `--remove-label`, `gh pr close`.

GitHub comparte un mismo espacio de numeración entre issues y PR, así que un
`#42` a secas puede ser cualquiera de los dos: resuélvelo con `gh pr view 42` y
recurre a `gh issue view 42` si falla.

## Cuando una skill dice «publica en el issue tracker»

Crea un GitHub Issue.

## Cuando una skill dice «trae el ticket correspondiente»

Ejecuta `gh issue view <número> --comments`.

## Operaciones de wayfinding

Las usa `/wayfinder`. El **mapa** es un issue único con issues **hijos** como
tickets.

- **Mapa**: un issue con la etiqueta `wayfinder:map`, que contiene el cuerpo de
  Notas / Decisiones-hasta-ahora / Niebla. `gh issue create --label wayfinder:map`.
- **Ticket hijo**: un issue enlazado al mapa como sub-issue de GitHub (`gh api`
  sobre el endpoint de sub-issues). Donde los sub-issues no estén habilitados,
  añade el hijo a una lista de tareas en el cuerpo del mapa y pon
  `Part of #<mapa>` al principio del cuerpo del hijo. Etiquetas:
  `wayfinder:<tipo>` (`research` / `prototype` / `grilling` / `task`). Una vez
  reclamado, el ticket se asigna a quien lo lleva.
- **Bloqueo**: usa las **dependencias nativas de issues** de GitHub, que son la
  representación canónica y visible en la interfaz. Añade una arista con
  `gh api --method POST repos/<owner>/<repo>/issues/<hijo>/dependencies/blocked_by -F issue_id=<id-del-bloqueador>`,
  donde `<id-del-bloqueador>` es el **id numérico de base de datos** del
  bloqueador (`gh api repos/<owner>/<repo>/issues/<n> --jq .id`, **no** el
  `#número` ni el `node_id`). GitHub informa de
  `issue_dependencies_summary.blocked_by` (solo bloqueadores abiertos, que es la
  condición viva). Donde las dependencias no estén disponibles, recurre a una
  línea `Blocked by: #<n>, #<n>` al principio del cuerpo del hijo. Un ticket
  está desbloqueado cuando todos sus bloqueadores están cerrados.
- **Consulta de frontera**: lista los hijos abiertos del mapa
  (`gh issue list --state open`, acotado a los sub-issues o la lista de tareas
  del mapa), descarta los que tengan un bloqueador abierto
  (`issue_dependencies_summary.blocked_by > 0`, o un issue abierto en la línea
  `Blocked by`) o ya tengan asignado; gana el primero en el orden del mapa.
- **Reclamar**: `gh issue edit <n> --add-assignee @me`, la primera escritura de
  la sesión.
- **Resolver**: `gh issue comment <n> --body "<respuesta>"`, después
  `gh issue close <n>`, y después añade un puntero de contexto (gist + enlace) a
  las Decisiones-hasta-ahora del mapa.
