# FRONT-07 — Panel de administración: listado de usuarios (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-07-admin-listado-usuarios.md` |
| Specs de backend | SPEC-03 (alta/consulta), SPEC-04 (baja/reactivación), SPEC-11 (roles/permisos), SPEC-15 (bloqueo manual) |
| Responsable | Por asignar |

---

## Objetivo

Un `ADMIN_SISTEMA` ve todas las cuentas, las filtra por estado/rol/correo y actúa
sin abrir el detalle: bloquear, desbloquear, dar de baja y reactivar. Es el
destino por defecto tras iniciar sesión para quien tiene `usuario.ver`
(FRONT-00 §1.4). Abre el detalle (FRONT-08) y el alta (FRONT-11).

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir, cambiar filtro/búsqueda/página | `GET /api/v1/usuarios?estado&rol&correo&pagina&tamano` | SPEC-03 |
| Confirmar «Bloquear» | `POST /api/v1/usuarios/{id}/bloquear` con `motivo` | SPEC-15 |
| Confirmar «Desbloquear» | `POST /api/v1/usuarios/{id}/desbloquear` | SPEC-15 |
| Confirmar «Dar de baja» | `DELETE /api/v1/usuarios/{id}` | SPEC-04 |
| Confirmar «Reactivar» | `POST /api/v1/usuarios/{id}/reactivar` | SPEC-04 |

---

## Permisos (SPEC-11) — solo afectan a lo que se **muestra**

| Elemento | Permiso |
|---|---|
| La pantalla entera | `usuario.ver` |
| Botón «Crear cuenta» | `usuario.crear` |
| Bloquear / desbloquear | `cuenta.bloquear` |
| Dar de baja | `usuario.desactivar` |
| Reactivar | `usuario.reactivar` |

El backend vuelve a comprobarlo siempre.

---

## Archivos propuestos

```
frontend/src/pantallas/admin-listado/
├── AdminListadoPage.tsx        # orquesta tabla, filtros, diálogos
├── TablaUsuarios.tsx           # columnas + estado (texto) + acciones por fila
├── FiltrosUsuarios.tsx         # estado, rol, correo, tamaño; viven en la URL
├── AccionesFila.tsx            # menú según estado y permisos
├── DialogoBloqueo.tsx          # motivo obligatorio (≤500)
├── DialogoConfirmacion.tsx     # desbloquear / baja / reactivar
└── useListadoUsuarios.ts       # GET /usuarios + acciones + recarga de la página actual
```

Reutiliza de FRONT-00: `api`, `errores`, `sesion` (permisos), `EstadoCuentaBadge`,
`DataTable` (del sistema de diseño).

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `AdminListadoPage` | Une filtros + tabla + diálogos; recarga la página actual tras una acción |
| `TablaUsuarios` | `<table>` con `<caption>` de filtros; columnas: correo, `nombreCompleto`, roles (etiquetas FRONT-00 §4), estado. **Sin «último acceso»** |
| `FiltrosUsuarios` | estado (4 o todos), rol (6 o todos), buscador por correo (fragmento, 300 ms de debounce), tamaño (20; máx 100) |
| `AccionesFila` | Menú «Acciones para {correo}» con acciones según estado (tabla abajo) |
| `DialogoBloqueo` | Campo motivo obligatorio con contador (máx 500) |
| `DialogoConfirmacion` | Textos de cada acción |
| `useListadoUsuarios` | Lee filtros de la URL, llama `api.listarUsuarios`, ejecuta acciones |

---

## Columna de estado (siempre con texto)

| Estado | Texto |
|---|---|
| `ACTIVO` | «Activa» |
| `PENDIENTE_VERIFICACION` | «Pendiente de verificación» |
| `BLOQUEADO` (`tipo=AUTOMATICO`, `hasta`) | «Bloqueada hasta {hh:mm}» |
| `BLOQUEADO` (`hasta=null`) | «Bloqueada sin vencimiento» |
| `INACTIVO` | «Inactiva» |

## Acciones por fila (según estado + permisos)

| Estado | Acciones |
|---|---|
| `ACTIVO` | Ver · Bloquear · Dar de baja |
| `BLOQUEADO` automático | Ver · Desbloquear · Bloquear manualmente · Dar de baja |
| `BLOQUEADO` manual | Ver · Desbloquear · Dar de baja |
| `PENDIENTE_VERIFICACION` | Ver · Dar de baja |
| `INACTIVO` | Ver · Reactivar |

En la fila de la **propia cuenta** no aparecen «Bloquear» ni «Dar de baja»
(RF-04.2, RF-15.1). Si es el último `ADMIN_SISTEMA` activo, lo responde el backend.

**Botón destacado** «Crear cuenta de vendedor o gestión» → FRONT-11.

---

## Estados de la pantalla

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Cargando | al abrir/cambiar filtros | filas de esqueleto; filtros usables |
| Con resultados | `200` | tabla + «Página {p} de {n}» + «{total} cuentas» |
| Sin resultados | `usuarios: []` con filtros | «Ninguna cuenta coincide con los filtros.» + «Quitar filtros» |
| Sin permiso | falta `usuario.ver` o `403` | «No tienes permiso para ver esta página.» |
| Error de carga | `503`/sin red | FRONT-00 §2 + «Reintentar» |
| Confirmando acción | pulsar acción | diálogo (bloquear pide motivo) |
| Acción en curso | confirmar | botón del diálogo deshabilitado |
| Acción correcta | `204` | cierra diálogo, aviso breve, recarga la página actual con los mismos filtros |
| Acción rechazada | `4xx` | mensaje en el diálogo, que sigue abierto |

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Motivo de bloqueo | obligatorio, sin contar espacios | «Escribe el motivo del bloqueo.» |
| Motivo de bloqueo | máx 500, con contador | «El motivo no puede superar los 500 caracteres.» |
| Buscador | consulta 300 ms tras dejar de escribir | — |

---

## Textos y mensajes

| Acción | Título / texto / botón |
|---|---|
| Bloquear | «Bloquear la cuenta de {correo}» · «Se cerrarán todas sus sesiones… Le avisaremos por correo, sin incluir el motivo.» + «Motivo (obligatorio)» · «Bloquear» |
| Desbloquear | «Desbloquear la cuenta de {correo}» · «Podrá volver a iniciar sesión. Le avisaremos por correo.» · «Desbloquear» |
| Dar de baja | «Dar de baja la cuenta de {correo}» · «La cuenta quedará inactiva y se cerrarán sus sesiones. No se borra nada: puedes reactivarla después.» · «Dar de baja» |
| Reactivar | «Reactivar la cuenta de {correo}» · «Volverá a estar activa con los mismos datos y roles. Tendrá que iniciar sesión de nuevo.» · «Reactivar» |

| Código | Mensaje |
|---|---|
| `204` | «Cuenta bloqueada.» / «Cuenta desbloqueada.» / «Cuenta dada de baja.» / «Cuenta reactivada.» |
| `ADMINISTRADOR_PROTEGIDO` | «No puedes hacer esto con tu propia cuenta ni con el último administrador del sistema activo.» |
| `CUENTA_NO_DISPONIBLE` | «Esta cuenta no se puede bloquear en su estado actual.» |
| `NO_ENCONTRADO` | «Esa cuenta ya no existe.» + recarga el listado |
| `VALIDACION` (motivo) | bajo el campo, FRONT-00 §2 |
| `SCOPE_INSUFICIENTE` / `NO_DISPONIBLE` | FRONT-00 §2 |

El motivo lo ven administradores, no el titular (RF-15.6); el diálogo lo dice.

---

## Accesibilidad

- `<table>` con `<th scope="col">` y `<caption>` que resume los filtros.
- Acciones de fila en menú «Acciones para {correo}», navegable con teclado.
- Diálogos atrapan foco, cierran con Escape, devuelven el foco al menú.
- El cambio de resultados se anuncia: «{total} cuentas encontradas».
- 390 px: tabla → tarjetas, una por cuenta, estado en la primera línea.

## Fuera de alcance

«Último acceso» y otras columnas que la API no devuelve; búsqueda por nombre;
acciones masivas; asignar/revocar roles (FRONT-08); bloqueos con fecha elegida.

## Hueco detectado

- **H-09**: las etiquetas de rol salen de FRONT-00 §4 porque `GET /roles` no
  admite token de usuario.
