# EP-02 — Autenticación y gestión de sesiones

| Campo | Valor |
|---|---|
| Dueño único | Jose Luis Limachi Sarmiento |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-05](../funcionales/SPEC-05-inicio-sesion.md).
- [SPEC-06](../funcionales/SPEC-06-renovacion-cierre-sesion.md).

## Interfaz y componentes

- [FRONT-00](../front/FRONT-00-comportamiento-comun.md) y [componentes](../componentes-react/FRONT-00-base-compartida.md).
- [FRONT-01](../front/FRONT-01-inicio-sesion.md) y [componentes](../componentes-react/FRONT-01-inicio-sesion.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable — incremento H3

**Selección H3 aprobada 12-oct:** revisión y aprobación formal del dueño
comunicadas por Luis; diseño interno cerrado bajo su delegación. Conserva
RF/ESC del alcance completo y no acredita implementación.

1. Login real, con sesión o desafío según reglas; composición con credenciales, estado, roles, segundo factor y auditoría de sus dueños.
2. Renovación con rotación, cierre y reúso revocando solo la familia; vínculo de acceso a familia requerido también por cambio de contraseña H3.
3. Acceso RS256/sub UUID consumido por Ventas en su operación protegida; evidencia propia y conjunta. Las mejoras y ampliación de pruebas continúan después.

[Plan H3 del dueño](../../plan/hito-3/jose-limachi/PLAN.md).

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-03 | Credenciales/política |
| EP-04 | Desafío MFA |
| EP-05 | Roles/permisos del token |
| EP-06 | Auditoría |
| EP-07 | Evaluación de bloqueo |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **KIT-01, KIT-02, KIT-03, JWT-01, H-11, INT-05** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido fue confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2), con Michael y fecha G5 jueves 15. Aún requiere revisión contractual identificada, URLs, rama, hora y aceptación de H-11; no acredita implementación.

## Trabajo del dueño

[Plan individual EP-02](../../plan/funcionalidades/EP-02-autenticacion-sesiones.md)
aprobado para H3 según comunicación de Luis; diseño interno cerrado; consume funcionales, UI/UX, API y React. Tareas
detalladas aún no generadas.

Las tareas EP-02-01 a EP-02-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-02). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
