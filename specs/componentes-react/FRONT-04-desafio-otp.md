# FRONT-04 — Desafío del código de un solo uso (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-04-desafio-otp.md` |
| Specs de backend | SPEC-09 (OTP), SPEC-07 (caducidad) |
| Responsable | Por asignar |

---

## Objetivo

Quien tiene segundo factor ya acertó su contraseña en FRONT-01 y tiene un
**desafío** (`challengeToken`), no una sesión. Aquí pide el código de 6 dígitos,
lo escribe y, si es correcto, recibe la sesión. Solo se entra desde FRONT-01;
por URL sin desafío en memoria → vuelve a FRONT-01.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir y al pulsar «Enviar otro código» | `POST /api/v1/auth/otp/solicitar` | SPEC-09 |
| Al completar 6 dígitos o «Verificar» | `POST /api/v1/auth/otp/verificar` | SPEC-09 |

El login **no** envía el código: `DesafioMfa.canal` es solo el preferido; el
envío lo dispara esta pantalla.

---

## Archivos propuestos

```
frontend/src/pantallas/otp/
├── OtpPage.tsx           # pantalla: PinInput de 6 dígitos, cuenta atrás, estados
├── useDesafioOtp.ts      # solicita/verifica el código, maneja los dos relojes
└── CuentaRegresiva.tsx   # cuenta atrás del código (aria-live a 60s y al vencer)
```

Reutiliza de FRONT-00: `api`, `errores`, `sesion` (guardar al éxito).

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `OtpPage` | `PinInput` de 6 casillas, botón «Verificar», enlace «Enviar otro código» |
| `useDesafioOtp` | Guarda `challengeToken` en memoria; llama `api.otpSolicitar` y `api.otpVerificar`; mantiene `intentosRestantes` |
| `CuentaRegresiva` | Cuenta atrás desde `expiraEn` (del código); al llegar a cero deshabilita |

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Enviando código | al abrir | «Enviando tu código…» |
| Esperando el código | `202` | «Escribe el código que enviamos a {destino}», 6 casillas, cuenta atrás, «Te quedan 3 intentos», «Verificar», «Enviar otro código» |
| Verificando | completo | casillas solo lectura |
| Código incorrecto | `401 CODIGO_INVALIDO` | mensaje con `intentosRestantes`, casillas vacías, foco en la primera |
| Intentos agotados | `401 OTP_INTENTOS_AGOTADOS` | casillas deshabilitadas, «Enviar otro código» destacado |
| Código vencido | `410 OTP_EXPIRADO` o cuenta atrás a cero | mensaje, casillas deshabilitadas, «Enviar otro código» |
| Demasiadas solicitudes | `429` en solicitar | mensaje, «Enviar otro código» deshabilitado |
| Desafío vencido | `401 TOKEN_INVALIDO` o pasó `expiraEn` del login | vuelve a FRONT-01 con aviso |
| Contraseña caducada | `403 PASSWORD_CADUCADA` | panel igual a FRONT-01 |
| Éxito | `200 SesionIniciada` | guarda sesión y navega |

**Dos relojes**: el desafío (`DesafioMfa.expiraEn`) y el código (`expiraEn` de
solicitar, 300 s). La cuenta atrás visible es la del código.

---

## Validaciones del cliente

| Campo | Regla |
|---|---|
| Código | solo dígitos, 6 casillas; `pattern: '^[0-9]{6}$'` |
| Pegar | un código de 6 dígitos pegado se reparte en las casillas |
| Envío | al completar la sexta casilla se envía solo |

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `202` | «Escribe el código que enviamos a {destino}. Vence en {mm:ss}.» |
| `CODIGO_INVALIDO` | «El código no es correcto. Te quedan {n} intentos.» |
| `OTP_INTENTOS_AGOTADOS` | «Agotaste los intentos…» |
| `OTP_EXPIRADO` | «El código venció…» |
| `DEMASIADAS_SOLICITUDES` | «Pediste varios códigos…» |
| `TOKEN_INVALIDO` | aviso en FRONT-01 |
| `PASSWORD_CADUCADA` | el de FRONT-01 |

---

## Accesibilidad

- Grupo con etiqueta «Código de verificación de 6 dígitos»; cada casilla
  `aria-label="Dígito N de 6"`; `autocomplete="one-time-code"` e
  `inputmode="numeric"`.
- Retroceso en casilla vacía → anterior; flechas entre casillas.
- Cuenta atrás anunciada a 60 s del final y al vencer (`aria-live="polite"`).
- Móvil 390 px: seis casillas en una fila.

## Huecos detectados

- **H-05**: `DEMASIADAS_SOLICITUDES` no indica cuándo reintentar.
- **H-06**: no está escrito si un código nuevo invalida el anterior; la pantalla
  no ofrece cambiar de canal y trata cada código como el único válido.
