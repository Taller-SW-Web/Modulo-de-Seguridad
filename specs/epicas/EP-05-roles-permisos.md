# EP-05 — Roles y permisos

| Campo | Valor |
|---|---|
| Dueño único | Sergio Alejandro Osorio Montenegro |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-11](../funcionales/SPEC-11-roles-permisos.md).

## Interfaz y componentes

- [FRONT-07](../front/FRONT-07-admin-listado-usuarios.md) y [componentes](../componentes-react/FRONT-07-admin-listado-usuarios.md).
- [FRONT-08](../front/FRONT-08-admin-detalle-usuario.md) y [componentes](../componentes-react/FRONT-08-admin-detalle-usuario.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable — incremento H3

**Selección H3 aprobada 12-oct:** revisión y aprobación formal del dueño
comunicadas por Luis; diseño interno cerrado bajo su delegación. Conserva
RF/ESC del alcance completo y no acredita implementación.

1. Asignación/retirada de los seis roles de uno en uno, cálculo de permisos efectivos y denegación/protección del administrador.
2. Bloque de roles completo en detalle administrativo, revocación de refrescos, auditoría crítica y evento de cambio.
3. Firma/JWKS/descubrimiento son capacidad técnica adicional, separada de la evidencia académica. Servicio/introspección/consultas y rotación completa siguen posteriormente.

[Plan H3 del dueño](../../plan/hito-3/sergio-osorio/PLAN.md).

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-01 | Cuenta administrativa existente |
| EP-04 | MFA obligatorio |
| EP-06 | Auditoría |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **H-09, H-10** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-05-01 a EP-05-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-05). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
