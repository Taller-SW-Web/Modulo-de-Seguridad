# Tareas del incremento H3

**Complemento técnico 12-oct:** [backend hexagonal nuevo](../../plan/hito-3/BACKEND-HEXAGONAL.md)
cierra paquetes, DTO/mappers, adaptadores JPA, JDBC, transacciones y repositorios
independientes. Los siete planes/tableros son 1.1.0, con el mismo alcance aprobado.
El backend Maven anterior queda descartado; ejecución/evidencia pendientes.


Las tareas se derivan de los [planes aprobados](../../plan/hito-3/README.md)
versión 1.1.0. Ya están los tableros de los siete estudiantes, preparados por
bloques. Esto no acredita implementación ni pruebas.

| Estudiante | Tablero | Recorrido H3 |
|---|---|---|
| Eva Lucía Moreno Zevallos | [TAREAS](eva-moreno/TAREAS.md) | Consulta administrativa real de cuentas y fixtures técnicas |
| Jose Luis Limachi Sarmiento | [TAREAS](jose-limachi/TAREAS.md) | Login, sesión, renovación y cierre; composición React |
| Juan José Cano Vasquez | [TAREAS](juan-cano/TAREAS.md) | Política y cambio de contraseña; capacidades de credencial/token de uso único |
| Luis David Morales Brenis | [TAREAS](luis-morales/TAREAS.md) | OTP de login y bloqueo/desbloqueo automático, con evidencia separada |
| Sergio Alejandro Osorio Montenegro | [TAREAS](sergio-osorio/TAREAS.md) | Roles/permisos; además firma, validación y descubrimiento/JWKS |
| Christian Gabriel Arancivia Salas | [TAREAS](christian-arancivia/TAREAS.md) | Auditoría/actividad propia; además entorno, transporte y despliegue |
| Valery Cristin Gutierrez Bendezu | [TAREAS](valery-gutierrez/TAREAS.md) | Perfil propio y edición de nombres/apellidos/celular |

Las dependencias enlazan tareas ya creadas o capacidades del plan del dueño;
no autorizan al consumidor a implementar las reglas/tablas del proveedor.

## Cómo trabajar

1. Empezar por una tarea sin dependencias pendientes. Cada tablero indica su ruta.
2. Implementar solo el resultado de esa tarea, comprobar sus criterios y registrar
   evidencia. Cada estudiante mantiene su recorrido completo, incluido frontend,
   backend, datos y pruebas.
3. Consumir capacidades de otros dueños mediante los ports acordados. Un doble
   sirve para una prueba aislada; la aceptación conjunta exige proveedores reales.
4. Cambiar el estado con evidencia: **Pendiente → En curso → En revisión →
   Terminada**. Usar **Bloqueada** solo cuando falte una entrega necesaria para
   la tarea; anotar cuál y continuar otra tarea independiente.

Todas comienzan **Pendiente**. P0 indica base/acceso que habilita otras entregas;
P1 indica recorrido propio y evidencia igualmente exigidos para cerrar H3.
El dueño propone el resultado; otro integrante revisa código y criterios antes
del cierre, dejando su nombre en el registro. No se solicita otra aprobación
rutinaria del plan ya aprobado.

No acceder a tablas/repositorios ajenos, cambiar el contrato público ni presentar
mocks como integración. Las 48 filas anteriores de seguimiento son cobertura
general, no otro tablero de ejecución. El plan define el alcance; las tareas lo
descomponen sin añadir funcionalidades posteriores.

## Instrucción inicial para la IA del integrante

Sustituir nombre, rutas de su plan/tablero e ID; no pedir todas las tareas juntas:

> Soy [nombre]. Lee AGENTS.md, mi PLAN.md de H3 y mi TAREAS.md. Implementa
> únicamente [ID de tarea], consultando sus fuentes. Respeta los ports y las
> tablas de cada dueño. Ejecuta las comprobaciones indicadas, registra evidencia
> real y explica cualquier dependencia pendiente. No implementes otras tareas
> ni hagas commits automáticamente.

Empezar las tareas independientes sin esperar a todo el grupo. Configuración
privada, esquema/historia Supabase, SMTP/broker, claves y servicios G5 se comprueban
en las tareas que los necesitan; la redacción de este tablero no acredita esos
insumos ni la demo conjunta.
