# Contratos de Seguridad

| Documento | Versión | Uso |
|---|---|---|
| [openapi.yaml](openapi.yaml) | 1.2.0 publicada | Evolución aditiva de v1; login/JWKS mantienen la interfaz publicada. 1.2.0 añade `formatoAcceso` opcional y `usuario.permisos` ([ADR-009](../../docs/arquitectura/adr/009-acceso-cifrado-opcional.md)) |
| [openapi-v2.yaml](openapi-v2.yaml) | 2.1.0 publicada, parcial | Configuración MFA segura, alta administrativa estricta y, desde 2.1.0, gestión de clientes técnicos ([SPEC-19](../funcionales/SPEC-19-clientes-tecnicos.md)) |
| [Base histórica](historico/openapi-1.0.0.yaml) | 1.0.0 congelada | Referencia exacta del contrato público fijado, no MFA a desplegar |

Definiciones publicadas por [PR #45/#46](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/pull/46),
main 5796622f7c039021e03dd25b2338c6a208320e05, comprobado 12-oct.
No acredita servicios implementados. Aviso/adopción externa siguen en GOV-01.
Sergio conserva
propiedad contractual; edición de esta revisión delegada según autorización
comunicada por Luis. [ADR-008](../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md)
registra compatibilidad, límites de publicación y transición.

## Lote de contrato H5 (10-nov-2026)

Publicado por Sergio (PO-T1). Todo es aditivo: nada de lo que ya consumen los
otros módulos cambia.

| Cambio | Documento | Afecta a los módulos consumidores |
|---|---|---|
| `formatoAcceso` opcional y `usuario.permisos` (FEAT-16, [ADR-009](../../docs/arquitectura/adr/009-acceso-cifrado-opcional.md)) | v1 1.2.0 | No, salvo que pidan `formatoAcceso: CIFRADO`. Sin ese campo la respuesta es la de siempre |
| Gestión de clientes técnicos en `/clientes-servicio` y `/scopes` (FEAT-14, SPEC-19) | v2 2.1.0 | No. Son rutas de administración para `ADMIN_SISTEMA` |
| Permiso `integracion.gestionar` en el claim `permisos` de `ADMIN_SISTEMA` | SPEC-11 | No. `permisos` solo lleva permisos de este módulo |
| Alta administrativa de personal `POST /api/v2/usuarios` (FEAT-13) | v2, sin cambios desde 2.0.0 | No. El alcance está en [decisiones del PO para H5](../../plan/hito-5/DECISIONES-PO.md) |

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
límites y evidencia por caso. Servidores y prueba conjunta siguen pendientes.

## Pendientes de implementación y publicación

DAT-01 exige esquema de OTP/cambios y vínculo jti-familia; H-12 requiere términos
para registro/activación; H-14 contacto de soporte. Aviso ADR-003 conservado.
Rutas MFA históricas v1 no se despliegan: no completan confirmación.
La integración mínima de Ventas utiliza login/JWKS v1. El recorrido,
Michael y entrega G5 jueves 15 fueron confirmados en
[G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2);
configuración, rama y prueba siguen pendientes. No depende de alta ni
configuración MFA v2.
