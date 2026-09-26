# FRONT-02 — Registro de cliente

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-01, SPEC-07 (y SPEC-02 al terminar) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 2 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un visitante sin cuenta crea su cuenta de cliente. Llega desde «Crear una
cuenta» en FRONT-01 o desde cualquier enlace de registro de la SPA. Al terminar
no queda con sesión abierta (RF-01.7): pasa a FRONT-03 a esperar el correo de
verificación.

Solo se registran clientes. Vendedores y personal de gestión los da de alta un
administrador (FRONT-11).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir la pantalla, para el medidor | `GET /api/v1/password/politica` | SPEC-07 |
| Al enviar el formulario | `POST /api/v1/auth/registro` con `canalOrigen: WEB` | SPEC-01, SPEC-02 (RF-02.8) |

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Al abrir | Formulario vacío: nombres, apellidos, correo, celular (prefijo `+51` fijo y 9 dígitos), contraseña con medidor de fuerza (FRONT-00 §3), confirmación de contraseña, casilla de términos, botón «Crear cuenta» y enlace «¿Ya tienes cuenta? Inicia sesión» |
| Escribiendo | Mientras completa | El medidor actualiza cada regla. Los errores de campo aparecen al salir del campo, no al primer carácter |
| Errores de validación | Validación del cliente o `400 VALIDACION` / `422 POLITICA_INCUMPLIDA` | Cada error bajo su campo, resumen arriba del formulario y foco en el primer campo con error. Nada de lo escrito se borra |
| Cargando | Tras enviar | Botón deshabilitado con «Creando tu cuenta…» |
| Correo no disponible | `409 CORREO_NO_DISPONIBLE` | Mensaje arriba del formulario con enlaces a iniciar sesión y a recuperar la contraseña. El resto de campos se conserva |
| Error de servicio | `503` o sin conexión | Mensaje de FRONT-00 §2; el formulario intacto |
| Éxito | `201` | Navega a FRONT-03, estado «Revisa tu correo», con el correo escrito (solo en memoria) |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Nombres | Obligatorio | «Escribe tu nombre.» | `RegistroRequest.required` |
| Apellidos | Obligatorio | «Escribe tus apellidos.» | `RegistroRequest.required` |
| Correo | Obligatorio y con formato de correo | «Escribe un correo válido, como nombre@dominio.com.» | `RegistroRequest` (`format: email`) |
| Celular | Exactamente 9 dígitos tras el `+51`; se envía como `+51XXXXXXXXX` | «Escribe los 9 dígitos de tu celular.» | RF-01.5 |
| Contraseña | Obligatoria. El medidor informa, pero **no impide** el envío | Los de FRONT-00 §3 | RF-07.1 a RF-07.3, RF-07.5 |
| Contraseña | Regla de datos personales: orientativa, contra nombres, apellidos y la parte local del correo ya escritos | «No puede contener tu nombre, tu apellido ni tu correo.» | RF-07.3 |
| Confirmación | Igual a la contraseña. Solo existe en la interfaz: no se envía | «Las contraseñas no coinciden.» | Interfaz |
| Términos | Casilla marcada | «Debes aceptar los términos y el tratamiento de tus datos personales para crear tu cuenta.» | RF-01.4 |

El botón «Crear cuenta» se habilita siempre; al pulsarlo, si hay errores del
cliente, no se envía nada y se muestran los errores. Así el usuario sabe por
qué no avanza.

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `VALIDACION` | Cada `errores[].mensaje` bajo su `campo` (FRONT-00 §2). Para `aceptaTerminos` y `celular`, los mensajes de la tabla anterior |
| `POLITICA_INCUMPLIDA` | Los de FRONT-00 §3, bajo la contraseña |
| `CORREO_NO_DISPONIBLE` | «No pudimos completar el registro con este correo. Si ya tienes una cuenta, puedes iniciar sesión o recuperar tu contraseña.» |
| `NO_DISPONIBLE` | Según FRONT-00 §2 |

Texto junto a la casilla de términos: «Acepto los [términos y condiciones] y
el [tratamiento de mis datos personales] conforme a la Ley N.º 29733.» Los
enlaces abren el texto en una ventana nueva sin perder el formulario (ver H-12).

> **Sobre `CORREO_NO_DISPONIBLE`.** El mensaje no afirma que el correo esté
> registrado: sugiere qué hacer *si* el usuario tiene cuenta. Es la misma
> línea que sigue el `detail` del contrato.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-02.1 Registro correcto
- **Dado** un visitante que completa todos los campos con datos válidos y acepta los términos
- **Cuando** pulsa «Crear cuenta»
- **Entonces** la SPA envía `canalOrigen: WEB`, abre FRONT-03 con el correo
  enmascarado y **no** abre sesión.

### UI-02.2 Medidor de fuerza en vivo
- **Dado** el formulario abierto y la política cargada
- **Cuando** el visitante escribe `marketplace`
- **Entonces** el medidor marca como cumplida la regla de minúscula y como no
  cumplidas longitud, mayúscula, número y carácter especial, y deja «que no sea
  común» como «Se comprueba al guardar».

### UI-02.3 Sin aceptar los términos *(caso borde legal)*
- **Dado** el formulario completo con la casilla sin marcar
- **Cuando** pulsa «Crear cuenta»
- **Entonces** no se envía nada, la casilla muestra su mensaje y recibe el foco.

### UI-02.4 Celular con otro formato *(caso borde)*
- **Dado** un visitante que escribe `98765432` (8 dígitos)
- **Cuando** sale del campo
- **Entonces** ve «Escribe los 9 dígitos de tu celular.».

### UI-02.5 Correo ya usado *(caso borde de seguridad)*
- **Dado** un correo que ya tiene cuenta
- **Cuando** el visitante intenta registrarse con él
- **Entonces** ve el mensaje de `CORREO_NO_DISPONIBLE` con los dos enlaces y el
  resto del formulario intacto.

### UI-02.6 La contraseña es común *(caso borde)*
- **Dado** una contraseña que cumple todas las reglas visibles pero está en la lista de comunes
- **Cuando** el visitante envía el formulario
- **Entonces** el backend responde `422` con `CONTRASENA_COMUN`, el medidor
  marca esa regla como no cumplida y aparece su mensaje bajo la contraseña.

### UI-02.7 La política no carga *(caso borde)*
- Se aplica UI-00.7.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- El prefijo `+51` es texto visible fuera del campo, no parte del valor, y
  el campo usa `inputmode="numeric"`.
- La casilla de términos es un control nativo con etiqueta clicable.
- La lista de reglas del medidor está enlazada al campo de contraseña con
  `aria-describedby`.
- Variante móvil de 390 px obligatoria: una columna; el medidor debajo del
  campo, sin tapar el teclado.

---

## Fuera de alcance — ¿qué NO hará?

- Registro de vendedores o personal de gestión (FRONT-11).
- Registro con Google, Facebook u otros proveedores.
- Documento de identidad, fecha de nacimiento o direcciones: se completan
  después en *Mi cuenta* (FRONT-06).
- Iniciar sesión automáticamente tras el registro (RF-01.7).

---

## Huecos detectados en el backend

- **H-12** — No hay de dónde leer el texto vigente de los términos ni su
  versión, aunque RF-01.4 guarda la versión aceptada.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Cada código de error del backend tiene su mensaje, sin afirmar que el correo existe
- [ ] Las validaciones del cliente coinciden con RF-01.4, RF-01.5 y el esquema `RegistroRequest`
- [ ] El medidor usa `GET /password/politica` y no reglas propias
- [ ] Se envía `canalOrigen: WEB`
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
