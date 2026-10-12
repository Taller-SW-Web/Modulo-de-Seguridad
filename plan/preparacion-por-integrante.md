# Preparación para desarrollar por integrante

**Corte:** 12-oct-2026. Mantiene [ocho épicas](../specs/epicas/README.md),
[flujo SDD](flujo-sdd.md) y [registro único](../docs/registro-inconsistencias.md).
No es un tablero de tareas.

## Estado actual

Siete planes H3 versión 1.0.0 aprobados por sus dueños según comunicación formal
de Luis el 12-oct-2026; detalles técnicos cerrados por delegación. Las cuatro
entradas se mantienen y las [tareas H3](../tareas/hito-3/README.md) ya se
derivaron por estudiante; implementación y evidencia todavía pendientes.
[Índice](hito-3/README.md) y [cierre del plan](hito-3/CIERRE-PLAN.md).

## Plan y entregas por dueño

| Estudiante / épica | Plan aprobado | Datos/capacidades que implementa o consume |
|---|---|---|
| Eva — EP-01 | [Cuentas](hito-3/eva-moreno/PLAN.md) | Único mapping/repositorio usuario, cuenta/locks y escritura interna acotada; listado/detalle por composición y fixtures de identidad |
| Jose Luis — EP-02 | [Sesiones](hito-3/jose-limachi/PLAN.md) | Familia estable, acceso asociado, rotación/cierre; login compone proveedores, base HTTP/router/sesión en memoria |
| Juan José — EP-03 | [Credenciales](hito-3/juan-cano/PLAN.md) | Argon2id/version/política/lista/historial, cambio propio y TokenUnUsoPort; consume familia/auditoría/correo |
| Luis David — EP-04 | [OTP](hito-3/luis-morales/PLAN.md) | Desafío/revisión, OTP vinculado y HMAC/cuota, EMAIL real, pantalla/consumo único y sesión mediante Jose Luis |
| Sergio — EP-05 | [Roles](hito-3/sergio-osorio/PLAN.md) | Tablas RBAC/cálculo/guard, asignación-revocación y SeccionRoles; consume cuenta, revocación, auditoría y evento |
| Christian — EP-06 | [Auditoría](hito-3/christian-arancivia/PLAN.md) | Writer/registro_id, criticidad/reintento/grants, actividad titular y su pantalla |
| Luis David — EP-07 | [Protección](hito-3/luis-morales/PLAN.md) | Racha/huellas/bloqueo, contador mediante Eva, enlace mediante Juan José, auditoría/correo/evento y pantalla propia |
| Valery — EP-08 | [Perfil](hito-3/valery-gutierrez/PLAN.md) | Proyección/PATCH/validaciones y perfiles propios; nombres/apellidos/celular se persisten mediante Eva; UI propia |

[Interfaces](hito-3/ACUERDOS-INTERNOS.md), [persistencia](hito-3/DISENO-PERSISTENCIA.md)
y [entorno/React](hito-3/ENTORNO-Y-REACT.md) ya concretan las decisiones.
Nadie queda con una capa ni importa el repositorio de otro dueño.

## Capacidades adicionales y entorno

| Responsable | Plan técnico H3 alineado | Comprobación de ejecución |
|---|---|---|
| Sergio, SPEC-17/18 | [TEC-IDENTIDAD](transversales/TEC-IDENTIDAD.md) | Firma/JWKS/issuer, custodia TEC-03 y runtime real TEC-04 |
| Christian, infraestructura | [TEC-INFRA](transversales/TEC-INFRA.md) | Entorno/DB/SMTP/broker y despliegue TEC-01; no absorbe las épicas ajenas |

La [ficha G5](../docs/integracion/hito-3-ventas.md) conserva rama/URLs/origen,
revisión consumida, transporte, hora y aceptación H-11 pendientes. Objetivo
jueves 15, presentación sábado 17; solo pruebas reales acreditan disponibilidad.

## Salida hacia tareas y código

Los siete tableros derivados están en [tareas H3](../tareas/hito-3/README.md),
con IDs, fuentes, dependencias y criterios por resultado. Cada dueño implementa una tarea lista por vez y
comprueba fuentes y aceptación; la integración usa proveedores reales.

[Antes de codificar](hito-3/ANTES-DE-CODIFICAR.md) distingue lo ya cerrado de
insumos de ejecución pendientes. Supabase está confirmado como proveedor;
proyecto/esquema/secretos/grants, nube y configuración G5 se comprueban antes
de las tareas que los utilizan. No se inventan pruebas o despliegue realizados.
