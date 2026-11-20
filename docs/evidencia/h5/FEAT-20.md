# FEAT-20 — Compra por canal con la misma identidad de G7

**Responsable:** Luis David Morales Brenis. **Corte:** 19-nov-2026, hora de Lima.
**Estado:** G5 reportó sus versiones y la configuración. La fecha y la prueba
con Marketplace (G1) siguen pendientes.

## Fuentes y versiones

| Componente | URL y versión | Procedencia |
|---|---|---|
| G5 Ventas (M1) | [Servicio](https://g5-servicio-ventas.onrender.com) · [`f3b3ebb`](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/commit/f3b3ebbbb1af03bca3d998f90b48e38baa39a088), merge del PR backend G5 #70 | SHA Live reportado por G5 el 17-nov. Se comprobó que el commit existe en GitHub. |
| G5 API Gateway | [Gateway](https://g5-api-gateway.onrender.com) · SHA del gateway por confirmar | URL reportada por G5 el 17-nov. |
| G7 Seguridad | [API](https://g7-auth-service.onrender.com/api/v1) · SHA Live por registrar al ejecutar | El [descubrimiento público](https://g7-auth-service.onrender.com/api/v1/auth/.well-known/openid-configuration) confirma `issuer` `https://g7-auth-service.onrender.com/api/v1/auth` y el [JWKS](https://g7-auth-service.onrender.com/api/v1/auth/.well-known/jwks.json). |
| G1 Marketplace | URL y versiones Live del frontend y del BFF por confirmar | Coordinación en [docs #101](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/101). |
| Contrato de G5 | [Contrato de Ventas §§1.1 y 1.4](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/a6468b18bf18d4004c29a430ef275e359055e07c/specs/api/api-ventas.md) y [acuerdo con G7](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/a6468b18bf18d4004c29a430ef275e359055e07c/integraciones/ACUERDO_G7_SEGURIDAD.md) | Documentación fijada al commit `a6468b1`. |

## FEAT-20-T1 — Confirmación de G5

G5 informó el 17-nov por coordinación privada que `POST /api/v1/pedidos` y
`GET /api/v1/pedidos/me` devuelven `401` sin token tanto en el servicio de Ventas
como en el gateway. También reportó que validan el canal contra el `client_id`
del token y registran el `client_id` en cada creación (RNF-03). La validación de
`clienteId` contra Seguridad sigue en evaluación por G5.

**Nivel:** resultados reportados por G5. Queda confirmar con G5 y G1 el día y
la hora de Lima en la ventana del 19 o 20 de noviembre. La coordinación con G5
se sigue en [docs #100](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/100).

## FEAT-20-T2 — Prueba desplegada con Marketplace (G1)

**Ejecución:** pendiente. Participan G1 (compra desde el canal), G5 (resultado en
Ventas) y G7 (identidades y evidencia). Antes de empezar, registrar hora de Lima,
los SHA Live y la URL de cada componente que intervenga. Usar dos cuentas de
prueba `CLIENTE` de G7, A y B, y datos de compra de prueba acordados con G5.

| Paso | Resultado esperado | Obtenido |
|---|---|---|
| 1. G1 obtiene un token de servicio fresco de `modulo-marketplace`. | G7 emite `200`, con `ventas:pedidos:crear` y `api-ventas` en `aud`. | Pendiente |
| 2. A inicia sesión en Marketplace y crea un pedido de prueba. | G1 llama a `POST /api/v1/pedidos` con su token de servicio y `contacto.clienteId` igual al `sub` de A. G5 devuelve `201` en la primera creación. | Pendiente |
| 3. Consultar `GET /api/v1/pedidos/me` en G5 con el token de usuario de A. | `200` y el pedido del paso 2 aparece en la respuesta. | Pendiente |
| 4. Repetir la consulta con el token de usuario de B. | `200` y el pedido del paso 2 queda fuera de la respuesta. Revisar las páginas necesarias para acreditar la ausencia. | Pendiente |

Conservar el mismo `pedidoId` como referencia en los pasos 2 a 4. Registrar
solo datos sanitizados de la prueba, sin tokens, secretos ni datos personales.
Si la emisión funciona y Ventas rechaza la creación, anotar el HTTP, el código
de error y la ruta para que G5 revise esa llamada antes de repetir la compra.

## FEAT-20-T3 — Retail (G4)

**Reportado por G4 el 19-nov:** [POS](https://g4-retail-frontend-gamma.vercel.app)
en `e59e039` y [API](https://g4-retail-backend-production.up.railway.app) en
`cae1c8a`. G4 informó que su backend usa `client_credentials` con
`modulo-retail` y que la creación en G5 con `canal: RETAIL` e
`Idempotency-Key` funcionó con pedidos reales. Los SHA Live y el resultado
provienen del reporte de G4.

**Identidad G7 pendiente.** El POS usa un identificador de su padrón de
mostrador como `contacto.clienteId`. Para la prueba conjunta, G4 obtendrá el
UUID desde la búsqueda por documento o el alta asistida de G7 con el token
del vendedor. G4 enviará ese UUID al crear el pedido. La cuenta compradora
debe ver el pedido en `/api/v1/pedidos/me` con su token de usuario G7 y una
segunda cuenta debe quedar sin acceso a ese pedido. Registrar los HTTP, las
versiones Live y el canal, sin adjuntar documentos, tokens ni datos personales.

**Prueba por API acordada con G4:** se propone el viernes 20 de noviembre,
en forma asíncrona. Falta fijar con G5 la hora de Lima. G4 confirmó que ya
registró y verificó en G7 la cuenta `CLIENTE` compradora (A). G7 aporta una
segunda cuenta `CLIENTE` de prueba (B).

1. G4 crea el pedido por `POST /api/v1/retail/ventas` de su backend desplegado.
   Su backend llama a G5 con `modulo-retail`, `canal: RETAIL` y el UUID de A
   como `contacto.clienteId`.
2. G4 comparte por coordinación privada el `pedidoId`, la hora de Lima y la
   `Idempotency-Key`. G5 verifica el canal y el vínculo con el UUID de A.
3. G4 consulta `GET /api/v1/pedidos/me` con la cuenta A y confirma que aparece el
   pedido. G7 consulta con B y comprueba que el mismo pedido queda fuera.

Registrar los HTTP y el resultado por cuenta, con los datos del pedido
sanitizados. La ejecución sigue pendiente. El flujo del POS de búsqueda y alta
asistida se verifica cuando estén disponibles las dos rutas.

**Seguimiento:** [docs #103](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/103)
y [ajuste de alcance para Retail](../../../plan/hito-5/DECISIONES-PO.md#8-ajuste-del-19-nov-para-retail).

## FEAT-20-T4 — Chatbot (G3)

**No probado al corte.** No consta confirmación de URL y ventana de prueba en
[docs #102](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/102).

La [decisión del PO](../../../plan/hito-5/DECISIONES-PO.md#g3-y-g4-en-la-compra-por-canal-feat-20)
fija Marketplace como canal obligatorio. G3 queda pendiente de confirmar
disponibilidad. El ajuste de G4 se registra en la sección de Retail.
