# FRONT-10 — Cambiar contraseña

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-07 |
| **Wireframe** | Sin wireframe — pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un usuario con sesión cambia su contraseña confirmando la actual (RF-07.8,
HU-07.2). Llega desde la sección Seguridad de *Mi cuenta* (FRONT-06) o desde
*Actividad reciente* (FRONT-13) si ve un acceso que no reconoce. No sirve a
quien olvidó la contraseña ni a quien la tiene caducada: esos usan FRONT-05,
porque sin contraseña vigente no hay sesión.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir, para el medidor | `GET /api/v1/password/politica` | SPEC-07 |
| Al guardar | `POST /api/v1/password/cambiar` con `contrasenaActual` y `nuevaContrasena` | SPEC-07 |

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Al abrir | Campos «Contraseña actual», «Contraseña nueva» con medidor (FRONT-00 §3) y «Repite la contraseña nueva», botones «Cambiar contraseña» y «Cancelar» |
| Errores de validación | Cliente o `422 POLITICA_INCUMPLIDA` | Errores bajo los campos, medidor actualizado. Nada se borra salvo lo indicado en los textos |
| Cargando | Tras enviar | Botón deshabilitado con «Cambiando…» |
| Actual incorrecta | `401 CREDENCIALES_INVALIDAS` | Mensaje bajo «Contraseña actual», que se vacía y recibe el foco. **La sesión sigue abierta** |
| Éxito | `204` | Vuelve a *Mi cuenta* con el aviso de éxito. La sesión actual sigue abierta |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Contraseña actual | Obligatoria | «Escribe tu contraseña actual.» | Esquema de `/password/cambiar` |
| Contraseña nueva | Obligatoria; medidor informativo | Los de FRONT-00 §3 | RF-07.1 a RF-07.4 |
| Contraseña nueva | Datos personales: orientativa, con nombres, apellidos y correo de la sesión (`/auth/me`) | «No puede contener tu nombre, tu apellido ni tu correo.» | RF-07.3 |
| Contraseña nueva | Distinta de la actual | «La contraseña nueva debe ser distinta de la actual.» | RF-07.4 (la actual es la última del historial) |
| Repetir | Igual a la nueva; no se envía | «Las contraseñas no coinciden.» | Interfaz |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `204` | «Tu contraseña se cambió. Cerramos tus sesiones en otros dispositivos; esta sigue abierta. Te enviamos un aviso por correo.» |
| `CREDENCIALES_INVALIDAS` | «La contraseña actual no es correcta.» |
| `POLITICA_INCUMPLIDA` | Los de FRONT-00 §3 |
| `TOKEN_INVALIDO` | Renovación (FRONT-00 §1.2) y reintento; si no se puede, lo que diga FRONT-00 |
| `VALIDACION`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

> **`401` no siempre es sesión vencida.** Aquí `CREDENCIALES_INVALIDAS`
> significa que la contraseña actual no coincide. La SPA no debe renovar la
> sesión ni echar al usuario (FRONT-00 §2).

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-10.1 Cambio correcto
- **Dado** un usuario con sesión
- **Cuando** escribe su contraseña actual y una nueva válida dos veces
- **Entonces** vuelve a *Mi cuenta* con el aviso de éxito y sigue con sesión.

### UI-10.2 Contraseña actual incorrecta *(caso borde)*
- **Dado** el formulario completo
- **Cuando** la contraseña actual es incorrecta
- **Entonces** ve «La contraseña actual no es correcta.», el campo se vacía y
  la sesión **no** se cierra.

### UI-10.3 Contraseña reciente *(caso borde)*
- **Dado** el formulario
- **Cuando** la nueva es una de sus últimas cinco
- **Entonces** ve «Ya usaste esta contraseña hace poco…».

### UI-10.4 Otra sesión abierta *(caso borde)*
- **Dado** el usuario con sesión en el móvil y en el portátil
- **Cuando** cambia la contraseña desde el portátil
- **Entonces** el portátil sigue con sesión y, en el móvil, la siguiente
  renovación falla y aparece UI-00.3.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- `autocomplete="current-password"` en la actual y `new-password` en las otras dos.
- Botón para mostrar u ocultar cada campo.

---

## Fuera de alcance — ¿qué NO hará?

- Recuperar una contraseña olvidada o caducada (FRONT-05).
- Cambiar el correo (FRONT-06).
- Elegir qué sesiones cerrar: el backend cierra todas las demás.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] `CREDENCIALES_INVALIDAS` no cierra la sesión
- [ ] El medidor usa `GET /password/politica`
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Existe un wireframe y coincide con la implementación
