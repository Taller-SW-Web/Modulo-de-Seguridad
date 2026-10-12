# Revisión documental por épica — 11-oct-2026

Primer bloque acordado: revisar los cambios internos por dueño; segundo:
resolver propuestas contractuales con edición delegada. Esta es una revisión
técnica documental del agente, **no la revisión personal de los siete alumnos**.
[Reparto](../responsabilidades.md): ocho épicas, Luis David tiene dos y los
otros seis estudiantes una; SPEC-17/18 son responsabilidad técnica de Sergio.

Se comprobaron responsables, dependencias, siete secciones de las dieciocho
specs, contratos y fichas de interfaz/componentes. Los cierres son de definición
local; RF/ESC y tareas de implementación no se marcan terminados.

| Épica / dueño | Revisión y corrección local | Pendiente antes de la parte afectada |
|---|---|---|
| EP-01 Eva | SPEC-01–04, FRONT-02/03/07/08/11: altas con cuenta pendiente y activación A6, sin contraseña inicial; nombres/apellidos obligatorios; alta estricta v2 sin CLIENTE ni rol obligatorio | H-12 términos, pantalla de activación A6; datos/cuentas de demo por preparar |
| EP-02 Jose Luis | SPEC-05/06 y FRONT-00/01: acceso 15 min, refresh 7 días, memoria, reúso revoca familia/sesión; no todas las sesiones | Implementación y vínculo interno jti-familia para cambio MFA; acuerdo de consumo de Ventas |
| EP-03 Juan José | SPEC-07/08 y FRONT-05/10: política conservada, enlace validado al enviar, no endpoint de preconsulta | Implementación; la nueva pantalla de activación consume su política, no le transfiere EP-01 |
| EP-04 Luis David | SPEC-09/10, FRONT-04/09: login y cambio MFA separados; estado, dos acciones, reenvío, tiempos, cuota, intentos, roles y auditoría definidos; v1 histórico no desplegable | DAT-01, servicios y publicación v1 1.1.0/v2; wireframe pendiente |
| EP-05 Sergio | SPEC-11 y bloques de rol: seis roles, permisos distintos de scopes; titularidad solo para campos propios permitidos, sin ampliar edición de documento/atributos de rol | Implementación; catálogos y concesiones pendientes según matriz |
| EP-06 Christian | SPEC-12/13 y FRONT-13/14: detalle de bloqueo con null para manual indefinido y automático desde cuarto consecutivo; filtro de una acción; exportación aplazada de H3 | Auditoría crítica transaccional y suscripciones externas; alinear filtros antes de exportación |
| EP-07 Luis David | SPEC-14/15 y FRONT-12/bloques: bloqueo automático y manual conservados; manual no se revela en login genérico; vencimiento conforme a SPEC-14 | H-14 contacto de soporte; implementación y auditoría |
| EP-08 Valery | SPEC-16, FRONT-06/bloques: lectura privada por rol, ambos conjuntos en cuentas con CLIENTE+VENDEDOR; documento enmascarado, edición administrativa, celular no verificado por A2 | Implementación de privacidad/denegación; edición/borrado de direcciones aplazados |
| Técnico Sergio, sin novena épica | SPEC-17/18, kit, acuerdos: RS256/issuer/tipo/UUID y política de aud documentados; A10 respaldado, A9 condicionado | JWT del kit y compilación comprobados localmente; provisión, publicación, servidor y confirmaciones externas pendientes |

Cada dueño construye interfaz, backend, persistencia, validaciones, pruebas y
demostración de su épica. Revisiones y consumo de capacidades compartidas no
asignan frontend y backend a distintos estudiantes.

## Hallazgos conservados

[Registro vigente](../registro-inconsistencias.md) concentra estados. DAT-01
continúa abierto: DDL/DBML/migraciones no son equivalentes y V5 declarada no
existe. No se modificaron SQL ni código de aplicación. Tampoco se validó una
base remota. H-12/H-14 carecen de contenido suministrado en esta revisión.
INT-05 ya tiene recorrido/Michael/fecha de Ventas confirmados en #2; faltan entornos, rama, hora y prueba. Concesiones A9/suscripciones y comunicaciones no se
resuelven con aprobación interna.

## Qué puede continuar

Capacidades internas con spec definida, login/JWKS v1 y preparación de datos/
despliegue pueden avanzar por sus dueños. El cambio MFA requiere primero
diseño de persistencia y asociación de familia; registro/activación requieren
H-12. El recorrido de Ventas quedó confirmado en #2; configuración y ejecución de la demo siguen pendientes.

Comprobaciones: [contratos](../../specs/contrato-api/README.md) y
[verificador documental](../../scripts/verificar-documentacion.py).
No se acreditan implementación, demo, aprobación de cada alumno ni publicación.


## Resultado de las comprobaciones del corte

- `verificar-documentacion.py`: reparto 8/18/30/48, siete secciones obligatorias
  de las 18 specs y enlaces locales correctos. No comprueba anchors remotos.
- `verificar-contratos.py`: OpenAPI 3.1 correcto en los tres documentos;
  75 ejemplos v1 revisado y 30 v2 correctos, más 73 de la base congelada.
  Quince casos de schema válidos/inválidos; ningún código de error nuevo.
- Las 42 operaciones v1 conservan peticiones, parámetros, operationId y
  seguridad de la base; no hay rutas retiradas. La comprobación no atribuye
  compatibilidad de ejecución a operaciones que todavía no existen.
- Base histórica conserva SHA-256; diff sin errores de espacios; índice
  sin cambios staged. Comprobación sin commit ni publicación.

La continuación del punto 9 comprobó [JWT firmados y ejemplos Spring/.NET](validacion-hito-3.md):
88 resultados esperados y 68 peticiones mock. No se probaron servidores de
los módulos, consumo OTP, carreras, base real ni auditoría; esas capacidades
siguen pendientes de implementación.
