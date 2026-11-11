# FEAT-14 — Módulo de configuración: clientes técnicos

Responsable: Luis David Morales Brenis. Corte: 10-nov-2026, America/Lima.
Estado: **T1 a T4 terminadas; recorrido desplegado comprobado**.
Especificación en [SPEC-19](../../../specs/funcionales/SPEC-19-clientes-tecnicos.md)
y [FRONT-15](../../../specs/front/FRONT-15-admin-clientes-tecnicos.md).

## Origen

Observación del profesor tras H4: faltaba un módulo de configuración para los
clientes técnicos. Hasta H4 un cliente se creaba con SQL siguiendo el runbook del
backend. Ahora un `ADMIN_SISTEMA` con el permiso `integracion.gestionar` lo hace
desde «Configuración → Clientes técnicos»: crear, rotar el secreto, desactivar,
reactivar, y conceder o revocar scopes del catálogo. Cada acción se audita.

Crear scopes queda fuera de alcance a propósito (SPEC-19, acuerdo A4). Un scope
solo tiene sentido si la API dueña lo valida, así que lo define ese módulo, se
aprueba en la [matriz de scopes](../../integracion/matriz-scopes.md) y entra por
`R__catalogo_scopes.sql`. La pantalla solo concede scopes que ya están en el catálogo.

## Fuentes y versiones

| Componente | PR fusionado | Commit | URL |
|---|---|---|---|
| SPEC-19 y FRONT-15 | [#89](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/pull/89) (docs) | `edc84` en `main` | — |
| Propuesta de contrato v2 y ajustes a SPEC-11, 12, 17 y al catálogo | este PR de evidencia (docs) | — | — |
| Backend | [#87](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/87) (backend) | `0e8ec3885f90681eaf4999732ea0a7de7a6c970d` | https://g7-auth-service.onrender.com |
| Frontend | [#24](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/24) (frontend) | `6e27a78db9832081a0b640daaa12bffeeb667611` | https://g7-seguridad-spa.onrender.com |

Las rutas nuevas están en `/api/v2/clientes-servicio` y `/api/v2/scopes`, porque
el verificador de contratos exige que v1 no cambie.

**Orden de despliegue.** David aplicó en Supabase el permiso `integracion.gestionar`
y su asignación a `ADMIN_SISTEMA` (dos `INSERT ... ON CONFLICT DO NOTHING`)
*después* de desplegar el backend `0e8ec38`. El backend anterior no conoce ese
código de permiso y fallaría al resolver los permisos del administrador. La sesión
de administrador se abrió después del cambio, para que `usuario.permisos` trajera
el permiso nuevo y la SPA mostrara «Configuración».

## Pruebas automatizadas

- **Backend** (PR #87 del backend, build completo con 1171 pruebas):
  - `GestionClientesTecnicosServiceTest`:
    - solo se guarda el hash, y el secreto, de 256 bits, se entrega una vez;
    - validación del `clientId`, del nombre y de los scopes;
    - 404 y 409;
    - sin permiso se rechaza antes de mirar si el cliente existe (ESC-19.8);
    - solo se audita lo que cambia.
  - `GobiernoClientesAdapterTest`: exige `ADMIN_SISTEMA` + `integracion.gestionar` y registra `ACCESO_DENEGADO`.
  - `ClientesTecnicosHttpTest`:
    - sin token, `401 TOKEN_INVALIDO`; con token de servicio, `403 TOKEN_NO_APLICABLE`;
    - el listado no trae secreto ni hash;
    - códigos de error publicados y campo en las validaciones.
  - `ClientesTecnicosPostgresTest` (8 pruebas, Testcontainers): ESC-19.1 a ESC-19.7, ESC-19.9 y ESC-19.10.
    - Entre ellas, dos concesiones simultáneas dejan una fila y una auditoría.
    - Si la auditoría falla, no cambia nada.
  - Pruebas de catálogo y semilla actualizadas: 9 permisos de `ADMIN_SISTEMA` y 11 filas en `rol_permiso`.
- **Frontend** (PR #24 del frontend): `ClientesTecnicosPage.test.tsx`, 11 pruebas.
  - Alta con el secreto mostrado una sola vez y la casilla «Ya guardé el secreto».
  - Rotación, desactivación con confirmación, y concesión y revocación de scopes.
  - Mensajes de error.

## Recorrido desplegado

Ejecutor: David, 10-nov-2026 por la noche, hora de Lima, en la SPA desplegada,
con una cuenta `ADMIN_SISTEMA` con MFA. Los tokens se emitieron desde PowerShell
contra `POST /api/v1/auth/token`. El secreto se leyó con `Read-Host -AsSecureString`
y el script solo imprimió el código HTTP, los scopes, `aud` y `sub`.

Ni los secretos ni los tokens están en el repositorio ni en esta evidencia.

| # | Paso | Resultado observado |
|---|---|---|
| 1 | Crear `modulo-prueba-h5` con `usuarios:leer` | La SPA muestra el secreto una sola vez. Se guardó solo de forma temporal |
| 2 | Emitir un token | `HTTP 200` · scopes `usuarios:leer` · `aud: api-seguridad` · `sub: modulo-prueba-h5` |
| 3 | Conceder `cotizaciones:calcular` y emitir | `HTTP 200` · scopes `cotizaciones:calcular, usuarios:leer` · `aud: api-despacho, api-seguridad` |
| 4 | Revocar `cotizaciones:calcular` y emitir | `HTTP 200` · scopes `usuarios:leer` · `aud: api-seguridad` |
| 5 | Rotar el secreto y emitir con el nuevo | `HTTP 200` |
| 6 | Emitir con el secreto anterior | `HTTP 401` |
| 7 | Desactivar y emitir con el secreto vigente | `HTTP 401`. La tarjeta muestra el cliente como inactivo |

Los pasos 3 y 4 muestran dos cosas. La emisión toma los scopes vigentes en la
base. Y la audiencia sale de la API dueña de cada scope: `cotizaciones:calcular`
pertenece a Despacho.

El cliente quedó **desactivado** al final, y los secretos temporales se borraron.

### Auditoría en Supabase

Consulta de solo lectura sobre `auditoria_seguridad`, que filtra por las seis
acciones nuevas y muestra solo `clientId`, `scope` y `scopes` del detalle:

| fecha (UTC) | accion | resultado | cliente | scope | scopes iniciales |
|---|---|---|---|---|---|
| 2026-11-11 01:40:55 | `CLIENTE_SERVICIO_DESACTIVADO` | EXITO | `modulo-prueba-h5` | — | — |
| 2026-11-11 01:39:13 | `SECRETO_SERVICIO_ROTADO` | EXITO | `modulo-prueba-h5` | — | — |
| 2026-11-11 01:38:11 | `SCOPE_REVOCADO` | EXITO | `modulo-prueba-h5` | `cotizaciones:calcular` | — |
| 2026-11-11 01:37:05 | `SCOPE_CONCEDIDO` | EXITO | `modulo-prueba-h5` | `cotizaciones:calcular` | — |
| 2026-11-11 01:01:51 | `CLIENTE_SERVICIO_CREADO` | EXITO | `modulo-prueba-h5` | — | `usuarios:leer` |

Hay una fila por cada acción del recorrido, y ninguna trae el secreto ni el hash:
`RegistroAuditoria` rechaza esas claves en el detalle.

## Despliegue por pipeline

El backend `0e8ec38` y el frontend `6e27a78` están desplegados en Render, y el
smoke del pipeline de `develop` del backend pasó para `0e8ec38`. El E2E contra
Render sigue *skipped* hasta que exista `E2E_RENDER_HABILITADO`, que depende de
las cuentas de OPS-T2.

## Corrección en SPEC-19

Al implementar T2 se vio que el código ya hacía otra cosa que la spec: la cadena de seguridad
rechaza un token de servicio en una ruta de persona con `403 TOKEN_NO_APLICABLE`
(ESC-17.4), antes de llegar a la regla de permisos. RF-19.1 y ESC-19.8 decían
`SCOPE_INSUFICIENTE` para ese caso; en este mismo cambio se corrigieron, y en el
catálogo de errores se añadió SPEC-19 a `TOKEN_NO_APLICABLE`. Un usuario sin el
permiso sigue recibiendo `403 SCOPE_INSUFICIENTE`.

## Límites

- La reactivación no se probó en la nube. La cubre `ClientesTecnicosPostgresTest`
  (ESC-19.7: al reactivar, el cliente conserva sus scopes).
- Los 403 sin el permiso (cuenta `CLIENTE`) y con token de servicio tampoco se
  probaron en la nube. Los cubren `GestionClientesTecnicosServiceTest`,
  `GobiernoClientesAdapterTest` y `ClientesTecnicosHttpTest`.
- Un token ya emitido sigue siendo válido hasta su `exp` después de revocar un
  scope o desactivar el cliente. Es el límite declarado de SPEC-17: lo que cambia
  es la siguiente emisión.
