# Matriz de scopes y concesiones

**Corte:** 11-oct-2026. Registro documental, no aprovisionamiento realizado.
Cada fila identifica dueño de API, scope, audiencia, consumidor y evidencia.
Un scope solicitado/provisional/condicionado no se emite como aprobado.

[Acuerdos](acuerdos.md) gobierna decisiones y [kit](../../specs/kit-integracion.md)
resume el consumo. El mecanismo sigue siendo genérico: registrar scope con
su audiencia y relacionarlo con cliente; no crear endpoints por módulo ni PR
externos. Los pedidos nuevos siguen por issues de integración.

## Concesiones por consumidor

| API dueña | Scope | Audiencia de servicio | Consumidor | Estado de aprobación | Acuerdo / issue | Aprovisionamiento |
|---|---|---|---|---|---|---|
| Seguridad | usuarios:leer | api-seguridad | modulo-marketplace | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | usuarios:leer | api-seguridad | modulo-chatbot | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | tokens:introspeccion | api-seguridad | modulo-chatbot | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | usuarios:leer | api-seguridad | modulo-retail | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | roles:leer | api-seguridad | modulo-retail | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | usuarios:leer | api-seguridad | modulo-ventas | Aprobada | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | usuarios:leer | api-seguridad | modulo-postventa | Aprobada | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | usuarios:leer | api-seguridad | modulo-despacho | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | direcciones:leer | api-seguridad | modulo-despacho | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | tokens:introspeccion | api-seguridad | modulo-productos | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Seguridad | roles:leer | api-seguridad | modulo-productos | Aprobada | [Kit base](../../specs/kit-integracion.md#5-qué-scope-pide-cada-módulo) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Despacho | cotizaciones:calcular | api-despacho | modulo-marketplace | Aprobada; aud/401/403 confirmados en #18 | [A4 / #18](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/18) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Despacho | seguimientos:leer | api-despacho | modulo-marketplace | Aprobada; aud/401/403 confirmados en #18 | [A4 / #18](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/18) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Despacho | cotizaciones:calcular | api-despacho | modulo-chatbot | Aprobada; contrato A8 documentado | [A8](acuerdos.md) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Despacho | seguimientos:leer | api-despacho | modulo-chatbot | Aprobada; contrato A8 documentado | [A8](acuerdos.md) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Despacho | despachos:crear | api-despacho | modulo-ventas | Aprobada en A10; dueño contrastado en contrato Despacho §3.4 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) / [evidencia G2](correcciones-modulos.md#g2--despacho) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Despacho | despachos:cancelar | api-despacho | modulo-ventas | Aprobada en A10; dueño contrastado en contrato Despacho §3.4 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) / [evidencia G2](correcciones-modulos.md#g2--despacho) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:crear | api-ventas | modulo-marketplace | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:anular | api-ventas | modulo-marketplace | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:devoluciones:escribir | api-postventa | modulo-marketplace | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:reclamos:escribir | api-postventa | modulo-marketplace | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:crear | api-ventas | modulo-chatbot | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:anular | api-ventas | modulo-chatbot | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:devoluciones:escribir | api-postventa | modulo-chatbot | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:reclamos:escribir | api-postventa | modulo-chatbot | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pagos:webhook | api-ventas | modulo-chatbot | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:crear | api-ventas | modulo-retail | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:anular | api-ventas | modulo-retail | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:devoluciones:escribir | api-postventa | modulo-retail | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:reclamos:escribir | api-postventa | modulo-retail | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pedidos:estado | api-ventas | modulo-despacho | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Ventas | ventas:pagos:webhook | api-ventas | simulador-pasarela | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Postventa | postventa:reembolsos:procesar | api-postventa | modulo-ventas | Aprobada en A10 | [A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | catalogo:leer | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | precios:leer | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | promociones:leer | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | promociones:evaluar | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | cupones:validar | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | recomendaciones:leer | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | inventario:disponibilidad:leer | api-productos | modulo-chatbot | Condicionada: confirmar catálogo del PO de Productos | [A9 / #33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33#issuecomment-5945904161) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | inventario:reservar | api-productos | modulo-ventas | Aprobada; scope y cliente fijados por el dueño en su contrato §13 | [#51](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/51#issuecomment-6157683747) / [contrato G6 §13](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/blob/671f2aa03baaf0d7930e9efce4db126d6fbcc58e/contratos/http/Contrato_Api.md) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | inventario:consumir | api-productos | modulo-ventas | Aprobada; scope y cliente fijados por el dueño en su contrato §13 | [#51](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/51#issuecomment-6157683747) / [contrato G6 §13](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/blob/671f2aa03baaf0d7930e9efce4db126d6fbcc58e/contratos/http/Contrato_Api.md) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | inventario:liberar | api-productos | modulo-ventas | Aprobada; scope y cliente fijados por el dueño en su contrato §13 | [#51](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/51#issuecomment-6157683747) / [contrato G6 §13](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/blob/671f2aa03baaf0d7930e9efce4db126d6fbcc58e/contratos/http/Contrato_Api.md) | Pendiente; ninguna credencial/provisión acreditada aquí |
| Productos | inventario:reintegrar | api-productos | modulo-postventa | Aprobada; scope y cliente fijados por el dueño en su contrato §13 | [#51](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/51#issuecomment-6157683747) / [contrato G6 §13](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/blob/671f2aa03baaf0d7930e9efce4db126d6fbcc58e/contratos/http/Contrato_Api.md) | Pendiente; ninguna credencial/provisión acreditada aquí |

## Clientes y límites

Se mantienen los seis client_id originales; A10 añade modulo-postventa y
simulador-pasarela: ocho clientes **previstos**, no ocho credenciales ya creadas.
Un client_secret pertenece al backend; nunca a la SPA ni al repositorio.
Entrega general según acuerdo Hito 4. La demo login/mis pedidos de H3 usa
identidad de persona, no necesita adelantar estos secretos.

A10 identifica **siete scopes propios distintos** de Ventas/Postventa tras
el retiro de CSAT aceptado en [#44](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44#issuecomment-6118403027), y
concesiones adicionales de APIs ajenas. El informe inicial hablaba de doce;
la fuente operativa es la lista final, no ese recuento. api-postventa es singular.
Los dos scopes de Despacho se contrastaron con su contrato §3.4, que identifica
Ventas como consumidor. [Evidencia](correcciones-modulos.md#g2--despacho);
A10 respalda las concesiones y la provisión sigue pendiente. Despacho también
enumera Ventas como consumidor previsto de seguimientos:leer, pero G5 declaró
no necesitarlo; no se añade esa concesión.

Las cuatro filas de inventario de #51 (15-oct) siguen el contrato de
Productos: `inventario:reservar`, `inventario:consumir` e `inventario:liberar`
solo para modulo-ventas, e `inventario:reintegrar` solo para modulo-postventa.
Productos no aprobó reservar/consumir para modulo-postventa ni
`inventario:disponibilidad:leer` para modulo-postventa; no se registran.

## Retirados o no concedidos

- postventa:csat:escribir: retirado de modulo-marketplace, modulo-chatbot y
  modulo-retail por #44, sin reemplazo. POST /api/v2/csat pertenece a Postventa:
  token del usuario, rol CLIENTE y propiedad del pedido comprobada por sub.
  No añade endpoints a Seguridad ni modifica las reglas de tokens de servicio.
- ventas:pedidos:leer y ventas:admin: autorización por rol CLIENTE/ADMIN_VENTAS,
  no scopes de servicio ni claims nuevos de tokens de usuario.
- postventa:devoluciones:leer y postventa:reclamos:leer: sustituidos por /me.
- tokens:introspeccion para Ventas/Postventa: retirado en A10; consultar estado
  de cuenta con usuarios:leer no equivale a validar una sesión particular.
- usuarios:leer:documento para Ventas: retirado sin reemplazo por
  [confirmación final de Joseph en #42](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6118843852),
  12-oct. Retail emite comprobantes; Ventas no necesita documento en claro.
  El scope genérico permanece disponible solo para concesiones aprobadas.
- Otros consumidores previstos por Productos: solicitud/confirmación pendiente;
  publicar una matriz propia no concede automáticamente sus 16 scopes.

Seguridad firma/emite. Cada API valida RS256, iss, aud de servicio, tipo,
exp y scope; su dominio decide qué permite. Perfiles como repartidor, cajero
u operador son locales; no amplían el catálogo cerrado de seis roles.

## Eventos: catálogo y suscripción son distintos

Mantener el catálogo publicado de usuario.creado/desactivado/reactivado,
usuario.bloqueado/desbloqueado, usuario.roles_cambiados y
usuario.atributos_actualizados. Las listas de consumidores de trazabilidad
son previstas. EVT-01 sigue pendiente: registrar por consumidor el subconjunto,
issue, aprobación y cola/configuración antes de asegurar suscripción efectiva.
No dar por acordado un evento adicional por aparecer en el documento ajeno.


## Catálogo de Productos contrastado — no concedido automáticamente

Se verificaron 16 scopes en su OpenAPI 0.5.0 de la revisión fijada en
[fuentes](../fuentes-oficiales.md), incluidos los siete de A9 y
`productos:fisicos:leer` para `modulo-despacho`. Los metadatos permanecen
`pending-security-registration`. La matriz de consumidores del dueño de
API sirve como propuesta; no prueba aprobación del PO requerida por A9.
[Mensaje G6 y evidencia](correcciones-modulos.md#g6--productos-y-ofertas).

G6 #87 C1 ya solicita esa confirmación y no tenía respuesta al consultar.
Antes de provisionar, enlazar confirmación/issue por cliente y conservar
audiencia `api-productos`. No convertir el catálogo en concesiones efectivas.
