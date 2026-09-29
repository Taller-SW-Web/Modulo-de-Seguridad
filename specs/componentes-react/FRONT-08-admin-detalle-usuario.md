# FRONT-08 — Panel de administración: detalle de usuario (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-08-admin-detalle-usuario.md` |
| Specs de backend | SPEC-11 (roles), SPEC-13 (auditoría), SPEC-15 (bloqueo) · y SPEC-03, SPEC-04, SPEC-16 |
| Responsable | Por asignar |

---

## Objetivo

Un `ADMIN_SISTEMA` abre una cuenta concreta (desde FRONT-07 o tras crearla en
FRONT-11) para ver sus datos, gestionar sus roles, entender **por qué y hasta
cuándo** está bloqueada, revisar sus intentos de acceso y actuar. Atiende sobre
todo a HU-15.2.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir | `GET /api/v1/usuarios/{id}` | SPEC-03, SPEC-15 (RF-15.6) |
| Asignar rol | `POST /api/v1/usuarios/{id}/roles` con `rol` | SPEC-11 |
| Revocar rol | `DELETE /api/v1/usuarios/{id}/roles/{rol}` | SPEC-11 |
| Pestaña «Accesos correctos» | `GET /api/v1/auditoria?objetivoUsuarioId={id}&accion=SESION_INICIADA` | SPEC-13 |
| Pestaña «Accesos fallidos» | `GET /api/v1/auditoria?objetivoUsuarioId={id}&accion=SESION_FALLIDA` | SPEC-13 |
| Pestaña «Bloqueos» | `GET /api/v1/auditoria?objetivoUsuarioId={id}&accion=CUENTA_BLOQUEADA` | SPEC-13 |
| Bloquear/desbloquear/baja/reactivar | los de FRONT-07 | SPEC-15, SPEC-04 |
| Guardar atributos de vendedor | `PATCH /api/v1/usuarios/{id}/atributos` | SPEC-16 (RF-16.8) |

---

## Permisos (SPEC-11)

| Elemento | Permiso |
|---|---|
| La pantalla | `usuario.ver` |
| Asignar y revocar roles | `rol.asignar` |
| Pestañas de historial y «Ver en auditoría» | `auditoria.ver` |
| Bloquear / desbloquear | `cuenta.bloquear` |
| Dar de baja / reactivar | `usuario.desactivar` / `usuario.reactivar` |
| Editar atributos de vendedor | `atributos.editar` |

**El historial se carga solo al abrir su pestaña** (cada consulta se audita como
`AUDITORIA_CONSULTADA`, RF-13.4).

---

## Archivos propuestos

```
frontend/src/pantallas/admin-detalle/
├── AdminDetallePage.tsx          # orquesta secciones y carga
├── CabeceraCuenta.tsx            # nombre, correo, estado + acciones por estado
├── SeccionDatosCuenta.tsx        # nombres, apellidos, celular, correo, documento
├── SeccionBloqueo.tsx            # tipo, vencimiento, motivo (si manual)
├── SeccionRoles.tsx              # roles asignados + «Asignar rol» + diálogos
├── SeccionAtributosVendedor.tsx  # código, tienda, fecha de ingreso (VENDEDOR)
├── HistorialTabs.tsx             # 3 pestañas con carga bajo demanda
└── useDetalleUsuario.ts          # carga cuenta, roles, historial, acciones
```

Reutiliza de FRONT-00 y de FRONT-07: `api`, `errores`, `sesion` (permisos),
`EstadoCuentaBadge`, `DialogoBloqueo`, `DialogoConfirmacion`, textos de acción.

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `AdminDetallePage` | Carga `/usuarios/{id}`; maneja estados y permisos |
| `CabeceraCuenta` | `nombreCompleto`, correo, estado y botones de acción (misma tabla que FRONT-07) |
| `SeccionDatosCuenta` | Nombres, apellidos, celular (Verificado/Sin verificar), correo (Verificado), tipo y `documentoEnmascarado` |
| `SeccionBloqueo` | Solo si `BLOQUEADO`: tipo («Automático, por intentos fallidos»/«Manual»), vencimiento («Hasta dd/mm/aaaa hh:mm»/«Sin vencimiento») y motivo (si manual) |
| `SeccionRoles` | Rol asignado + «Revocar»; control «Asignar rol» con los roles del catálogo que la cuenta no tiene |
| `SeccionAtributosVendedor` | Solo si `VENDEDOR`: código, tienda, fecha de ingreso editables (H-01) |
| `HistorialTabs` | 3 pestañas WAI-ARIA; carga bajo demanda; tabla paginada 20 en 20, más reciente primero; enlace «Ver todo en auditoría» a FRONT-14 |
| `useDetalleUsuario` | Carga cuenta/roles/historial; acciones de rol y estado |

Las pestañas separan por `accion` porque `/auditoria` filtra una sola (H-07).

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Cargando | al abrir | esqueleto de cabecera y datos |
| Datos cargados | `200` | secciones |
| No existe | `404 NO_ENCONTRADO` | «No encontramos esa cuenta.» + «Volver al listado» |
| Sin permiso | falta `usuario.ver` o `403` | «No tienes permiso para ver esta página.» |
| Historial cargando | abrir pestaña | esqueleto en la tabla |
| Historial vacío | `registros: []` | «No hay registros de este tipo en los últimos 90 días.» |
| Confirmando cambio de rol | «Asignar»/«Revocar» | diálogo |
| Rol cambiado | `200` asignar / `204` revocar | aviso breve + recarga `/usuarios/{id}` |
| Acción de estado | bloquear/desbloquear/baja/reactivar | igual que FRONT-07; recarga el detalle |

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Asignar rol | solo roles del catálogo que la cuenta no tiene | — |
| Revocar `ADMIN_SISTEMA` propio | el botón no aparece | — |
| Motivo de bloqueo | igual que FRONT-07 | igual que FRONT-07 |
| Fecha de ingreso (vendedor) | fecha válida | «Escribe una fecha válida.» |

---

## Textos y mensajes

| Acción | Texto |
|---|---|
| Asignar rol | «¿Asignar el rol {rol} a {correo}? Se cerrarán sus sesiones y tendrá que volver a iniciar sesión para usar el nuevo acceso.» |
| Revocar rol | «¿Quitar el rol {rol} a {correo}? Se cerrarán sus sesiones.» |
| Revocar el único rol | Además: «La cuenta quedará sin ningún rol y no podrá usar ninguna función protegida.» |
| Asignar rol de gestión | Además: «Con este rol, el segundo factor será obligatorio para esta cuenta.» (RF-10.1) |

| Código | Mensaje |
|---|---|
| `200`/`204` roles | «Rol asignado.» / «Rol revocado.» |
| `ADMINISTRADOR_PROTEGIDO` | «No puedes hacer esto con tu propia cuenta ni con el último administrador del sistema activo.» |
| `VALIDACION` (rol) | «Ese rol no existe.» |
| `ATRIBUTO_NO_APLICABLE` | «Ese dato no aplica a una cuenta con estos roles.» |
| `NO_ENCONTRADO` | «Esa cuenta ya no existe.» |
| bloqueo/baja/reactivación | los de FRONT-07 |
| `SCOPE_INSUFICIENTE` / `NO_DISPONIBLE` | FRONT-00 §2 |

Historial: `EXITO` → «Correcto», `FALLO` → «Fallido». El agente se resume
(«Chrome en Windows») con el texto completo en un `title`.

---

## Accesibilidad

- Pestañas WAI-ARIA (`role="tablist"`, flechas, `aria-selected`).
- La sección Bloqueo se anuncia como región «Bloqueo activo».
- Diálogos igual que FRONT-07.
- 390 px: secciones apiladas; tablas del historial → listas.

## Fuera de alcance

Desactivar el segundo factor de otra cuenta; ver documento/contraseña completos;
cambiar el correo de otra cuenta; ver/editar direcciones del cliente; editar
nombres/apellidos/celular de otra cuenta (el wireframe no lo incluye).

## Huecos detectados

- **H-01**: `Usuario` no devuelve `codigoVendedor`, `tienda` ni `fechaIngreso`.
- **H-07**: `/auditoria` filtra una sola `accion`; SPEC-12 no fija `detalle` de
  `CUENTA_BLOQUEADA`. La pestaña «Bloqueos» muestra fecha, IP y actor, y el
  motivo solo si aparece en `detalle`.
- **H-09**: la lista de roles para asignar es el enum `CodigoRol` con etiquetas
  de FRONT-00 §4.
