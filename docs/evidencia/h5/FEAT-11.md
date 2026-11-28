# FEAT-11 — Favoritos de Marketplace y acceso al catálogo de G6

**Corte:** 27-nov-2026 (Lima). Continúa la [evidencia de H4](../h4/FEAT-11.md).

## T6 — Favoritos A/B en despliegue

**Estado:** pendiente de verificación desplegada. G1 publicó pruebas automatizadas
de titularidad y cerró la implementación de lectura y borrado. Las tareas de
integración de [lectura](https://github.com/Taller-SW-Web/G1-Marketplace-Backend/issues/182)
y [borrado](https://github.com/Taller-SW-Web/G1-Marketplace-Backend/issues/187)
siguen abiertas.

Para cerrar T6, registrar con dos cuentas de prueba, en el despliegue de
Marketplace, que A gestiona sus favoritos y que B no puede consultar, modificar
ni borrar un favorito de A. Adjuntar fecha, SHA desplegado, rutas y resultados
HTTP. El aislamiento A/B de los pedidos está documentado en
[FEAT-20](FEAT-20.md#feat-20-t2--prueba-desplegada-con-marketplace-g1).

## T7 — Llamada real a G6 por APIM

**Estado:** positivo verificado por G1. El 27-nov a las 15:15 Lima, Jim confirmó
que el BFF obtuvo de G7 un token de `modulo-marketplace` con `catalogo:leer` y
audiencia `api-productos`, y que el catálogo real respondió `200` vía APIM de
G6. Hora y claims reportados en la coordinación de integración.

G1 publicó un retest del mismo día a las 15:04:39 Lima: `GET /api/v1/catalog/home`
respondió `200` con fuente `REAL`, en el BFF desplegado `bcac2f6`. G1 confirmó
de forma independiente ese resultado funcional y dejó indicado que solo dispone
del identificador del BFF, sin correlación de la petición en APIM.
[Retest de G1](https://github.com/Taller-SW-Web/G1-Marketplace-Backend/issues/45#issuecomment-7227214020) ·
[Revisión de G1](https://github.com/Taller-SW-Web/G1-Marketplace-Backend/issues/45#issuecomment-7229188487).

**Pendiente:** G1 y G6 deben confirmar un `403` de la política de scope para una
operación de prueba acordada, con token válido, audiencia `api-productos` y sin
el scope de esa operación. Registrar fecha, ruta, código y versiones, más el
identificador de correlación de APIM si está disponible.
