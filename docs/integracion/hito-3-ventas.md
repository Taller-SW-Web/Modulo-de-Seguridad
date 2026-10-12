# Hito 3 — Seguridad con Ventas

**Decisión del equipo G7:** priorizar únicamente Ventas para la integración de
este hito. **Estado del recorrido:** confirmado por Ventas en
[G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2)
y aceptado como alcance por G7. Michael confirmó disponibilidad para F1 y
coordina JWT del lado de Ventas. **Objetivo de disponibilidad:** jueves
15/10/2026. Ventas lo compromete en el issue; Luis adopta ese objetivo para
Seguridad en esta conversación. [Acuse de G7 publicado por Luis](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2#issuecomment-6118366251).
Marketplace y los otros cinco consumidores conservan su alcance general;
Marketplace ya no es el recorrido de salida de Hito 3.

Este documento prepara la coordinación; no acredita endpoints implementados,
credenciales entregadas ni despliegue. La aceptación del recorrido tiene
evidencia externa; configuración, implementación y prueba conjunta siguen
pendientes. El plan de nueve
puntos sigue vigente en [plan](../../plan/README.md).

El [plan técnico del recorrido](../../plan/PLAN-IMPLEMENTACION.md) organiza
capacidades, fases y casos de aceptación. Sus planes individuales/tareas aún
deben redactarse y revisarse; esta ficha conserva la coordinación bilateral.

## Recorrido confirmado

Frontend de demostración de Ventas → `POST /api/v1/auth/login` de Seguridad →
token de usuario RS256 → `GET /api/v1/pedidos/me` de Ventas → validación por
issuer/JWKS → autorización `CLIENTE` y consulta persistida filtrada por `sub`.

Usar cuentas CLIENTE activas y sin MFA habilitado, con pedidos precargados en
la base de Ventas. Esto evita ejecutar checkout, stock, despacho y pagos en la
demo. Los pedidos son fixtures persistidos, no respuestas simuladas. MFA sigue
siendo obligatorio para cualquier rol de gestión.

El token de usuario no requiere `client_id`, `client_secret` ni scopes de A10.
La integración de servicio de A10 sigue siendo otro recorrido: token de
servicio + consulta de identidad antes de operaciones sensibles. Su acuerdo
está confirmado; no demuestra la ejecución. La confirmación de esta demo
se registra por separado como A11, sin alterar A10.

## Ficha que deben completar ambos equipos

| Dato | Estado / responsable |
|---|---|
| Aceptación del recorrido H3 | Ventas confirmó en G5 Backend #2; Luis publicó el acuse de G7. Configuración y ejecución aún pendientes |
| Disponibilidad de Ventas | Comprometida por Joseph: como máximo jueves 15/10/2026 |
| Objetivo de disponibilidad de Seguridad | Jueves 15/10/2026, indicado por Luis; capacidad lista y comprobada todavía pendientes |
| Responsable de F1 de Ventas | Michael, disponibilidad confirmada en #2; incluye el recorrido de pedidos |
| Responsable del validador JWT de Ventas | Michael, confirmado en #2 |
| Rama/ubicación de /pedidos/me | Ventas la añadirá en #2 cuando esté lista; todavía pendiente |
| URL HTTPS de Seguridad y issuer exacto | Pendiente del despliegue; Jose Luis/Sergio/Christian |
| URL de descubrimiento y JWKS | Derivadas del entorno real, no del mock |
| URL HTTPS de Ventas y origen del frontend | Pendientes de Ventas |
| Orígenes CORS autorizados | Los orígenes concretos acordados, sin inventar URLs |
| Revisión de contrato/kit consumida | Base pública fijada en fuentes; revisión nueva pendiente de publicación del PO |
| UUID de clientes A/B y vendedor de prueba | Identificadores de fixture, por confirmar con Eva; no crear cuentas reales aquí |
| Pedidos de A, ningún pedido de B | Preparación de Ventas; misma identidad UUID |
| Sesión | Memoria en esta versión; recargar exige login. G7 lo define, Ventas confirma consumo |
| Prueba conjunta en nube | Objetivo jueves 15; falta hora concreta y evidencia de ambos servicios disponibles |
| Presentación | Sábado 17/10/2026 según la aclaración de Luis; hora no indicada |
| Margen de correcciones | Objetivo aproximado de dos días, o día y medio según las horas; no afirmar 48 horas exactas sin fijarlas |

No publicar secretos en este documento ni incluir tokens en URLs o registros.

## Coordinación con Ventas

Luis confirmó el recorrido y el objetivo jueves 15 en
[su comentario en G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2#issuecomment-6118366251),
consultado 12-oct. Este acuse es una comunicación humana ya publicada; no
acredita endpoints implementados ni disponibilidad comprobada de G7.

La coordinación continúa en ese issue: URLs, issuer/JWKS, revisión consumida,
hora de prueba, UUID de fixtures y aceptación de H-11. Las credenciales se
entregan por canal privado. A10 conserva el recorrido de servicio; su catálogo
se ajusta por #44 retirando CSAT, sin afectar esta demo.

## Trabajo que puede comenzar — sin cambiar dueños

| Dueño | Entrega necesaria para esta demo | Evidencia antes de prueba conjunta |
|---|---|---|
| Jose Luis — EP-02 | Login y sesión de SPEC-05/06 con reglas vigentes; consume roles, credenciales, bloqueo y auditoría de sus dueños | Login real exitoso/fallido, cuenta no disponible sin enumeración y token de acceso de 15 minutos |
| Sergio — EP-05 + SPEC-17/18 | Rol CLIENTE, firma RS256, descubrimiento/JWKS y configuración por entorno | Issuer exacto, JWKS del entorno y validación con el kit; no exigir aud nueva al usuario |
| Eva — EP-01 | Dos cuentas CLIENTE activas de pruebas, sin MFA voluntario; identidad UUID compartida con Ventas | Existencia y login comprobados; no inventar aceptación de términos; ningún secreto publicado |
| Christian — EP-06 e infraestructura | Despliegue, salud, comprobación y auditoría consumida por los dueños | HTTPS accesible, configuración aplicada, evidencia de eventos de login y prueba reproducible |
| Michael — Ventas F1/JWT | `/pedidos/me`, pantalla, pedidos persistidos y autorización | Rama enlazada en #2, identidad solo por sub, rol CLIENTE y servicio en nube |

Las dependencias siguen perteneciendo a sus dueños: política de Juan José,
bloqueo de Luis David y demás capacidades de las ocho épicas. La demo mínima
no cierra esas épicas ni divide su implementación por capas. Aquí se documenta
el orden; esta actualización no implementa capacidades ni crea cuentas.

**Orden:** fijar revisión contractual/configuración y responsables de entrega;
comprobar capacidades internas; entregar UUID y precargar pedidos; desplegar;
ejecutar prueba conjunta; corregir y ensayar. No esperar de nuevo una
confirmación del recorrido que ya dio Ventas. H-11, URLs, rama y hora siguen
requiriendo su información concreta.

## Aceptación de la demo

| Caso | Resultado esperado |
|---|---|
| Cliente A autenticado | 200; solo pedidos de A |
| Cliente B sin pedidos | 200; `items: []`, `total: 0`, paginación de su contrato |
| Sin token, vencido o firma alterada | 401 en Ventas |
| Usuario válido sin rol CLIENTE | 403 en Ventas |
| Identificador ajeno enviado por navegador | Nunca cambia el titular derivado de `sub`; no muestra datos ajenos |
| Recarga de página | Se pierde sesión en memoria; nuevo login |

Las [pruebas locales del kit con tokens firmados](validacion-hito-3.md)
pasaron en el punto 9. No ejecutan este recorrido ni prueban el filtrado de
pedidos: las llamadas en nube y los casos anteriores siguen pendientes.
El recorrido está confirmado; completar esta ficha y comprobar disponibilidad
de ambos servicios antes de acreditar integración.

## Evidencia revisada

- [Contrato Ventas §1.4b](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/specs/api/api-ventas.md).
- [README de specs](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/README.md#estado-del-proyecto) todavía dice implementación pendiente.
- [Backend separado](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/tree/1f3e0b63ce446680b02f75384dd07af84b4d0bde): base publicada, con métodos pendientes y sin `/me` declarado en PedidoController. [Contraste puntual](correcciones-modulos.md#g5--ventas-y-postventa). No acredita despliegue.
- [A10 confirmado por Joseph](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534).
- [Acuerdo de G5 con Seguridad](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/integraciones/ACUERDO_G7_SEGURIDAD.md).
- [Confirmación H3 de Joseph, Michael y fecha objetivo](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2), consultada 12-oct; acuse de Luis enlazado en esta ficha.
- Aclaración de Luis en esta conversación: presentación el sábado y objetivo de disponibilidad el jueves para dejar margen de corrección.


## Revisión interna posterior — interfaces del recorrido conservadas

La revisión local 11-oct [v1 1.1.0/v2 parcial](../../specs/contrato-api/README.md)
corrige huecos de Seguridad; no se anuncia publicada. El recorrido propuesto
sigue consumiendo login/JWKS v1 existentes, sin depender de alta/MFA v2.
La aceptación del recorrido proviene de G5 Backend #2 y la decisión de G7;
esta revisión y A10 no acreditan ejecución.

Para las pantallas propias que consuman Retry-After desde otro origen, el
despliegue debe exponer esa cabecera mediante CORS. Configurar CORS solo para
orígenes acordados; no acredita configuración ni servicio ya desplegado.
