# H5 — tareas de Eva Lucía Moreno Zevallos

**Épica:** EP-01 Registro y gestión de usuarios.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Ramas:** `feat/EP-01-alta-administrativa` (backend y frontend) y la ya abierta `feat/EP-01-baja-reactivacion`.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama. Revisión cruzada con Valery.

**Por qué importa:** el profesor pidió ver a un superadministrador que da roles de gestor. El rol ya existe (`ADMIN_SISTEMA`); falta dar de alta a un trabajador con su rol.

**Fuentes comunes:** [SPEC-03](../../../specs/funcionales/SPEC-03-alta-consulta-cuentas.md),
[SPEC-04](../../../specs/funcionales/SPEC-04-baja-reactivacion.md),
[SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md),
[FRONT-11](../../../specs/front/FRONT-11-admin-alta-usuario.md),
[FRONT-08](../../../specs/front/FRONT-08-admin-detalle-usuario.md),
[OpenAPI v2](../../../specs/contrato-api/openapi-v2.yaml) (`/usuarios`),
[ADR-008](../../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-13-T1 | Alta administrativa de un trabajador con rol (backend) | P1 | — | Pendiente |
| FEAT-13-T2 | Pantalla FRONT-11 | P1 | T1 | Pendiente |
| FEAT-13-T3 | Recorrido desplegado y evidencia | P1 | T1, T2 | Pendiente |
| FEAT-02-T1 | Cierre de baja y reactivación | P2 | Revisión del PR de la pantalla | En PR |

## FEAT-13 — Alta administrativa v2 (P1)

No depende de nadie: el contrato v2 de `/usuarios` ya está publicado.

### FEAT-13-T1 — Backend
- **Resultado:** `POST /api/v2/usuarios` para que `ADMIN_SISTEMA` cree la cuenta de un trabajador con uno de los roles del catálogo.
- **Primer paso:** listar los RF de SPEC-03 que faltan y acordar con Sergio cuáles entran en H5 (issues #36 y #37).
- **Aceptación:** pruebas PostgreSQL en CI; sin el permiso responde 403; los errores no permiten enumerar cuentas; el evento `usuario.creado` se publica.

### FEAT-13-T2 — Pantalla
- **Resultado:** formulario de alta con los roles del catálogo, accesible desde el listado administrativo.
- **Aceptación:** pruebas y verificación de tipos en CI; visible solo con el permiso.

### FEAT-13-T3 — Recorrido desplegado
- **Resultado:** el administrador crea un gestor, el gestor inicia sesión y su rol aparece en el detalle administrativo. Incluye asignar un segundo rol desde el detalle.
- **Primer paso:** pedir a David la versión desplegada.
- **Evidencia:** `docs/evidencia/h5/FEAT-13.md`, con fecha, versión y capturas sanitizadas.

## FEAT-02 — Baja y reactivación (P2, cierre)

### FEAT-02-T1
- **Situación:** el backend está fusionado; la pantalla sigue en PR.
- **Primer paso:** actualizar la rama de la pantalla sobre `develop` (comparte archivos con la edición administrativa ya fusionada).
- **Aceptación:** recorrido desplegado: baja, ingreso rechazado y reactivación.
- **Evidencia:** `docs/evidencia/h5/FEAT-02.md`.

También: fusionar el PR de documentación con la evidencia de FEAT-01.
