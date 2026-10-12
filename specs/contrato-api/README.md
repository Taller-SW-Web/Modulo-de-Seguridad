# Contratos de Seguridad

| Documento | Versión | Uso |
|---|---|---|
| [openapi.yaml](openapi.yaml) | 1.1.0 local | Evolución aditiva de v1; login/JWKS mantienen la interfaz publicada |
| [openapi-v2.yaml](openapi-v2.yaml) | 2.0.0 local, parcial | Configuración MFA segura y alta administrativa estricta |
| [Base histórica](historico/openapi-1.0.0.yaml) | 1.0.0 congelada | Referencia exacta del contrato público fijado, no MFA a desplegar |

No hay revisión nueva publicada ni servicios acreditados. Sergio conserva
propiedad contractual; edición de esta revisión delegada según autorización
comunicada por Luis. [ADR-008](../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md)
registra compatibilidad, límites de publicación y transición.

## Verificar

En un entorno Python aislado: instalar `scripts/requirements-contract.txt`
y ejecutar `python scripts/verificar-contratos.py` desde la raíz.
La validación usa OpenAPI 3.1 y JSON Schema 2020-12; verifica referencias
externas de v2 al documento v1 y ejemplos con formatos.
[Especificación oficial](https://spec.openapis.org/oas/v3.1.0.html).

`python scripts/verificar-documentacion.py` comprueba reparto y enlaces.
Estas herramientas no prueban firmas JWT, estados de servidor ni despliegues.
La [validación adicional del punto 9](../../docs/integracion/validacion-hito-3.md)
ejecutó mocks y los ejemplos del kit con firmas RS256; también registra sus
límites y evidencia por caso. Servidores/publicación siguen pendientes.

## Pendientes de implementación y publicación

DAT-01 exige esquema de OTP/cambios y vínculo jti-familia; H-12 requiere términos
para registro/activación; H-14 contacto de soporte. Aviso ADR-003 conservado.
Rutas MFA históricas v1 no se despliegan: no completan confirmación.
La integración mínima de Ventas utiliza login/JWKS v1. El recorrido,
Michael y entrega G5 jueves 15 fueron confirmados en
[G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2);
configuración, rama y prueba siguen pendientes. No depende de alta ni
configuración MFA v2.
