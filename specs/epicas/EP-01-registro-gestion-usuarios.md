# EP-01 — Registro y gestión de usuarios

| Campo | Valor |
|---|---|
| Dueño único | Eva Lucía Moreno Zevallos |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-01](../funcionales/SPEC-01-registro-clientes.md).
- [SPEC-02](../funcionales/SPEC-02-verificacion-correo.md).
- [SPEC-03](../funcionales/SPEC-03-alta-consulta-cuentas.md).
- [SPEC-04](../funcionales/SPEC-04-baja-reactivacion.md).

## Interfaz y componentes

- [FRONT-02](../front/FRONT-02-registro-cliente.md) y [componentes](../componentes-react/FRONT-02-registro-cliente.md).
- [FRONT-03](../front/FRONT-03-verificacion-correo.md) y [componentes](../componentes-react/FRONT-03-verificacion-correo.md).
- [FRONT-07](../front/FRONT-07-admin-listado-usuarios.md) y [componentes](../componentes-react/FRONT-07-admin-listado-usuarios.md).
- [FRONT-08](../front/FRONT-08-admin-detalle-usuario.md) y [componentes](../componentes-react/FRONT-08-admin-detalle-usuario.md).
- [FRONT-11](../front/FRONT-11-admin-alta-usuario.md) y [componentes](../componentes-react/FRONT-11-admin-alta-usuario.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable — incremento H3

**Selección H3 aprobada 12-oct:** revisión y aprobación formal del dueño
comunicadas por Luis; diseño interno cerrado bajo su delegación. Conserva
RF/ESC del alcance completo y no acredita implementación.

1. Consulta administrativa real de listado/filtros/paginación y detalle, con ADMIN_SISTEMA que completó MFA; CLIENTE sin permiso no accede.
2. Preparación técnica reproducible de cuentas/UUID para el equipo y Ventas, consumiendo credenciales y roles de sus dueños.
3. Registro/verificación, alta/activación, búsqueda por documento y baja/reactivación quedan para siguientes incrementos. Términos H-12 y UI de activación deben resolverse antes de esos flujos.

[Plan H3 del dueño](../../plan/hito-3/eva-moreno/PLAN.md).

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-03 | Política de contraseña |
| EP-05 | Permisos administrativos |
| EP-06 | Auditoría |
| EP-08 | Perfil y verificación del cambio de correo |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **UI-01, H-10, H-12, INT-05** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-01-01 a EP-01-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-01). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
