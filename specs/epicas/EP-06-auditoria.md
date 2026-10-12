# EP-06 — Auditoría de seguridad

| Campo | Valor |
|---|---|
| Dueño único | Christian Gabriel Arancivia Salas |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-12](../funcionales/SPEC-12-registro-auditoria.md).
- [SPEC-13](../funcionales/SPEC-13-consulta-auditoria.md).

## Interfaz y componentes

- [FRONT-08](../front/FRONT-08-admin-detalle-usuario.md) y [componentes](../componentes-react/FRONT-08-admin-detalle-usuario.md).
- [FRONT-13](../front/FRONT-13-actividad-propia.md) y [componentes](../componentes-react/FRONT-13-actividad-propia.md).
- [FRONT-14](../front/FRONT-14-admin-auditoria.md) y [componentes](../componentes-react/FRONT-14-admin-auditoria.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable

1. Registro de eventos de seguridad de las épicas integradas sin secretos y con garantías de SPEC-12.
2. Consulta de actividad propia y consulta administrativa paginada con filtros/permisos de SPEC-13.
3. Exportación aplazada de Hito 3; conserva dueño y RF del alcance completo. Antes de implementarla en Hito 4, cerrar H-08 y alinear sus filtros.

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-01 a EP-08 | Cada productor entrega su evento según SPEC-12; Christian implementa registro y consulta |
| EP-05 | Permisos de consulta |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **H-07, H-08, EVT-01** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-06-01 a EP-06-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-06). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
