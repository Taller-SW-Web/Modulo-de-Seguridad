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
| F-001 Registro y verificación | Registro `201` y duplicado `409`. Confirmación `410 ENLACE_YA_USADO` a las 16:54. Se coordinó repetir el caso. |
| F-002 Login y MFA | 6/6 correctos. |
| F-003 Logout | 2/2 correctos, incluye rechazo del refresco tras el cierre. |
| F-004 Recuperación | Solicitud `202`. |
| F-005 Restablecimiento | Restablecimiento e ingreso con la nueva contraseña correctos. Reúso rechazado con `401 TOKEN_RECUPERACION_INVALIDO`. |
| F-022 Direcciones | 3/3 correctos: alta y listado propios, y rechazo entre cuentas con `403 SCOPE_INSUFICIENTE`. |

La causa del `410` sigue por confirmar. En la cuenta candidata, G7 observó un
token de verificación emitido a las 16:53:08, vigente durante el intento y
sin uso registrado. Queda corroborar con G1 que esa era la cuenta del intento.
La mini-ronda acordada cubre verificación, recuperación con enlace vencido y
solicitud de recuperación con un alias de prueba sin registrar. Al corte no
se incorporaron resultados de esa mini-ronda.

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
