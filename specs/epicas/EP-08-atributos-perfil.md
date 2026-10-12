# EP-08 — Atributos y perfil de usuario

| Campo | Valor |
|---|---|
| Dueño único | Valery Cristin Gutierrez Bendezu |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-16](../funcionales/SPEC-16-atributos.md).

## Interfaz y componentes

- [FRONT-06](../front/FRONT-06-mi-cuenta.md) y [componentes](../componentes-react/FRONT-06-mi-cuenta.md).
- [FRONT-08](../front/FRONT-08-admin-detalle-usuario.md) y [componentes](../componentes-react/FRONT-08-admin-detalle-usuario.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable

1. Consulta/edición propia de atributos y bloque administrativo de perfil con restricciones de SPEC-16.
2. Cambio de correo con verificación y listado/creación de direcciones según contrato vigente.
3. H-01/H-13 definidos en contrato local; su publicación y pruebas reales de privacidad/denegación están pendientes. Edición/borrado de direcciones no se incorporan en esta preparación.

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-01 | Cuenta y verificación de correo |
| EP-02 | Identidad de sesión |
| EP-05 | Permisos |
| EP-06 | Auditoría |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **H-01, H-03, H-04, H-13** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-08-01 a EP-08-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-08). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
