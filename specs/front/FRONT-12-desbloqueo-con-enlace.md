# FRONT-12 — Desbloqueo con el enlace del correo

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-14 |
| **Wireframe** | Sin wireframe — pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Cuando una cuenta sufre un bloqueo automático, su titular recibe un correo con
un enlace de un solo uso, válido 30 minutos (RF-14.8, RF-14.11). Esta pantalla
es el destino de ese enlace: levanta el bloqueo sin esperar a que venza ni a un
administrador (HU-14.2).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al pulsar «Desbloquear mi cuenta» | `POST /api/v1/auth/desbloquear` con `token` | SPEC-14 |

**El desbloqueo no se lanza solo al abrir la página.** Algunos filtros de
correo abren los enlaces para analizarlos; si la pantalla consumiera el token al
cargar, un robot podría gastarlo antes que la persona. Un botón explícito lo
evita y deja claro qué va a pasar.

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Abre el enlace con token | Título «Desbloquear tu cuenta», texto explicativo y botón «Desbloquear mi cuenta» |
| Enlace incompleto | La URL no trae token | Mensaje de enlace no válido y enlace a FRONT-05 |
| Cargando | Tras pulsar | Botón deshabilitado con «Desbloqueando…» |
| Éxito | `204` | Mensaje de éxito, botón «Iniciar sesión» y la recomendación de cambiar la contraseña si no fue el titular quien falló |
| Enlace vencido | `410 TOKEN_DESBLOQUEO_EXPIRADO` | Mensaje y enlace «Restablecer mi contraseña» (FRONT-05), que también levanta un bloqueo automático (RF-14.12) |
| Enlace no válido | `401 TOKEN_DESBLOQUEO_INVALIDO` | Mensaje y enlace «Restablecer mi contraseña» |
| Error de servicio | `503` o sin conexión | Mensaje de FRONT-00 §2; el botón vuelve a estar disponible |

---

## Validaciones del lado del cliente

No hay campos. Si el parámetro del token falta o está vacío, se muestra el
estado «Enlace incompleto» sin llamar al backend.

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| Inicial | «Bloqueamos tu cuenta después de varios intentos fallidos de inicio de sesión. Pulsa el botón para desbloquearla ahora.» |
| `204` | «Tu cuenta está desbloqueada. Ya puedes iniciar sesión. Si no fuiste tú quien intentó entrar, te recomendamos cambiar tu contraseña.» |
| `TOKEN_DESBLOQUEO_EXPIRADO` | «Este enlace venció: los enlaces de desbloqueo duran 30 minutos. Puedes recuperar el acceso restableciendo tu contraseña.» |
| `TOKEN_DESBLOQUEO_INVALIDO` | «Este enlace ya no es válido: puede que ya se haya usado o que haya uno más reciente. Puedes recuperar el acceso restableciendo tu contraseña.» |
| `NO_DISPONIBLE` | Según FRONT-00 §2 |

> **Lo que los mensajes no dicen.** `TOKEN_DESBLOQUEO_INVALIDO` también llega
> cuando un administrador bloqueó la cuenta después (ESC-14.14). La pantalla no
> lo menciona: restablecer la contraseña tampoco levanta un bloqueo manual, y
> el titular de un bloqueo manual ya recibió su propio correo de aviso (RF-15.7).

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-12.1 Desbloqueo correcto
- **Dado** un enlace recibido hace 5 minutos
- **Cuando** el titular lo abre y pulsa «Desbloquear mi cuenta»
- **Entonces** el token desaparece de la URL al cargar, y tras pulsar ve «Tu
  cuenta está desbloqueada…» con el botón «Iniciar sesión».

### UI-12.2 Abrir el enlace no desbloquea *(caso borde)*
- **Dado** un enlace válido
- **Cuando** se abre la página y nadie pulsa el botón
- **Entonces** no se ha llamado a `/auth/desbloquear`.

### UI-12.3 Enlace vencido *(caso borde)*
- **Dado** un enlace de hace 45 minutos
- **Cuando** el titular pulsa el botón
- **Entonces** ve «Este enlace venció…» y el enlace a restablecer la contraseña.

### UI-12.4 Enlace reemplazado por otro bloqueo *(caso borde)*
- **Dado** que la cuenta volvió a bloquearse y llegó un enlace nuevo
- **Cuando** el titular usa el antiguo
- **Entonces** ve «Este enlace ya no es válido…».

### UI-12.5 Doble clic *(caso borde)*
- **Dado** la pantalla inicial
- **Cuando** el titular pulsa el botón dos veces
- **Entonces** se envía una sola petición.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00 (incluido §1.5 sobre el token en la URL).
- El resultado recibe el foco y se anuncia.
- Una columna en 390 px, botón de ancho completo.

---

## Fuera de alcance — ¿qué NO hará?

- Levantar un bloqueo manual (solo un administrador, SPEC-15).
- Decir si el bloqueo tiene vencimiento o cuánto falta.
- Pedir un enlace de desbloqueo nuevo: no hay endpoint; la alternativa es FRONT-05.

---

## Huecos detectados en el backend

- **H-14** — No hay un canal de soporte definido para quien tiene un bloqueo manual.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] El token solo se consume al pulsar el botón
- [ ] El token se quita de la URL al cargar
- [ ] Ningún mensaje revela si hay un bloqueo manual
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Existe un wireframe y coincide con la implementación
