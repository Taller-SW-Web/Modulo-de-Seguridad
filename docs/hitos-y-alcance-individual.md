# Hitos y responsabilidad individual

**Corte:** 12-oct-2026. Luis confirma que H3 es avance funcional por incremento,
dejando recorridos para siguientes hitos. También requiere que los siete
estudiantes tengan avance propio. Se conserva el reparto de
[ocho épicas](../specs/epicas/README.md).

## Fuentes de evaluación

El PDF original `D:/PRA_Proyecto_curso_TCSW_2026-II.pdf`, página 4, sitúa
primera demo en nube en semana 8 y segunda demo/integración/CI/CD en semana 11.
También indica evaluación individual semanal y exposición de funcionalidades
implementadas.

La rúbrica más reciente compartida por Luis exige ya en H3 primera demo,
diagramas, uso de IA/código, integración vía API y despliegue en nube.
Se prepara esta rúbrica reciente, incluida la conexión con Ventas. No se
deduce un 40 % ni una obligación de terminar todas las épicas al 100 %.

La confirmación del 12-oct resuelve la duda para la planificación de G7:
**incrementos operativos propios en H3, alcance restante en próximas entregas**.
No se inventa una rúbrica adicional, puntuación por escenario ni aprobación
individual del profesor que no esté documentada. Duración/orden de defensa
se coordinan con el PO según sus indicaciones.

## Épica completa e incremento operable

El dueño conserva interfaz/HTTP/lógica/datos/pruebas/demostración durante el
curso. Puede cerrar un recorrido concreto sin terminar toda la épica:
login con OTP real no completa la configuración de habilitar/deshabilitar MFA.

Solo interfaz o solo tablas no muestran todavía el recorrido. Cada estudiante
debe poder demostrar su incremento, resultado correcto y error relevante,
y explicar las dependencias que consume. Luis conserva evidencia separada
para segundo factor y bloqueo, aunque tenga un único plan H3.

Los [siete planes](../plan/hito-3/README.md) concretan alcance H3 y posterior.
Las fichas de épica se actualizaron a esa selección de planificación sin
cambiar RF/ESC del alcance completo. Los dueños todavía deben revisarlas;
redactar plan no acredita revisión, código, tareas o disponibilidad.

## Integración del grupo

[Login real G7 → mis pedidos G5](integracion/hito-3-ventas.md): G5 valida JWT
RS256 mediante JWKS, exige CLIENTE y obtiene propietario de sub. Datos de
prueba persistidos, servicios desplegados y llamadas reales. G7 es proveedor;
no requiere llamar a una API G5 para demostrar esa conexión.

El recorrido conjunto no sustituye evidencia individual. Firma/JWKS es
capacidad adicional de Sergio, cuya épica es roles/permisos; infraestructura
es adicional de Christian, cuya épica es auditoría.

## Progresión por hitos

| Hito | Preparación según la rúbrica reciente |
|---|---|
| H3 | Incrementos propios, defensa/IA, primera demo e integración G7/G5 en nube |
| H4 | Nuevos recorridos y mejoras, CI/CD y controles de pantallas/APIs |
| H5 | Evidencia de integración/rendimiento, revisión de seguridad, API Management y mecanismo asíncrono |
| H6 | Alcance final integrado, UX, OWASP, observabilidad/alertas y contribución GitHub |

Autenticación/seguridad aplicables deben funcionar desde H3 porque son el
producto G7. Evaluarlas también en H4 no autoriza a eludirlas antes.
Los eventos obligatorios de las mutaciones H3 se preparan con esos recorridos;
la evidencia completa del mecanismo asíncrono sigue siendo posterior.

## Siguiente etapa

Revisar planes y [decisiones necesarias](../plan/hito-3/ANTES-DE-CODIFICAR.md),
derivar tareas verificables y después implementar una a la vez. El siguiente
incremento tiene su propio plan, preservando alcance, dueño y evidencia H3.
No se promete implementación de los siete recorridos para el jueves por
haber terminado la documentación.

## Aprobación de incrementos H3

El 12-oct Luis comunica que los siete dueños revisaron y aprobaron formalmente
sus planes H3, RF, UI y composición, delegando detalles técnicos internos.
[Cierre documentado](../plan/hito-3/CIERRE-PLAN.md). El avance es incremental;
se conserva alcance posterior y evidencia end-to-end de cada dueño. No se
confunde aprobación del incremento con implementación completa de su épica.
