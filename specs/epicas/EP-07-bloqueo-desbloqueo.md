# EP-07 — Bloqueo y desbloqueo de cuentas

| Campo | Valor |
|---|---|
| Dueño único | Luis David Morales Brenis |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-14](../funcionales/SPEC-14-bloqueo-automatico.md).
- [SPEC-15](../funcionales/SPEC-15-bloqueo-manual.md).

## Interfaz y componentes

- [FRONT-07](../front/FRONT-07-admin-listado-usuarios.md) y [componentes](../componentes-react/FRONT-07-admin-listado-usuarios.md).
- [FRONT-08](../front/FRONT-08-admin-detalle-usuario.md) y [componentes](../componentes-react/FRONT-08-admin-detalle-usuario.md).
- [FRONT-12](../front/FRONT-12-desbloqueo-con-enlace.md) y [componentes](../componentes-react/FRONT-12-desbloqueo-con-enlace.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable — incremento H3

**Selección H3 aprobada 12-oct:** revisión y aprobación formal del dueño
comunicadas por Luis; diseño interno cerrado bajo su delegación. Conserva
RF/ESC del alcance completo y no acredita implementación.

1. Bloqueo automático con contador/huella de fallos distintos, quinta entrada, escalera/vencimiento, respuesta genérica y sesiones previas conservadas.
2. Correo/enlace de uso único y pantalla de desbloqueo, con transición/auditoría/evento reales; el enlace no levanta manual.
3. Bloqueo/desbloqueo manual y su indicación de soporte H-14 quedan para siguiente incremento; el login siempre respeta estado efectivo.

[Plan H3 del dueño](../../plan/hito-3/luis-morales/PLAN.md).

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-02 | Autenticación llama protección de cuentas |
| EP-01 | Cuenta y ciclo de vida |
| EP-05 | Permisos administrativos |
| EP-06 | Auditoría |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **H-07, H-14** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-07-01 a EP-07-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-07). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
