# Hito 3 — planes por integrante

**Complemento técnico 12-oct:** [backend hexagonal nuevo](BACKEND-HEXAGONAL.md)
cierra paquetes, DTO/mappers, adaptadores JPA, JDBC, transacciones y repositorios
independientes. Los siete planes/tableros son 1.1.0, con el mismo alcance aprobado.
El backend Maven anterior queda descartado; ejecución/evidencia pendientes.


**Corte:** 12-oct-2026. Luis confirma que H3 evalúa avance funcional y que
la épica continúa en incrementos posteriores. Cada uno de los siete
estudiantes debe presentar avance propio de punta a punta. No se usa un
porcentaje arbitrario de completitud.

**Aprobación formal:** Luis comunica el 12-oct que los siete dueños revisaron
y aprobaron su alcance H3, requisitos, UI y composición. Delega los detalles
técnicos restantes; quedan cerrados en los documentos comunes de este índice.
Los siete planes versión 1.1.0 están **listos para derivar tareas**.
No acreditan código funcionando ni despliegue. La descomposición para los siete
dueños ya está en [tareas H3](../../tareas/hito-3/README.md): cada tablero indica
orden, dependencias y criterios de cierre, sin reemplazar su plan.

## Empieza por tu plan

| Estudiante / dueño completo | Épica | Incremento H3 aprobado | Plan |
|---|---|---|---|
| Eva Lucía Moreno Zevallos | EP-01, cuentas | Administrador consulta listado, filtros y detalle de cuentas; datos de prueba reproducibles para los demás | [Eva](eva-moreno/PLAN.md) |
| Jose Luis Limachi Sarmiento | EP-02, autenticación/sesiones | Login, desafío cuando aplica, sesión persistida, renovación y cierre; acceso real consumido por Ventas | [Jose Luis](jose-limachi/PLAN.md) |
| Juan José Cano Vasquez | EP-03, credenciales | Política y cambio propio de contraseña, historial y revocación de las otras sesiones | [Juan José](juan-cano/PLAN.md) |
| Luis David Morales Brenis | EP-04 y EP-07 | OTP de login por correo; bloqueo automático y desbloqueo por enlace, con evidencia separada | [Luis David](luis-morales/PLAN.md) |
| Sergio Alejandro Osorio Montenegro | EP-05 + identidad técnica | Asignar/revocar roles, permisos efectivos y revocación de sesiones; además firma RS256, descubrimiento y JWKS | [Sergio](sergio-osorio/PLAN.md) |
| Christian Gabriel Arancivia Salas | EP-06 + infraestructura | Registro de seguridad y actividad propia; además despliegue y transporte de mensajes de las capacidades entregadas | [Christian](christian-arancivia/PLAN.md) |
| Valery Cristin Gutierrez Bendezu | EP-08, perfil | Consultar perfil y editar nombres/apellidos/celular propios, con denegación de cambios ajenos o privilegiados | [Valery](valery-gutierrez/PLAN.md) |

Luis tiene dos épicas dentro de su plan, con criterios de aceptación distintos.
Cada dueño implementa interfaz, controlador/servicio, persistencia, pruebas y
demostración de su incremento. Consumir una dependencia no transfiere una capa.

La elección tiene revisión y aprobación de los dueños comunicadas por Luis.
No declara los siete incrementos implementados o disponibles el jueves.
El siguiente incremento conserva el alcance restante de cada épica.

## Las cuatro entradas y la salida

Cada PLAN.md enlaza specs funcionales, UI/UX, contrato OpenAPI y componentes
React. Añade diseño de datos, dependencias internas, fases, pruebas y límites.

```text
specs funcionales + UI/UX + OpenAPI + componentes React
                         ↓
              plan H3 del estudiante
                         ↓
           aprobación y diseño cerrados
                         ↓
          tableros de tareas verificables disponibles
                         ↓
       código incremental + prueba + evidencia
```

El [flujo SDD](../flujo-sdd.md) gobierna este orden. Las 48 filas antiguas de
seguimiento son cobertura histórica, no tareas ejecutables de estos planes.
El cierre inicial del plan no generó tareas. La continuación por bloques de
Luis ya produjo [siete tableros H3](../../tareas/hito-3/README.md) con IDs de
ejecución; todos pendientes de implementación.

## Diseño común cerrado y siguiente paso

1. [Interfaces y composición](ACUERDOS-INTERNOS.md): ports/DTO, dueño físico,
   coordinador y orden de locks/transacciones de cada recorrido.
2. [Persistencia](DISENO-PERSISTENCIA.md): mappings JPA, familia/acceso, OTP,
   huellas, cifrado/outbox, migraciones previstas y un dueño por tabla.
3. [Entorno y React](ENTORNO-Y-REACT.md): JDBC Supabase, runtime, firma,
   rutas/props/session/HTTP, configuración y fixtures por capacidades.
4. [Salida del plan](CIERRE-PLAN.md) y [antes de codificar](ANTES-DE-CODIFICAR.md):
   lo aprobado, insumos de ejecución y tableros de tareas disponibles.
5. [TEC-INFRA](../transversales/TEC-INFRA.md) y
   [TEC-IDENTIDAD](../transversales/TEC-IDENTIDAD.md) se integran en los tableros
   de Christian/Sergio, sin asignarles otra épica.

Las tareas ya derivadas no exigen repetir aprobación de decisiones técnicas
rutinarias. Cada dueño comienza por una tarea cuyas condiciones de inicio
estén satisfechas y conserva evidencia de pruebas antes de cerrarla.

Puede avanzar el trabajo independiente que ya tenga sus fuentes y diseño
claros; no se espera el cierre de todas las épicas. Una fixture interna prueba
el aislamiento de una capacidad, pero la demo final usa sus dependencias reales.

## Demo del grupo y evidencia individual

La integración comprometida es [login G7 → mis pedidos G5](../PLAN-IMPLEMENTACION.md).
G5 valida el JWT con JWKS y usa CLIENTE y sub. No se exige a G7 llamar a G5,
provisionar A10 ni integrar Marketplace para H3.

**Objetivo de entrega:** servicios y prueba conjunta el jueves 15/10; ensayo
viernes 16; presentación sábado 17. Horas/URLs y configuración bilateral
siguen pendientes. Estos objetivos no sustituyen comprobación real.

Cada estudiante conserva evidencia de su recorrido: revisión de fuentes,
pantalla, petición/respuesta sanitizada, persistencia relevante, caso correcto,
denegación/error y pruebas en el entorno real. Guarda también cómo usó IA,
qué revisó y qué corrigió; no declarar autoría manual de código generado.

El PDF original y la rúbrica más reciente se distinguen en
[hitos y responsabilidad individual](../../docs/hitos-y-alcance-individual.md).
La confirmación de Luis define avance por incrementos; quedan para el PO la
duración/orden de la exposición y cualquier precisión adicional de evaluación.
