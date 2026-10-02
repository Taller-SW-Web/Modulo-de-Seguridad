# FRONT-12 — Desbloqueo con el enlace del correo (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-12-desbloqueo-con-enlace.md` |
| Specs de backend | SPEC-14 |
| Wireframe | Sin wireframe — pendiente |
| Responsable | Por asignar |

---

## Objetivo

Cuando una cuenta sufre un bloqueo automático, su titular recibe un correo con
un enlace de un solo uso, válido 30 minutos (RF-14.8, RF-14.11). Esta pantalla es
el destino: levanta el bloqueo sin esperar a que venza ni a un administrador
(HU-14.2).

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al pulsar «Desbloquear mi cuenta» | `POST /api/v1/auth/desbloquear` con `token` | SPEC-14 |

**El desbloqueo no se lanza al abrir la página.** Los filtros de correo abren los
enlaces para analizarlos; consumir el token al cargar permitiría que un robot lo
gastara antes que la persona. Se requiere un botón explícito.

---

## Archivos propuestos

```
frontend/src/pantallas/desbloqueo/
├── DesbloqueoPage.tsx     # estados; lee el token de la URL (y lo quita)
└── useDesbloqueo.ts       # consume el token solo al pulsar el botón
```

Reutiliza de FRONT-00: `api`, `errores` y §1.5 (token fuera de la URL al cargar).

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `DesbloqueoPage` | Título, texto explicativo y botón; estados de resultado |
| `useDesbloqueo` | Lee el token (memoria), llama `api.desbloquear` solo al pulsar |

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | abre con token | «Desbloquear tu cuenta» + texto + botón «Desbloquear mi cuenta» |
| Enlace incompleto | URL sin token | «enlace no válido» + enlace a FRONT-05 |
| Cargando | tras pulsar | «Desbloqueando…» |
| Éxito | `204` | mensaje + «Iniciar sesión» + recomendación de cambiar contraseña |
| Enlace vencido | `410 TOKEN_DESBLOQUEO_EXPIRADO` | mensaje + «Restablecer mi contraseña» (FRONT-05) |
| Enlace no válido | `401 TOKEN_DESBLOQUEO_INVALIDO` | mensaje + «Restablecer mi contraseña» |
| Error servicio | `503`/sin red | FRONT-00 §2; el botón vuelve a estar disponible |

---

## Validaciones del cliente

No hay campos. Si falta el token, se muestra «Enlace incompleto» sin llamar al
backend.

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| Inicial | «Bloqueamos tu cuenta después de varios intentos fallidos de inicio de sesión. Pulsa el botón para desbloquearla ahora.» |
| `204` | «Tu cuenta está desbloqueada. Ya puedes iniciar sesión. Si no fuiste tú quien intentó entrar, te recomendamos cambiar tu contraseña.» |
| `TOKEN_DESBLOQUEO_EXPIRADO` | «Este enlace venció: los enlaces de desbloqueo duran 30 minutos. Puedes recuperar el acceso restableciendo tu contraseña.» |
| `TOKEN_DESBLOQUEO_INVALIDO` | «Este enlace ya no es válido: puede que ya se haya usado o que haya uno más reciente. Puedes recuperar el acceso restableciendo tu contraseña.» |
| `NO_DISPONIBLE` | FRONT-00 §2 |

`TOKEN_DESBLOQUEO_INVALIDO` también llega cuando un admin bloqueó después
(ESC-14.14); la pantalla no lo menciona (el titular de un bloqueo manual ya
recibió su propio correo, RF-15.7).

---

## Accesibilidad

- El resultado recibe el foco y se anuncia.
- Una columna en 390 px, botón de ancho completo.

## Fuera de alcance

Levantar un bloqueo manual (solo admin, SPEC-15); decir si el bloqueo vence o
cuánto falta; pedir un enlace de desbloqueo nuevo (no hay endpoint; alternativa
FRONT-05).

## Hueco detectado

- **H-14**: no hay canal de soporte definido para un bloqueo manual.
