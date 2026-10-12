# Commits y publicación de la preparación de Hito 3

**Corte:** 12-oct-2026. Comandos para ejecutar desde la raíz, en PowerShell.
Esta guía no ha ejecutado `git add`, commits, push ni creación de PR.
Publica documentación y herramientas de comprobación; no una integración
implementada. [Estado y revisiones](../docs/fuentes-oficiales.md).

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

## Bloque siguiente: planes y tareas de implementación

Seguir el flujo **spec funcional + UI + API + React → plan → tareas → código
incremental**. La referencia consultada es
[Marketplace, plan](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/README.md)
y [seguimiento](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/seguimiento/README.md).

El [seguimiento actual](seguimiento-epicas.md) tiene 48 tareas generales:
sirve para fijar dueño y cobertura, pero no reemplaza un plan técnico ni
tareas pequeñas derivadas de él. En el siguiente bloque se prepararán planes
por épica y capacidades técnicas, dependencias, tareas con criterio verificable
y evidencia, manteniendo el dueño completo. No se ha generado código de
producción ni se ha iniciado ese desglose en esta preparación de commits.
