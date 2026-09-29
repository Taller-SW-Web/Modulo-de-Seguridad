# FRONT-14 — Panel de administración: auditoría (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-14-admin-auditoria.md` |
| Specs de backend | SPEC-13 · catálogo de acciones de SPEC-12 |
| Wireframe | Sin wireframe — pendiente |
| Responsable | Por asignar |

---

## Objetivo

Un `ADMIN_SISTEMA` investiga un incidente: filtra el registro de seguridad por
cuenta afectada, actor, acción, resultado, IP y fechas, y exporta el resultado en
CSV o JSON (RF-13.1, RF-13.2, HU-13.1). Se llega desde el menú del panel o desde
«Ver todo en auditoría» del detalle (FRONT-08), con la cuenta ya filtrada.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| «Buscar» y paginar | `GET /api/v1/auditoria` con los filtros | SPEC-13 |
| «Exportar CSV» / «Exportar JSON» | `GET /api/v1/auditoria/exportar?formato=csv\|json` con los filtros | SPEC-13 |

Permiso: `auditoria.ver`.

**La búsqueda no se lanza sola.** Cada consulta queda auditada como
`AUDITORIA_CONSULTADA` (RF-13.4); se consulta al pulsar «Buscar» y al paginar.
Al abrir sin filtros no se consulta nada; desde FRONT-08 se consulta una vez con
la cuenta filtrada.

---

## Archivos propuestos

```
frontend/src/pantallas/admin-auditoria/
├── AdminAuditoriaPage.tsx   # filtros + tabla + exportación
├── FiltrosAuditoria.tsx     # form de filtros (viven en la URL)
├── TablaAuditoria.tsx       # columnas + «Ver detalle» (aria-expanded)
├── etiquetasAccion.ts       # compartido con FRONT-13
└── useAuditoria.ts          # consulta, paginación, exportación con token
```

Reutiliza de FRONT-00: `api`, `errores`, `sesion` (permiso), `DataTable`.

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `AdminAuditoriaPage` | Une filtros + tabla + exportación; aviso fijo «Tus consultas y exportaciones también quedan registradas en la auditoría.» |
| `FiltrosAuditoria` | `<form>` con los filtros; se envía con Intro |
| `TablaAuditoria` | Fecha, acción, resultado, tipo de actor, actor, cuenta afectada, IP y «Ver detalle» (`aria-expanded`) |
| `useAuditoria` | Consulta y pagina; exporta con `fetch` + `Authorization` |

---

## Filtros (viven en la URL)

| Filtro | Control | Parámetro |
|---|---|---|
| Cuenta afectada | texto (id) | `objetivoUsuarioId` |
| Actor | texto (usuario o `client_id`) | `actorId` |
| Acción | lista del catálogo SPEC-12 (etiquetas de FRONT-13) | `accion` |
| Resultado | Todos · Correcto · Fallido | `resultado` |
| IP | texto | `ip` |
| Desde / Hasta | fecha y hora (Lima → UTC) | `desde`, `hasta` |
| Tamaño de página | 50 (def.), 100, 200 | `tamano` |

El actor y la cuenta afectada enlazan a FRONT-08 cuando son ids de usuario.

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | al abrir sin filtros | filtros vacíos + «Elige al menos un filtro y pulsa Buscar.» |
| Cargando | «Buscar»/paginar | filas de esqueleto |
| Con resultados | `200` | tabla + «{total} registros» + paginación |
| Sin resultados | `registros: []` | «Ningún registro coincide con los filtros.» |
| Exportando | pulsar exportar | «Preparando archivo…» |
| Exportado | `200` | descarga con el nombre de `Content-Disposition` |
| Exportación demasiado grande | `413 EXPORTACION_DEMASIADO_GRANDE` | mensaje + `detail` |
| Sin permiso | falta `auditoria.ver` o `403` | «No tienes permiso para ver esta página.» |
| Error | `503`/sin red | FRONT-00 §2 + «Reintentar» |

**Exportar con filtros que la exportación no admite** (H-08: `actorId`,
`resultado`, `ip`): los botones de exportar se deshabilitan con «La exportación
todavía no admite los filtros de actor, resultado ni IP. Quítalos para exportar.»
(un archivo con más registros que la pantalla sería engañoso).

**La descarga se hace con `fetch`** y el token en `Authorization`; un enlace
directo no llevaría el token.

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Cuenta afectada | formato UUID | «Escribe un identificador de cuenta válido.» |
| Desde / Hasta | «Desde» no posterior a «Hasta» | «La fecha inicial no puede ser posterior a la final.» |
| Buscar | al menos un filtro | «Elige al menos un filtro.» |

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `EXPORTACION_DEMASIADO_GRANDE` | «El filtro abarca demasiados registros para exportarlo. Acota el rango de fechas.» + `detail` |
| `VALIDACION` | FRONT-00 §2 |
| `SCOPE_INSUFICIENTE` / `NO_DISPONIBLE` | FRONT-00 §2 |

Etiquetas de `accion`/`resultado`: las de FRONT-13. `actorTipo`: `USUARIO` →
«Usuario», `MODULO` → «Módulo», `SISTEMA` → «Sistema».

---

## Accesibilidad

- Los filtros son un `<form>` que se envía con Intro.
- «Ver detalle» usa `aria-expanded`.
- 390 px: filtros en panel «Filtros» y tabla → tarjetas.

## Fuera de alcance

Alertas en tiempo real; borrar/editar registros (SPEC-12); registros de más de 90
días.

## Huecos detectados

- **H-07**: `accion` admite un solo valor por consulta.
- **H-08**: `/auditoria/exportar` admite menos filtros que `/auditoria`.
