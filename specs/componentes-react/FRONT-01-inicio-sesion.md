# FRONT-01 — Inicio de sesión (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-01-inicio-sesion.md` |
| Specs de backend | SPEC-05 (login), SPEC-07 (caducidad), SPEC-14 (bloqueo), SPEC-09 (deriva a MFA) |
| Responsable | Por asignar |

---

## Objetivo

Persona con cuenta (cliente, vendedor o personal de gestión) se identifica con
correo y contraseña. Deriva a FRONT-04 si hay segundo factor, a FRONT-05 si la
contraseña caducó, o al destino tras la sesión.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al enviar | `POST /api/v1/auth/login` | SPEC-05 |

---

## Archivos propuestos

```
frontend/src/pantallas/login/
├── LoginPage.tsx     # pantalla: estado global, avisos de llegada, navegación
└── useLogin.ts       # estado del form, envío, ramificación de la respuesta
```

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `LoginPage` | Renderiza campos (correo/contraseña), avisos de llegada y enlaces; orquesta los estados |
| `useLogin` | Controla `correo`/`contrasena`, `enviando`, error; llama `api.login` y ramifica |

- Reutiliza de FRONT-00: `httpClient`/`api`, `errores`, `sesion`.

---

## Estados de la pantalla

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | al abrir | Campos, botón «Iniciar sesión», enlaces «¿Olvidaste tu contraseña?», «Crear una cuenta», «¿No te llegó el correo de verificación?»; aviso de llegada si lo hay |
| Cargando | submit | Botón «Iniciando sesión…», campos solo lectura |
| Error credenciales | `401 CREDENCIALES_INVALIDAS` | Mensaje genérico; conserva correo, borra contraseña y le da foco |
| Contraseña caducada | `403 PASSWORD_CADUCADA` | Panel «Tu contraseña caducó» + botón «Restablecer mi contraseña» (a FRONT-05 con correo) |
| Error servicio | `503`/sin red | Mensaje FRONT-00 §2 |
| Éxito sin MFA | `200 SesionIniciada` | Guarda sesión (FRONT-00 §1.1) y navega al destino |
| Éxito con MFA | `200 DesafioMfa` | Navega a FRONT-04 pasando `challengeToken`/`canal`/`expiraEn` en memoria |

**No existe estado «bloqueada/inactiva/sin verificar»**: las tres responden
`401 CREDENCIALES_INVALIDAS` idéntico (RF-05.4, RF-14.6).

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Correo | obligatorio | «Escribe tu correo.» |
| Correo | formato email | «Escribe un correo válido, como nombre@dominio.com.» |
| Contraseña | obligatoria | «Escribe tu contraseña.» |

La contraseña **no** se valida contra la política al iniciar sesión.

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `CREDENCIALES_INVALIDAS` | «El correo o la contraseña no son correctos.» |
| `PASSWORD_CADUCADA` | panel «Tu contraseña caducó» |
| `VALIDACION` / `NO_DISPONIBLE` | FRONT-00 §2 |

Avisos de llegada (estado inicial): sesión revocada/vencida, cierre de sesión,
restablecimiento correcto, verificación correcta, desafío vencido.

---

## Casos borde clave

- **Doble clic**: una sola petición (botón deshabilitado al enviar).
- **Anti-enumeración**: contraseña incorrecta, correo inexistente y cuenta
  bloqueada/inactiva/sin verificar se ven **idénticos** (mismo mensaje, posición
  y aspecto). No hay contador de intentos ni aviso de bloqueo.
- **`challengeToken`** nunca en la URL.
- **Sin conexión**: conserva lo escrito, contraseña incluida.

---

## Accesibilidad

- Error con `role="alert"`.
- Botón mostrar/ocultar contraseña (`aria-pressed`, etiqueta «Mostrar contraseña»).
- `autocomplete="email"` y `autocomplete="current-password"`.
- Móvil 390 px: una columna, botón ancho completo.

## Fuera de alcance

Mostrar intentos restantes o estado de bloqueo; «Recordarme» (H-11); login con
Google/Facebook; CAPTCHA; el formulario de login de otros módulos.
