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
| FEAT-13-T1 | Alta administrativa de un trabajador con rol (backend) | P1 | — | Verificada — backend [#93](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/93), merge `05ad567`; [evidencia](../../../docs/evidencia/h5/FEAT-13.md) |
| FEAT-13-T2 | Pantalla FRONT-11 | P1 | T1 | Verificada — frontend [#28](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/28), merge `a706417`; [evidencia](../../../docs/evidencia/h5/FEAT-13.md) |
| FEAT-13-T3 | Recorrido desplegado y evidencia | P1 | T1, T2 | Verificada — [evidencia](../../../docs/evidencia/h5/FEAT-13.md) del 23-nov; enlace vencido pendiente del PO |
| FEAT-02-T1 | Cierre de baja y reactivación | P2 | Revisión del PR de la pantalla | Verificada — frontend [#21](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/21) y backend [#84](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/84); [evidencia](../../../docs/evidencia/h5/FEAT-02.md) del 23-nov |

## FEAT-13 — Alta administrativa v2 (P1)

No depende de nadie: el contrato v2 de `/usuarios` ya está publicado.

### FEAT-13-T1 — Backend
- **Resultado:** `POST /api/v2/usuarios` para que `ADMIN_SISTEMA` cree la cuenta de un trabajador con uno de los roles del catálogo.
- **Primer paso:** listar los RF de SPEC-03 que faltan y acordar con Sergio cuáles entran en H5 (issues #36 y #37).
- **Aceptación:** pruebas PostgreSQL en CI; sin el permiso responde 403; los errores no permiten enumerar cuentas; el evento `usuario.creado` se publica.
- **Estado (23-nov):** el primer paso se resolvió con la decisión del PO del 10-nov (`DECISIONES-PO.md` §3): entran el alta (RF-03.1) y la activación (RF-03.3 a RF-03.5), por eso T1 incluye `POST /api/v1/auth/activar-cuenta`. Corrige además el reenvío de verificación, que activaba cuentas creadas por el administrador sin contraseña ni términos. 1218/1218 y CI en verde; aprobado por Sergio y Luis.

### FEAT-13-T2 — Pantalla
- **Resultado:** formulario de alta con los roles del catálogo, accesible desde el listado administrativo.
- **Aceptación:** pruebas y verificación de tipos en CI; visible solo con el permiso.
- **Estado (23-nov):** alta en `/admin/usuarios/nuevo` (solo con `usuario.crear`), pantalla de activación en `/activar-cuenta` y página de términos `2026-11-v1` en `/terminos`, enlazada desde el registro y la activación (H-12). Con los cambios que pidió Luis en la revisión. 222/222 y CI en verde.

### FEAT-13-T3 — Recorrido desplegado
- **Resultado:** el administrador crea un gestor, el gestor inicia sesión y su rol aparece en el detalle administrativo. Incluye asignar un segundo rol desde el detalle.
- **Primer paso:** pedir a David la versión desplegada.
- **Evidencia:** `docs/evidencia/h5/FEAT-13.md`, con fecha, versión y capturas sanitizadas.
- **Estado (23-nov):** recorrido completo con backend `05ad567` y frontend `a706417`. Queda pendiente del PO cómo se resuelve un enlace vencido: hoy el alta con el mismo correo responde 409.

## FEAT-02 — Baja y reactivación (P2, cierre)

### FEAT-02-T1
- **Situación:** el backend está fusionado; la pantalla sigue en PR.
- **Estado (23-nov):** pantalla fusionada (#21, `479f2b5`) y desplegada en `a706417`. Recorrido con guion: la baja cierra la sesión abierta y rechaza el login con `401 CREDENCIALES_INVALIDAS`; la reactivación devuelve el ingreso sin restituir la sesión anterior.
- **Primer paso:** actualizar la rama de la pantalla sobre `develop` (comparte archivos con la edición administrativa ya fusionada).
- **Aceptación:** recorrido desplegado: baja, ingreso rechazado y reactivación.
- **Evidencia:** `docs/evidencia/h5/FEAT-02.md`.

También: fusionar el PR de documentación con la evidencia de FEAT-01. **Hecho:** docs [#83](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/pull/83), fusionado el 14-nov.
