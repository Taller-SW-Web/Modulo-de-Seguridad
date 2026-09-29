# FRONT-08 — Panel de administración: detalle de usuario

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-11, SPEC-13, SPEC-15 (y SPEC-03, SPEC-04, SPEC-16) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 8 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un `ADMIN_SISTEMA` abre una cuenta concreta —desde el listado (FRONT-07) o tras
crearla (FRONT-11)— para ver sus datos, gestionar sus roles, entender por qué y
hasta cuándo está bloqueada, revisar sus intentos de acceso y actuar: bloquear,
desbloquear, dar de baja o reactivar. Atiende sobre todo a la HU-15.2: «ver por
qué y hasta cuándo está bloqueada una cuenta y poder desbloquearla».

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir | `GET /api/v1/usuarios/{id}` | SPEC-03, SPEC-15 (RF-15.6) |
| Al asignar un rol | `POST /api/v1/usuarios/{id}/roles` con `rol` | SPEC-11 |
| Al revocar un rol | `DELETE /api/v1/usuarios/{id}/roles/{rol}` | SPEC-11 |
| Al abrir la pestaña de accesos correctos | `GET /api/v1/auditoria?objetivoUsuarioId={id}&accion=SESION_INICIADA` | SPEC-13 |
| Al abrir la pestaña de accesos fallidos | `GET /api/v1/auditoria?objetivoUsuarioId={id}&accion=SESION_FALLIDA` | SPEC-13 |
| Al abrir la pestaña de bloqueos | `GET /api/v1/auditoria?objetivoUsuarioId={id}&accion=CUENTA_BLOQUEADA` | SPEC-13 |
| Bloquear, desbloquear, dar de baja, reactivar | Los mismos de FRONT-07 | SPEC-15, SPEC-04 |
| Al guardar atributos de vendedor | `PATCH /api/v1/usuarios/{id}/atributos` | SPEC-16 (RF-16.8) |

### Permisos

| Elemento | Permiso (SPEC-11) |
|---|---|
| La pantalla | `usuario.ver` |
| Asignar y revocar roles | `rol.asignar` |
| Pestañas de historial y enlace «Ver en auditoría» | `auditoria.ver` |
| Bloquear y desbloquear | `cuenta.bloquear` |
| Dar de baja / reactivar | `usuario.desactivar` / `usuario.reactivar` |
| Editar atributos de vendedor | `atributos.editar` |

**El historial se carga solo al abrir su pestaña.** Cada consulta a la
auditoría queda, a su vez, auditada como `AUDITORIA_CONSULTADA` (RF-13.4); abrir
un detalle para ver un correo no debe dejar tres registros de consulta.

---

## Secciones

| Sección | Contenido |
|---|---|
| Cabecera | `nombreCompleto`, correo, estado (texto de FRONT-07) y botones de acción según el estado (misma tabla que FRONT-07) |
| Datos de la cuenta | Nombres, apellidos, celular con «Verificado» / «Sin verificar», correo con «Verificado», tipo de documento y `documentoEnmascarado` |
| Bloqueo (solo si `estado = BLOQUEADO`) | Tipo («Automático, por intentos fallidos» o «Manual»), vencimiento («Hasta dd/mm/aaaa hh:mm» o «Sin vencimiento») y, si es manual, el `motivo` |
| Roles | Cada rol asignado con su botón «Revocar», y el control «Asignar rol» con los roles del catálogo que la cuenta no tiene |
| Atributos de vendedor (solo si tiene `VENDEDOR`) | Código de vendedor, tienda y fecha de ingreso, editables (HU-16.5). Ver H-01 |
| Historial | Tres pestañas: «Accesos correctos», «Accesos fallidos», «Bloqueos». Cada una es una tabla con fecha, IP, agente de usuario y resultado, paginada de 20 en 20 y de más reciente a más antiguo. Enlace «Ver todo en auditoría» a FRONT-14 con `objetivoUsuarioId` ya puesto |

Las pestañas separan por acción porque `/auditoria` filtra una sola `accion`
por consulta (H-07): así cada tabla pagina con su propio `total` sin mezclar
dos listas en el cliente.

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Cargando | Al abrir | Esqueleto de la cabecera y los datos |
| Datos cargados | `200` de `/usuarios/{id}` | Las secciones anteriores |
| No existe | `404 NO_ENCONTRADO` | «No encontramos esa cuenta.» y enlace «Volver al listado» |
| Sin permiso | Falta `usuario.ver` o `403` | «No tienes permiso para ver esta página.» |
| Historial cargando | Abre una pestaña | Filas de esqueleto en la tabla |
| Historial vacío | `registros: []` | «No hay registros de este tipo en los últimos 90 días.» (retención de SPEC-12) |
| Confirmando cambio de rol | Pulsa «Asignar» o «Revocar» | Diálogo de confirmación (ver textos) |
| Rol cambiado | `200` al asignar / `204` al revocar | Aviso breve y se recarga `/usuarios/{id}` |
| Acción de estado | Bloquear, desbloquear, baja, reactivar | Igual que en FRONT-07; al terminar se recarga el detalle |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Asignar rol | Solo se ofrecen roles del catálogo cerrado que la cuenta no tiene | — | RF-11.1, RF-11.3 |
| Revocar `ADMIN_SISTEMA` sobre la propia cuenta | El botón no aparece | — | RF-11.7 |
| Motivo del bloqueo | Igual que FRONT-07 | Igual que FRONT-07 | RF-15.1 |
| Fecha de ingreso (vendedor) | Fecha válida | «Escribe una fecha válida.» | `AtributosUsuario.fechaIngreso` |

---

## Textos y mensajes

| Acción | Texto del diálogo |
|---|---|
| Asignar rol | «¿Asignar el rol {rol} a {correo}? Se cerrarán sus sesiones y tendrá que volver a iniciar sesión para usar el nuevo acceso.» |
| Revocar rol | «¿Quitar el rol {rol} a {correo}? Se cerrarán sus sesiones.» |
| Revocar el único rol | Además: «La cuenta quedará sin ningún rol y no podrá usar ninguna función protegida.» |
| Asignar un rol de gestión | Además: «Con este rol, el segundo factor será obligatorio para esta cuenta.» (RF-10.1) |

| Código del backend | Mensaje en pantalla |
|---|---|
| `200` / `204` de roles | «Rol asignado.» / «Rol revocado.» |
| `ADMINISTRADOR_PROTEGIDO` | «No puedes hacer esto con tu propia cuenta ni con el último administrador del sistema activo.» |
| `VALIDACION` (rol) | «Ese rol no existe.» (no debería ocurrir: el control solo ofrece roles válidos) |
| `ATRIBUTO_NO_APLICABLE` | «Ese dato no aplica a una cuenta con estos roles.» |
| `NO_ENCONTRADO` | «Esa cuenta ya no existe.» |
| Los de bloqueo, baja y reactivación | Los de FRONT-07 |
| `SCOPE_INSUFICIENTE`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

Traducción de `resultado` en el historial: `EXITO` → «Correcto», `FALLO` →
«Fallido». El agente de usuario se resume («Chrome en Windows») con el texto
completo en un `title`.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-08.1 Ver por qué está bloqueada
- **Dado** una cuenta con bloqueo manual y motivo «Actividad sospechosa»
- **Cuando** el administrador abre su detalle
- **Entonces** la sección Bloqueo muestra «Manual», «Sin vencimiento» y el motivo.

### UI-08.2 Bloqueo automático con vencimiento
- **Dado** una cuenta con bloqueo automático que vence a las 10:32
- **Cuando** el administrador abre su detalle
- **Entonces** ve «Automático, por intentos fallidos», «Hasta … 10:32» y el
  botón «Desbloquear».

### UI-08.3 Asignar un rol
- **Dado** una cuenta con `VENDEDOR`
- **Cuando** el administrador asigna `GESTOR_DESPACHO` y confirma
- **Entonces** el diálogo avisó del cierre de sesiones y del segundo factor
  obligatorio, y la sección Roles muestra los dos.

### UI-08.4 Quitarse el rol de administrador *(caso borde)*
- **Dado** un `ADMIN_SISTEMA` que abre su propio detalle
- **Cuando** mira sus roles
- **Entonces** `ADMIN_SISTEMA` no tiene botón «Revocar».

### UI-08.5 Historial bajo demanda
- **Dado** el detalle recién abierto
- **Cuando** el administrador no abre ninguna pestaña del historial
- **Entonces** la SPA no ha llamado a `/auditoria`.

### UI-08.6 Accesos fallidos
- **Dado** una cuenta con fallos recientes
- **Cuando** el administrador abre «Accesos fallidos»
- **Entonces** ve cada intento con fecha en hora de Lima, IP, agente y «Fallido».

### UI-08.7 Sin permiso de auditoría *(caso borde)*
- **Dado** un administrador con `usuario.ver` pero sin `auditoria.ver`
- **Cuando** abre el detalle
- **Entonces** no ve las pestañas del historial.

### UI-08.8 Cuenta inexistente *(caso borde)*
- **Dado** una URL con un identificador que no existe
- **Cuando** el administrador la abre
- **Entonces** ve «No encontramos esa cuenta.» con el enlace al listado.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- Las pestañas siguen el patrón WAI-ARIA *tabs* (`role="tablist"`, flechas
  para moverse, `aria-selected`).
- La sección Bloqueo se anuncia como región con título «Bloqueo activo».
- Los diálogos, igual que en FRONT-07.
- En 390 px las secciones se apilan y las tablas del historial pasan a lista.

---

## Fuera de alcance — ¿qué NO hará?

- Desactivar el segundo factor de otra cuenta (fuera de alcance de SPEC-10).
- Ver el documento completo o la contraseña.
- Cambiar el correo de otra cuenta: el cambio de correo es solo del titular (RF-16.6).
- Ver o editar las direcciones del cliente: el wireframe no lo pide.
- Editar nombres, apellidos o celular de otra cuenta: RF-16.8 lo permite,
  pero el wireframe no lo incluye. Se añade si el equipo lo decide.

---

## Huecos detectados en el backend

- **H-01** — `Usuario` no devuelve `codigoVendedor`, `tienda` ni
  `fechaIngreso`: los campos de vendedor se muestran vacíos y solo sirven para
  escribir un valor nuevo.
- **H-07** — `/auditoria` filtra una sola `accion` por consulta, y SPEC-12 no
  fija qué lleva `detalle` en `CUENTA_BLOQUEADA` (tipo, motivo, vencimiento).
  La pestaña «Bloqueos» muestra fecha, IP y actor, y el motivo solo si aparece
  en `detalle`.
- **H-09** — La lista de roles para asignar es el enum `CodigoRol` con las
  etiquetas de FRONT-00 §4.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] El bloqueo muestra tipo, vencimiento y, si es manual, el motivo
- [ ] Cada cambio de rol avisa del cierre de sesiones
- [ ] El historial se carga solo al abrir su pestaña
- [ ] Cada elemento depende de su permiso
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
