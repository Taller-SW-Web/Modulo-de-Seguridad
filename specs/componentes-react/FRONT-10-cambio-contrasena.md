# FRONT-10 — Cambiar contraseña (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-10-cambio-contrasena.md` |
| Specs de backend | SPEC-07 |
| Wireframe | Sin wireframe — pendiente |
| Responsable | Por asignar |

---

## Objetivo

Un usuario con sesión cambia su contraseña confirmando la actual (RF-07.8,
HU-07.2). Llega desde la sección Seguridad de *Mi cuenta* (FRONT-06) o desde
*Actividad reciente* (FRONT-13) si ve un acceso que no reconoce. **No** sirve a
quien olvidó la contraseña ni a quien la tiene caducada: esos usan FRONT-05.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir (medidor) | `GET /api/v1/password/politica` | SPEC-07 |
| Al guardar | `POST /api/v1/password/cambiar` (`contrasenaActual`, `nuevaContrasena`) | SPEC-07 |

---

## Archivos propuestos

```
frontend/src/pantallas/cambiar-contrasena/
├── CambiarContrasenaPage.tsx   # formulario + medidor
└── useCambiarContrasena.ts     # estado, validación, envío
```

Reutiliza de FRONT-00: `PasswordStrengthMeter`, `usePoliticaPassword`, `api`,
`errores`, `sesion` (para los datos personales del medidor).

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `CambiarContrasenaPage` | Campos actual/nueva/repetir + medidor; «Cambiar contraseña» y «Cancelar» |
| `useCambiarContrasena` | Valida, llama `api.cambiarContrasena`, ramifica `CREDENCIALES_INVALIDAS` sin cerrar sesión |

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | al abrir | 3 campos + medidor + botones |
| Errores validación | cliente o `422` | errores bajo campos, medidor actualizado |
| Cargando | submit | «Cambiando…» |
| Actual incorrecta | `401 CREDENCIALES_INVALIDAS` | mensaje bajo «Contraseña actual», que se vacía y recibe foco; **sesión sigue abierta** |
| Éxito | `204` | vuelve a *Mi cuenta* con aviso; la sesión actual sigue abierta |

---

## Validaciones del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Contraseña actual | obligatoria | «Escribe tu contraseña actual.» | esquema |
| Contraseña nueva | medidor informativo | FRONT-00 §3 | RF-07.1–07.4 |
| Contraseña nueva | datos personales orientativa (nombres, apellidos, correo de `/auth/me`) | «No puede contener tu nombre, apellido ni correo.» | RF-07.3 |
| Contraseña nueva | distinta de la actual | «La contraseña nueva debe ser distinta de la actual.» | RF-07.4 |
| Repetir | igual; no se envía | «Las contraseñas no coinciden.» | interfaz |

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `204` | «Tu contraseña se cambió. Cerramos tus sesiones en otros dispositivos; esta sigue abierta. Te enviamos un aviso por correo.» |
| `CREDENCIALES_INVALIDAS` | «La contraseña actual no es correcta.» |
| `POLITICA_INCUMPLIDA` | FRONT-00 §3 |
| `TOKEN_INVALIDO` | renovación (FRONT-00 §1.2) y reintento |
| `VALIDACION` / `NO_DISPONIBLE` | FRONT-00 §2 |

**`401` no siempre es sesión vencida**: aquí `CREDENCIALES_INVALIDAS` es
«contraseña actual incorrecta». No renovar ni echar al usuario.

---

## Accesibilidad

- `autocomplete="current-password"` en la actual y `new-password` en las otras.
- Botón mostrar/ocultar en cada campo.

## Fuera de alcance

Recuperar contraseña olvidada/caducada (FRONT-05); cambiar correo (FRONT-06);
elegir qué sesiones cerrar (el backend cierra todas las demás).
