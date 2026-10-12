# Correcciones para los seis módulos — mensajes preparados

**Revisión:** 11-oct; cierre documental 12-oct-2026. **Coordinación:** Sergio, con edición local delegada.
**Estado:** borradores revisados contra fuentes públicas; ninguno enviado.
Corresponde al punto 8 del [plan de nueve puntos](../../plan/README.md).

Ventas confirmó el recorrido de Hito 3, Michael y su fecha de entrega en
[G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2).
La rama, configuración y prueba conjunta siguen pendientes. Marketplace y los
otros módulos conservan el alcance general. Corregir su documentación no
significa exigir que los seis integren con Seguridad en esta presentación.

## Referencia que acompaña a los mensajes

La referencia pública sigue siendo [G7 main fijado](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/blob/fb4f6c7f8aee6ad72411e6096b7cadfbf3860dca/specs/openapi.yaml).
Las revisiones locales v1 1.1.0, v2 parcial y kit corregido todavía no están
publicadas. [Fuentes](../fuentes-oficiales.md) y [versionado](../../specs/contrato-api/README.md)
identifican ambos estados. No comunicar el nuevo directorio como disponible
en main ni presentar una ruta local v2 como servicio operativo.

Reglas comunes: RS256; emisor exacto del descubrimiento obtenido de una URL
confiable configurada; UUID `sub` para persona; `tipo: acceso` para usuario.
Actualmente no se exige `aud` al usuario. Servicio lleva `tipo: servicio`,
`sub = client_id`, `aud` de la API receptora y scopes en texto separado por
espacios. Permisos de Seguridad, scopes técnicos y perfiles locales tienen
propósitos distintos. Ningún secreto de servicio va en el navegador.

Login usa `correo`/`contrasena`. Su `200` tiene dos variantes: sesión con
`accessToken`/`refreshToken` o desafío con `mfaRequerido`/`challengeToken`.
El desafío no es una sesión ni autoriza operaciones de negocio. Los errores
de login mantienen `401 CREDENCIALES_INVALIDAS` sin enumerar cuentas; el
`403 PASSWORD_CADUCADA` tiene su comportamiento propio, ya documentado.

## Lista de trabajo y destino

| Equipo | Hallazgo vigente | Destino propuesto | Criterio de cierre externo |
|---|---|---|---|
| G1 Marketplace | Mocks de Seguridad usan rutas, campos, identidad y algoritmo distintos | Issue de integración documental por identificar; #18 conserva A4/scopes | Mocks y adapter contrastados con contrato/kit y revisión enlazada |
| G2 Despacho | ES-F05-05, ES-F03-01 y modelo contradicen su contrato principal/A5/A6 | Seguimiento en G7 #19; #18/#42 para scopes, sin duplicar acuerdos | Archivos alineados, procedimiento de vínculo explícito; suscripciones confirmadas aparte |
| G3 Chatbot | Issuer literal y supuesto rol de servicio siguen en docs, aunque #20 registra corrección confirmada | G7 #20; #31 para Despacho, #33 para Productos, #42 para Ventas | Alinear las fuentes publicadas con la confirmación; no exigir rehacer código sin comprobarlo |
| G4 Retail | Contrato general antiguo y F03 contradicen el contrato consolidado | Issue de integración por identificar; A6/A7 en PR #35 como antecedente | Declarar contrato vigente, corregir formatos/búsqueda/alta y marcar documentos reemplazados |
| G5 Ventas/Postventa | `customerId` alternativo, `403` por scope retirado y base de backend antigua en el corte revisado | G7 #42 para A10; G5 Backend #2 para demo H3/A11 | Recorrido/Michael/fecha G5 confirmados; faltan correcciones enlazadas, rama, configuración y prueba |
| G6 Productos | Catálogo verificable; concesiones pendientes y condición de A9 sin respuesta del PO | G7 #33 y G6 #87, apartado C1 ya abierto | PO confirma catálogo/grants y registra evidencia; publicación/provisión se comprueban después |

Las búsquedas no localizaron un issue específico de estas correcciones en
G1/G2/G4. Al publicar, revisar de nuevo y reutilizar el hilo pertinente si
existe; no crear duplicados por anticipado. #35 es un **PR** histórico
fusionado, no un issue nuevo ni aprobación de los scopes de Productos.

## G1 — Marketplace

**Evidencia:** [Contratos de Mocks API, registro L17 y login L47](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/Arquitectura%20de%20la%20Aplicaci%C3%B3n/Contratos%20de%20Mocks%20API.md#L17)
usa `/auth/register`, `/auth/recover-password` y `/auth/reset-password`,
`firstName`/`email`/`password`, respuesta `token`/`customer` e identidad
`CUST-8842`. El JWT ilustrativo declara HS256. Estos ejemplos no corresponden
a Seguridad, aunque su matriz reconozca la integración.
Su [plan H3](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/repartici%C3%B3n%20por%20integrantes/README.md)
prioriza Productos y excluye login: no acredita compromiso con nuestra demo.

**Mensaje listo:**

> Hola equipo Marketplace. Al contrastar sus mocks con el contrato de
> Seguridad encontramos diferencias en “Contratos de Mocks API.md”:
> registro es `POST /api/v1/auth/registro`; recuperación/restablecimiento son
> `POST /api/v1/password/recuperar` y `/password/restablecer`.
> Login recibe `correo` y `contrasena`; devuelve la sesión con `accessToken`,
> `refreshToken` y `usuario`, o un desafío de segundo factor. La identidad
> del usuario es el UUID de `sub` y la firma es RS256.
>
> Por favor alineen los mocks de Seguridad, formatos y errores con la
> revisión contractual indicada arriba, o marquen explícitamente este
> documento como reemplazado si su adapter ya consume otra fuente.
> Consideren solicitud/verificación OTP, refresh y logout como parte del
> manejo de sesión; un desafío no permite acceder a recursos protegidos.
>
> A1 mantiene `canalOrigen: MARKETPLACE`, verificación en Seguridad y regreso
> al login configurado del canal, sin token en la URL. Las nuevas correcciones
> locales de Seguridad se comunicarán con su versión cuando se publiquen.
> Para nuestro Hito 3 el recorrido con Ventas está confirmado; Marketplace conserva el alcance
> general y coordinaremos su recorrido en el hito correspondiente.

**Cierre comprobable:** enlace al archivo corregido/depreciado, al mock usado y
a la revisión del `SecurityAdapter`; mantener A4 en [#18](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/18).

## G2 — Despacho

Su [contrato principal §3.3 y §10.1](https://github.com/Taller-SW-Web/G2-Despacho-Specs/blob/e727f41ce76f9434e2099e02f2653de3c257c16d/integraciones/api-contract.md#L59)
ya distingue perfil local, token de servicio y audiencia.
Persisten contradicciones en:

- [ES-F05-05 L21](https://github.com/Taller-SW-Web/G2-Despacho-Specs/blob/e727f41ce76f9434e2099e02f2653de3c257c16d/especificaciones/ES-F05-05-VincularRepartidorConSeguridad.md#L21): creación automática con rol `REPARTIDOR`.
- [ES-F03-01 L17](https://github.com/Taller-SW-Web/G2-Despacho-Specs/blob/e727f41ce76f9434e2099e02f2653de3c257c16d/especificaciones/ES-F03-01-acceso-y-habilitacion.md#L17): exige `REPARTIDOR` en JWT.
- [Modelo L207](https://github.com/Taller-SW-Web/G2-Despacho-Specs/blob/e727f41ce76f9434e2099e02f2653de3c257c16d/arquitectura/modelo-datos.md#L207): solicita cuenta con token de servicio.

[Confirmación A5/A6 en #19](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/19#issuecomment-5945901233)
resuelve alta por administrador, `roles: []` y activación por enlace.
El contrato de Despacho sigue dejando esos detalles pendientes (§10.1/§16).
Su §10.2 incluye cinco eventos previstos; la lista no prueba suscripción.

**Mensaje listo:**

> Hola equipo Despacho. Su contrato principal ya refleja A5 correctamente.
> Falta llevar esa decisión a ES-F05-05, ES-F03-01 y arquitectura/modelo-datos:
> Seguridad no emite el rol `REPARTIDOR` ni crea cuentas usando su token de
> servicio. `ADMIN_SISTEMA` realiza el alta, puede omitir rol y el titular
> activa su cuenta por enlace. Ustedes enlazan `usuario_id` con `sub` y
> autorizan la operación por su perfil local activo y la propiedad del recurso.
>
> Alineen también los pendientes de su §10.1/§16 con A6/#19. Precisen cómo
> registrarán el UUID y comprobarán el vínculo/estado, sin asumir una
> confirmación de activación que todavía no tenga mecanismo acordado.
>
> Sus `despachos:crear` y `despachos:cancelar` para `modulo-ventas` están
> definidos en §3.4 y respaldados por A10/#42; provisión pendiente. Ventas
> confirmó que no requiere `seguimientos:leer`: mantengan su fila como
> consumidor previsto, sin convertirla en concesión aprobada.
> Para `productos:fisicos:leer` necesitamos la confirmación trazable de
> Productos. Confirmemos aparte el subconjunto de eventos, su aprobación y
> configuración de consumo, incluido `usuario.roles_cambiados`.

**Cierre:** corrección de los tres archivos y pendientes; [#18](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/18#issuecomment-5945902217)
ya confirma audiencia/401/403. No pedir esa confirmación otra vez.

## G3 — Chatbot

[#20](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/20#issuecomment-5945899367)
registra que Chatbot confirmó configurar el issuer desde descubrimiento.
Sin embargo, [contratos-integracion L24](https://github.com/Taller-SW-Web/G3-Chatbot-specs/blob/ad4e7b597c8e3f2d143b5e30e2eea72655fdadd0/docs/contratos-integracion.md#L24)
y [inicio-sesion/spec L166](https://github.com/Taller-SW-Web/G3-Chatbot-specs/blob/ad4e7b597c8e3f2d143b5e30e2eea72655fdadd0/openspec/specs/inicio-sesion/spec.md#L166)
aún publican `iss=auth-service`. Su tabla A4/A11 (L289/L296) espera el
rol inexistente `SERVICIO_INTEGRACION`. Sus números de acuerdos son locales:
“A4 de Chatbot” no es “A4 de Seguridad”.

**Mensaje listo:**

> Hola equipo Chatbot. #20 registra su confirmación de la corrección del issuer;
> encontramos todavía `auth-service` en docs/contratos-integracion.md L24 y
> openspec/specs/inicio-sesion/spec.md L166. Por favor alineen esas fuentes con
> su implementación confirmada y enlacen la revisión que la contiene.
> El issuer se obtiene desde descubrimiento confiable configurado por entorno,
> nunca desde el token presentado por el usuario.
>
> En A4/A11 de su tabla retiren la espera del rol `SERVICIO_INTEGRACION`.
> Servicio se identifica por `tipo: servicio` y `sub: modulo-chatbot`;
> Despacho valida `aud: api-despacho` y los scopes ya concedidos por #31.
>
> A3 de Seguridad conserva `tokens:introspeccion` para Chatbot; el retiro
> de introspección en A10 corresponde a Ventas/Postventa. Los cinco scopes
> de Ventas/Postventa para `modulo-chatbot` constan en #42 con el ajuste de #44:
> `postventa:csat:escribir` se retira, sin reemplazo. CSAT usa el token del cliente,
> rol `CLIENTE` y propiedad del pedido validada por Postventa.
> Se mantiene el cliente existente, con provisión pendiente. Para Productos seguimos
> esperando la confirmación del PO en #33/G6 #87 C1. Mantengamos esos estados
> separados para que solicitud, aprobación y credencial entregada no se confundan.

**Cierre:** docs publicadas alineadas con #20; rol ficticio eliminado; estados
de [#31](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/31),
[#33](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33) y
[#42](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42) separados.
No se auditó aquí su backend para afirmar que rechaza tokens actualmente.

## G4 — Retail

[specs/api-contracts.md §4.1](https://github.com/Taller-SW-Web/G4-Retail-specs/blob/99fb06d634e1642673c2577e61528f4ab3ac70a1/specs/api-contracts.md#L178)
ya reconoce JWKS, RUC fuera de Seguridad, búsqueda con vendedor y rutas
`/usuarios/busqueda-documento`/`/usuarios/clientes`, pero deja payload/MFA
pendientes. Otro documento sigue publicado como fuente central:
[generales/api-contracts.md L15](https://github.com/Taller-SW-Web/G4-Retail-specs/blob/99fb06d634e1642673c2577e61528f4ab3ac70a1/specs/generales/api-contracts.md#L15)
usa `email/password`, respuesta `token`, `usr-001`, `rol` singular,
`codigoVendedor`/`tiendaId`, rutas `/clientes` y estado
`PENDIENTE_ACTIVACION`. [F03 L18/L48](https://github.com/Taller-SW-Web/G4-Retail-specs/blob/99fb06d634e1642673c2577e61528f4ab3ac70a1/specs/f03-gestion-clientes/f03-clientes.md#L18)
debe distinguir su búsqueda local de la delegada. Usar la revisión comprobada
del índice de fuentes para esta spec.

**Mensaje listo:**

> Hola equipo Retail. Su contrato consolidado ya separa correctamente RUC y
> búsqueda con token del vendedor. Falta declarar cuál documento gobierna el
> consumo de Seguridad y corregir o marcar como reemplazado
> specs/generales/api-contracts.md, que mantiene formatos diferentes.
>
> Login recibe `correo/contrasena` y responde con sesión o desafío; el acceso
> dura 15 minutos y el refresco rota. Los roles llegan en `roles`; `sub` e
> identificadores de usuario son UUID. `codigoVendedor`/`tiendaId` no forman
> parte de la respuesta de login publicada; sus datos de tienda y perfiles
> operativos se resuelven en Retail. La continuación del desafío usa
> `/auth/otp/solicitar` y `/auth/otp/verificar` v1.
>
> Búsqueda es `POST /usuarios/busqueda-documento` con tipo/número exactos;
> alta asistida es `POST /usuarios/clientes`, sin contraseña inicial, estado
> `PENDIENTE_VERIFICACION` y activación por enlace. Completen los payloads con
> OpenAPI y alineen F03: no deleguen RUC, correo ni celular a Seguridad.
> El retorno de RETAIL usa su login configurado. A6/A7 están definidos;
> las URLs de entorno y el grant adicional de introspección siguen por acordar.

**Cierre:** contrato consumidor único identificado, ejemplos correctos y F03
separando búsqueda local de A7. El informe anterior mencionaba un token de
ocho horas; no se encontró esa frase en estos contratos fijados y no se
presenta como una discrepancia vigente sin evidencia adicional.

## G5 — Ventas y Postventa

[api-ventas §1.4b L308](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/specs/api/api-ventas.md#L308)
admite `sub/customerId` en una frase aunque luego exige `sub`.
[Errores L72](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/specs/api/catalogo-errores.md#L72)
atribuye `403` al retirado `ventas:pedidos:leer`.
[A10 final](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534)
ya está confirmado; no se pide confirmarlo otra vez.

Se localizó además [backend separado](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/tree/1f3e0b63ce446680b02f75384dd07af84b4d0bde):
[PedidoController](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/blob/1f3e0b63ce446680b02f75384dd07af84b4d0bde/servicio-ventas/src/main/java/ventas/pedido/PedidoController.java)
tiene métodos que lanzan `NoImplementadoException` y no declara `/me`;
[SecurityConfig L31](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/blob/1f3e0b63ce446680b02f75384dd07af84b4d0bde/servicio-ventas/src/main/java/ventas/compartido/SecurityConfig.java#L31)
aún utiliza `ventas:pedidos:leer`/`ventas:admin`/`ventas:pedidos:escribir`.
El [application.yml L29](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/blob/1f3e0b63ce446680b02f75384dd07af84b4d0bde/servicio-ventas/src/main/resources/application.yml#L29)
solo configura JWKS con una URL de ejemplo en puerto 9000. No se acredita
validación de issuer/tipo/rol de usuario ni despliegue. Es una revisión
puntual de esos archivos, no una auditoría completa de la aplicación.

**Mensaje listo:**

> Hola Joseph. A10 está confirmado y conserva provisión pendiente. Para
> documentación, quitemos `customerId` como alternativa al `sub` en §1.4b y
> corrijamos el `403` de `/pedidos/me`: falta del rol `CLIENTE`, no falta del
> scope retirado `ventas:pedidos:leer`.
>
> Registramos su confirmación en el issue #2: Michael coordina F1 y JWT,
> y tendrán lo necesario como máximo el jueves 15/10. Seguridad adopta ese
> mismo día como objetivo para estar en nube y hacer la prueba conjunta,
> antes de presentar el sábado 17/10.
>
> Encontramos su repo separado de backend. En la revisión consultada
> PedidoController aún no declara `/me` y tiene operaciones pendientes;
> SecurityConfig conserva los scopes anteriores y application.yml tiene una
> URL JWKS de ejemplo. Cuando esté lista, enlacen en #2 la rama real de trabajo
> y las correcciones, para contrastar esa implementación con el contrato.
>
> El recorrido confirmado es: login real de Seguridad → JWT RS256 validado por Ventas
> → `GET /api/v1/pedidos/me` autorizado por `CLIENTE` y filtrado exclusivamente
> por `sub`. Pedidos precargados persistidos; no requiere scopes de servicio
> ni credenciales nuevas de A10. La regla `/me` debe diferenciarse de las
> rutas con tokens de servicio, sin aceptar servicio/refresco como usuario.
>
> Falta fijar URLs, issuer/JWKS, revisión consumida y hora de prueba, y confirmar
> el manejo de sesión en memoria: recargar exige iniciar sesión. La ficha
> preparada contiene configuración y casos de aceptación; el margen hasta
> el sábado será de aproximadamente dos días o día y medio, según las horas.

**Estado:** recorrido, Michael y entrega de G5 confirmados en [#2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2).
**Cierre restante:** G5 enlaza correcciones y rama de aplicación; ambos equipos
completan configuración y ejecutan la prueba de la [ficha H3](hito-3-ventas.md).
El acuse de G7 está publicado por Luis en [#2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2#issuecomment-6118366251). No exigir
`aud: api-ventas` a los tokens de usuario para esta demo.

**Ajuste A10, 12-oct:** [#44](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44)
solicita retirar `postventa:csat:escribir` de los tres canales; Luis aceptó el
ajuste en el comentario enlazado por la matriz. G5 ya actualizó
[su acuerdo](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/a6468b18bf18d4004c29a430ef275e359055e07c/integraciones/ACUERDO_G7_SEGURIDAD.md)
y [api-postventas.md](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/a6468b18bf18d4004c29a430ef275e359055e07c/specs/api/api-postventas.md#L40).
El retiro está reflejado localmente en G7, pendiente de publicación, sin
aprovisionamiento ejecutado. Los dos detalles de `/pedidos/me` arriba persisten
también en esa revisión actualizada de G5.

## G6 — Productos y Ofertas

Se contrastaron [Contrato_Api §19–21](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/blob/be5c2db16aca6d6d18b3005a2da4205f724038e3/contratos/http/Contrato_Api.md#L1490)
y [OpenAPI 0.5.0](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/blob/be5c2db16aca6d6d18b3005a2da4205f724038e3/contratos/http/openapi.yaml#L24392).
Hay 16 scopes, matriz de consumidores y
`pending-security-registration`. Incluye `productos:fisicos:leer` para
Despacho; los siete pedidos de Chatbot están en el catálogo.

La presencia en YAML prueba definición, no la confirmación requerida por A9.
[G6 #87 C1](https://github.com/Taller-SW-Web/G6-Productos-Ofertas-docs/issues/87)
ya solicita esa confirmación y no tenía respuestas en este corte. El documento
humano conserva referencias a versiones anteriores: al confirmar, fijar
commit y versión OpenAPI que efectivamente gobiernan.

**Mensaje listo:**

> Hola equipo Productos. Contrastamos su OpenAPI 0.5.0 y catálogo de 16 scopes.
> La condición de A9 sigue pendiente: necesitamos confirmación de su PO de que
> los siete scopes pedidos por `modulo-chatbot` son oficiales, con audiencia
> `api-productos` y concesión para ese cliente. Podemos dejarla en #33 de
> Seguridad y referenciar la respuesta a C1 del issue #87 de ustedes, ya abierto.
>
> Confirmen por separado las concesiones para Marketplace, Retail, Ventas y
> Despacho, incluida `productos:fisicos:leer`. El catálogo y la matriz son
> verificables, pero no equivalen a registro efectivo, secretos entregados o
> scopes aprobados para todos los consumidores. Si ya existe aprobación,
> enlacen su issue/confirmación para incorporarla sin duplicar el pedido.
>
> Mantengan validación local de usuarios con `GESTOR_COMERCIAL` e
> introspección para cambios de precio según su política vigente. Seguridad
> emite tokens; Productos valida `iss/aud/tipo/exp` y el scope requerido.
> Las revisiones locales de nuestro kit se publicarán con su versión.

**Cierre:** confirmación del PO, lista exacta cliente/scope/audiencia/issue,
revisión identificada; actualizar [matriz de G7](matriz-scopes.md) antes de
provisionar. No registrar automáticamente los 16 scopes por aparecer publicados.

## Seguimiento al publicar

Registrar fecha, enlace al comentario efectivamente enviado, revisión de G7,
revisión de respuesta/corrección y pendiente por equipo. Esa evidencia se
añade al [registro único](../registro-inconsistencias.md), no a un segundo
inventario paralelo. Este documento contiene borradores y fuentes, no un
historial de comunicaciones enviadas.
