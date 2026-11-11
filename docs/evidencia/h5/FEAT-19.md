# FEAT-19 — API Management

Responsable: Sergio Alejandro Osorio Montenegro (PO). Corte: 10-nov-2026, America/Lima.
Estado: **T1, T2 y T3 terminadas; pasarela desplegada y comprobada.**
Decisión de plataforma: [ADR-010](../../arquitectura/adr/010-plataforma-api-management.md).

## Qué cubre de la rúbrica

| Criterio «uso de API Management» | Cómo se cumple | Sección |
|---|---|---|
| Publicar la API en una plataforma de gestión | Azure API Management importa 15 operaciones del contrato | T2 |
| Asegurarla | Clave de suscripción obligatoria; sin ella, `401` antes de llegar a Render | T3 |
| Limitar su uso | Política `rate-limit` de 30 peticiones por minuto; por encima, `429` con `Retry-After` | T3 |
| Monitorearla | Métrica *Requests* de Azure Monitor, separada por código de respuesta | T3 |

## Fuentes y versiones

| Componente | Referencia |
|---|---|
| Plantilla, política y guion | `infra/api-management/` del backend [#89](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/89) |
| Contrato publicado por la pasarela | OpenAPI v1 1.2.0, subconjunto de 15 operaciones generado con `generar_openapi_apim.py` |
| Backend detrás de la pasarela | `5f67594` en `develop`, desplegado en Render el 10-nov a las 22:45 |
| Plataforma | Azure API Management, plan Consumption, región South Central US |
| Suscripción | Azure for Students de Sergio (UNMSM) |

## FEAT-19-T1 — plataforma

Azure API Management en plan Consumption. Motivo, alternativas y plan B en el
[ADR-010](../../arquitectura/adr/010-plataforma-api-management.md). De los tres
puntos que el ADR dejó «por comprobar»:

| Punto | Resultado |
|---|---|
| La cuenta institucional califica para Azure for Students | Sí. 100 US$ de crédito, sin tarjeta |
| La importación acepta el contrato | Sí, con el OpenAPI 3.0.3 reducido. No hizo falta probar el 3.1 completo |
| Las métricas de Consumption bastan como panel | Sí. Azure Monitor separa las peticiones por código; no hizo falta Application Insights |

## FEAT-19-T2 — publicación

**Nivel de evidencia:** propia desplegada. **Fecha:** 10-nov-2026, 23:25 (America/Lima).

| Paso | Resultado |
|---|---|
| `az bicep build` | Sin errores |
| Registro del proveedor `Microsoft.ApiManagement` | Registrado |
| Grupo de recursos `g7-seguridad` | Creado en South Central US |
| Despliegue `feat19-apim` | `Succeeded` en 1 min 45 s |
| URL de la pasarela | `https://g7-seguridad-apim.azure-api.net/seguridad/v1` |

Dos restricciones de la suscripción de la UNMSM que cambiaron el plan:

- Solo admite cinco regiones (`southcentralus`, `northcentralus`, `canadacentral`,
  `westus` y `chilecentral`). La región prevista, East US, habría fallado.
- Crear recursos exige MFA, aunque las lecturas no lo pidan.

**Equivalencia con la URL directa.** Salida de `probar_pasarela.py`, 11-nov
04:27 UTC (10-nov 23:27 en Lima):

| Petición | Directa | Por la pasarela | Resultado |
|---|---|---|---|
| `GET /auth/.well-known/jwks.json` | 200 · kid: 12VnSlge | 200 · kid: 12VnSlge | Igual |
| `GET /auth/.well-known/openid-configuration` | 200 · issuer: https://g7-auth-service.onrender.com/api/v1/auth | 200 · issuer: https://g7-auth-service.onrender.com/api/v1/auth | Igual |
| `GET /usuarios/00000000-0000-4000-8000-000000000000` | 401 · code: TOKEN_INVALIDO | 401 · code: TOKEN_INVALIDO | Igual |
| `GET /roles` | 401 · code: TOKEN_INVALIDO | 401 · code: TOKEN_INVALIDO | Igual |

Los `401` de la pasarela los decide el backend de G7: la pasarela reenvía la
cabecera `Authorization` sin tocarla. **La pasarela no reemplaza la validación
del token, del scope ni del permiso.**

**La URL directa sigue funcionando.** Ningún módulo está obligado a cambiar.

## FEAT-19-T3 — seguridad, límite y monitoreo

### Acceso

| Petición | Resultado |
|---|---|
| Sin clave de suscripción | `401`, sin llegar a Render |
| Con una clave inventada | `401` |
| Con la clave de demostración | `200`, con la cabecera `X-G7-Pasarela: azure-apim` |
| Ruta de administración no publicada (`GET /usuarios`) | `404`: la pasarela solo expone las 15 operaciones de integración y sesión |

### Límite de peticiones

Ráfaga de 40 peticiones seguidas, tras esperar 61 s para empezar con la ventana limpia:

| Comprobación | Resultado |
|---|---|
| Peticiones aceptadas | 27 con `200` |
| Peticiones rechazadas | 13 con `429` |
| Primera rechazada | La número 27. Una petición posterior todavía recibió `200`, así que las aceptadas suman 27 |
| `Retry-After` del primer `429` | 43 s |

El límite configurado es 30 por minuto y empezó a rechazar antes, en la 27. Microsoft
advierte que en una pasarela distribuida el límite nunca es del todo exacto. Lo
que se exige es que nunca pase de 30, y no pasó. Las peticiones siguientes, hasta
vencer la ventana, también recibieron `429` («Rate limit is exceeded. Try again
in 25 seconds.»).

### Monitoreo

Métrica *Requests* de Azure Monitor para `g7-seguridad-apim`, separada por
`GatewayResponseCode`, en la hora de la prueba:

| Código | Peticiones | De dónde salen |
|---|---:|---|
| `200` | 29 | 2 de la equivalencia y 27 de la ráfaga |
| `429` | 15 | 13 de la ráfaga y 2 reintentos manuales antes de que venciera la ventana |
| `401` | 4 | 2 de la equivalencia (decididos por el backend), 1 sin clave y 1 con clave inventada |
| `404` | 3 | 1 de la prueba de ruta no publicada; las otras 2 no son de la prueba (posiblemente tráfico externo a una URL pública) |

Consulta usada, sin captura del portal:

```bash
az monitor metrics list --resource <id de g7-seguridad-apim> --metric Requests   --interval PT5M --offset 2h --dimension GatewayResponseCode --aggregation Total
```

En el portal se ve en el recurso, sección **Métricas**, métrica **Requests**,
con «Aplicar división» por **Gateway Response Code**. Falta añadir esa captura.

Las métricas tardan unos minutos en consolidarse.

## Seguridad de la evidencia

- La clave de suscripción no aparece aquí ni en ningún repositorio. Solo la tiene
  Sergio, y se entrega por canal privado a quien la pida.
- Ninguna petición de la prueba usó credenciales de G7: solo rutas públicas y
  casos de `401`.

## Límites que se declaran

- La pasarela es opcional. Ningún módulo la usa todavía.
- El límite es por suscripción y no distingue rutas.
- La pasarela añade latencia. FEAT-17 mide el rendimiento contra la URL directa.
- La instancia vive en la suscripción de estudiante de Sergio, con 100 US$ de
  crédito hasta el 10-nov-2027. El plan Consumption no tiene costo fijo y la
  prueba usó unas 50 de su millón de llamadas gratuitas al mes.
