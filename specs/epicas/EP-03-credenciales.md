# EP-03 — Gestión de credenciales

| Campo | Valor |
|---|---|
| Dueño único | Juan José Cano Vasquez |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-07](../funcionales/SPEC-07-politica-cambio-contrasena.md).
- [SPEC-08](../funcionales/SPEC-08-recuperacion-contrasena.md).

## Interfaz y componentes

- [FRONT-05](../front/FRONT-05-recuperar-contrasena.md) y [componentes](../componentes-react/FRONT-05-recuperar-contrasena.md).
- [FRONT-10](../front/FRONT-10-cambio-contrasena.md) y [componentes](../componentes-react/FRONT-10-cambio-contrasena.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable

1. Política de contraseña consumible por registro y alta sin duplicar reglas.
2. Cambio propio con contraseña actual e historial/política de SPEC-07.
3. Recuperación por enlace y respuesta genérica, incluido vencimiento y uso único según SPEC-08.

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-01 | Cuenta y enlaces |
| EP-02 | Sesión para cambio propio |
| EP-06 | Auditoría |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **H-15** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-03-01 a EP-03-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-03). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
