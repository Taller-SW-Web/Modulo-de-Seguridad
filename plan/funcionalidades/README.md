# Planes individuales por épica

Este índice prepara el desglose; **los ocho planes todavía no están redactados**.
Cada plan usará [la plantilla](../PLANTILLA.md), las fuentes de su ficha y el
[plan H3](../PLAN-IMPLEMENTACION.md). Después de revisarlo se derivarán tareas
con [estas reglas](../seguimiento/README.md).

| Épica / entrada | Dueño completo | Specs | Estado del plan individual |
|---|---|---|---|
| [EP-01, cuentas](../../specs/epicas/EP-01-registro-gestion-usuarios.md) | Eva Lucía Moreno Zevallos | 01–04 | Por redactar |
| [EP-02, autenticación/sesiones](../../specs/epicas/EP-02-autenticacion-sesiones.md) | Jose Luis Limachi Sarmiento | 05–06 | Por redactar; prioridad del siguiente bloque |
| [EP-03, credenciales](../../specs/epicas/EP-03-credenciales.md) | Juan José Cano Vasquez | 07–08 | Por redactar |
| [EP-04, OTP/MFA](../../specs/epicas/EP-04-segundo-factor.md) | Luis David Morales Brenis | 09–10 | Por redactar |
| [EP-05, roles/permisos](../../specs/epicas/EP-05-roles-permisos.md) | Sergio Alejandro Osorio Montenegro | 11 | Por redactar |
| [EP-06, auditoría](../../specs/epicas/EP-06-auditoria.md) | Christian Gabriel Arancivia Salas | 12–13 | Por redactar |
| [EP-07, bloqueo/desbloqueo](../../specs/epicas/EP-07-bloqueo-desbloqueo.md) | Luis David Morales Brenis | 14–15 | Por redactar |
| [EP-08, atributos/perfil](../../specs/epicas/EP-08-atributos-perfil.md) | Valery Cristin Gutierrez Bendezu | 16 | Por redactar |

Cada plan reúne entradas funcionales, UI, API y React. Pantallas que componen
varias épicas consumen bloques completos de sus dueños. Las prioridades de
la demo no cambian el reparto ni cancelan la primera versión individual.

## Capacidades técnicas adicionales

- `TEC-IDENTIDAD`: Sergio; SPEC-17/18. Para H3 se priorizan firma RS256,
  descubrimiento/JWKS y configuración del issuer. Tokens de servicio,
  introspección y consultas conservan su alcance posterior.
- `TEC-INFRA`: Christian; despliegue/comprobación de entregables de los dueños.
  Su épica académica sigue siendo auditoría.

Son planes técnicos por redactar, no una novena o décima épica. La dependencia
de cada uno se concreta en el plan individual consumidor antes de generar tareas.
