# FRONT-07 — Panel de administración: listado de usuarios

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-03, SPEC-04, SPEC-11, SPEC-15 |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 7 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un `ADMIN_SISTEMA` ve todas las cuentas del marketplace, las filtra por estado,
rol y correo, y actúa sobre ellas sin abrir el detalle: bloquear, desbloquear,
dar de baja y reactivar. Es el destino por defecto tras iniciar sesión para
quien tiene `usuario.ver` (FRONT-00 §1.4). Desde aquí se abre el detalle
(FRONT-08) y el alta de cuentas (FRONT-11).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir, al cambiar un filtro, la búsqueda o la página | `GET /api/v1/usuarios?estado&rol&correo&pagina&tamano` | SPEC-03 |
| Al confirmar «Bloquear» | `POST /api/v1/usuarios/{id}/bloquear` con `motivo` | SPEC-15 |
| Al confirmar «Desbloquear» | `POST /api/v1/usuarios/{id}/desbloquear` | SPEC-15 |
| Al confirmar «Dar de baja» | `DELETE /api/v1/usuarios/{id}` | SPEC-04 |
| Al confirmar «Reactivar» | `POST /api/v1/usuarios/{id}/reactivar` | SPEC-04 |

### Permisos

La pantalla lee `permisos` del token (FRONT-00 §1.1) y solo **muestra** lo que
el usuario puede hacer. El backend vuelve a comprobarlo siempre.

| Elemento | Permiso (SPEC-11) |
|---|---|
| La pantalla entera | `usuario.ver` |
| Botón «Crear cuenta» | `usuario.crear` |
| Bloquear y desbloquear | `cuenta.bloquear` |
| Dar de baja | `usuario.desactivar` |
| Reactivar | `usuario.reactivar` |

---

## Contenido

**Tabla** con las columnas: correo, nombre (`nombreCompleto`), roles (todos,
con las etiquetas de FRONT-00 §4) y estado. **Sin «último acceso»**: la API no
lo devuelve.

**Columna de estado**, siempre con texto (los wireframes son en gris):

| Estado | Texto |
|---|---|
| `ACTIVO` | «Activa» |
| `PENDIENTE_VERIFICACION` | «Pendiente de verificación» |
| `BLOQUEADO` con `bloqueo.tipo = AUTOMATICO` y `hasta` | «Bloqueada hasta {hh:mm}» |
| `BLOQUEADO` con `bloqueo.hasta = null` | «Bloqueada sin vencimiento» |
| `INACTIVO` | «Inactiva» |

**Filtros**: estado (los cuatro, o todos), rol (los seis, o todos), buscador
por correo (fragmento, sin distinguir mayúsculas: el contrato no busca por
nombre) y tamaño de página (20 por defecto, máximo 100). Los filtros viven en
la URL, para que recargar o compartir la dirección conserve la vista.

**Acciones por fila**, según el estado de la cuenta (RF de cada spec y
[`estados-usuario.md`](../../docs/arquitectura/estados-usuario.md)):

| Estado | Acciones |
|---|---|
| `ACTIVO` | Ver · Bloquear · Dar de baja |
| `BLOQUEADO` automático | Ver · Desbloquear · Bloquear manualmente (reemplaza al automático, RF-15.2) · Dar de baja |
| `BLOQUEADO` manual | Ver · Desbloquear · Dar de baja |
| `PENDIENTE_VERIFICACION` | Ver · Dar de baja |
| `INACTIVO` | Ver · Reactivar |

En la fila de la **propia cuenta** del administrador no aparecen «Bloquear» ni
«Dar de baja» (RF-04.2, RF-15.1). Que una cuenta sea el último
`ADMIN_SISTEMA` activo la pantalla no lo sabe: lo responde el backend.

**Botón destacado** «Crear cuenta de vendedor o gestión», que abre FRONT-11.

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Cargando | Al abrir o al cambiar filtros | La tabla con filas de esqueleto; los filtros siguen usables |
| Con resultados | `200` con `usuarios` | Tabla, paginación con «Página {pagina} de {⌈total/tamano⌉}» y «{total} cuentas» |
| Sin resultados | `200` con `usuarios: []` y algún filtro | «Ninguna cuenta coincide con los filtros.» y botón «Quitar filtros» |
| Sin permiso | Falta `usuario.ver`, o `403 SCOPE_INSUFICIENTE` | «No tienes permiso para ver esta página.» |
| Error de carga | `503` o sin conexión | Mensaje de FRONT-00 §2 y botón «Reintentar» |
| Confirmando acción | Pulsa una acción | Diálogo de confirmación (ver textos). Bloquear pide el motivo |
| Acción en curso | Tras confirmar | Botón del diálogo deshabilitado con indicador |
| Acción correcta | `204` | Se cierra el diálogo, aviso breve y se recarga **la página actual** del listado con los mismos filtros |
| Acción rechazada | `4xx` | El mensaje en el diálogo, que sigue abierto |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Motivo del bloqueo | Obligatorio, sin contar espacios | «Escribe el motivo del bloqueo.» | RF-15.1, ESC-15.2 |
| Motivo del bloqueo | Máximo 500 caracteres, con contador visible | «El motivo no puede superar los 500 caracteres.» | `maxLength: 500` |
| Buscador | Se consulta 300 ms después de dejar de escribir | — | Interfaz |

---

## Textos y mensajes

Diálogos de confirmación:

| Acción | Título | Texto | Botón |
|---|---|---|---|
| Bloquear | «Bloquear la cuenta de {correo}» | «Se cerrarán todas sus sesiones y no podrá entrar hasta que un administrador la desbloquee. Le avisaremos por correo, sin incluir el motivo.» + campo «Motivo (obligatorio)» | «Bloquear» |
| Desbloquear | «Desbloquear la cuenta de {correo}» | «Podrá volver a iniciar sesión. Le avisaremos por correo.» | «Desbloquear» |
| Dar de baja | «Dar de baja la cuenta de {correo}» | «La cuenta quedará inactiva y se cerrarán sus sesiones. No se borra nada: puedes reactivarla después.» | «Dar de baja» |
| Reactivar | «Reactivar la cuenta de {correo}» | «Volverá a estar activa con los mismos datos y roles. Tendrá que iniciar sesión de nuevo.» | «Reactivar» |

| Código del backend | Mensaje en pantalla |
|---|---|
| `204` | «Cuenta bloqueada.» / «Cuenta desbloqueada.» / «Cuenta dada de baja.» / «Cuenta reactivada.» |
| `ADMINISTRADOR_PROTEGIDO` | «No puedes hacer esto con tu propia cuenta ni con el último administrador del sistema activo.» |
| `CUENTA_NO_DISPONIBLE` | «Esta cuenta no se puede bloquear en su estado actual.» |
| `NO_ENCONTRADO` | «Esa cuenta ya no existe.» y se recarga el listado |
| `VALIDACION` (motivo) | Bajo el campo motivo, según FRONT-00 §2 |
| `SCOPE_INSUFICIENTE`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

> El motivo del bloqueo lo ven los administradores, no el titular (RF-15.6),
> y así lo dice el diálogo para que nadie escriba algo pensando que lo leerá
> el usuario.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-07.1 Filtrar por estado y rol
- **Dado** un `ADMIN_SISTEMA` en el listado
- **Cuando** elige «Bloqueada» y «Vendedor»
- **Entonces** la tabla muestra solo esas cuentas, la URL contiene los dos
  filtros y la paginación vuelve a la página 1.

### UI-07.2 Bloquear con motivo
- **Dado** una cuenta activa
- **Cuando** el administrador pulsa «Bloquear», escribe «Actividad sospechosa» y confirma
- **Entonces** aparece «Cuenta bloqueada.» y la fila muestra «Bloqueada sin vencimiento».

### UI-07.3 Bloquear sin motivo *(caso borde)*
- **Dado** el diálogo de bloqueo abierto
- **Cuando** el administrador confirma con el motivo vacío o solo con espacios
- **Entonces** no se envía nada y el campo muestra «Escribe el motivo del bloqueo.».

### UI-07.4 Bloquear al último administrador *(caso borde)*
- **Dado** que solo hay un `ADMIN_SISTEMA` activo y otro administrador lo intenta bloquear (por ejemplo, desde una pestaña desactualizada)
- **Cuando** confirma
- **Entonces** el diálogo muestra el mensaje de `ADMINISTRADOR_PROTEGIDO` y la
  cuenta sigue activa.

### UI-07.5 Fila propia *(caso borde)*
- **Dado** el administrador en el listado
- **Cuando** encuentra su propia cuenta
- **Entonces** no ve «Bloquear» ni «Dar de baja» en esa fila.

### UI-07.6 Reactivar una cuenta inactiva
- **Dado** una cuenta «Inactiva»
- **Cuando** el administrador la reactiva
- **Entonces** la fila pasa a «Activa».

### UI-07.7 Búsqueda sin coincidencias
- **Dado** el listado
- **Cuando** busca `zzz@`
- **Entonces** ve «Ninguna cuenta coincide con los filtros.» y el botón «Quitar filtros».

### UI-07.8 Estado cambiado por otro administrador *(caso borde)*
- **Dado** que otro administrador dio de baja una cuenta mientras esta pantalla estaba abierta
- **Cuando** este intenta bloquearla
- **Entonces** ve «Esta cuenta no se puede bloquear en su estado actual.» y,
  al cerrar el diálogo, el listado se recarga.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- La tabla usa `<table>` con encabezados `<th scope="col">` y un `<caption>`
  que resume los filtros aplicados.
- Las acciones de cada fila están en un menú con la etiqueta «Acciones para
  {correo}», navegable con teclado.
- Los diálogos atrapan el foco, se cierran con Escape y devuelven el foco al
  menú de la fila.
- El cambio de resultados se anuncia: «{total} cuentas encontradas».
- En 390 px la tabla pasa a tarjetas, una por cuenta, con el estado como
  primera línea.

---

## Fuera de alcance — ¿qué NO hará?

- Último acceso, fecha de alta u otras columnas que la API no devuelve.
- Buscar por nombre: el contrato solo busca por fragmento de correo.
- Acciones masivas sobre varias cuentas.
- Asignar o revocar roles: se hace en el detalle (FRONT-08).
- Bloqueos con fecha de fin elegida por el administrador (fuera de alcance de SPEC-15).

---

## Huecos detectados en el backend

- **H-09** — Las etiquetas de los roles salen de FRONT-00 §4 porque `GET /roles`
  no admite el token de un usuario.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Las acciones de cada fila dependen del estado y de los permisos
- [ ] El estado se distingue por texto, sin depender del color
- [ ] Bloquear exige motivo de hasta 500 caracteres
- [ ] Los filtros sobreviven a una recarga
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
