# Commits y publicación de la preparación de Hito 3

**Corte:** 12-oct-2026. Comandos para ejecutar desde la raíz, en PowerShell.
El agente no ha ejecutado `git add`, commits, push ni creación de PR.
Publica documentación y herramientas de comprobación; no una integración
implementada. [Estado y revisiones](../docs/fuentes-oficiales.md).

**Actualización 12-oct:** los cuatro commits y el complemento SDD
`b9f76733e8b6c30410f36a755400daf92b988f76` ya fueron realizados por Luis.
PR #45/#46 están fusionados; main público comprobado:
`5796622f7c039021e03dd25b2338c6a208320e05`. No repetir esos comandos.
Las secciones anteriores de preparación son antecedentes. El nuevo bloque
EP-02/TEC-IDENTIDAD, auditoría de issues y ajustes de catálogo/índices sigue
local, sin staging ni commits del agente. [Fuentes actuales](../docs/fuentes-oficiales.md).

## Comprobación previa

Trabajar en la rama actual `brenis`. La base local comprobada es
`5fff5cd4a24690354dadc7e1f7e63f9e5d4bbfed`; el índice estaba vacío al preparar
esta guía. No cambiar de rama con el trabajo pendiente.

```powershell
git branch --show-current
git status --short
git diff --cached --name-only
git diff --check
```

Si el índice contiene archivos, revisar su contenido antes de agrupar: los
commits siguientes deben incluir exclusivamente el grupo indicado. No usar
`git add .` ni `git add -A` para incorporar cambios ajenos al corte.

La comprobación de documentación solo necesita Python. Para validar los
contratos en otro equipo, instalar las dependencias fijadas en un entorno
temporal aislado:

```powershell
python scripts/verificar-documentacion.py
python -m venv "$env:TEMP\g7-validacion-contrato"
& "$env:TEMP\g7-validacion-contrato\Scripts\python.exe" -m pip install -r scripts/requirements-contract.txt
& "$env:TEMP\g7-validacion-contrato\Scripts\python.exe" scripts/verificar-contratos.py
```

Los resultados de mocks y tokens firmados están en
[validación](../docs/integracion/validacion-hito-3.md) y
[casos](../docs/integracion/evidencia-validacion-hito-3.md). Ninguna de estas
comprobaciones acredita login, revocación ni pedidos en servicios reales.

## Cuatro commits, en este orden

Los cuatro forman una revisión conjunta. Sus enlaces y acuerdos deben
revisarse sobre el resultado completo, antes de incorporarlo a `main`.

### 1. Épicas y especificaciones funcionales/de interfaz

Ocho épicas, un dueño completo por épica, responsables y trazabilidad;
reglas, escenarios y componentes alineados. Luis conserva EP-04 y EP-07.
SPEC-17/18 siguen siendo responsabilidad técnica adicional de Sergio.

```powershell
git add -- specs/epicas specs/funcionales specs/front specs/componentes-react specs/_PLANTILLA.md specs/historias-usuario.md specs/trazabilidad.md
git diff --cached --stat
git diff --cached --check
git commit -m "docs(sdd): fijar ocho epicas y alinear specs de usuario e interfaz"
```

### 2. Contrato versionado, errores y kit

v1 1.1.0 conserva las 42 operaciones publicadas; v2 2.0.0 parcial documenta
los cambios incompatibles de MFA/alta. Incluye el original histórico intacto,
ADR-008, el kit corregido y el comprobador documental del contrato.
El kit retira CSAT por #44; no crea endpoints de Seguridad para Postventa.

```powershell
git add -- specs/contrato-api specs/catalogo-errores.md specs/kit-integracion.md docs/arquitectura/adr/003-contrato-antes-que-codigo.md docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md scripts/verificar-contratos.py scripts/requirements-contract.txt
git diff --cached --stat
git diff --cached --check
git commit -m "docs(api): versionar contrato y corregir kit de integracion"
```

### 3. Acuerdos y preparación con Ventas

Fuentes comprobadas, inventario, matriz de concesiones, A10 ajustado por
#44 y A11 para login/mis pedidos. Incluye mensajes preparados para los seis
equipos y evidencias locales. Los acuses humanos ya publicados se citan;
las demás comunicaciones siguen pendientes de envío.

```powershell
git add -- docs/integracion docs/fuentes-oficiales.md docs/registro-inconsistencias.md
git diff --cached --stat
git diff --cached --check
git commit -m "docs(integracion): actualizar A10 y acordar demo con Ventas"
```

### 4. Orientación del repositorio y publicación

Índices, glosario, arquitectura descriptiva, responsabilidades, antecedentes,
seguimiento inicial y esta guía. Se corrige únicamente el carácter `q`
accidental de `enum canal_otp` en DBML; no se modifica SQL ni migraciones.
Se excluyen cachés Python. La guía UX/UI ya tenía un ajuste local menor.

**Continuación SDD-1/2:** este mismo grupo incorpora la estructura/plantillas
SDD, el plan técnico H3 y sus índices. Todavía no incluye los ocho planes
individuales ni las tareas incrementales, y no modifica código de producción.

```powershell
git add -- .gitignore AGENTS.md CONTEXT.md README.md backend/README.md docs/agents/domain.md docs/arquitectura/README.md docs/arquitectura/estados-usuario.md docs/arquitectura/modelo-datos.md docs/arquitectura/schema.dbml docs/plan-hito-1.md docs/responsabilidades.md docs/revision-inconsistencias.md "docs/sistema-diseño/Guia UX_UI Seguridad.md" specs/README.md plan scripts/verificar-documentacion.py
git diff --cached --stat
git diff --cached --check
git commit -m "docs(proyecto): actualizar orientacion y preparar publicacion"
```

## Subir y revisar la rama completa

La rama también contiene commits anteriores a estos cuatro: reorganización
de specs/contrato, diseño y la base del modelo. Revisar todo el diff frente
a `origin/main`, no solo los nuevos commits. En el corte había siete commits
exclusivos de `brenis` frente a `main`; no son todos cambios de esta jornada.

```powershell
git status --short
git log -4 --oneline
git fetch origin
git log --oneline origin/main..HEAD
git diff --stat origin/main...HEAD
git push origin brenis
```

Si hay nuevos cambios remotos, revisar la divergencia antes de resolverla.
No usar force push. Abrir el PR desde
[comparación main/brenis](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/compare/main...brenis)
y revisar los cuatro grupos junto con los cambios previos de la rama.
**Subir `brenis` no actualiza el contrato que los equipos leen en `main`:**
esa referencia cambia al fusionar el PR.

Descripción sugerida para el PR:

> Fija ocho épicas con responsables de punta a punta y alinea las specs
> funcionales, de interfaz y React. Conserva el contrato v1, separa los
> cambios incompatibles en v2 parcial, corrige el kit de validación JWT y
> retira el scope CSAT según #44. Registra A10/A11 y la preparación de la
> demo real con Ventas, con pendientes explícitos y evidencias de validación.
>
> Validación local: 178 ejemplos OpenAPI, 42 operaciones v1 conservadas,
> 15 casos de schemas, 68 peticiones a mocks y 88 comprobaciones de tokens
> RS256 contra los validadores del kit; enlaces y reparto comprobados.
> No acredita implementación, despliegue ni prueba conjunta en nube.

## Referencia que recibirán los equipos

Tras fusionar, registrar el SHA efectivo de `main` en el aviso o release,
las versiones 1.1.0/2.0.0 parcial y las rutas actuales:

- `specs/contrato-api/openapi.yaml`: contrato v1.
- `specs/contrato-api/openapi-v2.yaml`: definición parcial v2.
- `specs/contrato-api/historico/openapi-1.0.0.yaml`: base congelada.
- `specs/kit-integracion.md`: ejemplos y configuración por entorno.
- `docs/integracion/matriz-scopes.md`: concesiones y provisión pendiente.
- `docs/integracion/hito-3-ventas.md`: recorrido y pendientes bilaterales.

Los estados «local/sin publicar» de este corte describen la preparación.
Después del merge, actualizar fuentes/estados con el SHA efectivo en un
seguimiento documental; no inventar ahora el SHA que todavía no existe.
Publicar una definición no significa que el endpoint esté disponible.
Las rutas MFA históricas v1 siguen bloqueadas para implementación; v2 no es
una sustitución de todo v1. GOV-01 conserva la decisión pendiente sobre el
aviso contractual de una semana frente al calendario de la presentación.

## Complemento SDD-1/2 después de los cuatro commits

Commits iniciales comprobados localmente:

| Commit | Grupo |
|---|---|
| d2c49139c5ebd98bc1353eb2d6c1133ff4438936 | Épicas y specs |
| 4178f9ef089510ad46ae7702966452585982f88c | Contrato y kit |
| 7c2b7845c793d3b33f80e8cd9fcb2c898a88514c | Acuerdos e integración |
| 1f743bf76445bbc70e1dc3ec94a21818dec9492b | Orientación y parte de estructura SDD |

Los cambios restantes completan el plan técnico, plantilla de seguimiento,
índices y registro de decisiones. No generan código ni tableros individuales.

```powershell
git add -- README.md docs/fuentes-oficiales.md docs/responsabilidades.md docs/registro-inconsistencias.md docs/integracion/hito-3-ventas.md docs/integracion/preparacion-interna.md specs/epicas/README.md plan/README.md plan/commits-publicacion.md plan/seguimiento-epicas.md plan/PLAN-IMPLEMENTACION.md plan/DECISIONES-Y-BLOQUEOS.md plan/seguimiento/PLANTILLA.md
git diff --cached --stat
git diff --cached --check
git commit -m "docs(sdd): completar estructura y plan tecnico de demo con Ventas"
```

Ejecutar después las comprobaciones de documentación y de rama antes del push.
Actualizar la descripción del PR para incluir estructura SDD y plan técnico H3.
Estos comandos son una propuesta: el agente no los ejecutó.

## Bloque siguiente: planes y tareas de implementación

Seguir el flujo **spec funcional + UI + API + React → plan → tareas → código
incremental**. La referencia consultada es
[Marketplace, plan](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/README.md)
y [seguimiento](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/seguimiento/README.md).

El [seguimiento actual](seguimiento-epicas.md) tiene 48 filas generales:
sirve para fijar dueño y cobertura, pero no reemplaza un plan individual ni
tareas pequeñas derivadas de él. La continuación preparó el
[flujo/plantillas SDD](flujo-sdd.md) y el [plan técnico H3](PLAN-IMPLEMENTACION.md).
En los siguientes bloques se redactarán planes por épica/capacidad y se
derivarán tareas verificables, manteniendo el dueño completo. No se ha generado
código de producción ni los ocho planes y tableros individuales de una vez.

## Histórico del 12-oct — EP-02 y TEC-IDENTIDAD

Complemento documental posterior a main 5796622f7c039021e03dd25b2338c6a208320e05:

- Dos planes preparados para revisión, con entradas funcional/UI/API/React,
  decisiones, fases, dependencias y pruebas; sin tareas nuevas.
- Fuentes primarias SDD, condiciones por integrante y auditoría de 14 issues.
- [Hitos y responsabilidad individual](../docs/hitos-y-alcance-individual.md):
  diferencia PDF/rúbrica reciente y propuesta de avance por estudiante.
- Retiro de documento para Ventas según el último comentario #42, sin reemplazo.
- Reconciliación SPEC-17/18 y actualización de índices tras publicación.

Los OpenAPI no cambian. El kit solo cambia texto y concesiones; sus bloques
de validación conservan contenido. Los comandos anteriores ya ejecutados
por Luis no se repiten. Al publicar este complemento, revisar el diff completo
y usar un commit documental identificable, por ejemplo
docs(sdd): preparar planes de sesiones e identidad y estado de issues.
El agente conserva cambios locales sin staging/commit/push; no cerró issues.

## Continuación — siete incrementos H3 por estudiante

Luis confirma avance por incremento y requiere que los siete estudiantes
puedan mostrar trabajo propio. El bloque añade [los siete planes](hito-3/README.md),
[interfaces cerradas](hito-3/ACUERDOS-INTERNOS.md),
[condiciones de inicio](hito-3/ANTES-DE-CODIFICAR.md) y
[TEC-INFRA](transversales/TEC-INFRA.md). Luis conserva sus dos épicas.

Fichas, navegación, notas UI/React, calendario de eventos y DAT-01 se alinean
con el incremento seleccionado y lo posterior. No cambian OpenAPI, Java,
SQL, Docker ni ejemplos de código del kit. Planes aprobados H3 según Luis y diseño cerrado;
no se generan tareas, código, commits o mensajes externos.

Antes de publicar, revisar el diff documental completo: incluye el bloque
anterior EP-02/identidad/issues y esta continuación, aún locales. Los comandos
históricos ya ejecutados por Luis no se repiten para este complemento.

## Cierre del plan H3 por aprobación y delegación

El complemento ahora incluye [cierre](hito-3/CIERRE-PLAN.md),
[propiedad/mappings](hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](hito-3/ENTORNO-Y-REACT.md) y siete PLAN.md 1.0.0.
No genera carpeta de tareas ni cambia Java/SQL/YAML/base remota. Aprobación
formal comunicada por Luis; insumos y pruebas operativas no acreditados.
Antes de publicación humana revisar el diff completo, incluidos cambios previos.
El agente no ejecuta add, commit, push ni comentarios de issues.

## Publicación del complemento completo — autorización posterior del 12-oct

Luis solicita ejecutar la publicación en dos commits y crear o actualizar el PR
`brenis → main`. Esta instrucción posterior autoriza la publicación por el agente;
las notas anteriores conservan el estado de sus bloques históricos.

Los siete planes H3 están aprobados según comunicación de Luis y sus
[tableros de tareas](../tareas/hito-3/README.md) ya se derivaron por bloques.
README, índices y guías se actualizan para conducir a esos tableros; las 48
filas antiguas permanecen como cobertura general. Código, OpenAPI y base remota
no se modifican. No se presenta documentación como integración desplegada.

La secuencia acordada, después de comprobar enlaces/IDs y diff, es:

```bash
git add -- docs specs
git diff --cached --stat
git diff --cached --check
git commit -m "docs: alinear specs y acuerdos con el incremento H3"

git add -- README.md plan tareas
git diff --cached --stat
git diff --cached --check
git commit -m "docs(sdd): cerrar planes y tareas H3 de los siete integrantes"

git status
git push origin brenis
```

Ambos commits se publican juntos y se proponen hacia main sin fusionar el PR
automáticamente. Sus hashes y el enlace del PR se informan al terminar; no se
inventa un SHA futuro dentro de este documento. Tras fusionarlo, cada integrante
actualiza su rama y ejecuta una tarea lista por vez con sus fuentes y criterios.

## Complemento: backend nuevo y hexagonal — 12-oct-2026

Este bloque es posterior a los commits 80f3f58a/6996e8fa; no sustituye su historia.
Los repositorios Git son independientes. Publicar el ajuste documental y luego
el build/guías del backend, conservando las comprobaciones registradas.

En el repo de documentación:

```bash
git add -- .gitignore AGENTS.md README.md backend/README.md docs/arquitectura/README.md docs/fuentes-oficiales.md plan tareas
git diff --cached --check
git diff --cached --stat
git commit -m "docs(sdd): alinear H3 con backend hexagonal independiente"
git push origin brenis
```

En la raíz del repo backend (clon actual Backend-auth-service):

```bash
git add -- .gitignore build.gradle gradle/wrapper/gradle-wrapper.properties src/test/java/com/marketplace/auth_service/TestcontainersConfiguration.java README.md AGENTS.md
git diff --cached --check
git diff --cached --stat
git commit -m "build: usar Spring Boot 3.5 y preparar guias hexagonales"
```

El push del backend se hace a la rama de trabajo elegida, mediante PR hacia
main según coordinación del equipo. No agregar Backend-auth-service al padre.
La compilación de Java/tests pasó al ajustar el build; la prueba contextLoads
no pudo completar por Docker no disponible. Repetir `gradlew test` con Docker
antes de acreditar arranque. No hay migraciones ni funcionalidades H3 implementadas
por este complemento; las tareas de cada dueño siguen pendientes.
