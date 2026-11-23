# ADR-010 — Plataforma de API Management

| Campo | Valor |
|---|---|
| **Estado** | Aceptada; pendiente de comprobar al crear la instancia (ver «Por comprobar») |
| **Fecha** | 10-nov-2026 (H5, FEAT-19-T1) |
| **Decide** | Sergio Alejandro Osorio Montenegro (PO) |

## Contexto

La rúbrica de H5 pide «uso de API Management»: publicar la API en una plataforma
de gestión, asegurarla, limitar su uso y monitorearla. Hoy la API de G7 se
consume directo en `https://g7-auth-service.onrender.com` y no hay nada de eso.

Condiciones del equipo:

- No hay presupuesto. La plataforma debe poder usarse sin pagar y, de
  preferencia, sin registrar una tarjeta.
- Seis módulos ya consumen la URL directa. Ninguno puede verse obligado a
  cambiar antes del 23-nov.
- El contrato está en OpenAPI 3.1 y lo edita solo el PO.
- Quedan trece días y FEAT-19 tiene un solo responsable.

## Opciones

| Opción | Costo para el equipo | Límite de uso | Monitoreo | Problema principal |
|---|---|---|---|---|
| **Azure API Management, plan Consumption** | Sin costo con Azure for Students: no pide tarjeta, da 100 USD de crédito y el plan Consumption incluye un millón de llamadas gratis al mes | Política `rate-limit` por suscripción, disponible en Consumption | Métricas de Azure Monitor; Application Insights opcional | No admite `rate-limit-by-key`; el primer arranque tras un periodo sin uso es lento |
| AWS API Gateway | Créditos para cuentas nuevas, pero el alta pide tarjeta | Planes de uso con cuota y ráfaga | CloudWatch | Nadie del equipo tiene cuenta; la tarjeta es un bloqueo |
| Apigee (Google Cloud) | Evaluación de duración limitada | Políticas de cuota y de picos | Panel propio, el más completo | La evaluación caduca y la puesta en marcha es la más lenta |

## Decisión

**Azure API Management en plan Consumption**, con la suscripción Azure for
Students de un integrante.

- Es una de las tres plataformas que la rúbrica nombra como ejemplo.
- Es la única de las tres que el equipo puede usar sin tarjeta.
- El plan Consumption no tiene costo fijo: se paga por llamada y el volumen de
  la demostración queda muy por debajo del millón gratuito.
- Cubre los cuatro puntos de la rúbrica con piezas propias de la plataforma:
  importar el OpenAPI, exigir una clave de suscripción, aplicar `rate-limit` y
  mostrar las peticiones en sus métricas.

## Cómo se usa

- **La URL directa no cambia.** La pasarela es una segunda entrada a la misma
  API. Ningún módulo está obligado a migrar; quien quiera usarla pide su clave.
- **La pasarela no reemplaza la seguridad de G7.** Reenvía la cabecera
  `Authorization` tal cual. El backend sigue validando el token, el scope y el
  permiso. La clave de suscripción identifica al consumidor ante la pasarela;
  no sustituye al token.
- **Solo se publican por la pasarela las rutas de integración y de sesión** que
  consumen otros módulos (`/auth/token`, `/auth/introspeccion`, JWKS,
  descubrimiento, `/usuarios/{id}`, `/usuarios/lote`, direcciones, `/roles`,
  `/permisos`, login, refresh y logout). Las rutas de administración quedan
  fuera: solo las usa la SPA de G7.
- **Límite de la demostración:** 30 peticiones por minuto por suscripción. Una
  petición por encima responde `429` con `Retry-After`.
- La configuración vive en el repo backend, carpeta `infra/api-management/`,
  como plantilla Bicep, política XML y guiones de prueba. Nada con claves.

## Por comprobar al crear la instancia

No se pudo comprobar desde el repositorio; se resuelve en FEAT-19-T2:

1. Que la cuenta institucional del integrante califica para Azure for Students.
2. Que la importación acepta el contrato. El contrato es OpenAPI 3.1 y la
   documentación consultada solo confirma OpenAPI 3. Por eso el repo incluye un
   guion que genera una versión 3.0 reducida solo con las rutas publicadas.
3. Que las métricas de Azure Monitor del plan Consumption bastan como «panel de
   uso». Si no, se activa Application Insights, que tiene capa gratuita.

**Plan B**, si el punto 1 falla antes del vie 13-nov: AWS API Gateway con la
tarjeta de un integrante y alerta de presupuesto en 1 USD. La política y los
guiones de prueba se reutilizan; cambia solo la plantilla.

## Consecuencias

- Aparece una dependencia más para quien use la pasarela: si Azure falla, la
  URL directa sigue disponible.
- El límite por suscripción no distingue rutas. Para H5 es suficiente.
- La pasarela añade latencia. Las pruebas de rendimiento de FEAT-17 se hacen
  contra la URL directa, y se anota una medición por la pasarela como dato.
- La clave de suscripción es un secreto nuevo: se entrega por canal privado y
  no se escribe en evidencias.

## Fuentes

- [Políticas de API Management por plan](https://learn.microsoft.com/azure/api-management/api-management-policies)
- [Política `rate-limit`](https://learn.microsoft.com/azure/api-management/rate-limit-policy)
- [Azure for Students](https://azure.microsoft.com/en-us/free/students)
- [Precios de Amazon API Gateway](https://aws.amazon.com/api-gateway/pricing/)
