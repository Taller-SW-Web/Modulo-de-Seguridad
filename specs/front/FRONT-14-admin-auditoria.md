# FRONT-14 — Panel de administración: auditoría

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-13, catálogo de acciones de SPEC-12 |
| **Wireframe** | Sin wireframe — pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un `ADMIN_SISTEMA` investiga un incidente: filtra el registro de seguridad por
cuenta afectada, actor, acción, resultado, IP y fechas, y exporta el resultado
en CSV o JSON (RF-13.1, RF-13.2, HU-13.1). Se llega desde el menú del panel o
desde «Ver todo en auditoría» en el detalle de una cuenta (FRONT-08), con la
cuenta ya filtrada.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al pulsar «Buscar» y al cambiar de página | `GET /api/v1/auditoria` con los filtros | SPEC-13 |
| Al pulsar «Exportar CSV» o «Exportar JSON» | `GET /api/v1/auditoria/exportar?formato=csv\|json` con los filtros | SPEC-13 |

Permiso necesario: `auditoria.ver`.

**La búsqueda no se lanza sola.** Cada consulta queda auditada como
`AUDITORIA_CONSULTADA` (RF-13.4); buscar mientras se escribe llenaría la
auditoría de consultas a medias. Se consulta al pulsar «Buscar» y al paginar.
Al abrir la pantalla sin filtros no se consulta nada; si llega desde FRONT-08,
se consulta una vez con la cuenta filtrada.

---

## Contenido

**Filtros** (viven en la URL):

| Filtro | Control | Parámetro |
|---|---|---|
| Cuenta afectada | Texto (identificador) | `objetivoUsuarioId` |
| Actor | Texto (identificador de usuario o `client_id` de un módulo) | `actorId` |
| Acción | Lista con las acciones del catálogo de SPEC-12, con las etiquetas de FRONT-13 | `accion` |
| Resultado | Todos · Correcto · Fallido | `resultado` |
| IP | Texto | `ip` |
| Desde / Hasta | Fecha y hora en hora de Lima, enviadas en UTC | `desde`, `hasta` |
| Tamaño de página | 50 (por defecto), 100 o 200 | `tamano` |

**Resultados**: tabla con fecha, acción, resultado, tipo de actor («Usuario»,
«Módulo», «Sistema»), actor, cuenta afectada, IP y un botón «Ver detalle» que
despliega `detalle` y el agente completo. El actor y la cuenta afectada enlazan
al detalle de la cuenta (FRONT-08) cuando son identificadores de usuario.

Aviso fijo bajo los filtros: «Tus consultas y exportaciones también quedan
registradas en la auditoría.»

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Al abrir sin filtros | Filtros vacíos y «Elige al menos un filtro y pulsa Buscar.» |
| Cargando | Tras «Buscar» o paginar | Filas de esqueleto |
| Con resultados | `200` | Tabla, «{total} registros» y paginación |
| Sin resultados | `200` con `registros: []` | «Ningún registro coincide con los filtros.» |
| Exportando | Tras pulsar exportar | Botón deshabilitado con «Preparando archivo…» |
| Exportado | `200` | Se descarga el archivo con el nombre de `Content-Disposition` |
| Exportación demasiado grande | `413 EXPORTACION_DEMASIADO_GRANDE` | Mensaje, con el `detail` del backend, que dice cuántos registros abarca |
| Sin permiso | Falta `auditoria.ver`, o `403` | «No tienes permiso para ver esta página.» |
| Error | `503` o sin conexión | Mensaje de FRONT-00 §2 y «Reintentar» |

**Exportar con filtros que la exportación no admite.** `/auditoria/exportar`
no recibe `actorId`, `resultado` ni `ip` (H-08). Si alguno está puesto, los
botones de exportar se deshabilitan con la explicación «La exportación todavía
no admite los filtros de actor, resultado ni IP. Quítalos para exportar.»: un
archivo con más registros de los que se ven en pantalla sería engañoso.

**La descarga se hace con `fetch`** y el token en la cabecera `Authorization`,
y se entrega al navegador como archivo. Un enlace directo no llevaría el token.

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Cuenta afectada | Formato UUID | «Escribe un identificador de cuenta válido.» | `format: uuid` |
| Desde / Hasta | «Desde» no posterior a «Hasta» | «La fecha inicial no puede ser posterior a la final.» | Interfaz |
| Buscar | Al menos un filtro | «Elige al menos un filtro.» | Interfaz: evita consultas y registros de auditoría vacíos de sentido |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `EXPORTACION_DEMASIADO_GRANDE` | «El filtro abarca demasiados registros para exportarlo. Acota el rango de fechas.» seguido del `detail` («…el filtro alcanza 312480 registros.») |
| `VALIDACION` | Según FRONT-00 §2 |
| `SCOPE_INSUFICIENTE`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

Etiquetas de `accion` y `resultado`: las de FRONT-13. `actorTipo`: `USUARIO` →
«Usuario», `MODULO` → «Módulo», `SISTEMA` → «Sistema».

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-14.1 Investigar una cuenta
- **Dado** un `ADMIN_SISTEMA` en el detalle de una cuenta
- **Cuando** pulsa «Ver todo en auditoría»
- **Entonces** llega aquí con la cuenta filtrada y ve sus registros, del más
  reciente al más antiguo.

### UI-14.2 Filtrar fallos por fechas
- **Dado** la pantalla
- **Cuando** elige «Intento de inicio de sesión fallido», del 01/09 al 13/09, y pulsa «Buscar»
- **Entonces** ve solo esos registros y el total de coincidencias (ESC-13.1).

### UI-14.3 Exportar a CSV
- **Dado** un filtro por cuenta y acción con resultados
- **Cuando** pulsa «Exportar CSV»
- **Entonces** se descarga un archivo con nombre fechado y los mismos registros
  que la consulta (ESC-13.4).

### UI-14.4 Exportación demasiado grande *(caso borde)*
- **Dado** un filtro de un año sin otra restricción
- **Cuando** pulsa «Exportar JSON»
- **Entonces** ve el mensaje de `EXPORTACION_DEMASIADO_GRANDE` y no se descarga nada.

### UI-14.5 Exportar con filtro no admitido *(caso borde)*
- **Dado** un filtro con IP
- **Cuando** el administrador mira los botones de exportar
- **Entonces** están deshabilitados con la explicación de H-08.

### UI-14.6 Escribir no consulta *(caso borde)*
- **Dado** la pantalla
- **Cuando** el administrador escribe en los filtros sin pulsar «Buscar»
- **Entonces** no se ha llamado a `/auditoria`.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- Los filtros son un `<form>` que se envía con Intro.
- «Ver detalle» usa un botón con `aria-expanded`.
- En 390 px los filtros se pliegan en un panel «Filtros» y la tabla pasa a tarjetas.

---

## Fuera de alcance — ¿qué NO hará?

- Alertas en tiempo real (fuera de alcance de SPEC-13).
- Borrar o editar registros: el registro no se modifica (SPEC-12).
- Mostrar registros de más de 90 días: la retención es de 90 días.

---

## Huecos detectados en el backend

- **H-07** — `accion` admite un solo valor por consulta.
- **H-08** — `/auditoria/exportar` admite menos filtros que `/auditoria`,
  aunque RF-13.2 pide «el mismo criterio de filtrado».

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Solo se consulta al pulsar «Buscar» y al paginar
- [ ] La exportación usa los mismos filtros que la consulta, o se deshabilita
- [ ] La descarga lleva el token en la cabecera
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Existe un wireframe y coincide con la implementación
