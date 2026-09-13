# ADR-002 — Ofrecer dos mecanismos de validación y dejar elegir al consumidor

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | Semana 4 — septiembre de 2026 |
| **Decide** | Sergio Osorio (PO) |

## Contexto

[ADR-001](001-rs256-frente-a-hs256.md) permite que cada módulo verifique un
token por su cuenta, sin llamarnos. Eso es rápido, pero tiene un coste conocido:
un token firmado sigue siendo válido hasta que vence aunque el usuario haya sido
bloqueado hace un minuto.

Para un módulo eso puede ser aceptable o puede no serlo, y la diferencia no la
sabemos nosotros. Que el Marketplace muestre un catálogo a alguien recién
desactivado es molesto. Que el módulo de Ventas le apruebe un reembolso es otra
cosa.

## Decisión

Publicamos **dos mecanismos** y cada módulo consumidor elige cuál usa en cada
operación:

| Mecanismo | Cuándo usarlo | Qué cuesta |
|---|---|---|
| **Validación local** con la clave pública del JWKS | En cada petición ordinaria. Es el caso normal | Nada de red. No detecta cambios de estado hasta que el token vence (15 min) |
| **Introspección remota** `POST /api/v1/auth/introspeccion` | Antes de operaciones sensibles: anulaciones, reembolsos, cambios de precio, cambios de rol | Una llamada de red y una dependencia de nuestra disponibilidad |

No imponemos cuál usar. Documentamos el coste de cada uno y la decisión queda en
el módulo que conoce el riesgo de su propia operación.

## Por qué no elegir uno solo

**Obligar a validación local** dejaría al módulo de Ventas sin forma de saber si
quien pide un reembolso sigue siendo un vendedor activo.

**Obligar a introspección** pondría una llamada de red nuestra en el camino
crítico de cada petición de los seis módulos. Nos convertiría en el punto único
de fallo del marketplace entero: si este servicio cae, no hay catálogo, ni
carrito, ni despacho. Exactamente lo contrario de lo que busca una arquitectura
de microservicios.

## Consecuencias

- Tenemos que mantener **dos caminos** documentados y probados, no uno.
- Cada equipo consumidor recibe un **token de servicio propio**, de modo que la
  auditoría permita saber qué módulo consultó qué dato y cuándo.
- El kit de integración debe explicar el compromiso con ejemplos, no solo
  enumerar los endpoints. Un equipo que use validación local donde debía usar
  introspección tendrá un fallo de seguridad silencioso.
- La meta de rendimiento de la introspección es **menos de 200 ms en el
  percentil 95** con 50 usuarios concurrentes: si es lenta, nadie la usará.

## Tercera vía, complementaria

Publicamos además eventos `usuario.*` en RabbitMQ (`usuario.desactivado`,
`usuario.bloqueado`, `usuario.roles_cambiados`, `usuario.creado`,
`usuario.atributos_actualizados`). No sustituye a ninguno de los dos mecanismos:
sirve para que los módulos que cachean datos de usuario se enteren sin
preguntar. Es eventualmente consistente y no debe usarse como control de acceso.
