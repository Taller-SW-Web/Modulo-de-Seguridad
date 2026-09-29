# FRONT-00 — Base compartida de la SPA

> No es una pantalla: es lo que heredan todas. Fija sesión, manejo de errores
> transversales y el medidor de fuerza de contraseña. Fuente: `specs/front/FRONT-00-comportamiento-comun.md`.

| Campo | Valor |
|---|---|
| Specs de backend | SPEC-06, SPEC-07, SPEC-11 |
| Wireframe | No aplica |

---

## 1. Sesión (SPEC-06)

### 1.1 Qué se guarda y dónde

| Dato | Origen | Dónde |
|---|---|---|
| `accessToken` | `SesionIniciada` | Solo en memoria. Nunca en `localStorage`, URL ni consola |
| `refreshToken` | `SesionIniciada` | En memoria (decisión H-11 pendiente) |
| `usuario` (`id`, `nombreCompleto`, `correo`, `roles`) | `SesionIniciada` | En memoria, junto a la sesión |
| `permisos` | claim `permisos` | Se leen del token decodificado, sin verificar firma: solo ocultan/muestran opciones |
| `challengeToken` | `DesafioMfa` | En memoria, mientras dura el desafío (FRONT-04) |

### 1.2 Renovación

- Renovar con `POST /auth/refresh` cuando el access token esté por vencer o al
  recibir `401 TOKEN_INVALIDO`.
- **Una sola renovación en vuelo**: varias peticiones con `401` esperan la misma
  renovación. La rotación reutilizada revoca la sesión entera (RF-06.3).
- Al recibir `401 REFRESCO_INVALIDO`: borrar sesión local y llevar a FRONT-01
  con «Tu sesión terminó. Inicia sesión de nuevo.».

### 1.3 Cierre de sesión

- `POST /auth/logout` con el `refreshToken`, y **borrar la sesión local pase lo
  que pase**. Después, FRONT-01 con «Cerraste sesión.».

### 1.4 Rutas protegidas

| Tipo | Pantallas | Regla |
|---|---|---|
| Pública | 01, 02, 03, 04, 05, 12 | Sin sesión. Con sesión, 01 y 02 redirigen al destino |
| Autenticada | 06, 09, 10, 13 | Sin sesión → 01 recordando la ruta |
| Administración | 07, 08, 11, 14 | Además exige permiso; sin él, «No tienes permiso…» y no llama al endpoint |

- `volver` solo acepta rutas internas relativas (empieza por `/` y no por `//`).

### 1.5 Enlaces por correo

- FRONT-03, 05 y 12 reciben un token en la URL; al cargar se **quita de la barra
  de direcciones** (`history.replaceState`).
- Esas pantallas se sirven con `Referrer-Policy: no-referrer`.

---

## 2. Errores transversales

Ramificar por `code`, nunca por `detail`. Mensajes de la interfaz:

| Código | Acción | Mensaje |
|---|---|---|
| `VALIDACION` | Cada `errores[]` bajo su `campo` | El `mensaje` de cada error; si falta, «Revisa este campo.» |
| `TOKEN_INVALIDO` (autenticada) | Renovar (§1.2) | — |
| `SCOPE_INSUFICIENTE` | No reintentar | «No tienes permiso para realizar esta acción.» |
| `NO_ENCONTRADO` | Según pantalla | «No encontramos lo que buscas.» |
| `DEMASIADAS_SOLICITUDES` | Deshabilitar la acción | Según pantalla |
| `NO_DISPONIBLE` | Reintentar | «El servicio no está disponible…» |
| Sin respuesta | Reintentar, no reenvía formularios | «No pudimos conectar…» |
| Desconocido | Error genérico + `instance` | «Algo salió mal. Intenta de nuevo.» |

Solo `TOKEN_INVALIDO` dispara renovación; `CREDENCIALES_INVALIDAS` y
`CODIGO_INVALIDO` no lo son.

---

## 3. Medidor de fuerza de contraseña

Lo usan FRONT-02, 05, 10 y 11. Se construye con `GET /password/politica`, nunca
con reglas en el código. Tres estados por regla (texto e icono, no solo color):

| Estado | Cuándo |
|---|---|
| Cumple | La SPA puede comprobarla y se cumple |
| No cumple | La SPA puede comprobarla y no se cumple |
| Se comprueba al guardar | No la puede comprobar la SPA |

| Campo de la política | Regla | ¿La comprueba la SPA? |
|---|---|---|
| `longitudMinima` | «Al menos {n} caracteres» | Sí |
| `requiereMayuscula` | «Una letra mayúscula» | Sí |
| `requiereMinuscula` | «Una letra minúscula» | Sí |
| `requiereDigito` | «Un número» | Sí |
| `requiereCaracterEspecial` | «Un carácter especial…» | Sí |
| `rechazaComunes` | «Que no sea común» | No |
| `rechazaDatosPersonales` | «Que no contenga tu nombre…» | Orientativa |
| `historial` | «Que no sea de tus últimas {n}» | No |

- Si la política no carga: aviso y el formulario sigue funcionando.
- El medidor no bloquea el envío (el backend valida).
- `422 POLITICA_INCUMPLIDA` marca cada regla de `errores[]` como «No cumple».

---

## Estructura de archivos propuesta

```
frontend/src/
├── theme.ts                 # tema Mantine (sistema de diseño)
├── lib/
│   ├── tipos.ts             # tipos del contrato (Problem, SesionIniciada, …)
│   ├── errores.ts           # ApiError, NetworkError, mensajes transversales
│   ├── api.ts               # httpClient + endpoints
│   ├── sesion.ts            # store en memoria (access/refresh/usuario)
│   └── mascara.ts           # enmascarar correo (m***a@ejemplo.com)
├── hooks/
│   └── usePoliticaPassword.ts
├── componentes/
│   └── PasswordStrengthMeter.tsx
└── pantallas/ …             # una carpeta por pantalla
```
