# FRONT-03 — Verificación de correo (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-03-verificacion-correo.md` |
| Specs de backend | SPEC-02 (verificación y reenvío) · SPEC-16 (cambio de correo) |
| Responsable | Por asignar |

---

## Objetivo

Dos entradas:

1. **Tras registrarse** (FRONT-02) o desde «¿No te llegó el correo de
   verificación?» de FRONT-01: esperar el correo y poder pedir otro.
2. **Desde el enlace del correo** (trae el token): confirmar el correo y pasar
   al inicio de sesión. El mismo enlace confirma un cambio de correo (RF-16.6);
   la pantalla no distingue los casos.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir con token en URL | `POST /api/v1/auth/verificar-correo` | SPEC-02, SPEC-16 |
| Al pulsar «Reenviar enlace» | `POST /api/v1/auth/verificar-correo/reenviar` | SPEC-02 |

---

## Archivos propuestos

```
frontend/src/pantallas/verificacion/
├── VerificacionCorreoPage.tsx   # pantalla única con sus dos entradas
└── useVerificacionCorreo.ts     # detecta token en URL, verifica, reenvía
```

Reutiliza de FRONT-00: `api`, `errores`, `mascara` (enmascarar correo) y §1.5
(quitar token de la URL).

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Revisa tu correo | desde FRONT-02 con correo en memoria | «Te enviamos un enlace a m\*\*\*a@ejemplo.com.», vence en 24 h, revisar spam, «Reenviar enlace» |
| Reenvío | sin correo en memoria | campo de correo + «Enviar un enlace nuevo» |
| Enviando | tras reenviar | botón «Enviando…» |
| Reenvío aceptado | `202` | mensaje neutro; botón disponible |
| Demasiadas solicitudes | `429` | mensaje; reenvío deshabilitado |
| Verificando | con token, esperando backend | «Estamos verificando tu correo…» |
| Verificado | `204` | «Tu correo quedó verificado.» + «Iniciar sesión» (o «Ir a Mi cuenta» si hay sesión) |
| Enlace vencido | `410 ENLACE_EXPIRADO` | explicación + formulario de reenvío |
| Enlace ya usado | `410 ENLACE_YA_USADO` | explicación + «Iniciar sesión» + reenvío |
| Enlace incompleto | token vacío | igual que vencido, sin llamar al backend |

---

## Validaciones del cliente

| Campo | Regla | Mensaje |
|---|---|---|
| Correo (reenvío) | obligatorio, email | «Escribe un correo válido, como nombre@dominio.com.» |

Enmascarado: primera y última letra de la parte local + dominio completo
(`m***a@ejemplo.com`).

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `202` reenvío | «Si hay una cuenta pendiente… te enviamos un enlace nuevo.» (neutro) |
| `DEMASIADAS_SOLICITUDES` | «Ya pediste varios enlaces…» |
| `204` | «Tu correo quedó verificado.» |
| `ENLACE_EXPIRADO` | «Este enlace venció…» |
| `ENLACE_YA_USADO` | «Este enlace ya se usó…» |

El mensaje de reenvío es condicional: no afirma que la cuenta exista (RF-02.4).

---

## Fuera de alcance

Mostrar cuánto falta para vencer o cuántos reenvíos quedan; verificar el celular
(A2); verificar sin enlace.

## Hueco detectado

- **H-05**: `DEMASIADAS_SOLICITUDES` no indica cuándo reintentar.
