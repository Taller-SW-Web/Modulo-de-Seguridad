# FRONT-04 — Desafío del código de un solo uso

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-09, SPEC-07 (caducidad) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 4 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Quien tiene el segundo factor activo acaba de acertar su contraseña en FRONT-01
y todavía no tiene sesión: solo un **desafío** (`challengeToken`). Aquí pide el
código de 6 dígitos, lo escribe y, si es correcto, recibe la sesión. Solo se
llega desde FRONT-01; entrar por la URL sin un desafío en memoria devuelve al
inicio de sesión.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir la pantalla, y al pulsar «Enviar otro código» | `POST /api/v1/auth/otp/solicitar` con `challengeToken` y `canal` | SPEC-09 |
| Al completar los 6 dígitos o pulsar «Verificar» | `POST /api/v1/auth/otp/verificar` con `challengeToken` y `codigo` | SPEC-09 |

El login ya **no** envía el código: `DesafioMfa.canal` es solo el canal
preferido y el envío lo dispara esta pantalla.

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Enviando código | Al abrir, mientras responde `/otp/solicitar` | Indicador «Enviando tu código…» |
| Esperando el código | `202` de `/otp/solicitar` | «Escribe el código que enviamos a {destinoEnmascarado}.», seis casillas, cuenta atrás de validez del código desde `expiraEn`, «Te quedan 3 intentos», botón «Verificar» y enlace «Enviar otro código» |
| Verificando | Tras completar el código | Casillas de solo lectura e indicador |
| Código incorrecto | `401 CODIGO_INVALIDO` | Mensaje con `intentosRestantes`; casillas vacías y foco en la primera |
| Intentos agotados | `401 OTP_INTENTOS_AGOTADOS` | Mensaje, casillas deshabilitadas y botón destacado «Enviar otro código» |
| Código vencido | `410 OTP_EXPIRADO`, o la cuenta atrás llega a cero | Mensaje, casillas deshabilitadas y botón destacado «Enviar otro código» |
| Demasiadas solicitudes | `429 DEMASIADAS_SOLICITUDES` en `/otp/solicitar` | Mensaje y «Enviar otro código» deshabilitado. Si queda un código vigente, se puede seguir escribiendo |
| Desafío vencido | `401 TOKEN_INVALIDO`, o pasó el `expiraEn` del desafío que dio el login | Vuelve a FRONT-01 con el aviso «El tiempo para ingresar el código terminó. Inicia sesión de nuevo.» |
| Contraseña caducada | `403 PASSWORD_CADUCADA` en `/otp/verificar` | El mismo panel que FRONT-01 (estado «Contraseña caducada») |
| Éxito | `200` con `SesionIniciada` | Se guarda la sesión y se navega al destino (FRONT-00 §1.4) |

**Dos relojes distintos.** El desafío del login dura `DesafioMfa.expiraEn`
segundos; cada código dura el `expiraEn` de `/otp/solicitar` (300). La cuenta
atrás visible es la del código. Si vence el desafío, ningún código sirve y se
vuelve a FRONT-01.

**Intentos.** El contador arranca en 3 (RF-09.5) porque `/otp/solicitar` no
devuelve el número; después se toma siempre de `intentosRestantes`. Un código
nuevo vuelve a mostrar 3.

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Código | Solo dígitos; cada casilla acepta uno | — (los demás caracteres no se escriben) | `pattern: '^[0-9]{6}$'` |
| Código | Exactamente 6 dígitos para enviar | «Escribe los 6 dígitos del código.» | RF-09.1 |

- Al pegar un código de 6 dígitos en cualquier casilla, se reparte en las seis.
- Al completar la sexta casilla se envía solo; el botón «Verificar» queda para
  quien prefiera pulsarlo.

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `202` | «Escribe el código que enviamos a {destinoEnmascarado}. Vence en {mm:ss}.» |
| `CODIGO_INVALIDO` | «El código no es correcto. Te quedan {intentosRestantes} intentos.» (con 1: «Te queda 1 intento.») |
| `OTP_INTENTOS_AGOTADOS` | «Agotaste los intentos para este código. Pide uno nuevo.» |
| `OTP_EXPIRADO` | «El código venció. Pide uno nuevo.» |
| `DEMASIADAS_SOLICITUDES` | «Pediste varios códigos seguidos. Espera unos minutos antes de pedir otro.» |
| `TOKEN_INVALIDO` | Aviso en FRONT-01: «El tiempo para ingresar el código terminó. Inicia sesión de nuevo.» |
| `PASSWORD_CADUCADA` | El de FRONT-01 |
| `NO_DISPONIBLE` | Según FRONT-00 §2 |

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-04.1 Código correcto
- **Dado** un vendedor que acertó su contraseña y tiene el segundo factor activo
- **Cuando** llega a esta pantalla y escribe el código recibido
- **Entonces** la sesión se abre sin pulsar ningún botón y ve *Mi cuenta*.

### UI-04.2 Código incorrecto con intentos disponibles
- **Dado** un código vigente
- **Cuando** el usuario escribe seis dígitos equivocados
- **Entonces** ve «El código no es correcto. Te quedan 2 intentos.», las
  casillas se vacían y el foco vuelve a la primera.

### UI-04.3 Tercer fallo *(caso borde)*
- **Dado** un código con dos fallos
- **Cuando** el usuario vuelve a equivocarse
- **Entonces** ve «Agotaste los intentos…», las casillas se deshabilitan y
  «Enviar otro código» queda destacado.

### UI-04.4 La cuenta atrás llega a cero *(caso borde)*
- **Dado** un código pedido hace 5 minutos
- **Cuando** la cuenta atrás termina
- **Entonces** las casillas se deshabilitan y aparece «El código venció. Pide
  uno nuevo.» sin esperar a que el usuario envíe nada.

### UI-04.5 Cuarto código en 15 minutos *(caso borde)*
- **Dado** que el usuario ya pidió tres códigos en 15 minutos
- **Cuando** pulsa «Enviar otro código»
- **Entonces** ve el mensaje de demasiadas solicitudes y el enlace queda deshabilitado.

### UI-04.6 Recargar la página *(caso borde)*
- **Dado** el estado «Esperando el código»
- **Cuando** el usuario recarga
- **Entonces** vuelve a FRONT-01: el desafío solo vivía en memoria.

### UI-04.7 Pegar el código
- **Dado** que el usuario copió `483920` de su correo
- **Cuando** lo pega en la primera casilla
- **Entonces** las seis casillas se llenan y se envía la verificación.

### UI-04.8 Contraseña caducada tras el código *(caso borde)*
- **Dado** un `GESTOR_DESPACHO` con la contraseña de más de 90 días
- **Cuando** escribe el código correcto
- **Entonces** ve el panel «Tu contraseña caducó» y no recibe sesión.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- Las seis casillas forman un grupo con la etiqueta «Código de verificación
  de 6 dígitos»; cada una tiene `aria-label="Dígito N de 6"`. La primera usa
  `autocomplete="one-time-code"` e `inputmode="numeric"`.
- Retroceso en una casilla vacía mueve el foco a la anterior; las flechas
  mueven entre casillas.
- La cuenta atrás **no** se anuncia cada segundo: se anuncia a 60 segundos del
  final y al vencer, con `aria-live="polite"`.
- Variante móvil de 390 px obligatoria: las seis casillas caben en una fila.

---

## Fuera de alcance — ¿qué NO hará?

- Activar o desactivar el segundo factor (FRONT-09).
- Aplicaciones TOTP, llaves de seguridad o códigos de respaldo (fuera de
  alcance de SPEC-09).
- Mostrar el código en pantalla, ni siquiera en desarrollo: el canal SMS
  simulado tiene su propio mecanismo de pruebas (ESC-09.2).
- Elegir canal si la cuenta no tiene celular: ver H-06.

---

## Huecos detectados en el backend

- **H-05** — `DEMASIADAS_SOLICITUDES` no dice cuándo reintentar, aunque
  ESC-09.7 pide indicarlo.
- **H-06** — No está escrito si pedir un código nuevo invalida el anterior, ni
  qué responde `/otp/solicitar` con `canal: SMS` en una cuenta sin celular.
  Hasta que se aclare, la pantalla no ofrece cambiar de canal y trata cada
  código pedido como el único válido.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Cada código de error del backend tiene su mensaje
- [ ] La cuenta atrás es la del código y el vencimiento del desafío devuelve a FRONT-01
- [ ] El `challengeToken` solo vive en memoria
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
