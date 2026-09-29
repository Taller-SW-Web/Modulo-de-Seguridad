# FRONT-11 — Panel de administración: alta de cuenta (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-11-admin-alta-usuario.md` |
| Specs de backend | SPEC-03 (alta), SPEC-07 (política) |
| Wireframe | Sin wireframe — el listado (pantalla 7) tiene el botón que lleva aquí |
| Responsable | Por asignar |

---

## Objetivo

Un `ADMIN_SISTEMA` crea la cuenta de un vendedor o de personal de gestión
(RF-03.1, HU-03.1). Llega desde «Crear cuenta de vendedor o gestión» del listado
(FRONT-07). La cuenta nace activa, sin verificación de correo, y al terminar se
abre su detalle (FRONT-08). Los clientes no se crean aquí.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir (medidor) | `GET /api/v1/password/politica` | SPEC-07 |
| Al enviar | `POST /api/v1/usuarios` | SPEC-03 |

Permiso: `usuario.crear`. Sin él, «No tienes permiso para ver esta página.» sin
llamar a nada.

---

## Archivos propuestos

```
frontend/src/pantallas/admin-alta/
├── AdminAltaPage.tsx      # formulario + nota de rol de gestión
└── useAltaUsuario.ts      # estado, validación, envío
```

Reutiliza de FRONT-00: `PasswordStrengthMeter`, `usePoliticaPassword`, `api`,
`errores`, `sesion` (permiso `usuario.crear`).

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `AdminAltaPage` | Campos correo, nombres, apellidos, rol, contraseña inicial + medidor, repetir; «Crear cuenta»/«Cancelar» |
| `useAltaUsuario` | Valida, llama `api.crearUsuario`, ramifica 409/422 |

Nota bajo la contraseña: «Comunica la contraseña inicial a la persona por un
canal seguro. No la envíes por el mismo correo de la cuenta.»

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | al abrir | formulario |
| Rol de gestión elegido | rol ∈ {4 de gestión} | nota: «Con este rol, la cuenta tendrá el segundo factor obligatorio y su contraseña caducará cada {caducidadDiasAdmin} días.» |
| Errores validación | cliente, `400` o `422` | errores bajo campos |
| Cargando | submit | «Creando…» |
| Correo no disponible | `409` | mensaje bajo el correo |
| Éxito | `201` con `Usuario` | abre FRONT-08 de la cuenta nueva con «Cuenta creada. Ya puede iniciar sesión.» |

---

## Validaciones del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Correo | obligatorio, email | «Escribe un correo válido.» | `CrearUsuarioRequest` |
| Rol | obligatorio; solo `VENDEDOR`, `ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA` | «Elige un rol.» | RF-03.1 (H-10) |
| Nombres/apellidos | recomendados (aviso, no bloquea) | «Escribe el nombre de la persona.» | `CrearUsuarioRequest` |
| Contraseña inicial | obligatoria; medidor con datos personales contra nombres/apellidos/correo | FRONT-00 §3 | RF-03.3, RF-07 |
| Repetir | igual; no se envía | «Las contraseñas no coinciden.» | interfaz |

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `201` | «Cuenta creada. Ya puede iniciar sesión.» |
| `CORREO_NO_DISPONIBLE` | «Ese correo no se puede usar para una cuenta nueva. Revisa si la persona ya tiene cuenta en el listado.» |
| `POLITICA_INCUMPLIDA` | FRONT-00 §3 |
| `VALIDACION` | FRONT-00 §2 |
| `SCOPE_INSUFICIENTE` / `NO_DISPONIBLE` | FRONT-00 §2 |

Quien usa esta pantalla tiene `usuario.ver`, así que sugerir buscar en el listado
no revela nada nuevo.

---

## Accesibilidad

- Lista de roles: `<select>` nativo o grupo de opciones con etiqueta.
- `autocomplete="off"` en el correo y `new-password` en las contraseñas.

## Fuera de alcance

Crear clientes (FRONT-02); varios roles en el alta (el contrato recibe uno; el
resto en FRONT-08); generar/enviar la contraseña inicial; celular/documento/
atributos de vendedor (se completan después).

## Hueco detectado

- **H-10**: `CrearUsuarioRequest.rol` admite `CLIENTE` aunque RF-03.1 lo excluye;
  la pantalla no ofrece la opción.
