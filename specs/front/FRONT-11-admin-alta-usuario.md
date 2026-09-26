# FRONT-11 — Panel de administración: alta de cuenta

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-03, SPEC-07 (política) |
| **Wireframe** | Sin wireframe — el listado (pantalla 7) tiene el botón que lleva aquí |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un `ADMIN_SISTEMA` crea la cuenta de un vendedor o de alguien del personal de
gestión (RF-03.1, HU-03.1). Llega desde el botón «Crear cuenta de vendedor o
gestión» del listado (FRONT-07). La cuenta nace activa, sin verificación de
correo, y al terminar se abre su detalle (FRONT-08).

Los clientes no se crean aquí: se registran solos (FRONT-02).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir, para el medidor | `GET /api/v1/password/politica` | SPEC-07 |
| Al enviar | `POST /api/v1/usuarios` | SPEC-03 |

Permiso necesario: `usuario.crear`. Sin él, la pantalla muestra «No tienes
permiso para ver esta página.» sin llamar a nada.

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | Al abrir | Campos: correo, nombres, apellidos, rol (lista), contraseña inicial con medidor, repetir contraseña; botones «Crear cuenta» y «Cancelar» |
| Rol de gestión elegido | El rol es uno de los cuatro de gestión | Nota bajo el rol: «Con este rol, la cuenta tendrá el segundo factor obligatorio y su contraseña caducará cada {caducidadDiasAdmin} días.» |
| Errores de validación | Cliente, `400` o `422 POLITICA_INCUMPLIDA` | Errores bajo sus campos |
| Cargando | Tras enviar | Botón deshabilitado con «Creando…» |
| Correo no disponible | `409 CORREO_NO_DISPONIBLE` | Mensaje bajo el correo |
| Éxito | `201` con `Usuario` | Abre FRONT-08 de la cuenta nueva con el aviso «Cuenta creada. Ya puede iniciar sesión.» |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Correo | Obligatorio y con formato | «Escribe un correo válido.» | `CrearUsuarioRequest` |
| Rol | Obligatorio; solo `VENDEDOR`, `ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL` o `ADMIN_SISTEMA` | «Elige un rol.» | RF-03.1 (ver H-10) |
| Nombres, apellidos | Recomendados: el contrato no los exige, pero sin ellos la cuenta no tiene `nombreCompleto` legible | «Escribe el nombre de la persona.» (aviso, no bloquea) | `CrearUsuarioRequest` |
| Contraseña inicial | Obligatoria; medidor informativo, con datos personales comprobados contra los nombres, apellidos y correo escritos | Los de FRONT-00 §3 | RF-03.3, RF-07 |
| Repetir | Igual; no se envía | «Las contraseñas no coinciden.» | Interfaz |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `201` | «Cuenta creada. Ya puede iniciar sesión.» |
| `CORREO_NO_DISPONIBLE` | «Ese correo no se puede usar para una cuenta nueva. Revisa si la persona ya tiene cuenta en el listado.» |
| `POLITICA_INCUMPLIDA` | Los de FRONT-00 §3 |
| `VALIDACION` | Según FRONT-00 §2 |
| `SCOPE_INSUFICIENTE`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

> Quien usa esta pantalla tiene `usuario.ver` y ya puede buscar cualquier
> correo en el listado, así que sugerirle que lo busque no revela nada nuevo.

Nota fija bajo la contraseña: «Comunica la contraseña inicial a la persona por
un canal seguro. No la envíes por el mismo correo de la cuenta.»

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-11.1 Alta de un vendedor
- **Dado** un `ADMIN_SISTEMA`
- **Cuando** completa el formulario con rol «Vendedor» y una contraseña válida
- **Entonces** llega al detalle de la cuenta nueva, que figura «Activa».

### UI-11.2 Alta de un gestor
- **Dado** el formulario
- **Cuando** elige «Gestor de despacho»
- **Entonces** aparece la nota sobre el segundo factor obligatorio y la caducidad.

### UI-11.3 Correo repetido *(caso borde)*
- **Dado** un correo que ya tiene cuenta
- **Cuando** el administrador envía el formulario
- **Entonces** ve el mensaje de `CORREO_NO_DISPONIBLE` bajo el correo y no se
  crea nada.

### UI-11.4 Contraseña débil *(caso borde)*
- **Dado** la contraseña `vendedor1`
- **Cuando** el administrador envía el formulario
- **Entonces** el medidor y los mensajes marcan longitud y carácter especial (ESC-03.4).

### UI-11.5 El rol cliente no está *(caso borde)*
- **Dado** el formulario
- **Cuando** el administrador abre la lista de roles
- **Entonces** «Cliente» no aparece.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- La lista de roles es un `<select>` nativo o un grupo de opciones con etiqueta.
- `autocomplete="off"` en el correo y `new-password` en las contraseñas: el
  navegador no debe proponer los datos del administrador.

---

## Fuera de alcance — ¿qué NO hará?

- Crear clientes (FRONT-02).
- Asignar varios roles en el alta: el contrato recibe uno; los demás se
  añaden en el detalle (FRONT-08).
- Generar la contraseña inicial o enviarla por correo.
- Celular, documento o atributos de vendedor en el alta: se completan después.

---

## Huecos detectados en el backend

- **H-10** — `CrearUsuarioRequest.rol` admite `CLIENTE`, aunque RF-03.1 lo
  excluye, y no hay código de error definido para ese caso. La pantalla no
  ofrece la opción.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] La lista de roles no incluye `CLIENTE`
- [ ] El medidor usa `GET /password/politica`
- [ ] Al crear, abre el detalle de la cuenta nueva
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Existe un wireframe y coincide con la implementación
