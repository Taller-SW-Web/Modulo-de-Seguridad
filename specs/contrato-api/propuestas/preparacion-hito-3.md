# Cierre de propuestas internas — revisión local 11-oct

**Estado:** propuestas trasladadas al contrato local por edición delegada.
Sergio conserva propiedad; Luis comunicó autorización para trabajar por él.
No se presume publicación, implementación ni aceptación de consumidores.

La propuesta inicial de rutas aditivas MFA en v1 se **sustituye por v2 parcial**:
evita modificar silenciosamente el flujo publicado incompleto.
[ADR-008](../../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md).

| Asunto | Decisión aplicada | Fuente |
|---|---|---|
| H-01 | Perfil propio exige mfaHabilitado efectivo; lectura administrativa y atributos según rol; servicio omite campos privados | [v1 local 1.1.0](../openapi.yaml), Usuario/PerfilUsuario; SPEC-16 |
| H-02 | Solicitar, reenviar y confirmar con código de correo; token opaco vinculado a usuario/familia/acción; no emite sesión | [v2 local 2.0.0](../openapi-v2.yaml), [SPEC-10](../../funcionales/SPEC-10-activacion-mfa.md) |
| H-05 | Retry-After positivo en todas las respuestas 429 existentes, según ventana funcional | v1 y v2 locales |
| H-06 | Reenvío invalida código anterior del desafío sin extender desafío; SMS sin celular 400 VALIDACION, sin envío/cuota | v1, SPEC-09 |
| H-03 | Celular cambiado no verificado; A2 excluye prueba externa de contacto | v1, SPEC-16 |
| H-10 | Alta estricta en POST /api/v2/usuarios, rol opcional entre cinco; sin CLIENTE ni contraseña inicial | v2, SPEC-03 y FRONT-11 |
| H-13 | Edición propia nombres/apellidos/celular; documento y atributos de rol requieren atributos.editar | v1, SPEC-11/16 |
| KIT-02 | Reúso de refresh revoca familia/sesión afectada, otras sesiones independientes se conservan | v1, SPEC-06 |
| KIT-04 / A1 | Retorno al login configurado de cada canal, sin retorno arbitrario ni token en URL | v1, kit |
| JWT-01 | Issuer exacto del descubrimiento; RS256; usuario UUID/tipo acceso; no exigir aud de servicio al usuario | v1, kit |

El detalle de vencimientos, errores, intentos, consumo único, concurrencia y
auditoría del cambio se mantiene en SPEC-10 y sus schemas v2.
FRONT-09 está definida localmente. No se despliegan las rutas MFA históricas
v1 sin confirmación; su descripción original se conserva en
[base congelada](../historico/openapi-1.0.0.yaml).

## Pendientes reales

- DAT-01: nueva representación OTP/cambio, vínculo jti-familia y migración
  revisada; no modificar V1–V4 ya aplicadas. [Contraste](../../../docs/integracion/revision-datos.md).
- H-12: texto, versión y fuente de términos; Luis no identifica una fuente existente.
  No inventar consentimiento ni versión para fixtures.
- H-14: contacto de soporte concreto aún no definido; no inventar correo/URL.
- Publicación: revisión identificable, aviso de una semana de ADR-003 y
  coordinación externa. No se acuerda excepción por la demo en cinco días.
- Pruebas de servidor de OTP, auditoría, firma JWT y despliegues. El punto 9
  validó mocks y ejemplos del kit con firmas reales; no probó el servidor.

La demo propuesta para Ventas usa login/JWKS v1, no alta ni configuración MFA
v2. Recorrido, Michael y fecha G5 están confirmados en G5 Backend #2, con
acuse de Luis publicado; faltan configuración y ejecución. A10 sigue siendo
otro acuerdo y su catálogo se ajusta por #44. Marketplace conserva alcance general.

## Comprobaciones reproducibles

`python scripts/verificar-contratos.py`: OpenAPI, referencias, ejemplos,
schemas y peticiones v1 conservadas. Dependencias en
`scripts/requirements-contract.txt`; [guía](../README.md).

`python scripts/verificar-documentacion.py`: enlaces, ocho épicas,
dieciocho specs, treinta fichas y cuarenta y ocho tareas.
Estas comprobaciones no acreditan servidores ni la integración real.
