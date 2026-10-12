# EP-04 — Segundo factor: OTP y MFA

| Campo | Valor |
|---|---|
| Dueño único | Luis David Morales Brenis |
| Unidad académica | Una funcionalidad completa, de punta a punta |
| Estado | Preparación documental; implementación y evidencia pendientes |
| Primera versión | Objetivo Hito 3 / semana 8; condicionado a decisiones pendientes |

## Alcance y fuentes

Agrupa las specs siguientes. Sus RF y escenarios conservan las reglas vigentes y aprobación registrada; reasignar al dueño no aprueba cambios de comportamiento.

- [SPEC-09](../funcionales/SPEC-09-mfa-inicio-sesion.md).
- [SPEC-10](../funcionales/SPEC-10-activacion-mfa.md).

## Interfaz y componentes

- [FRONT-04](../front/FRONT-04-desafio-otp.md) y [componentes](../componentes-react/FRONT-04-desafio-otp.md).
- [FRONT-09](../front/FRONT-09-segundo-factor.md) y [componentes](../componentes-react/FRONT-09-segundo-factor.md).

FRONT-07/08 integra bloques completos. Eva mantiene navegación/contenedor; cada dueño conserva interfaz, servicio, datos y pruebas de su bloque. FRONT-00 coordina sesión con Jose Luis, política con Juan José y autorización con Sergio; véase [responsabilidades](../../docs/responsabilidades.md).

## Primera versión demostrable — incremento H3

**Selección H3 aprobada 12-oct:** revisión y aprobación formal del dueño
comunicadas por Luis; diseño interno cerrado bajo su delegación. Conserva
RF/ESC del alcance completo y no acredita implementación.

1. Login con desafío, solicitud y verificación OTP por correo real antes de abrir sesión; uso único, reenvío, vencimiento y cuota.
2. MFA efectivo obligatorio con cualquier rol de gestión. Canal de demo EMAIL; SMS simulado conserva contrato para desarrollo/pruebas.
3. Activación/desactivación confirmada v2/FRONT-09 y su persistencia quedan para siguiente incremento; no implementar rutas históricas v1 bloqueadas ni verificación externa de contactos.

[Plan H3 del dueño](../../plan/hito-3/luis-morales/PLAN.md).

## Dependencias

| Capacidad ajena | Consumo previsto |
|---|---|
| EP-02 | Sesión/desafío de login |
| EP-05 | Obligatoriedad por rol |
| EP-06 | Auditoría |
| EP-08 | Contactos del titular |

Sergio mantiene el contrato y SPEC-17/SPEC-18. Registro/login públicos no requieren un token de servicio para esta demostración.

## Pendientes y criterio para comenzar

Consultar **H-01, H-02, H-03, H-05, H-06** en el [registro](../../docs/registro-inconsistencias.md). Una decisión abierta bloquea la parte afectada. Esta ficha no resuelve ni amplía esos requisitos. El recorrido de Hito 3 con Ventas está confirmado en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2); quedan pendientes revisión consumida, configuración y prueba conjunta según la [ficha](../../docs/integracion/hito-3-ventas.md).

## Trabajo del dueño

Las tareas EP-04-01 a EP-04-06 están en el [seguimiento](../../plan/seguimiento-epicas.md#ep-04). Todas mantienen el mismo dueño: definir, construir interfaz, backend, persistencia, integrar y demostrar. El revisor no implementa una capa.

## Aceptación y evidencia

- Trazar comportamiento a RF/ESC de las specs, con peticiones/respuestas y estados persistidos.
- Verificar éxito, denegación y error sin enumerar cuentas ni exponer secretos; cubrir auditoría/eventos aplicables.
- Adjuntar evidencia reproducible de primera versión y registrar lo aplazado del alcance completo.
- Mocks acreditan validación de contrato, no integración real ni despliegue.
- Cerrar la épica completa solo al cumplir todas sus specs, no por completar la primera demo.
