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
| El 6-nov se registró que no existía ningún cliente técnico de prueba provisionado | [FEAT-05 y FEAT-06](../evidencia/h4/FEAT-05-06.md) | Contradice las dos filas anteriores; se resuelve con Q1 |
| El catálogo de scopes aprobados está en `R__catalogo_scopes.sql` | Repo backend, `develop` | 2 como archivo; no consta que esté aplicado |
| Se configuraron clientes para Marketplace, Productos, Ventas, Postventa, Retail, Chatbot y Despacho | Indicado por David | Sin registro en los repositorios; se confirma con Q1 y Q2 |

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

**Resultado:** pendiente de ejecución.

## 3. Grants que H5 puede requerir

Se ejecutan **solo** para las filas que Q3 devuelva como `FALTA CONCEDER`. Cada una tiene respaldo en la matriz.

| Cliente | Scope | Respaldo | Para |
|---|---|---|---|
| `modulo-marketplace`, `modulo-retail`, `modulo-chatbot` | `ventas:pedidos:crear` | Aprobado en el acuerdo A10, según la [matriz de scopes](matriz-scopes.md); incluido en `concesiones-servicio.sql` | FEAT-20 compra por canal |
| `modulo-productos` | `catalogo:leer`, `precios:leer` | Aprobado por G6 en el issue #78, según la matriz; **no** está en `concesiones-servicio.sql` | Integración interna de G6 |
| `modulo-postventa` | `inventario:disponibilidad:leer` | Aprobado por G6 el 5-nov, según la matriz; **no** está en `concesiones-servicio.sql` | Flujo de cambio de Postventa |

```sql
-- Idempotente: solo concede a clientes y scopes que ya existen y no duplica filas.
-- Dejar en VALUES únicamente las filas que Q3 marcó como FALTA CONCEDER.
INSERT INTO cliente_servicio_scope (client_id, scope)
SELECT v.client_id, v.scope
FROM (VALUES
  ('modulo-marketplace','ventas:pedidos:crear'),
  ('modulo-retail','ventas:pedidos:crear'),
  ('modulo-chatbot','ventas:pedidos:crear'),
  ('modulo-productos','catalogo:leer'),
  ('modulo-productos','precios:leer')
) AS v(client_id, scope)
JOIN cliente_servicio c ON c.client_id = v.client_id
JOIN scope s ON s.codigo = v.scope
ON CONFLICT (client_id, scope) DO NOTHING;
```

Reglas:
- Si Q3 devuelve `CLIENTE NO EXISTE`, primero se crea el cliente con el runbook del repo backend. Este SQL no crea clientes.
- Si Q3 devuelve `SCOPE NO ESTA EN CATALOGO`, primero se aplica el catálogo; no se inserta el scope a mano.
- `productos:fisicos:leer` para `modulo-despacho` no se concede: G6 no lo ha confirmado.
- Un cliente de prueba para la carga o para el módulo de configuración es un `client_id` nuevo y necesita la aprobación de Sergio.
- Después de conceder, se repite Q3 y se comprueba la emisión. Solo se anotan `client_id`, scopes, audiencia y código HTTP.
- Los grants aprobados que no están en `concesiones-servicio.sql` se añaden a ese archivo para que el repositorio refleje la base.
