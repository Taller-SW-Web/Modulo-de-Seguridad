# FRONT-05 — Recuperar contraseña (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-05-recuperar-contrasena.md` |
| Specs de backend | SPEC-08 (recuperación), SPEC-07 (política), SPEC-14 (levanta bloqueo automático) |
| Responsable | Por asignar |

---

## Objetivo

Dos pasos con la misma spec:

- **5a · Pedir el enlace.** Quien olvidó su contraseña escribe su correo. Llega
  desde «¿Olvidaste tu contraseña?» (FRONT-01) o del panel de contraseña
  caducada (FRONT-01/04) con el correo ya escrito.
- **5b · Definir la contraseña nueva.** Quien abre el enlace escribe la nueva.
  Es también la salida de un bloqueo automático (RF-08.5), sin mencionarlo.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| 5a, al enviar correo | `POST /api/v1/password/recuperar` | SPEC-08 |
| 5b, al abrir (medidor) | `GET /api/v1/password/politica` | SPEC-07 |
| 5b, al guardar | `POST /api/v1/password/restablecer` (`token`, `nuevaContrasena`) | SPEC-08 |

---

## Archivos propuestos

```
frontend/src/pantallas/recuperacion/
├── RecuperacionPage.tsx    # decide 5a vs 5b según si hay token en la URL
├── PedirEnlace.tsx         # 5a: campo correo + «Enviar enlace»
├── DefinirContrasena.tsx   # 5b: nueva + repetir + medidor + «Guardar contraseña»
└── useRecuperacion.ts      # 5a (recuperar) y 5b (restablecer)
```

Reutiliza de FRONT-00: `PasswordStrengthMeter`, `usePoliticaPassword`, `api`,
`errores`, `sesion` (borrar al éxito) y §1.5 (token fuera de la URL).

---

## Estados

### 5a · Pedir el enlace

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | al abrir | correo (prellenado si viene de caducada), «Enviar enlace», «Volver a iniciar sesión» |
| Cargando | submit | «Enviando…» |
| Enviado | `202` | confirmación neutra, correo, dura 30 min, pedir otro invalida el anterior, «Volver» |
| Demasiadas solicitudes | `429` | mensaje; botón deshabilitado |
| Error servicio | `503`/sin red | FRONT-00 §2 |

### 5b · Definir la contraseña nueva

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | abre enlace con token | «Contraseña nueva» con medidor + «Repite la contraseña» + «Guardar contraseña» |
| Enlace incompleto | URL sin token | «enlace no válido» + «Pedir un enlace nuevo» |
| Errores validación | cliente o `422` | errores bajo campos, medidor actualizado |
| Cargando | submit | «Guardando…» |
| Enlace no válido | `401 TOKEN_RECUPERACION_INVALIDO` | mensaje + «Pedir un enlace nuevo» |
| Enlace vencido | `410 TOKEN_RECUPERACION_EXPIRADO` | mensaje + «Pedir un enlace nuevo» |
| Éxito | `204` | borra sesión local y navega a FRONT-01 con aviso |

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Correo (5a) | obligatorio, email | «Escribe un correo válido…» |
| Contraseña (5b) | medidor (no bloquea) | FRONT-00 §3 |
| Repetir (5b) | igual, no se envía | «Las contraseñas no coinciden.» |

En 5b, «datos personales» e «historial» se muestran como «Se comprueba al
guardar» (sin sesión no se conocen los datos del usuario).

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `202` (5a) | «Si el correo {correo} corresponde a una cuenta, te enviamos un enlace…» |
| `DEMASIADAS_SOLICITUDES` | «Ya pediste varios enlaces…» |
| `POLITICA_INCUMPLIDA` (5b) | FRONT-00 §3 |
| `TOKEN_RECUPERACION_INVALIDO` | «Este enlace ya no es válido…» |
| `TOKEN_RECUPERACION_EXPIRADO` | «Este enlace venció…» |

El éxito no menciona el bloqueo levantado ni las sesiones cerradas.

---

## Accesibilidad

- En 5b, `autocomplete="new-password"` en ambos campos.
- Mensaje de 5a tras `202` recibe foco y se anuncia.

## Huecos detectados

- **H-05**: `DEMASIADAS_SOLICITUDES` no indica cuándo reintentar.
- **H-15**: no hay forma de saber si el enlace sigue vigente antes de enviar.
