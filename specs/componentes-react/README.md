# Componentes React — Módulo de Seguridad y Autenticación (G7)

Especificación de los componentes React de la SPA, derivada del análisis de las
specs de backend (`specs/`) y de interfaz (`specs/front/`). **No contiene
código**: describe la estructura, estados, hooks, validaciones y endpoints que
debe implementar cada pantalla. Es la guía de entrada para el desarrollo por
lotes.

| Lote | Pantalla (FRONT) | Specs de backend | Estado |
|---|---|---|---|
| — | [00 · Base compartida](FRONT-00-base-compartida.md) | 06, 07, 11 | Documentado |
| **1** | [01 · Inicio de sesión](FRONT-01-inicio-sesion.md) | 05, 07, 14, 09 | Documentado |
| **1** | [02 · Registro de cliente](FRONT-02-registro-cliente.md) | 01, 07 | Documentado |
| **1** | [03 · Verificación de correo](FRONT-03-verificacion-correo.md) | 02, 16 | Documentado |
| **1** | [04 · Desafío del código OTP](FRONT-04-desafio-otp.md) | 09, 07 | Documentado |
| **1** | [05 · Recuperar contraseña](FRONT-05-recuperar-contrasena.md) | 08, 07, 14 | Documentado |
| **2** | [06 · Mi cuenta](FRONT-06-mi-cuenta.md) | 16, 18 | Documentado |
| **2** | [07 · Panel admin: listado de usuarios](FRONT-07-admin-listado-usuarios.md) | 03, 04, 11, 15 | Documentado |
| **2** | [08 · Panel admin: detalle de usuario](FRONT-08-admin-detalle-usuario.md) | 11, 13, 15 | Documentado |
| **3** | [09 · Segundo factor](FRONT-09-segundo-factor.md) | 10 | Documentado (⛔ H-01, H-02) |
| **3** | [10 · Cambiar contraseña](FRONT-10-cambio-contrasena.md) | 07 | Documentado |
| **3** | [11 · Panel admin: alta de cuenta](FRONT-11-admin-alta-usuario.md) | 03, 07 | Documentado |
| **3** | [12 · Desbloqueo con enlace](FRONT-12-desbloqueo-con-enlace.md) | 14 | Documentado |
| **3** | [13 · Actividad reciente](FRONT-13-actividad-propia.md) | 13 | Documentado |
| **3** | [14 · Panel admin: auditoría](FRONT-14-admin-auditoria.md) | 13 | Documentado |

## Convenciones comunes (todas las pantallas)

- **Stack**: React 18 + Vite + TypeScript + Mantine. Tema en
  `src/theme.ts` según [`docs/sistema-diseño`](../docs/sistema-diseño).
- **Color de acción**: `signal` (índigo) en pantallas de autenticación; `orange`
  en el panel de administración y «Mi cuenta».
- **Errores**: se ramifica por `code`, nunca por `detail`. Ningún mensaje revela
  si una cuenta existe o está bloqueada.
- **Estado compartido en memoria**: `accessToken`, `usuario` y el
  `challengeToken` viven solo en memoria, nunca en `localStorage` ni en la URL.
- **Accesibilidad**: WCAG AA, foco visible, `aria-live` en cambios anunciados.

## Lotes

- **Lote 1** (este directorio): pantallas públicas de autenticación, FRONT-01 a
  FRONT-05, más la base compartida FRONT-00.
- **Lote 2**: FRONT-06 a FRONT-08 («Mi cuenta» y panel admin básico).
- **Lote 3**: FRONT-09 a FRONT-14 (segundo factor, auditoría y flujos por enlace).
