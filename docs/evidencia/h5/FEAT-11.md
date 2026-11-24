# FEAT-11 — Ronda funcional de Marketplace con G7 (H5)

**Responsable:** Luis David Morales Brenis. **Corte:** 23-nov-2026, hora de Lima.
**Procedencia:** reporte de Jim (G1) en la coordinación. Continúa la [evidencia de H4](../h4/FEAT-11.md).

## Ronda del 23-nov

G1 reportó el inicio a las 16:53 de Lima, 19 solicitudes y ningún `429`.
Usó cuentas de prueba y una dirección de prueba. El SHA de G7 confirmado para
ese día en Render fue `05ad567d16d1323c1b13711aeb83f6ee5f041254`. La ronda no
incluyó carga, RabbitMQ ni una compra en Ventas.

| Caso de G1 | Resultado reportado |
|---|---|
| F-001 Registro y verificación | Registro `201` y duplicado `409`. La primera confirmación respondió `410 ENLACE_YA_USADO` a las 16:54. La mini-ronda confirmó la cuenta con resultado `204`. |
| F-002 Login y MFA | 6/6 correctos. |
| F-003 Logout | 2/2 correctos, incluye rechazo del refresco tras el cierre. |
| F-004 Recuperación | Solicitud `202`. |
| F-005 Restablecimiento | Restablecimiento e ingreso con la nueva contraseña correctos. Reúso rechazado con `401 TOKEN_RECUPERACION_INVALIDO`. |
| F-022 Direcciones | 3/3 correctos: alta y listado propios, y rechazo entre cuentas con `403 SCOPE_INSUFICIENTE`. |

## Mini-ronda del 23-nov

G1 reportó tres solicitudes a las **22:14 de Lima** (24-nov, 03:14 UTC), sin
`429`. Los tres resultados coincidieron con lo esperado:

| Solicitud | Resultado reportado |
|---|---|
| `POST /auth/verificar-correo` con el enlace original emitido a las 16:53 | `204` |
| `POST /password/restablecer` con un enlace de recuperación vencido | `410 TOKEN_RECUPERACION_EXPIRADO` |
| `POST /password/recuperar` con un alias de prueba sin registrar, según G1 | `202` |

G1 confirmó que la cuenta verificada era la misma que recibió el primer
`410`. Antes de la mini-ronda, G7 comprobó que su enlace original seguía
vigente y sin usar. G1 considera probable haber pegado otro token en el
primer intento. El `204` posterior acredita la verificación con el enlace
original. La causa exacta del primer rechazo queda sin confirmar.

G1 informó que con esta mini-ronda da por cerrados F-001 a F-005 y F-022.

## Pendientes de FEAT-11

- **T6 · Favoritos A/B:** falta la prueba desplegada de los favoritos de G1.
  El `403` de direcciones comprueba F-022.
- **T7 · Catálogo:** `modulo-marketplace` tiene concedido `catalogo:leer` con
  audiencia `api-productos`. Falta registrar la llamada real a G6 y el caso
  fuera de scope ([clientes técnicos H5](../../integracion/hito-5-clientes-tecnicos.md)).
- La compra Marketplace → Ventas tiene su propio seguimiento en
  [FEAT-20-T2](FEAT-20.md#feat-20-t2--prueba-desplegada-con-marketplace-g1).

Cada resultado refleja únicamente el recorrido reportado o consultado. No
se publican correos, contraseñas, tokens ni enlaces de un solo uso.
