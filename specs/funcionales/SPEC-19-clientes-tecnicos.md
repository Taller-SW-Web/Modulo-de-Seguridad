# SPEC-19 — Gestión de clientes técnicos

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 5 (FEAT-14) |
| **Estado** | Aprobada |
| **Aprobada por** | Sergio Osorio (PO), 10-nov-2026, con el lote de contrato H5 (PO-T1): [OpenAPI v2 2.1.0](../contrato-api/openapi-v2.yaml) |

---

## Contexto — ¿por qué?

Los seis módulos consumidores llaman a nuestra API, y a las de otros módulos,
con un token de servicio ([SPEC-17](SPEC-17-tokens-servicio.md)). Cada módulo
tiene su `client_id`, su secreto y una lista de scopes concedidos. Hasta H4 todo
eso se aprovisionaba con SQL directo en Supabase, siguiendo el runbook
`docs/runbooks/clientes-servicio.md` del repo backend: calcular el hash a mano,
insertarlo y conceder cada scope con un `INSERT`.

En la revisión de H4 el profesor observó que falta un módulo de configuración
para esos clientes. Sin él, dar de alta un módulo o rotar un secreto filtrado
depende de que alguien con acceso a la base escriba SQL correcto, no queda
auditado y es fácil equivocarse con un scope. Si un cliente queda mal
configurado, el módulo afectado recibe `401` o `403` y su integración se cae.

---

## Propósito — ¿para qué?

Que un `ADMIN_SISTEMA` gestione los clientes técnicos desde la SPA de Seguridad:
verlos, crearlos, desactivarlos, rotar su secreto y conceder o revocar scopes
del catálogo. El resultado observable es que la siguiente emisión de
`POST /auth/token` refleja el cambio, y que cada cambio queda en la auditoría
sin que el secreto aparezca en ningún sitio salvo en la respuesta que lo entrega.

---

## Alcance — ¿hasta dónde?

1. Listar los clientes técnicos con su estado y sus scopes concedidos.
2. Consultar el catálogo de scopes registrado, con la API dueña de cada uno.
3. Crear un cliente con secreto generado por el servidor y scopes iniciales.
4. Desactivar y reactivar un cliente.
5. Rotar el secreto de un cliente.
6. Conceder y revocar un scope del catálogo a un cliente.
7. Auditar cada una de esas acciones.

Las rutas son de `/api/v2`. El conjunto de rutas de v1 está congelado y la
validación de contrato lo comprueba (ver *Impacto en el contrato*).

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-19.1 | Todas las operaciones de esta spec exigen un token de usuario con el permiso `integracion.gestionar`, que solo tiene `ADMIN_SISTEMA`. Sin token válido, `401 TOKEN_INVALIDO`; con token de usuario sin el permiso, `403 SCOPE_INSUFICIENTE`; con token de servicio, `403 TOKEN_NO_APLICABLE`, igual que en las demás rutas de persona. La autorización se decide antes de comprobar si el cliente existe. |
| RF-19.2 | `GET /clientes-servicio` debe devolver todos los clientes con `clientId`, `nombre`, `activo` y `scopes` (código y audiencia), ordenados por `clientId`. **Ninguna respuesta de esta spec devuelve el hash del secreto**, y solo las de RF-19.4 y RF-19.6 devuelven el secreto. |
| RF-19.3 | `GET /scopes` debe devolver el catálogo de la tabla `scope` (`codigo`, `audiencia`, `descripcion`), ordenado por audiencia y código. Esta spec no crea ni edita scopes del catálogo. |
| RF-19.4 | `POST /clientes-servicio` crea un cliente activo a partir de `clientId`, `nombre` y `scopes` (opcional, puede ir vacío). El servidor genera el secreto con 256 bits aleatorios, en base64url sin relleno; guarda solo su hash SHA-256 en hex, el mismo formato que verifica `POST /auth/token`; y responde `201` con el cliente y `clientSecret`. |
| RF-19.5 | `clientId` debe cumplir `^[a-z0-9][a-z0-9-]{2,49}$`, y `nombre` tener entre 1 y 100 caracteres. Si no, `400 VALIDACION`. Si el `clientId` ya existe, activo o no, `409 CLIENTE_SERVICIO_EXISTENTE`. |
| RF-19.6 | `POST /clientes-servicio/{clientId}/secreto` reemplaza el hash por el de un secreto nuevo, generado igual que en RF-19.4, y responde `200` con `clientSecret`. El secreto anterior deja de servir para emitir tokens en ese momento; los tokens ya emitidos mueren en su `exp`, como mucho una hora después. |
| RF-19.7 | Las respuestas que contienen `clientSecret` llevan `Cache-Control: no-store`. El secreto no se guarda en claro, no se escribe en logs ni en la auditoría, y no se puede volver a consultar. |
| RF-19.8 | `POST /clientes-servicio/{clientId}/desactivar` y `.../reactivar` cambian `activo` y responden `204`. Un cliente inactivo no obtiene token: `401 CLIENTE_INVALIDO`, igual que hoy. Aplicar el estado que ya tiene responde `204` sin cambios ni auditoría. |
| RF-19.9 | `POST /clientes-servicio/{clientId}/scopes` con `{ "scope": "<codigo>" }` concede el scope y responde `204`; `DELETE /clientes-servicio/{clientId}/scopes/{scope}` lo revoca y responde `204`. Ambas son idempotentes: si no hay cambio, responden `204` sin auditar. |
| RF-19.10 | Solo se conceden scopes que existen en el catálogo. Un código que no está en `scope`, o que se repite en el alta, responde `400 VALIDACION` con el campo `scope` (o `scopes`) y no cambia nada. |
| RF-19.11 | Un `clientId` inexistente en cualquier operación de RF-19.6, RF-19.8 o RF-19.9 responde `404 NO_ENCONTRADO`. |
| RF-19.12 | La siguiente emisión de `POST /auth/token` refleja el estado, el secreto y los scopes vigentes. No hay caché que lo retrase. |
| RF-19.13 | Cada cambio efectivo se audita con `actorTipo: USUARIO`, el administrador como `actorId` y el `clientId` en `detalle`: `CLIENTE_SERVICIO_CREADO` (con los scopes iniciales), `CLIENTE_SERVICIO_DESACTIVADO`, `CLIENTE_SERVICIO_REACTIVADO`, `SECRETO_SERVICIO_ROTADO`, `SCOPE_CONCEDIDO` y `SCOPE_REVOCADO` (con el scope). Las seis acciones son críticas (RF-12.6): si la auditoría falla, la operación se revierte. |
| RF-19.14 | Cada operación de escritura se ejecuta en una sola transacción: el cambio y su registro de auditoría se confirman o se revierten juntos. |

---

## Escenarios — ¿cómo verificamos?

### ESC-19.1 Alta de un cliente con un scope
- **Dado** un `ADMIN_SISTEMA` con sesión y el scope `usuarios:leer` en el catálogo,
- **Cuando** envía `POST /api/v2/clientes-servicio` con `clientId: "modulo-prueba-h5"`, `nombre: "Cliente de prueba H5"` y `scopes: ["usuarios:leer"]`,
- **Entonces** responde `201` con el cliente activo, sus scopes y `clientSecret`, con `Cache-Control: no-store`; la base guarda solo el hash; y `POST /auth/token` con ese secreto responde `200` con `scopes: ["usuarios:leer"]` y `aud: ["api-seguridad"]`.

### ESC-19.2 Alta con un `clientId` existente *(caso borde)*
- **Dado** que `modulo-marketplace` ya existe,
- **Cuando** se intenta crear otro cliente con ese `clientId`,
- **Entonces** responde `409 CLIENTE_SERVICIO_EXISTENTE`, el secreto del cliente existente no cambia y no se audita nada.

### ESC-19.3 Alta con un scope fuera del catálogo *(caso borde)*
- **Dado** que `pagos:cobrar` no está en la tabla `scope`,
- **Cuando** se crea un cliente con `scopes: ["usuarios:leer", "pagos:cobrar"]`,
- **Entonces** responde `400 VALIDACION` con el campo `scopes` y no se crea el cliente: la operación es todo o nada.

### ESC-19.4 Conceder y revocar cambian la siguiente emisión
- **Dado** el cliente de ESC-19.1,
- **Cuando** el administrador concede `roles:leer`, emite un token, revoca `roles:leer` y emite otro,
- **Entonces** el primer token incluye `roles:leer` y el segundo ya no; quedan auditados un `SCOPE_CONCEDIDO` y un `SCOPE_REVOCADO`.

### ESC-19.5 Conceder un scope ya concedido *(caso borde)*
- **Dado** que el cliente ya tiene `usuarios:leer`,
- **Cuando** se vuelve a conceder, incluso con dos peticiones simultáneas,
- **Entonces** ambas responden `204`, queda una sola fila y un solo `SCOPE_CONCEDIDO` como mucho.

### ESC-19.6 Rotación del secreto
- **Dado** un cliente activo con el secreto A,
- **Cuando** el administrador rota el secreto y recibe B,
- **Entonces** `POST /auth/token` con A responde `401 CLIENTE_INVALIDO`, con B responde `200`, y queda un `SECRETO_SERVICIO_ROTADO` cuyo `detalle` no contiene ni A ni B ni sus hashes.

### ESC-19.7 Cliente desactivado *(caso borde)*
- **Dado** un cliente activo con un token emitido hace 10 minutos,
- **Cuando** el administrador lo desactiva,
- **Entonces** una nueva emisión responde `401 CLIENTE_INVALIDO`, el token ya emitido sigue validando hasta su `exp` (límite declarado de SPEC-17) y al reactivar el cliente vuelve a emitir con los mismos scopes.

### ESC-19.8 Sin permiso o con token de servicio *(caso borde)*
- **Dado** un usuario `CLIENTE`, o un módulo con token de servicio,
- **Cuando** llama a cualquier ruta de esta spec, con un `clientId` que exista o no,
- **Entonces** responde `403` (`SCOPE_INSUFICIENTE` al usuario, `TOKEN_NO_APLICABLE` al módulo) con el mismo cuerpo exista o no el `clientId`, no ejecuta nada y registra `ACCESO_DENEGADO`.

### ESC-19.9 Ninguna lectura expone el secreto *(caso borde)*
- **Dado** varios clientes creados y rotados,
- **Cuando** se llama a `GET /clientes-servicio` y se consultan la auditoría y los logs de la aplicación,
- **Entonces** ninguno contiene un `clientSecret` ni un `client_secret_hash`.

### ESC-19.10 Auditoría que falla *(caso borde)*
- **Dado** que la escritura de auditoría falla,
- **Cuando** se rota un secreto,
- **Entonces** responde `503 NO_DISPONIBLE`, el hash guardado no cambia y el secreto anterior sigue funcionando.

---

## Requisitos no funcionales — ¿con qué condiciones?

- Las lecturas responden en menos de 300 ms en el percentil 95 con hasta 50 clientes.
- El secreto tiene 256 bits de entropía del generador seguro del sistema (`SecureRandom`). El hash es SHA-256 sin sal, como ya justifica `SecretoCliente`: con esa entropía no hay diccionario que probar.
- Ninguna respuesta, log ni registro de auditoría contiene el hash del secreto, y el secreto solo aparece en la respuesta que lo entrega.
- El rol `ADMIN_SISTEMA` exige MFA (SPEC-10): ningún cambio de esta spec se hace con una sesión que solo presentó contraseña.
- Esta spec no maneja datos personales: el `nombre` es el nombre del módulo, no de una persona (Ley N.º 29733 no aplica a este recurso).

---

## Fuera de alcance — ¿qué NO hará?

- **Borrar clientes.** Se desactivan, para conservar la auditoría que los referencia.
- **Editar el catálogo de scopes.** Un scope nuevo sigue el acuerdo A4: lo define el dueño de la API, se aprueba en la matriz y entra por `R__catalogo_scopes.sql`.
- **Periodo de convivencia de dos secretos** al rotar. El anterior deja de servir al instante; el equipo dueño recibe el nuevo antes de la rotación planificada.
- **Revocar tokens de servicio ya emitidos.** Mueren en su `exp`, igual que en SPEC-17.
- **Entregar el secreto al equipo dueño.** Lo hace el administrador por canal privado; esta API solo lo muestra una vez.
- **Limitación de tasa por cliente** y la pasarela de API Management (FEAT-19).
- **Editar el nombre** de un cliente después del alta.

---

## Impacto en el contrato

Rutas nuevas en `openapi-v2.yaml`, con base `/api/v2`. No cambian ninguna ruta
de v1 ni el token de usuario más allá del permiso nuevo. Publicadas por el PO en
[`openapi-v2.yaml`](../contrato-api/openapi-v2.yaml) 2.1.0, a partir de la implementación ya fusionada en el backend.

| Endpoint / catálogo | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v2/clientes-servicio` | Añade | ✅ Lote H5, 10-nov |
| `POST /api/v2/clientes-servicio` | Añade | ✅ Lote H5, 10-nov |
| `POST /api/v2/clientes-servicio/{clientId}/secreto` | Añade | ✅ Lote H5, 10-nov |
| `POST /api/v2/clientes-servicio/{clientId}/desactivar` y `/reactivar` | Añade | ✅ Lote H5, 10-nov |
| `POST /api/v2/clientes-servicio/{clientId}/scopes` | Añade | ✅ Lote H5, 10-nov |
| `DELETE /api/v2/clientes-servicio/{clientId}/scopes/{scope}` | Añade | ✅ Lote H5, 10-nov |
| `GET /api/v2/scopes` | Añade | ✅ Lote H5, 10-nov |
| Permiso `integracion.gestionar` para `ADMIN_SISTEMA` (SPEC-11) | Añade: el claim `permisos` del administrador pasa de 8 a 9 valores | ✅ Lote H5, 10-nov (SPEC-11) |
| Código `CLIENTE_SERVICIO_EXISTENTE` (409) | Añade | ✅ Lote H5, 10-nov (catálogo de errores) |
| Seis acciones auditables de RF-19.13 (SPEC-12) | Añade | ✅ Lote H5, 10-nov (SPEC-12) |
| Eventos | Ninguno | — |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints nuevos figuran en la documentación OpenAPI publicada
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
