# H3 — cierre del plan y traspaso a tareas

**Versión:** 1.0.0, 12-oct-2026. **Resultado:** los planes H3 están cerrados
para generar tareas. No se crearon tareas, código, SQL ni commits en ese cierre
inicial. **Actualización posterior del 12-oct:** las instrucciones de Luis
permitieron derivar por bloques los [siete tableros H3](../../tareas/hito-3/README.md).
Todos permanecen pendientes de implementación/evidencia.

## Aprobación registrada

Luis David Morales Brenis comunica que los siete estudiantes revisaron y
aprobaron formalmente el plan de su incremento H3: alcance, RF, diseño UI y
composición entre dueños. Delega a la IA detalles técnicos internos, respetando
Spring Boot/PostgreSQL, React/TypeScript y tablas de dueño único; confirma
que la DB está en Supabase. Es la fuente de esta aprobación. No se atribuyen
comentarios/firmas de GitHub que no existen en la evidencia consultada.

| Dueño | Plan versión 1.0.0 | Alcance aprobado |
|---|---|---|
| Eva | [PLAN](eva-moreno/PLAN.md) | Consulta administrativa de cuentas y fixtures técnicas por capacidades |
| Jose Luis | [PLAN](jose-limachi/PLAN.md) | Login, renovación, cierre y composición de autenticación |
| Juan José | [PLAN](juan-cano/PLAN.md) | Política/cambio de contraseña y capacidades de credencial/token de uso único |
| Luis David | [PLAN](luis-morales/PLAN.md) | OTP de login y bloqueo/desbloqueo automático, con evidencias separadas |
| Sergio | [PLAN](sergio-osorio/PLAN.md) | Roles/permisos; además identidad técnica RS256/JWKS |
| Christian | [PLAN](christian-arancivia/PLAN.md) | Auditoría/actividad propia; además entorno/transporte/despliegue |
| Valery | [PLAN](valery-gutierrez/PLAN.md) | Perfil propio y edición de nombres/apellidos/celular |

La aprobación alcanza **el incremento**. No declara specs completas H4–H6
implementadas ni aprueba por extensión una funcionalidad futura aún no definida.
Las cuatro entradas permanecen enlazadas desde cada PLAN.md.

## Decisiones técnicas cerradas

| Aspecto | Decisión / documento que gobierna |
|---|---|
| Tabla compartida usuario | Eva única escritora; Valery/Luis consumen operaciones acotadas, conservando sus reglas/UI/HTTP/pruebas. [Propiedad](DISENO-PERSISTENCIA.md) |
| Interfaces entre dueños | Ports Java, DTO inmutables, actor verificado, sin SQL/repositorio/entidad ajenos. [Composición](ACUERDOS-INTERNOS.md) |
| Locks/transacciones | Un DataSource, READ_COMMITTED, guard→cuentas→familias→filas internas; rechazos que actualizan defensas confirman antes del HTTP |
| Sesión actual | sesion_familia estable + acceso_sesion por jti; sin claim familiaId ni inferencia por sub |
| OTP | Vínculo usuario/desafío/revisión de credencial, HMAC, reenvío/uso único/cuota persistidos |
| Contraseña/fallos | Argon2id, historial cinco incluyendo actual, versión; huellas HMAC persistidas por racha y contadores mediante Eva |
| Auditoría y entregas | Crítica atómica, no crítica con deduplicación/reintento; outbox con lease, correo cifrado/purgado y eventos confirmados |
| Persistencia/cambios físicos | Mappings explícitos; versiones reservadas V101–V106 después de V100, sin reescribir historia aplicada |
| Entorno | JDBC Supabase con TLS y rol runtime separado; Java/Boot existentes, React/Vite/Mantine y composición concretos. [Entorno](ENTORNO-Y-REACT.md) |
| Identidad | RS256, RSA persistente, kid, issuer del entorno, perfil de usuario vigente sin aud de servicio añadida |
| UI compartida | Router/sesión/HTTP Jose Luis; panel Eva; cada bloque y recorrido mantiene dueño completo |
| Integración H3 | Login G7→mis pedidos G5; CLIENTE/sub/JWKS, sin scopes de servicio A10; configuración/prueba real pendientes |

Los insumos de ejecución desconocidos (proyecto G7 accesible, secretos, URLs,
SMTP, hora/rama G5) se identifican en [antes de codificar](ANTES-DE-CODIFICAR.md).
No son nuevos huecos de arquitectura ni vuelven a abrir la aprobación del plan.
Si el esquema real contradice este diseño se registra diferencia en DAT-01
antes de migrar; no se hace un cambio destructivo para ocultarla.

## Carpeta de tareas derivadas

La estructura decidida y ya creada en la continuación es:

```text
tareas/hito-3/
  README.md
  eva-moreno/TAREAS.md
  jose-limachi/TAREAS.md
  juan-cano/TAREAS.md
  luis-morales/TAREAS.md
  sergio-osorio/TAREAS.md
  christian-arancivia/TAREAS.md
  valery-gutierrez/TAREAS.md
```

Un tablero por estudiante/incremento. Luis agrupa EP-04/EP-07 con identidad
y evidencia separadas; Sergio/Christian distinguen lo académico de lo técnico
en su propio tablero. Ningún segundo tablero duplica una tarea de infraestructura.
Se reutiliza el formato de [plantilla de seguimiento](../seguimiento/PLANTILLA.md).

Cada tarea derivada enlaza versión del plan y RF/ESC/UI/OpenAPI/React aplicables,
resultado acotado, archivos propios, proveedores y orden, prueba correcta y
negativa, evidencia y criterio de cierre. Las dependencias entre estudiantes
apuntan a la entrega del port real, sin asignar al consumidor implementarlo.
Las 48 filas previas permanecen como cobertura; no se convierten en duplicados
ni sustituyen la descomposición del plan aprobado.

El orden de derivación sigue bases→acceso→recorridos→nube/G5. Se podrán
implementar tareas independientes en paralelo; la aceptación integrada espera
sus proveedores. Se genera y revisa el tablero antes de ejecutar una tarea.

## Límite de este cierre

Revisión documental y coherencia se verifican en el repositorio. No se ejecutan
pruebas del backend aún no implementado, no se acreditan grants o migraciones
remotas ni se anuncia demo terminada. La publicación se trata por separado en
[commits y publicación](../commits-publicacion.md); el paso posterior a las
tareas ya derivadas es implementar una tarea lista por vez y comprobarla.
