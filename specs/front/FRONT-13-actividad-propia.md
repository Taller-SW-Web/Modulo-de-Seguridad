# FRONT-13 — Actividad reciente de mi cuenta

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-13 (RF-13.5), catálogo de acciones de SPEC-12 |
| **Wireframe** | Sin wireframe — pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Cualquier usuario con sesión revisa los eventos de seguridad de **su propia**
cuenta —inicios de sesión con fecha e IP, cambios de contraseña, bloqueos—
para detectar un acceso que no hizo (HU-13.2). Se llega desde la sección
Seguridad de *Mi cuenta* (FRONT-06).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir y al cambiar de página | `GET /api/v1/auth/me/actividad?pagina&tamano` | SPEC-13 |

No necesita ningún permiso: se autoriza por titularidad (RF-11.10).

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Cargando | Al abrir o cambiar de página | Filas de esqueleto |
| Con registros | `200` con `registros` | Lista de más reciente a más antiguo: fecha (hora de Lima), qué pasó, resultado, IP y dispositivo (agente resumido). Paginación de 20 en 20 |
| Vacío | `registros: []` | «No hay actividad registrada en los últimos 90 días.» |
| Error | `503` o sin conexión | Mensaje de FRONT-00 §2 y «Reintentar» |

Arriba de la lista, siempre: «¿Ves algo que no reconoces? [Cambia tu
contraseña] y [activa el segundo factor].» (a FRONT-10 y FRONT-09).

---

## Validaciones del lado del cliente

No hay campos.

---

## Textos y mensajes

Traducción de las acciones del catálogo de SPEC-12 que pueden aparecer en la
propia cuenta. Una acción que no esté en la tabla se muestra con su código.

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
destacan con un icono de advertencia **y** la palabra «Atención», no solo con color.

La pantalla no muestra el `detalle` en bruto ni el `actorId`: son datos
internos. Si el actor es un administrador (`actorTipo = USUARIO` distinto del
titular), se añade «(por un administrador)».

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-13.1 Ver la actividad
- **Dado** un cliente que inició sesión ayer desde otra ciudad
- **Cuando** abre *Actividad reciente*
- **Entonces** ve «Inicio de sesión», la fecha en hora de Lima, la IP y el navegador.

### UI-13.2 Intentos fallidos *(caso borde)*
- **Dado** que alguien falló cinco veces la contraseña de la cuenta
- **Cuando** el titular abre la pantalla
- **Entonces** ve cinco «Intento de inicio de sesión fallido» y un «Cuenta
  bloqueada», destacados con «Atención».

### UI-13.3 Sin actividad
- **Dado** una cuenta sin eventos en 90 días
- **Cuando** se abre la pantalla
- **Entonces** ve «No hay actividad registrada en los últimos 90 días.».

### UI-13.4 Acción desconocida *(caso borde)*
- **Dado** que el backend añade una acción nueva al catálogo
- **Cuando** aparece en la lista
- **Entonces** se muestra con su código y la pantalla no falla.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- La lista es una tabla con encabezados en escritorio y tarjetas en 390 px.
- La paginación anuncia «Página N de M».

---

## Fuera de alcance — ¿qué NO hará?

- Filtros por fecha o por acción: `/auth/me/actividad` no los admite.
- Exportar la actividad.
- Cerrar sesiones concretas desde esta pantalla: no hay endpoint.
- Registros de otras cuentas (RF-13.5).

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Cada acción del catálogo tiene su texto, y una desconocida no rompe la pantalla
- [ ] Los eventos de riesgo se destacan con texto, no solo con color
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Existe un wireframe y coincide con la implementación
