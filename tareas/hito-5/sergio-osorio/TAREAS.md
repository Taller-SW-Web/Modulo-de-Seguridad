# H5 — tareas de Sergio Alejandro Osorio Montenegro

**Épicas:** EP-05 Roles y permisos, identidad técnica y Product Owner.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Ramas:** ramas `docs/…` para el contrato; `feat/TEC-INFRA-api-management` si hace falta configuración en los repositorios.
Sus PR los revisa Juan José; él revisa los de David, que trabaja en el paquete de identidad por delegación.

**Fuentes comunes:** [OpenAPI v1](../../../specs/contrato-api/openapi.yaml), [OpenAPI v2](../../../specs/contrato-api/openapi-v2.yaml),
[matriz de scopes](../../../docs/integracion/matriz-scopes.md),
[verificación de clientes técnicos H5](../../../docs/integracion/hito-5-clientes-tecnicos.md),
[SPEC-17](../../../specs/funcionales/SPEC-17-tokens-servicio.md),
[SPEC-18](../../../specs/funcionales/SPEC-18-consulta-identidad.md),
[evidencia H4 de FEAT-05 y FEAT-06](../../../docs/evidencia/h4/FEAT-05-06.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| PO-T1 | Lote único de contrato H5 | P1 · jue 12-nov | Propuestas de David (mié 11-nov) | Fusionado (#91). Falta el aviso a los seis módulos |
| PO-T2 | Matriz de scopes y concesiones al día | P1 | David OPS-T1 | Backend #88 fusionado y catálogo de 31 scopes aplicado en Supabase; matriz en documentación #90 |
| PO-T3 | Decisiones abiertas | P1 | — | Fusionado (#91): [decisiones del PO](../../../plan/hito-5/DECISIONES-PO.md) |
| FEAT-19-T1 | Plataforma de API Management elegida | P1 · jue 12-nov | — | Fusionado (#91): [ADR-010](../../../docs/arquitectura/adr/010-plataforma-api-management.md) |
| FEAT-19-T2 | API de G7 publicada en la plataforma | P1 | T1 | Desplegada y comprobada (10-nov): [evidencia](../../../docs/evidencia/h5/FEAT-19.md). Plantilla en backend #89 |
| FEAT-19-T3 | Seguridad, límite de uso y monitoreo, con evidencia | P1 | T2 | Verificada (10-nov): 401 sin clave, 429 sobre el límite, métricas en Azure Monitor. Falta la captura del panel |
| FEAT-05-T4 | Rotación de clave con dos `kid` | P2 | David ejecuta en Render | Programada para el jue 12-nov; guion corregido para el acceso cifrado |
| FEAT-06-T4 | Consulta de identidad con 200 y 403 | P2 | David OPS-T2 | Verificada (10-nov): [evidencia](../../../docs/evidencia/h5/FEAT-05-06.md) |

## PO — contrato y decisiones (P1)

### PO-T1 — Lote de contrato H5
- **Resultado:** publicar de una vez las rutas del módulo de configuración (FEAT-14) y el formato cifrado opcional del token (FEAT-16), y confirmar el alcance de SPEC-03 para FEAT-13.
- **Aceptación:** contrato publicado el jue 12-nov; aviso a los seis módulos. Todo es aditivo: nada de lo que ya consumen cambia.
- **Avance del 10-nov:** OpenAPI v2 2.1.0 con las rutas de SPEC-19, contrato v1 1.2.0 ratificado y alcance de SPEC-03 decidido. Detalle en [decisiones del PO](../../../plan/hito-5/DECISIONES-PO.md).

### PO-T2 — Matriz y concesiones
- **Resultado:** la matriz distingue scope aprobado, registrado en el catálogo, concedido, token emitido y operación probada, según lo que David obtenga de Supabase. Los grants aprobados que faltan en `concesiones-servicio.sql` se añaden.
- **Aceptación:** ya no hay contradicción entre la matriz y las evidencias de H4.
- **Hallazgo:** `modulo-retail` tiene dos concesiones sin respaldo. Se pide confirmación a G5 y G4 y, si no llega, se revocan antes del vie 20-nov.

### PO-T3 — Decisiones
- Términos de servicio (H-12) y contacto de soporte (H-14).
- Si G3 y G4 entran en la compra por canal.
- Alternativa si G2 no llega a consumir eventos.
- Persistencia de la sesión al recargar: no está en la rúbrica.

## FEAT-19 — API Management (P1)

API Management es gestionar la API de forma centralizada: publicarla, asegurarla, limitar su uso y monitorearla desde una plataforma especializada, como Apigee, Azure API Management o AWS API Gateway.

### FEAT-19-T1 — Plataforma
- **Primer paso:** comprobar cuál de esas plataformas puede usar el equipo sin costo y si importa OpenAPI.
- **Aceptación:** plataforma elegida el jue 12-nov, con el motivo.
- **Decisión del 10-nov:** Azure API Management, plan Consumption, con Azure for Students. Plan B: AWS API Gateway.

### FEAT-19-T2 — Publicación
- **Resultado:** la API de G7 importada desde el OpenAPI y accesible por la plataforma.
- **Aceptación:** las rutas responden igual por la plataforma que de forma directa. La URL directa sigue funcionando: ningún módulo está obligado a cambiar.
- **Estado (10-nov):** pasarela `g7-seguridad-apim` desplegada en South Central US; cuatro rutas responden igual por la pasarela y directo.

### FEAT-19-T3 — Seguridad, límite y monitoreo
- **Resultado:** una política de acceso, un límite de peticiones demostrable y el panel de uso de la plataforma.
- **Aceptación:** una petición por encima del límite se rechaza; el panel muestra el tráfico de la prueba.
- **Evidencia:** [`docs/evidencia/h5/FEAT-19.md`](../../../docs/evidencia/h5/FEAT-19.md), sin claves.
- **Estado (10-nov):** sin clave → 401; ráfaga de 40 → 27 con 200 y 13 con 429; Azure Monitor separa el tráfico por código. Falta adjuntar la captura del panel de métricas.

## FEAT-05-T4 y FEAT-06-T4 (P2)

### FEAT-05-T4 — Rotación de clave
- **Primer paso:** pasar a David los pasos del runbook para que los ejecute en Render.
- **Comprobación:** `infra/verificacion/comprobar_rotacion.py` del repo backend, antes y después de la rotación.
- **Aceptación:** el JWKS publica dos claves y un token anterior sigue validando.

### FEAT-06-T4 — Consulta de identidad
- **Aceptación:** con token de servicio, 200 con el scope y 403 sin él, en el despliegue.
- **Comprobación:** `infra/verificacion/comprobar_consulta_identidad.py` del repo backend, con el secreto de `modulo-prueba-h5` en una variable de entorno.
- **Evidencia de ambas:** `docs/evidencia/h5/FEAT-05-06.md`.
