# Validación local del contrato, mocks y kit — punto 9

**Cierre documental:** 12-oct-2026. Continúa el corte local del 11-oct.
**Resultado:** OpenAPI/ejemplos, mocks y validadores del kit comprobados.
No hay aplicación de Seguridad implementada, publicación nueva ni prueba en
nube con Ventas. [Plan](../../plan/README.md) y
[evidencia por caso](evidencia-validacion-hito-3.md).

## Contenido examinado

| Artefacto | Revisión | SHA-256 |
|---|---|---|
| OpenAPI v1 local | 1.1.0 | `08BBAEADA53DAC3011DF5572234C0E93C7F49AD145C4B2A3D9EB07DF45010C1E` |
| OpenAPI v2 local parcial | 2.0.0 | `C2B3D4712FE481072E96F097A357408E8D0FDB35229BE4926EFE0F53407F9511` |
| Kit con validadores ejecutados | Corte anterior al ajuste CSAT | `62BC920E4E38874C3EB38C602A9E5556CB1055F75A8097F4792A753DE42E7807` |
| Kit preparado para publicación | Ajuste documental CSAT, sin commit | `88A7D6D3D59F0BB72A83D10147AC5F99A06CEB139EF08BD2FA6BC58F3A06D219` |
| OpenAPI público congelado | 1.0.0 | `4E4420FD1395DBF2A3A0B19F6BB7C8DCAE9091547B0D77C16EA17E7AF2C946E7` |

No se modificaron endpoints ni schemas durante este bloque. La base pública
sigue fijada en [fuentes](../fuentes-oficiales.md). Las mejoras del kit son
documentales y locales; no prueban que un consumidor ya las haya adoptado.

La preparación de publicación del 12-oct retiró CSAT de la lista de scopes
del kit por #44. Se contrastaron literalmente los bloques Node, Spring y
.NET con los fragmentos ejecutados de la carpeta temporal: no cambiaron.
Los resultados de mocks y firmas corresponden a esa ejecución previa; no
se presentan como pruebas repetidas por un cambio de texto. OpenAPI se
validó de nuevo y conservó los hashes y los resultados indicados aquí.

## Resultado y cobertura

| Comprobación ejecutada | Resultado | Alcance |
|---|---|---|
| OpenAPI 3.1, claves YAML y referencias | Pasó en los tres documentos | Incluye referencias v2 al documento v1 |
| Ejemplos frente a JSON Schema 2020-12 y formatos | 178 correctos | 73 históricos + 75 v1 local + 30 v2 local |
| Compatibilidad estructural de v1 | 42 operaciones conservadas | Rutas, operationId, peticiones, parámetros, seguridad y respuestas existentes; no demuestra compatibilidad de un servidor |
| Casos positivos/negativos de schemas | 15 correctos | MFA, alta estricta y PerfilUsuario con mfaHabilitado |
| HTTP contra Prism | 68 peticiones correctas | 14 operaciones: 61 respuestas previstas y 7 peticiones inválidas |
| Respuestas de mock frente a schemas | 61 correctas | Incluye 204 vacío y referencias externas; 4 cabeceras Retry-After comprobadas |
| Fragmento Node del kit | Ejecutado sin errores de sintaxis | jose 6.2.12 y JWKS remoto temporal |
| Fragmentos Spring del kit | Compilación correcta | Spring Security 6.5.0 / Framework 6.2.7, JDK 23.0.2 |
| Fragmento .NET del kit | Compilación correcta, 0 errores/advertencias | SDK 10.0.401, JwtBearer 10.0.0; dos tipos de token del framework |
| Tokens firmados contra validadores | 88 resultados esperados | 22 casos × Node, Spring, .NET JsonWebToken y .NET JwtSecurityToken |

En la comprobación Node hubo una descarga de JWKS para los 22 casos: se
comparte su caché por proceso. .NET obtuvo el documento de descubrimiento y
sus claves. No se probó rotación real ni indisponibilidad de un proveedor en
producción; no ampliar estas conclusiones a esos escenarios.

## Correcciones que surgieron de ejecutar el kit

La primera ejecución encontró 11 aceptaciones inesperadas en el conjunto de
66 comprobaciones originales: `jti` vacío/no textual, ausencia de `iat` o
`jti`, y `roles` ausente o con formato incorrecto, según la librería.
Se corrigieron exclusivamente los bloques de ejemplo de
[kit-integracion.md](../../specs/kit-integracion.md):

- Node exige `jti` textual no vacío además de sus claims requeridos.
- Spring exige `iat` antes de convertir claims, y `jti` no vacío. Su
  conversor por defecto puede completar un `iat` ausente a partir de `exp`;
  comprobar solo `getIssuedAt()` no detectaba el problema.
- .NET comprueba los tipos JSON originales y `roles` como array, incluso
  vacío. Conserva tanto `JsonWebToken` como `JwtSecurityToken`, sin inferir
  la forma del array desde los claims aplanados del principal.
- El comando del mock fija Prism 5.16.0 y documenta el Node comprobado; la
  antigua indicación genérica de Node 18 no sirve para esa distribución.

La repetición final pasó los 88 casos. Usuarios con `roles: []` siguen
siendo tokens de usuario válidos; no acceden a una operación que exige
`CLIENTE`. Tampoco se exige una audiencia nueva a los tokens de usuario.

Referencias de las APIs utilizadas: [jose jwtVerify](https://github.com/panva/jose/blob/main/docs/jwt/verify/functions/jwtVerify.md),
[Spring JWT](https://docs.spring.io/spring-security/reference/6.5/servlet/oauth2/resource-server/jwt.html),
[conversor de claims Spring 6.5.0](https://github.com/spring-projects/spring-security/blob/6.5.0/oauth2/oauth2-jose/src/main/java/org/springframework/security/oauth2/jwt/MappedJwtClaimSetConverter.java)
y [JwtBearerOptions](https://learn.microsoft.com/en-us/dotnet/api/microsoft.aspnetcore.authentication.jwtbearer.jwtbeareroptions?view=aspnetcore-10.0).

## Cómo se ejecutó

Se usó una carpeta temporal fuera del repositorio:
`C:\Users\luism\AppData\Local\Temp\seguridad-validacion-01a12d44`.
Los programas auxiliares extraen los bloques literales del kit y agregan solo
imports, clase/contexto y lectura de casos para ejecutarlos. No implementan
operaciones de negocio ni se incorporan al backend/frontend.

1. Validar los tres YAML con las versiones de
   [requirements-contract.txt](../../scripts/requirements-contract.txt) y
   [verificar-contratos.py](../../scripts/verificar-contratos.py).
2. Extraer los ejemplos del kit, compilar Spring/.NET y configurar un issuer
   temporal confiable. Generar dos pares RSA de 2048 bits; publicar solo el
   primero en un servidor JWKS local, con descubrimiento.
3. Firmar fixtures en memoria: usuario válido, multirrol, sin roles, vendedor,
   vencido, emisor incorrecto, otra firma, servicio, servicio con UUID,
   refresco, UUID inválido, claims ausentes/mal tipados y HS256. Alterar
   además un payload sin volver a firmarlo y probar entrada sin token.
4. Comparar aceptación/rechazo y presencia de CLIENTE con la matriz. Las
   cuatro ejecuciones deben coincidir. `roles: []` y VENDEDOR son aceptados
   criptográficamente, pero no cumplen el requisito CLIENTE.
5. Arrancar Prism para v1/v2 en puertos temporales de `127.0.0.1`, con
   `--errors true`. Usar `Prefer: code=..., example=...` para seleccionar
   respuestas y contrastarlas con su schema/cabeceras. Cerrar ambos procesos.

En esta sesión se puede repetir con las herramientas temporales existentes:

```powershell
node "$env:TEMP\seguridad-validacion-01a12d44\jwt-harness.mjs" after
node "$env:TEMP\seguridad-validacion-01a12d44\mock-harness.mjs"
```

Usar el ejecutable Node 24.19.0 o uno compatible; el Node por defecto de este
equipo era 22.22.3 y no sirve para la versión de Prism utilizada.
Las herramientas temporales pueden desaparecer al limpiar Temp. La evidencia
consolidada queda en Markdown, con versiones, hashes y cada caso; no se añadió
un harness permanente al proyecto por el alcance documental solicitado.

Las claves privadas nunca se escribieron en archivos ni se imprimieron.
Las fixtures temporales son datos sintéticos, sin cuentas de la plataforma.
Los tokens válidos duran 15 minutos desde cada ejecución; se regeneran al
repetir la prueba. No se publicaron tokens ni credenciales reales.

## Qué falta para salir a integración

Los mocks comprueban formatos y ejemplos. `Prefer` selecciona un resultado:
no simula credenciales, revocación, estado de cuenta o autorización reales.
Las siete peticiones inválidas produjeron cinco `422` y dos `400` de Prism;
no se presentan como prueba de códigos de error del servidor de Seguridad.

Las pruebas firmadas comprobaron los **validadores de ejemplo** y su política
de roles, no un login de Seguridad ni un endpoint de Ventas. Los rechazos
401/403 y el aislamiento de pedidos entre A/B deben probarse en los servicios
implementados y desplegados según [ficha bilateral](hito-3-ventas.md).

Actualización posterior: Joseph confirmó recorrido/Michael/fecha en G5 Backend #2. Siguen pendientes entornos, rama, hora y aceptación de H-11; publicación de la
revisión y aviso del PO; aplicación/provisión; prueba conjunta en nube.
No se probaron sesiones/refresco reales, consumo único/concurrencia de OTP,
auditoría transaccional, persistencia, correo ni activación. DAT-01/H-12/H-14,
A9 y suscripciones conservan sus estados en el
[registro único](../registro-inconsistencias.md).

El punto 9 queda **validado localmente en este alcance**. Publicación e
integración real siguen pendientes; el plan completo no se declara terminado.
