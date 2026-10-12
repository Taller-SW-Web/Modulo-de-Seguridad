# H3 — del plan cerrado a tareas y código

**Corte:** 12-oct-2026. Los siete dueños aprobaron su incremento, según
comunicación de Luis. Él delega detalles técnicos; se concretaron
[interfaces/locks](ACUERDOS-INTERNOS.md), [persistencia](DISENO-PERSISTENCIA.md)
y [entorno/React](ENTORNO-Y-REACT.md). [Cierre verificable](CIERRE-PLAN.md).

## Planes cerrados y tareas disponibles

| Condición de diseño | Estado |
|---|---|
| Siete dueños, ocho épicas e incremento H3 identificable | Aprobado; Luis conserva EP-04/EP-07 |
| Fuentes funcionales, UI/UX, OpenAPI y React por plan | Enlazadas; alcance visual H3 aprobado, sin aprobar por extensión todos los flujos futuros |
| Revisión formal del plan propio y composición | Comunicada por Luis para los siete; registrada con esa procedencia |
| Monolito, ports/DTO, responsabilidades y tablas | Cerrado; repositorio/escritura única por tabla, consumidores mediante ports |
| Locks, commit de defensas, familia/acceso, OTP/huellas y outbox | Diseño concreto; no migra ni certifica la base actual |
| Runtime, frontend, hash/cifrado y conexión Supabase | Elegidos para H3; build/configuración se comprueban en implementación |
| Tareas pequeñas con fuentes y aceptación | Siete tableros H3 disponibles; ejecución/evidencia pendientes |

**No falta otra decisión arquitectónica interna rutinaria para empezar las tareas H3 definidas.**
No hace falta concluir H6, provisionar A10 o esperar a todos los módulos.
La [carpeta de tareas](../../tareas/hito-3/README.md) contiene un tablero por
dueño, conservando sus épicas y las capacidades adicionales correspondientes.
El [flujo SDD](../flujo-sdd.md) define el traspaso.

No comenzar copiando las 48 filas históricas ni pidiendo «implementa todo el
plan». Cada tarea del tablero describe resultado, fuentes, límites, dependencias,
caso correcto/negativo y evidencia. Ejecutar una lista por dueño en orden de
dependencias; otros dueños pueden avanzar sus tareas independientes.

## Insumos y verificaciones de ejecución

Estos puntos se transforman en condiciones de tareas de arranque/despliegue.
No permanecen como decisiones técnicas por inventar ni exigen otra aprobación
rutinaria; sí impiden ejecutar una parte contra un entorno desconocido.

| Insumo/comprobación | Responsable / referencia | Antes de |
|---|---|---|
| Proyecto/esquema G7 Supabase y acceso privado, pg_version, Flyway/DDL y grants | Christian con dueños; TEC-01/DAT-01 | Aplicar migraciones o fixtures; no consultar otro proyecto como sustituto |
| JDK 21, árbol Maven y paquetes frontend resueltos en lockfile | Jose Luis/Sergio; Christian verifica; TEC-04 | Acreditar arranque y pruebas; no atribuir resultados del kit al runtime propio |
| Proveedores internos implementados y comprobados | Dueño de cada capacidad | Ejecutar escenario integrado; dobles solo prueban aislamiento |
| RSA privada/kid/issuer persistentes, claves HMAC/AEAD y CA | Sergio/Luis/Valery; custodia Christian; TEC-03 | Emitir, verificar y entregar secretos de forma real |
| SMTP EMAIL y RabbitMQ reales/configurados | Christian; productores | Entrega OTP/enlace/aviso y publicación de hechos del incremento |
| Fixtures UUID A/B/V y otras cuentas, credenciales privadas válidas | Eva; Juan José/Sergio/Luis por capacidades; TEC-02 | Composición y demo sin alterar las cuentas compartidas G5 |
| Host backend/SPA, HTTPS, CORS y configuración bilateral G5 | Christian/Sergio con Joseph/Michael; INT-05 | Conectar servicios y ejecutar prueba conjunta |
| Rama/revisión G5, hora y aceptación de memoria H-11 | Sergio/Michael | Acreditar acuerdo de implementación y sesión en su frontend |
| SHA publicado del complemento y aviso/adopción | Publicación humana; PUB-01/GOV-01 | Que los módulos consumidores referencien la misma revisión |
| Evidencia real de cada recorrido y ensayo | Cada dueño; Christian coordina | Presentar funcionalidad operativa y contribución propia |

El proveedor de DB **ya es Supabase**. No se ha acreditado desde esta sesión
el esquema del proyecto G7, cadena aplicada, claves, correos o URL de nube.
El diseño nunca autoriza reset, baseline automático ni compartir credenciales.

## Fuera del incremento seleccionado

H-12 términos y UI de activación #36 afectan registro/activación posteriores;
Eva demuestra consulta de cuentas y aporta fixtures técnicas sin inventar
consentimiento. H-14 soporte corresponde a bloqueo manual posterior.
SPEC-10/cambio MFA v2 se implementa después; login OTP y gestión obligatoria
sí están en H3. Exportación, direcciones y provisión A9/A10 no bloquean estos
recorridos. Flujos futuros conservan specs y quedan ocultos en navegación H3.

## Cuándo se ejecuta y cuándo se considera terminado

Tarea lista: plan aprobado + fuentes + diseño aplicable cerrado + comprobación
escrita. Escenario integrado listo: sus proveedores y datos reales disponibles.
Entrega terminada: pruebas/criterios pasan y evidencia en entorno desplegado.

Objetivo del grupo: servicios y prueba bilateral jueves 15, ensayo viernes 16,
presentación sábado 17. Una fecha objetivo no reemplaza evidencia ni justifica
omitir MFA, revocación, autorización o privacidad. Ningún plan cerrado se marca
como funcionalidad implementada; la siguiente etapa es ejecutar las tareas
disponibles y registrar pruebas/revisión del código correspondiente.
