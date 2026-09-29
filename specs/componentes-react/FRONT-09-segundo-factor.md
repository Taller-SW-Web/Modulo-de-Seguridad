# FRONT-09 — Segundo factor: activar y desactivar (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-09-segundo-factor.md` |
| Specs de backend | SPEC-10 (reutiliza los códigos de SPEC-09) |
| Wireframe | Sin wireframe (hueco de `trazabilidad.md` §7) |
| Estado | **Bloqueada por H-01 y H-02** |

> Esta spec describe la pantalla que piden SPEC-10 y HU-10.1, **no la que
> permite hoy el contrato**. Donde falta un endpoint se marca ⛔, sin inventarlo.

---

## Objetivo

Un cliente o vendedor decide si quiere el segundo factor (RF-10.2/10.3). Quien
tiene un rol de gestión lo ve activo y sin opción de apagarlo (RF-10.1). Se llega
desde la sección Seguridad de *Mi cuenta* (FRONT-06).

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir, saber si está activo | ⛔ `GET /auth/me` no devuelve `mfaHabilitado` (H-01) | SPEC-16, SPEC-10 |
| «Activar» | `POST /api/v1/auth/otp/habilitar` → `202`, envía código | SPEC-10 |
| Confirmar activación con el código | ⛔ no hay endpoint (H-02) | SPEC-10 (RF-10.2) |
| «Desactivar» | `POST /api/v1/auth/otp/deshabilitar` → `204` | SPEC-10 |
| Código de confirmación de desactivación | ⛔ el contrato no lo pide (H-02) | SPEC-10 (RF-10.3) |

---

## Archivos propuestos

```
frontend/src/pantallas/segundo-factor/
├── SegundoFactorPage.tsx     # estados (obligatorio/desactivado/activo) + acciones
├── ConfirmarCodigo.tsx       # 6 casillas (reutiliza el control de FRONT-04)
└── useSegundoFactor.ts       # habilitar/deshabilitar + manejo de MFA_OBLIGATORIO
```

Reutiliza de FRONT-00: `api`, `errores`, `sesion` (roles para el estado
«Obligatorio»). Reutiliza el `OtpInput` de FRONT-04.

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `SegundoFactorPage` | Muestra el estado actual y el botón correspondiente |
| `ConfirmarCodigo` | 6 casillas iguales a FRONT-04 para confirmar la activación (⛔ H-02) |
| `useSegundoFactor` | Llama habilitar/deshabilitar; si `422 MFA_OBLIGATORIO`, pasa a «Obligatorio» |

Roles de gestión: `ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`,
`ADMIN_SISTEMA`. La pantalla los lee de la sesión para mostrar «Obligatorio» sin
esperar al `422`.

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Cargando | al abrir | indicador |
| Obligatorio | la cuenta tiene algún rol de gestión | «El segundo factor está activo y es obligatorio para tu rol.» Sin interruptor |
| Desactivado | ⛔ `mfaHabilitado=false` (H-01) | explicación + «Activar segundo factor» |
| Enviando código | tras «Activar» | «Enviando un código a tu correo…» |
| Confirmar activación | `202` de `/otp/habilitar` | 6 casillas + «Confirmar». ⛔ H-02 |
| Activado | tras confirmar | «El segundo factor está activo…» + «Desactivar» |
| Confirmar desactivación | «Desactivar» | diálogo; RF-10.3 añade código. ⛔ H-02 |
| Desactivado tras confirmar | `204` | «Desactivaste el segundo factor…» |
| Rechazado por el rol | `422 MFA_OBLIGATORIO` | mensaje + pasa a «Obligatorio» |

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Código | 6 dígitos (control de FRONT-04) | «Escribe los 6 dígitos del código.» |

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `202` `/otp/habilitar` | «Te enviamos un código a tu correo…» |
| `MFA_OBLIGATORIO` | «Tu rol exige el segundo factor; no puedes desactivarlo.» |
| `CODIGO_INVALIDO`, `OTP_EXPIRADO`, `OTP_INTENTOS_AGOTADOS`, `DEMASIADAS_SOLICITUDES` | los de FRONT-04 (si H-02 los devuelve) |
| `NO_DISPONIBLE` | FRONT-00 §2 |

Diálogo: «¿Desactivar el segundo factor? A partir de ahora, quien conozca tu
contraseña podrá entrar en tu cuenta.» · «Desactivar» / «Cancelar».

---

## Accesibilidad

- El estado actual («Activo»/«Desactivado»/«Obligatorio») es texto visible, no
  solo la posición de un interruptor.
- El código, igual que FRONT-04.

## Fuera de alcance

TOTP, llaves de seguridad, códigos de respaldo; elegir canal; verificar celular
(A2, H-03); que un administrador desactive el 2FA de otra cuenta.

## Huecos detectados (bloquean la pantalla)

- **H-01**: `Usuario` no incluye `mfaHabilitado`.
- **H-02**: `/otp/habilitar` envía un código sin endpoint que lo reciba;
  `/otp/deshabilitar` no recibe código aunque RF-10.3 lo exige.
