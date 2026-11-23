# Hito 5 — verificación de clientes técnicos y grants

**Corte:** 10-nov-2026. **Responsable:** Luis David Morales Brenis (ejecuta); Sergio (dueño del catálogo).
**Plan:** [H5](../../plan/hito-5/README.md). **Fuentes:** [matriz de scopes](matriz-scopes.md), [cierre H4 de FEAT-11](../evidencia/h4/FEAT-11.md), [evidencia H4 de FEAT-05 y FEAT-06](../evidencia/h4/FEAT-05-06.md), y `docs/migraciones-propuestas/concesiones-servicio.sql` y `docs/runbooks/clientes-servicio.md` del repo backend.

Este documento **no afirma que un grant falte ni que exista** mientras no esté el resultado de las consultas. Nunca se registran aquí secretos, hashes, tokens ni códigos.

Los niveles de una integración se mantienen separados:
1. Scope aprobado. 2. Scope registrado en el catálogo. 3. Grant concedido al cliente. 4. Emisión de token comprobada. 5. Operación real contra la API receptora. 6. Prueba conjunta desplegada con evidencia.

## 1. Lo que ya consta

| Dato | Fuente | Nivel acreditado |
|---|---|---|
| `modulo-marketplace` emitió token HTTP 200 con `catalogo:leer` y luego con permisos de Despacho | [FEAT-11](../evidencia/h4/FEAT-11.md) | 4, emisión. El grant se infiere |
| `modulo-postventa` tiene `inventario:disponibilidad:leer` y emitió token HTTP 200 | [matriz de scopes](matriz-scopes.md) | 3 y 4 |
| El 6-nov se registró que no existía ningún cliente técnico de prueba provisionado | [FEAT-05 y FEAT-06](../evidencia/h4/FEAT-05-06.md) | Superado: Q1 del 10-nov muestra ocho clientes activos |
| El catálogo de scopes aprobados está en `R__catalogo_scopes.sql` | Repo backend, `develop` | 2 como archivo; no consta que esté aplicado |
| Se configuraron clientes para Marketplace, Productos, Ventas, Postventa, Retail, Chatbot y Despacho | Indicado por David | Confirmado por Q1 y Q2 el 10-nov (nivel 3) |

## 2. Por verificar: consultas de solo lectura

Los nombres de tablas vienen del [esquema documentado](../arquitectura/schema.sql). El esquema real no se ha contrastado con el diseño, por eso la primera consulta lo comprueba.

```sql
-- Q0. ¿Existen las tablas y columnas esperadas?
SELECT table_name, column_name, data_type
FROM information_schema.columns
WHERE table_name IN ('cliente_servicio','cliente_servicio_scope','scope','usuario_rol','rol')
ORDER BY table_name, ordinal_position;

-- Q1. Clientes técnicos (sin el hash del secreto)
SELECT client_id, nombre, activo
FROM cliente_servicio
ORDER BY client_id;

-- Q2. Scopes concedidos por cliente, con su audiencia
SELECT c.client_id, c.activo, g.scope, s.audiencia
FROM cliente_servicio c
LEFT JOIN cliente_servicio_scope g ON g.client_id = c.client_id
LEFT JOIN scope s ON s.codigo = g.scope
ORDER BY c.client_id, s.audiencia, g.scope;

-- Q3. Lo esperado para H5 frente a lo concedido
SELECT e.client_id, e.scope,
       CASE WHEN c.client_id IS NULL THEN 'CLIENTE NO EXISTE'
            WHEN s.codigo   IS NULL THEN 'SCOPE NO ESTA EN CATALOGO'
            WHEN g.scope    IS NULL THEN 'FALTA CONCEDER'
            ELSE 'CONCEDIDO' END AS estado
FROM (VALUES
  ('modulo-marketplace','ventas:pedidos:crear'),
  ('modulo-retail','ventas:pedidos:crear'),
  ('modulo-chatbot','ventas:pedidos:crear'),
  ('modulo-marketplace','catalogo:leer'),
  ('modulo-postventa','inventario:disponibilidad:leer'),
  ('modulo-productos','tokens:introspeccion'),
  ('modulo-productos','roles:leer'),
  ('modulo-productos','catalogo:leer'),
  ('modulo-productos','precios:leer'),
  ('modulo-ventas','usuarios:leer'),
  ('modulo-despacho','usuarios:leer'),
  ('modulo-despacho','direcciones:leer')
) AS e(client_id, scope)
LEFT JOIN cliente_servicio c ON c.client_id = e.client_id
LEFT JOIN scope s ON s.codigo = e.scope
LEFT JOIN cliente_servicio_scope g ON g.client_id = e.client_id AND g.scope = e.scope
ORDER BY e.client_id, e.scope;

-- Q4. Catálogo de scopes aplicado, por audiencia
SELECT audiencia, count(*) AS scopes
FROM scope
GROUP BY audiencia
ORDER BY audiencia;

-- Q5. Concesiones a scopes que no están en el catálogo
SELECT g.client_id, g.scope
FROM cliente_servicio_scope g
LEFT JOIN scope s ON s.codigo = g.scope
WHERE s.codigo IS NULL;

-- Q6. Cuentas por rol (solo conteos, sin datos personales)
SELECT rol_codigo, count(*) AS cuentas
FROM usuario_rol
GROUP BY rol_codigo
ORDER BY rol_codigo;
```

David comparte solo cliente, scopes, audiencias, estados y conteos.

### Resultado del 10-nov-2026 (ejecutado por David en Supabase)

- **Q0:** las cinco tablas existen con las columnas del esquema documentado.
- **Q1:** ocho clientes, todos activos: `modulo-chatbot`, `modulo-despacho`, `modulo-marketplace`, `modulo-postventa`, `modulo-productos`, `modulo-retail`, `modulo-ventas` y `simulador-pasarela`. Esto resuelve la contradicción con la evidencia del 6-nov: los clientes existen.
- **Q5:** ninguna concesión apunta a un scope fuera del catálogo.
- **Q6:** CLIENTE 32, VENDEDOR 3, ADMIN_VENTAS 5, GESTOR_DESPACHO 1, GESTOR_COMERCIAL 1, ADMIN_SISTEMA 3.

**Q2 — concesiones por cliente** (nivel 3; no acredita emisión ni operación real):

| Cliente | Scopes concedidos |
|---|---|
| `modulo-chatbot` | `tokens:introspeccion` · `cotizaciones:calcular`, `seguimientos:leer` · `catalogo:leer`, `precios:leer`, `promociones:leer`, `promociones:evaluar`, `cupones:validar`, `recomendaciones:leer`, `inventario:disponibilidad:leer` |
| `modulo-despacho` | `usuarios:leer`, `direcciones:leer` · `ventas:pedidos:estado` · `productos:fisicos:leer` |
| `modulo-marketplace` | `cotizaciones:calcular`, `seguimientos:leer` · `catalogo:leer`, `precios:leer`, `inventario:disponibilidad:leer` · `ventas:pedidos:crear` |
| `modulo-postventa` | `usuarios:leer` · `inventario:disponibilidad:leer`, `inventario:reintegrar` · `ventas:pedidos:consultar`, `ventas:pedidos:reemplazar` |
| `modulo-productos` | `tokens:introspeccion`, `roles:leer` · `catalogo:leer`, `precios:leer` |
| `modulo-retail` | `ventas:pedidos:crear`, `ventas:pedidos:consultar`, `ventas:pedidos:estado` |
| `modulo-ventas` | `usuarios:leer` · `despachos:crear`, `despachos:cancelar` · `postventa:reembolsos:procesar` · `inventario:reservar`, `inventario:consumir`, `inventario:liberar`, `inventario:operaciones:leer` |
| `simulador-pasarela` | `ventas:pagos:webhook` |

**Q3:** todo lo esperado para H5 está concedido, salvo **`modulo-chatbot` · `ventas:pedidos:crear` → FALTA CONCEDER**.

**Q4 frente a `R__catalogo_scopes.sql`:**

| Audiencia | Base | Repo | Diferencia deducida |
|---|---:|---:|---|
| `api-seguridad` | 4 | 5 | Falta `usuarios:leer:documento` |
| `api-despacho` | 4 | 4 | — |
| `api-ventas` | 5 | 5 | La base tiene `ventas:pedidos:reemplazar` y no `ventas:pedidos:anular` |
| `api-postventa` | 1 | 3 | Faltan `postventa:devoluciones:escribir` y `postventa:reclamos:escribir` |
| `api-productos` | 13 | 11 | La base tiene además `productos:fisicos:leer` e `inventario:operaciones:leer` |

**Confirmado con Q7 el 10-nov:** la base tiene 27 scopes y la diferencia con el repositorio es exactamente la de la tabla.

```sql
-- Q7. Códigos del catálogo, para confirmar la diferencia con el repositorio
SELECT audiencia, codigo FROM scope ORDER BY audiencia, codigo;
```

**Hallazgos para PO-T2 (Sergio):**
- `ventas:pedidos:reemplazar`, `inventario:operaciones:leer` y `productos:fisicos:leer` están en la base y concedidos, pero no en el catálogo del repositorio. David informó el 10-nov que los acordó por chat con los módulos dueños y los concedió él mismo durante la semana del 2 al 6-nov, sin documentarlo; no se conserva la fecha exacta ni quién aprobó cada uno. Se registran en la matriz como «concedido; aprobación por chat, fuente no conservada» y se añaden a `R__catalogo_scopes.sql` y a `concesiones-servicio.sql`. La matriz todavía dice que `productos:fisicos:leer` no está confirmado por G6: conviene pedir a G6 una confirmación escrita.
- **Desde H5, toda concesión nueva se hace desde el módulo de configuración (FEAT-14),** que la audita con autor y fecha. Así no se repite este hueco.
- La matriz registra muchos más grants de los que dice «Pendiente»: se actualiza con este resultado.
- Los scopes de Postventa para los canales (`postventa:devoluciones:escribir`, `postventa:reclamos:escribir`), `ventas:pedidos:anular` y `usuarios:leer` para los canales están aprobados en la matriz, pero no concedidos. **H5 no los necesita** para la compra por canal; se conceden solo si un módulo los pide.

## 3. Grants que H5 puede requerir

Según Q3 del 10-nov, H5 requiere **un solo grant nuevo**. **Concedido el 10-nov** por David; la comprobación devolvió la fila esperada. Falta comprobar la emisión del token (nivel 4).

| Cliente | Scope | Respaldo | Para |
|---|---|---|---|
| `modulo-chatbot` | `ventas:pedidos:crear` | Aprobado en el acuerdo A10 (issue #42), según la [matriz de scopes](matriz-scopes.md); incluido en `concesiones-servicio.sql` | FEAT-20, compra desde Chatbot |

```sql
-- Idempotente: solo concede si el cliente y el scope existen; no duplica.
INSERT INTO cliente_servicio_scope (client_id, scope)
SELECT v.client_id, v.scope
FROM (VALUES ('modulo-chatbot','ventas:pedidos:crear')) AS v(client_id, scope)
JOIN cliente_servicio c ON c.client_id = v.client_id
JOIN scope s ON s.codigo = v.scope
ON CONFLICT (client_id, scope) DO NOTHING;

-- Comprobación (solo lectura)
SELECT client_id, scope FROM cliente_servicio_scope
WHERE client_id = 'modulo-chatbot' AND scope = 'ventas:pedidos:crear';
```

`modulo-productos` (`catalogo:leer`, `precios:leer`) y `modulo-postventa` (`inventario:disponibilidad:leer`) ya están concedidos; solo falta añadirlos a `concesiones-servicio.sql` para que el repositorio refleje la base.

Reglas:
- Si Q3 devuelve `CLIENTE NO EXISTE`, primero se crea el cliente con el runbook del repo backend. Este SQL no crea clientes.
- Si Q3 devuelve `SCOPE NO ESTA EN CATALOGO`, primero se aplica el catálogo; no se inserta el scope a mano.
- `productos:fisicos:leer` para `modulo-despacho` no se concede: G6 no lo ha confirmado.
- Un cliente de prueba para la carga o para el módulo de configuración es un `client_id` nuevo y necesita la aprobación de Sergio.
- Después de conceder, se repite Q3 y se comprueba la emisión. Solo se anotan `client_id`, scopes, audiencia y código HTTP.
- Los grants aprobados que no están en `concesiones-servicio.sql` se añaden a ese archivo para que el repositorio refleje la base.
