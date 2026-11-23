# FRONT-16 — Activación de una cuenta creada por otra persona

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Épica principal** | [EP-01](../epicas/EP-01-registro-gestion-usuarios.md) |
| **Specs de backend que consume** | [SPEC-03](../funcionales/SPEC-03-alta-consulta-cuentas.md) RF-03.3 y RF-03.5; política de [SPEC-07](../funcionales/SPEC-07-politica-cambio-contrasena.md) |
| **Wireframe** | Sin wireframe; sigue el [sistema de diseño](../../docs/diseno/sistema-diseno.md) y la pantalla de recuperación de FRONT-05 |
| **Estado** | En revisión (FEAT-13, H5): implementada en frontend #28 y recorrida en el despliegue el 23-nov |
| **Aprobada por** | Pendiente |

> Es la «pantalla de activación A6» que [FRONT-11](FRONT-11-admin-alta-usuario.md) pedía
> especificar aparte (issue [#36](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/36)).
> Se escribe después de implementarla, con el comportamiento ya probado; el alcance lo fijó el
> PO en [decisiones del PO](../../plan/hito-5/DECISIONES-PO.md) §3.

---

## Objetivo — ¿para qué sirve esta pantalla?

Una persona cuya cuenta creó un `ADMIN_SISTEMA` (FRONT-11) o, más adelante, un vendedor en tienda
(RF-03.6, fuera de H5) llega desde el enlace del correo «Activa tu cuenta». Aquí elige su propia
contraseña y acepta los términos; nadie más llega a conocerla (acuerdo A6). Ruta pública
`/activar-cuenta?token=…`.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir la pantalla | `GET /api/v1/password/politica` | SPEC-07 (medidor de fuerza) |
| Al enviar el formulario | `POST /api/v1/auth/activar-cuenta` con `token`, `contrasena` y `aceptaTerminos: true` | RF-03.5 |
| Al pulsar «Cerrar esa sesión e iniciar sesión», si había otra sesión abierta | `POST /api/v1/auth/logout` | SPEC-06 |

No hay un endpoint para saber si el enlace sigue vigente sin activar, así que la pantalla no
consulta el token al abrirse: su estado se conoce al enviar el formulario (igual que H-15 en la
recuperación).

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Sin token | La URL llega sin `token` | «El enlace está incompleto. Ábrelo de nuevo desde el correo que recibiste.» Sin formulario |
| Inicial | La URL trae `token` | El token se quita de la barra de direcciones y del historial (FRONT-00 §1.5). Contraseña con medidor, repetición y casilla de términos |
| Cargando | Enviar | Botón deshabilitado con «Activando…» |
| Contraseña rechazada | `422 POLITICA_INCUMPLIDA` | Las reglas incumplidas se marcan en el medidor y bajo el campo: «La contraseña no cumple las reglas marcadas.» El formulario sigue; el enlace no se gastó (ESC-03.4) |
| Activada | `204` | «Tu cuenta quedó activa. Ya puedes iniciar sesión.» y botón «Iniciar sesión», que lleva al login con ese aviso |
| Activada con otra sesión abierta | `204` y el navegador ya tenía una sesión | Además: «En este navegador hay otra sesión abierta. Ciérrala para entrar con la cuenta que acabas de activar.», con el correo de esa sesión enmascarado. El botón pasa a «Cerrar esa sesión e iniciar sesión» |
| Enlace vencido | `410 ENLACE_EXPIRADO` | «Este enlace venció: los enlaces de activación duran 72 horas. Contacta al administrador de la plataforma para revisar tu activación.» Sin formulario |
| Enlace ya usado | `410 ENLACE_YA_USADO`, también para un token desconocido | «Este enlace ya se usó. Si ya activaste tu cuenta, inicia sesión.» y enlace al login |
| Error | `400`, `503` o sin respuesta | Mensaje de FRONT-00; el formulario se conserva |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Contraseña | Obligatoria. La política la valida el backend (por eso una contraseña vacía o débil llega como `422`) | «Escribe tu contraseña.» | RF-03.3 |
| Repite la contraseña | Igual a la contraseña | «Las contraseñas no coinciden.» | FRONT-00 |
| Términos | Casilla marcada; enlaza a `/terminos` (texto `2026-11-v1`) en otra pestaña | «Debes aceptar los términos y el tratamiento de tus datos personales para activar tu cuenta.» | RF-03.5; H-12 |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `POLITICA_INCUMPLIDA` | «La contraseña no cumple las reglas marcadas.» y reglas marcadas en el medidor |
| `ENLACE_EXPIRADO` | «Este enlace venció: los enlaces de activación duran 72 horas. Contacta al administrador de la plataforma para revisar tu activación.» |
| `ENLACE_YA_USADO` | «Este enlace ya se usó. Si ya activaste tu cuenta, inicia sesión.» |
| `VALIDACION`, `NO_DISPONIBLE`, sin respuesta | Mensajes de FRONT-00 |

Los mensajes no dicen a quién pertenece la cuenta ni qué rol tiene. El enlace vencido no ofrece
reenvío: hoy el alta con el mismo correo responde `409` y la decisión está pendiente del PO
([FEAT-13](../../docs/evidencia/h5/FEAT-13.md), «Enlace vencido»).

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-16.1 Activación correcta
- **Dado** que la persona abre el enlace vigente del correo
- **Cuando** escribe una contraseña válida dos veces, acepta los términos y pulsa «Activar mi cuenta»
- **Entonces** la pantalla muestra «Tu cuenta quedó activa…» y el botón lleva al login con ese aviso

### UI-16.2 Contraseña que no cumple la política *(caso borde)*
- **Dado** el formulario con un enlace vigente
- **Cuando** envía `vendedor1`
- **Entonces** se marcan las reglas incumplidas, el formulario sigue y, con una contraseña válida, el mismo enlace activa la cuenta

### UI-16.3 Enlace ya usado *(caso borde)*
- **Dado** un enlace con el que ya se activó la cuenta
- **Cuando** se vuelve a enviar el formulario
- **Entonces** la pantalla muestra «Este enlace ya se usó…» sin formulario

### UI-16.4 Enlace vencido *(caso borde)*
- **Dado** un enlace de más de 72 horas
- **Cuando** se envía el formulario
- **Entonces** la pantalla pide contactar al administrador, sin formulario ni reenvío

### UI-16.5 Sin aceptar los términos *(caso borde)*
- **Dado** el formulario completo sin la casilla marcada
- **Cuando** pulsa «Activar mi cuenta»
- **Entonces** no se llama al backend y la casilla muestra su mensaje

### UI-16.6 Otra sesión abierta *(caso borde)*
- **Dado** un navegador con la sesión de otra cuenta
- **Cuando** activa la cuenta nueva
- **Entonces** se avisa con el correo de esa sesión enmascarado y el botón la cierra antes de ir al login

### UI-16.7 Enlace incompleto *(caso borde)*
- **Dado** que la URL no trae `token`
- **Cuando** se abre la pantalla
- **Entonces** se muestra «El enlace está incompleto…» sin formulario

---

## Accesibilidad y diseño adaptable

- Campos con etiqueta, errores asociados a cada campo y avisos con `role="status"`, `alert` o `note`.
- Botón de mostrar contraseña con nombre accesible en los dos campos.
- El enlace a los términos abre otra pestaña para no perder lo escrito.
- Tarjeta centrada de ancho pequeño, igual que el login y la recuperación; funciona en móvil.

---

## Fuera de alcance — ¿qué NO hará?

- Reenviar el enlace o pedir uno nuevo (pendiente de la decisión del PO).
- Iniciar sesión automáticamente al activar: la persona entra por el login, con segundo factor si
  su rol es de gestión.
- Activar cuentas de cliente registradas por un vendedor (RF-03.6, fuera de H5).

---

## Lista de completitud

- [x] Cada estado de la pantalla está implementado (frontend #28)
- [x] Cada código de error del backend tiene su mensaje, sin revelar datos de cuentas
- [x] Las validaciones del cliente coinciden con las de la spec de backend
- [x] Los escenarios de interfaz están automatizados (`ActivacionCuentaPage.test.tsx`) y UI-16.1 a UI-16.3 se recorrieron en el despliegue ([FEAT-13](../../docs/evidencia/h5/FEAT-13.md))
- [ ] Aprobación del PO
