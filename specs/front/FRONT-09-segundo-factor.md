# FRONT-09 — Segundo factor: activar y desactivar

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-10 (reutiliza los códigos de SPEC-09) |
| **Wireframe** | Sin wireframe: es el hueco que ya anota [`trazabilidad.md`](../trazabilidad.md) §7 |
| **Estado** | Borrador — **bloqueada por H-01 y H-02** |
| **Aprobada por** | Pendiente |

> **Esta spec describe la pantalla que piden SPEC-10 y la HU-10.1, no la que
> permite hoy el contrato.** Dos piezas del contrato faltan (H-01 y H-02). Donde
> falta una, la spec lo marca con ⛔ en vez de inventar un endpoint.

---

## Objetivo — ¿para qué sirve esta pantalla?

Un cliente o un vendedor decide si quiere el segundo factor (RF-10.2, RF-10.3).
Quien tiene un rol de gestión lo ve activo y sin opción de apagarlo (RF-10.1).
Se llega desde la sección Seguridad de *Mi cuenta* (FRONT-06).

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir, para saber si está activo | ⛔ `GET /api/v1/auth/me` no devuelve `mfaHabilitado` (H-01) | SPEC-16, SPEC-10 |
| Al pulsar «Activar» | `POST /api/v1/auth/otp/habilitar` → `202`, envía un código | SPEC-10 |
| Al escribir el código de activación | ⛔ No hay endpoint para confirmarlo (H-02) | SPEC-10 (RF-10.2) |
| Al pulsar «Desactivar» | `POST /api/v1/auth/otp/deshabilitar` → `204` | SPEC-10 |
| Código de confirmación de la desactivación | ⛔ El contrato no lo pide, aunque RF-10.3 lo exige (H-02) | SPEC-10 (RF-10.3) |

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Cargando | Al abrir | Indicador |
| Obligatorio | La cuenta tiene algún rol de gestión | «El segundo factor está activo y es obligatorio para tu rol.» Sin interruptor. Basta un rol de gestión (RF-10.1, ESC-10.5) |
| Desactivado | ⛔ `mfaHabilitado = false` (H-01) | Explicación breve y botón «Activar segundo factor» |
| Enviando código | Tras «Activar» | Indicador «Enviando un código a tu correo…» |
| Confirmar activación | `202` de `/otp/habilitar` | Seis casillas iguales a las de FRONT-04 y botón «Confirmar». ⛔ H-02 |
| Activado | Tras confirmar el código | «El segundo factor está activo. La próxima vez que inicies sesión te pediremos un código.» y botón «Desactivar» |
| Confirmar desactivación | Pulsa «Desactivar» | Diálogo de confirmación; RF-10.3 añade un código. ⛔ H-02 |
| Desactivado tras confirmar | `204` | «Desactivaste el segundo factor. La próxima vez solo te pediremos la contraseña.» |
| Rechazado por el rol | `422 MFA_OBLIGATORIO` | Mensaje y la pantalla pasa al estado «Obligatorio» |

**Los roles de gestión son `ADMIN_VENTAS`, `GESTOR_DESPACHO`,
`GESTOR_COMERCIAL` y `ADMIN_SISTEMA`.** La pantalla los lee de los roles de la
sesión para mostrar el estado «Obligatorio» sin esperar al `422`. La regla es
de RF-10.1; el backend la aplica igualmente.

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Código | 6 dígitos, mismo control que FRONT-04 | «Escribe los 6 dígitos del código.» | RF-09.1 |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `202` de `/otp/habilitar` | «Te enviamos un código a tu correo. Escríbelo para terminar de activar el segundo factor.» |
| `MFA_OBLIGATORIO` | «Tu rol exige el segundo factor; no puedes desactivarlo.» |
| `CODIGO_INVALIDO`, `OTP_EXPIRADO`, `OTP_INTENTOS_AGOTADOS`, `DEMASIADAS_SOLICITUDES` | Los de FRONT-04, si el endpoint de confirmación (H-02) los devuelve |
| `NO_DISPONIBLE` | Según FRONT-00 §2 |

Diálogo de desactivación: «¿Desactivar el segundo factor? A partir de ahora,
quien conozca tu contraseña podrá entrar en tu cuenta.» Botones «Desactivar» y
«Cancelar».

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-09.1 Activar como cliente
- **Dado** un cliente con el segundo factor desactivado
- **Cuando** pulsa «Activar», recibe el código y lo escribe
- **Entonces** ve «El segundo factor está activo…». ⛔ Depende de H-01 y H-02.

### UI-09.2 Desactivar como vendedor
- **Dado** un vendedor con el segundo factor activo
- **Cuando** pulsa «Desactivar» y confirma
- **Entonces** ve «Desactivaste el segundo factor…».

### UI-09.3 Rol de gestión *(caso borde)*
- **Dado** un usuario con `ADMIN_VENTAS`
- **Cuando** abre la pantalla
- **Entonces** ve «…es obligatorio para tu rol.» y ningún botón para desactivarlo.

### UI-09.4 Varios roles, uno de gestión *(caso borde)*
- **Dado** un usuario con `VENDEDOR` y `GESTOR_COMERCIAL`
- **Cuando** abre la pantalla
- **Entonces** ve el estado «Obligatorio», como en UI-09.3 (ESC-10.5).

### UI-09.5 Los roles cambiaron durante la sesión *(caso borde)*
- **Dado** un vendedor al que un administrador acaba de asignar un rol de gestión, con la pantalla abierta
- **Cuando** pulsa «Desactivar»
- **Entonces** recibe `422 MFA_OBLIGATORIO`, ve su mensaje y la pantalla pasa
  a «Obligatorio». (En la práctica, el cambio de rol revoca la sesión y lo
  normal es que antes vea UI-00.3.)

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00; el código, igual que FRONT-04.
- El estado actual («Activo», «Desactivado», «Obligatorio») es un texto
  visible, no solo la posición de un interruptor.

---

## Fuera de alcance — ¿qué NO hará?

- Aplicaciones TOTP, llaves de seguridad o códigos de respaldo (fuera de alcance de SPEC-09 y SPEC-10).
- Elegir el canal (correo o SMS): el contrato de `/otp/habilitar` no lo recibe.
- Verificar el celular (acuerdo A2, H-03).
- Que un administrador desactive el segundo factor de otra cuenta.

---

## Huecos detectados en el backend

- **H-01** — `Usuario` no incluye `mfaHabilitado`: la pantalla no puede saber
  si mostrar «Activar» o «Desactivar».
- **H-02** — `POST /auth/otp/habilitar` envía un código, pero no hay endpoint
  que lo reciba para completar la activación. `POST /auth/otp/deshabilitar` no
  recibe código, aunque RF-10.3 exige verificarlo.

---

## Lista de completitud

- [ ] H-01 y H-02 resueltos en el contrato antes de implementar
- [ ] Cada estado de la pantalla está implementado
- [ ] Un rol de gestión muestra «Obligatorio» sin opción de desactivar
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Existe un wireframe y coincide con la implementación
