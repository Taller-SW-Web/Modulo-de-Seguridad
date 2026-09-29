# FRONT-01 — Inicio de sesión

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-05, SPEC-07, SPEC-14 (y SPEC-09 al derivar al segundo factor) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 1 (artboards con la numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Cualquier persona con cuenta —cliente, vendedor o personal de gestión— entra
aquí para identificarse con su correo y su contraseña. Llega desde el enlace
«Iniciar sesión» de la SPA, al terminar una verificación o un restablecimiento,
o redirigida desde una ruta protegida. Sale hacia su destino (FRONT-00 §1.4),
hacia el desafío del segundo factor (FRONT-04) o hacia la recuperación de
contraseña (FRONT-05).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al enviar el formulario | `POST /api/v1/auth/login` | SPEC-05 |

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Al abrir la pantalla | Campos de correo y contraseña, botón «Iniciar sesión», enlaces «¿Olvidaste tu contraseña?», «Crear una cuenta» y «¿No te llegó el correo de verificación?». Si llega con un aviso (sesión terminada, sesión cerrada, contraseña restablecida), el aviso aparece arriba |
| Cargando | Tras enviar, hasta la respuesta | Botón deshabilitado con indicador «Iniciando sesión…». Campos de solo lectura |
| Error de credenciales | `401 CREDENCIALES_INVALIDAS` | Mensaje genérico arriba del formulario. El correo se conserva; la contraseña se borra y recibe el foco. Los enlaces siguen visibles |
| Contraseña caducada | `403 PASSWORD_CADUCADA` | Panel que explica que la contraseña caducó, con el botón «Restablecer mi contraseña» que lleva a FRONT-05 con el correo ya escrito. No hay tokens ni sesión |
| Error de servicio | `503 NO_DISPONIBLE` o sin conexión | Mensaje de FRONT-00 §2 y el formulario intacto para reintentar |
| Éxito sin segundo factor | `200` con `SesionIniciada` | Se guarda la sesión (FRONT-00 §1.1) y se navega al destino |
| Éxito con segundo factor | `200` con `DesafioMfa` (`mfaRequerido: true`) | Se navega a FRONT-04 pasándole `challengeToken`, `canal` y `expiraEn` en memoria, nunca en la URL |

**No existe un estado «cuenta bloqueada», «cuenta inactiva» ni «correo sin
verificar».** Las tres situaciones responden `401 CREDENCIALES_INVALIDAS`
(RF-05.4, RF-14.6) y la pantalla muestra exactamente lo mismo que ante una
contraseña incorrecta. Quien está bloqueado se entera por correo.

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Correo | Obligatorio | «Escribe tu correo.» | Esquema de `/auth/login` (`required`) |
| Correo | Formato de correo | «Escribe un correo válido, como nombre@dominio.com.» | Esquema (`format: email`) |
| Contraseña | Obligatoria | «Escribe tu contraseña.» | Esquema (`required`) |

**La contraseña no se valida contra la política al iniciar sesión.** La
política puede cambiar y una contraseña antigua válida no la cumpliría; además,
un mensaje de «demasiado corta» aquí no ayuda a nadie.

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `CREDENCIALES_INVALIDAS` | «El correo o la contraseña no son correctos.» |
| `PASSWORD_CADUCADA` | Título «Tu contraseña caducó». Texto: «Por seguridad, las cuentas de gestión renuevan su contraseña cada 90 días. Te enviaremos un enlace a tu correo para crear una nueva.» Botón «Restablecer mi contraseña» |
| `VALIDACION` | Según FRONT-00 §2 |
| `NO_DISPONIBLE` | Según FRONT-00 §2 |

Avisos de llegada (se muestran en el estado inicial):

| Viene de | Aviso |
|---|---|
| Sesión revocada o vencida (FRONT-00 §1.2) | «Tu sesión terminó. Inicia sesión de nuevo.» |
| Cierre de sesión (FRONT-00 §1.3) | «Cerraste sesión.» |
| Restablecimiento correcto (FRONT-05) | «Tu contraseña se actualizó. Ya puedes iniciar sesión.» |
| Verificación correcta (FRONT-03) | «Tu correo quedó verificado. Ya puedes iniciar sesión.» |
| Desafío vencido (FRONT-04) | «El tiempo para ingresar el código terminó. Inicia sesión de nuevo.» |

> **Por qué el texto de caducidad puede decir «cuentas de gestión».** Solo
> llega a `403 PASSWORD_CADUCADA` quien acertó la contraseña de una cuenta
> `ACTIVO` con rol de gestión (catálogo de errores, regla general). A esa
> persona no se le revela nada que no sepa.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-01.1 Inicio de sesión correcto sin segundo factor
- **Dado** un cliente con cuenta activa y sin segundo factor
- **Cuando** escribe su correo y su contraseña y pulsa «Iniciar sesión»
- **Entonces** el botón muestra «Iniciando sesión…» y después la SPA abre
  *Mi cuenta* (FRONT-06).

### UI-01.2 Contraseña incorrecta
- **Dado** un usuario con cuenta activa
- **Cuando** escribe una contraseña incorrecta
- **Entonces** la pantalla muestra «El correo o la contraseña no son
  correctos.», conserva el correo, borra la contraseña y le pone el foco.

### UI-01.3 Cuenta bloqueada con la contraseña correcta *(caso borde de seguridad)*
- **Dado** una cuenta `BLOQUEADO`
- **Cuando** su titular escribe la contraseña correcta
- **Entonces** la pantalla muestra **exactamente** el mismo mensaje, en la
  misma posición y con el mismo aspecto que en UI-01.2. No hay contador de
  intentos ni aviso de bloqueo.

### UI-01.4 Correo que no existe *(caso borde de seguridad)*
- **Dado** un correo que no pertenece a ninguna cuenta
- **Cuando** alguien intenta iniciar sesión con él
- **Entonces** la pantalla es idéntica a UI-01.2.

### UI-01.5 Cuenta con segundo factor
- **Dado** un vendedor con el segundo factor activo
- **Cuando** escribe correo y contraseña correctos
- **Entonces** la SPA abre FRONT-04 y la URL no contiene el `challengeToken`.

### UI-01.6 Contraseña caducada
- **Dado** un `ADMIN_SISTEMA` con la contraseña de más de 90 días
- **Cuando** inicia sesión (o completa el código en FRONT-04)
- **Entonces** ve el panel «Tu contraseña caducó» y, al pulsar «Restablecer mi
  contraseña», llega a FRONT-05 con su correo escrito.

### UI-01.7 Doble clic en «Iniciar sesión» *(caso borde)*
- **Dado** el formulario completo
- **Cuando** el usuario pulsa el botón dos veces seguidas
- **Entonces** se envía una sola petición.

### UI-01.8 Sin conexión *(caso borde)*
- **Dado** un usuario sin conexión
- **Cuando** envía el formulario
- **Entonces** ve «No pudimos conectar. Revisa tu conexión e intenta de
  nuevo.» y conserva lo escrito, contraseña incluida.

### UI-01.9 Usuario que no verificó su correo *(caso borde)*
- **Dado** un cliente que se registró y no verificó su correo
- **Cuando** inicia sesión con la contraseña correcta
- **Entonces** ve el mensaje genérico de UI-01.2 y puede usar el enlace
  «¿No te llegó el correo de verificación?», siempre visible, que lleva a
  FRONT-03 en el estado de reenvío.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- El mensaje de error se anuncia con `role="alert"`.
- Botón para mostrar u ocultar la contraseña, con `aria-pressed` y etiqueta
  «Mostrar contraseña».
- `autocomplete="email"` y `autocomplete="current-password"`, para que los
  gestores de contraseñas funcionen.
- Variante móvil de 390 px obligatoria (prompt de wireframes): una columna,
  botón de ancho completo.

---

## Fuera de alcance — ¿qué NO hará?

- Mostrar cuántos intentos quedan antes de un bloqueo, o que la cuenta está
  bloqueada: revelaría el estado de la cuenta (RF-14.6).
- «Recordarme» o sesiones persistentes: depende de H-11.
- Inicio de sesión con Google, Facebook u otros proveedores (fuera de alcance
  de SPEC-05).
- CAPTCHA (fuera de alcance de SPEC-14).
- El formulario de login de los otros módulos: pueden tener el suyo, siempre
  que llamen a este endpoint.

---

## Huecos detectados en el backend

- **H-14** — Quien sufre un bloqueo manual no tiene un canal de soporte
  definido al que acudir; el correo de aviso de SPEC-15 tampoco lo menciona.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Contraseña incorrecta, correo inexistente y cuenta bloqueada, inactiva o sin verificar se ven idénticos
- [ ] Las validaciones del cliente coinciden con el esquema de `/auth/login`
- [ ] El `challengeToken` nunca aparece en la URL
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
