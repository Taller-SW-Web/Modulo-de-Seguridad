# FRONT-05 — Recuperar contraseña

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-08, SPEC-07 (política), SPEC-14 (el restablecimiento levanta un bloqueo automático) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 5 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Son dos pasos con la misma spec, igual que en el wireframe:

- **5a · Pedir el enlace.** Quien olvidó su contraseña escribe su correo.
  Llega desde «¿Olvidaste tu contraseña?» en FRONT-01, o desde el panel de
  contraseña caducada (FRONT-01 y FRONT-04) con el correo ya escrito.
- **5b · Definir la contraseña nueva.** Quien abre el enlace del correo
  escribe la contraseña nueva. Es también la salida para quien quedó con un
  bloqueo automático (RF-08.5), aunque la pantalla no lo dice.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| 5a, al enviar el correo | `POST /api/v1/password/recuperar` | SPEC-08 |
| 5b, al abrir la pantalla, para el medidor | `GET /api/v1/password/politica` | SPEC-07 |
| 5b, al guardar | `POST /api/v1/password/restablecer` con `token` y `nuevaContrasena` | SPEC-08 |

---

## Estados de la pantalla

### 5a · Pedir el enlace

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Al abrir | Campo de correo (prellenado si viene de «contraseña caducada»), botón «Enviar enlace» y enlace «Volver a iniciar sesión» |
| Cargando | Tras enviar | Botón deshabilitado con «Enviando…» |
| Enviado | `202` | Mensaje de confirmación neutro, el correo escrito, aviso de que el enlace dura 30 minutos y que pedir otro invalida el anterior, y botón «Volver a iniciar sesión» |
| Demasiadas solicitudes | `429` | Mensaje; el botón queda deshabilitado mientras siga en la pantalla |
| Error de servicio | `503` o sin conexión | Mensaje de FRONT-00 §2 |

### 5b · Definir la contraseña nueva

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Abre el enlace con token | Campos «Contraseña nueva» con medidor (FRONT-00 §3) y «Repite la contraseña», botón «Guardar contraseña» |
| Enlace incompleto | La URL no trae token | Mensaje de enlace no válido y botón «Pedir un enlace nuevo» (a 5a), sin formulario |
| Errores de validación | Validación del cliente o `422 POLITICA_INCUMPLIDA` | Errores bajo los campos, medidor actualizado |
| Cargando | Tras guardar | Botón deshabilitado con «Guardando…» |
| Enlace no válido | `401 TOKEN_RECUPERACION_INVALIDO` | Mensaje, sin formulario, y botón «Pedir un enlace nuevo» |
| Enlace vencido | `410 TOKEN_RECUPERACION_EXPIRADO` | Mensaje, sin formulario, y botón «Pedir un enlace nuevo» |
| Éxito | `204` | Se borra cualquier sesión local de ese navegador (el backend revocó todas) y se navega a FRONT-01 con el aviso «Tu contraseña se actualizó. Ya puedes iniciar sesión.» |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Correo (5a) | Obligatorio y con formato de correo | «Escribe un correo válido, como nombre@dominio.com.» | Esquema de `/password/recuperar` |
| Contraseña nueva (5b) | Obligatoria. El medidor informa y no bloquea | Los de FRONT-00 §3 | RF-07.1 a RF-07.4 |
| Contraseña nueva (5b) | «Datos personales» e «historial» se muestran como «Se comprueba al guardar»: sin sesión, la SPA no conoce los datos del usuario | — | RF-07.3, RF-07.4 |
| Repetir contraseña (5b) | Igual a la nueva; no se envía | «Las contraseñas no coinciden.» | Interfaz |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `202` (5a) | «Si el correo {correo} corresponde a una cuenta, te enviamos un enlace para crear una contraseña nueva. Vence en 30 minutos. Si pides otro, este dejará de funcionar.» |
| `DEMASIADAS_SOLICITUDES` (5a) | «Ya pediste varios enlaces seguidos. Espera unos minutos antes de pedir otro.» |
| `POLITICA_INCUMPLIDA` (5b) | Los de FRONT-00 §3; `YA_UTILIZADA` usa `historial` de la política: «No puedes repetir ninguna de tus últimas {historial} contraseñas.» |
| `TOKEN_RECUPERACION_INVALIDO` (5b) | «Este enlace ya no es válido. Puede que ya lo hayas usado o que hayas pedido otro más reciente.» |
| `TOKEN_RECUPERACION_EXPIRADO` (5b) | «Este enlace venció: los enlaces para restablecer la contraseña duran 30 minutos.» |
| `VALIDACION`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

> **Lo que el éxito no dice.** Tras restablecer, el backend cierra las
> sesiones anteriores y levanta un bloqueo automático si lo había (RF-08.5,
> RF-14.12). La pantalla no menciona el bloqueo: quien no lo sabía no debe
> enterarse aquí, y quien lo sabía ya lo verá al iniciar sesión.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-05.1 Pedir el enlace
- **Dado** un usuario en 5a
- **Cuando** escribe su correo y pulsa «Enviar enlace»
- **Entonces** ve el mensaje de confirmación con su correo.

### UI-05.2 Correo sin cuenta *(caso borde de seguridad)*
- **Dado** un correo que no pertenece a nadie
- **Cuando** se pide el enlace con él
- **Entonces** la pantalla es idéntica a UI-05.1, también en el tiempo que
  tarda en aparecer el mensaje: la SPA no añade ni quita esperas.

### UI-05.3 Restablecer con un enlace válido
- **Dado** un enlace recibido hace 10 minutos
- **Cuando** el usuario escribe una contraseña válida dos veces y pulsa «Guardar contraseña»
- **Entonces** llega a FRONT-01 con «Tu contraseña se actualizó…».

### UI-05.4 Contraseña ya usada *(caso borde)*
- **Dado** 5b abierto con un enlace válido
- **Cuando** el usuario escribe una de sus últimas cinco contraseñas
- **Entonces** ve «Ya usaste esta contraseña hace poco…» bajo el campo, el
  formulario sigue abierto y el enlace sigue sirviendo.

### UI-05.5 Enlace vencido *(caso borde)*
- **Dado** un enlace de hace 40 minutos
- **Cuando** el usuario completa el formulario y guarda
- **Entonces** ve «Este enlace venció…» y el botón «Pedir un enlace nuevo»,
  que abre 5a.

### UI-05.6 Enlace reemplazado *(caso borde)*
- **Dado** que el usuario pidió dos enlaces y abre el primero
- **Cuando** guarda la contraseña
- **Entonces** ve «Este enlace ya no es válido…».

### UI-05.7 Viene de «contraseña caducada»
- **Dado** un `ADMIN_VENTAS` que vio «Tu contraseña caducó» en FRONT-01
- **Cuando** pulsa «Restablecer mi contraseña»
- **Entonces** llega a 5a con su correo escrito y el foco en «Enviar enlace».

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00 (incluido §1.5 sobre el token en la URL).
- En 5b, `autocomplete="new-password"` en los dos campos, para que el gestor
  de contraseñas proponga una y la guarde.
- El mensaje de 5a tras el `202` recibe el foco y se anuncia.

---

## Fuera de alcance — ¿qué NO hará?

- Cambiar la contraseña con la sesión iniciada (FRONT-10).
- Recuperar por SMS o con preguntas secretas (fuera de alcance de SPEC-08).
- Levantar un bloqueo manual: solo lo hace un administrador (RF-14.12).
- Iniciar sesión automáticamente tras restablecer.

---

## Huecos detectados en el backend

- **H-05** — `DEMASIADAS_SOLICITUDES` no dice cuándo reintentar.
- **H-15** — No hay forma de saber si el enlace sigue vigente antes de
  enviar el formulario: el usuario escribe su contraseña y solo al guardar se
  entera de que el enlace venció.

---

## Lista de completitud

- [ ] Cada estado de 5a y 5b está implementado
- [ ] El mensaje de 5a no confirma que la cuenta exista
- [ ] El token se quita de la URL al cargar 5b
- [ ] El medidor usa `GET /password/politica`
- [ ] Tras restablecer se borra la sesión local
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
