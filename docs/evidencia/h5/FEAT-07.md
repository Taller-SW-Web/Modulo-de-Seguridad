# FEAT-07 — Casos negativos del segundo factor en el despliegue

Responsable: Luis David Morales Brenis. Corte: 12-nov-2026, America/Lima.
Estado: **FEAT-07-T5 cumplida: los casos negativos del OTP de login responden
el error del catálogo en el despliegue.**

Continúa la [evidencia H4](../h4/FEAT-07.md). Allí quedó comprobado en el
despliegue el recorrido positivo, pero no los casos negativos: código erróneo,
vencido, agotado y cuota.

## Fuentes y versiones

| Componente | Referencia |
|---|---|
| Especificación | [SPEC-09](../../../specs/funcionales/SPEC-09-mfa-inicio-sesion.md) y [catálogo de errores](../../../specs/catalogo-errores.md) |
| Backend desplegado | `44f7d2953b4e8727c4f566a3be849ab25171c66a` en `develop` (merge del backend [#90](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/90)), desplegado por el [run 43109130867](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/43109130867) con smoke en verde |
| URL | https://g7-auth-service.onrender.com/api/v1 |
| Guion | [`guiones/FEAT-07-casos-negativos-mfa.ps1`](guiones/FEAT-07-casos-negativos-mfa.ps1) |

## Ejecución

**Nivel de evidencia:** propia desplegada. **Fecha:** 12-nov-2026, de 14:54 a
15:04, America/Lima. **Ejecutó:** David, con su cuenta `ADMIN_SISTEMA`, que
tiene el segundo factor obligatorio.

El guion pide la contraseña y los códigos con `Read-Host -AsSecureString` y solo
imprime estados HTTP y códigos de error. No hay contraseñas, códigos ni tokens en
esta evidencia. Los cinco casos corrieron seguidos, porque el último depende de
la cuota de 3 códigos cada 15 minutos.

Llegaron exactamente 3 correos, a las 14:55, 14:56 y 14:57: uno por cada uno de
los casos A, B y C. Los casos D y E no enviaron correo.

| Caso | Escenario | Paso | Esperado | Obtenido |
|---|---|---|---|---|
| D | ESC-09.11 | Solicitar código con el desafío alterado en un carácter | `401 TOKEN_INVALIDO` | `401 TOKEN_INVALIDO` |
| D | ESC-09.11 | Verificar con el desafío alterado | `401 TOKEN_INVALIDO` | `401 TOKEN_INVALIDO` |
| D | ESC-09.11 | Verificar con un desafío inventado | `401 TOKEN_INVALIDO` | `401 TOKEN_INVALIDO` |
| A | ESC-09.4 | Primer código erróneo | `401 CODIGO_INVALIDO` | `401 CODIGO_INVALIDO`, `intentosRestantes=2` |
| A | ESC-09.4 | Segundo código erróneo | `401 CODIGO_INVALIDO` | `401 CODIGO_INVALIDO`, `intentosRestantes=1` |
| A | ESC-09.5 | Tercer código erróneo | `401 OTP_INTENTOS_AGOTADOS` | `401 OTP_INTENTOS_AGOTADOS` |
| A | ESC-09.5 | Código **correcto** después de agotar los intentos | `401 OTP_INTENTOS_AGOTADOS` | `401 OTP_INTENTOS_AGOTADOS` |
| B | ESC-09.3 | Código correcto | `200` | `200`, token de acceso JWS de 3 partes |
| B | ESC-09.10 | El mismo código con el mismo desafío | `401 TOKEN_INVALIDO` | `401 TOKEN_INVALIDO` |
| B | — | Cerrar la sesión abierta en el paso anterior | `204` | `204` |
| C | ESC-09.6 | Código correcto presentado después de 5 minutos | `401 TOKEN_INVALIDO` | `401 TOKEN_INVALIDO` |
| E | ESC-09.7 | Cuarta solicitud de código en 15 minutos | `429 DEMASIADAS_SOLICITUDES` | `429 DEMASIADAS_SOLICITUDES`, `Retry-After=369` |

En cada caso, el login previo respondió `200` con desafío.

El `Retry-After` cuadra con la ventana. El primer código se pidió a las 14:55:3x;
15:04:22 + 369 s = 15:10:31, quince minutos después (RF-09.16).

**Resultado:** cumple la aceptación de FEAT-07-T5. Un código erróneo, agotado,
vencido o ya usado devuelve el error del catálogo, sin emitir tokens.

## Corrección en SPEC-09

Antes de ejecutar se leyó el backend (`OtpLoginService.verificar`). Dos escenarios
de la spec no coincidían con el código, y la ejecución lo confirmó:

| Escenario | La spec decía | Responde el despliegue | Motivo |
|---|---|---|---|
| ESC-09.6 Código expirado | `410 OTP_EXPIRADO` | `401 TOKEN_INVALIDO` | El desafío dura 300 s desde el login y el código 300 s desde que se pide, así que el desafío siempre vence primero |
| ESC-09.10 Reutilización | `401 CODIGO_INVALIDO` | `401 TOKEN_INVALIDO` | La verificación correcta consume el código y también el desafío |

Las dos respuestas son seguras: no emiten tokens ni abren una segunda sesión, y
no revelan nada de la cuenta. La SPA ya trata ese `401` como desafío vencido y
vuelve al login (FRONT-04). Se corrigió la spec y no el código: SPEC-09 y el
catálogo cambian en este mismo PR. El ajuste de las descripciones del contrato
queda propuesto a Sergio en
[`feat-07-otp-vencido-y-reutilizado.md`](../../../specs/contrato-api/propuestas/feat-07-otp-vencido-y-reutilizado.md).
El `410 OTP_EXPIRADO` se sigue usando en el cambio del segundo factor (ESC-10.15).

## Límites

- Solo se probó el canal `EMAIL`. El SMS sigue simulado.
- No se probaron en el despliegue el código de otro desafío (ESC-09.9), el
  reenvío que invalida el código anterior (ESC-09.12) ni la contraseña caducada
  tras el OTP (RF-09.11). Los cubren pruebas automatizadas del backend:
  `MfaTransaccionalPostgresTest` (desafíos distintos), `OtpLoginServiceTest` y
  `OtpOutboxPostgresTest` (reenvío), y `LoginHttpTest` (contraseña caducada).
- Los casos negativos del cambio del segundo factor (SPEC-10) tampoco se probaron
  en el despliegue.
- No se revisó la auditoría (`OTP_FALLIDO`) en Supabase en esta ronda.
