# FRONT-13 — Actividad reciente de mi cuenta (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-13-actividad-propia.md` |
| Specs de backend | SPEC-13 (RF-13.5) · catálogo de acciones de SPEC-12 |
| Wireframe | Sin wireframe — pendiente |
| Responsable | Por asignar |

---

## Objetivo

Cualquier usuario con sesión revisa los eventos de seguridad de **su propia**
cuenta —inicios de sesión con fecha e IP, cambios de contraseña, bloqueos— para
detectar un acceso que no hizo (HU-13.2). Se llega desde la sección Seguridad de
*Mi cuenta* (FRONT-06).

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir y al cambiar de página | `GET /api/v1/auth/me/actividad?pagina&tamano` | SPEC-13 |

No necesita permiso: se autoriza por titularidad (RF-11.10).

---

## Archivos propuestos

```
frontend/src/pantallas/actividad/
├── ActividadPage.tsx        # tabla + paginación + aviso de seguridad
├── TablaActividad.tsx       # fecha (Lima), acción, resultado, IP, dispositivo
├── etiquetasAccion.ts       # mapa accion → texto (catálogo SPEC-12)
└── useActividad.ts          # carga paginada
```

Reutiliza de FRONT-00: `api`, `errores`, `sesion`, `DataTable`.

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `ActividadPage` | Lista + aviso fijo «¿Ves algo que no reconoces? [Cambia tu contraseña] y [activa el segundo factor].» (a FRONT-10 y FRONT-09) |
| `TablaActividad` | Columnas: fecha (hora de Lima), qué pasó, resultado, IP, dispositivo (agente resumido) |
| `etiquetasAccion` | Traduce `accion` y `resultado`; acción desconocida se muestra con su código sin romper |

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Cargando | al abrir/cambiar página | filas de esqueleto |
| Con registros | `200` con `registros` | lista, más reciente primero; paginación 20 en 20 |
| Vacío | `registros: []` | «No hay actividad registrada en los últimos 90 días.» |
| Error | `503`/sin red | FRONT-00 §2 + «Reintentar» |

---

## Validaciones del cliente

No hay campos.

---

## Textos y mensajes

Etiquetas de las acciones del catálogo de SPEC-12 que pueden aparecer en la
propia cuenta (una acción no listada se muestra con su código):

| `accion` | Texto |
|---|---|
| `SESION_INICIADA` | Inicio de sesión |
| `SESION_FALLIDA` | Intento de inicio de sesión fallido |
| `SESION_CERRADA` | Cierre de sesión |
| `REFRESCO_REUTILIZADO` | Sesión cerrada por un uso sospechoso |
| `OTP_SOLICITADO` | Código de verificación enviado |
| `OTP_VERIFICADO` | Código de verificación correcto |
| `OTP_FALLIDO` | Código de verificación incorrecto |
| `MFA_ACTIVADO` / `MFA_DESACTIVADO` | Segundo factor activado / desactivado |
| `USUARIO_CREADO` | Cuenta creada |
| `USUARIO_VERIFICADO` | Correo verificado |
| `USUARIO_DESACTIVADO` / `USUARIO_REACTIVADO` | Cuenta dada de baja / reactivada |
| `CONTRASENA_CAMBIADA` | Contraseña cambiada |
| `RECUPERACION_SOLICITADA` | Enlace de recuperación solicitado |
| `CONTRASENA_RESTABLECIDA` | Contraseña restablecida |
| `ROL_ASIGNADO` / `ROL_REVOCADO` | Rol asignado / quitado |
| `CUENTA_BLOQUEADA` / `CUENTA_DESBLOQUEADA` | Cuenta bloqueada / desbloqueada |
| `ATRIBUTOS_ACTUALIZADOS` | Datos de perfil actualizados |
| `CORREO_CAMBIADO` | Correo cambiado |

| `resultado` | Texto |
|---|---|
| `EXITO` | Correcto |
| `FALLO` | Fallido |

`SESION_FALLIDA`, `REFRESCO_REUTILIZADO`, `OTP_FALLIDO` y `CUENTA_BLOQUEADA` se
destacan con icono de advertencia **y** la palabra «Atención», no solo color.

No se muestra `detalle` en bruto ni `actorId`. Si el actor es un administrador
(`actorTipo = USUARIO` distinto del titular), se añade «(por un administrador)».

---

## Accesibilidad

- Tabla con encabezados en escritorio y tarjetas en 390 px.
- La paginación anuncia «Página N de M».

## Fuera de alcance

Filtros por fecha/acción; exportar; cerrar sesiones concretas; registros de otras
cuentas (RF-13.5).
