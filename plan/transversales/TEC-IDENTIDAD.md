# Plan TEC-IDENTIDAD — Firma y API de identidad

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| ID / versión / fecha | TEC-IDENTIDAD / 1.0.0 / 12-oct-2026 |
| Dueño técnico | Sergio Alejandro Osorio Montenegro |
| Clasificación | Capacidad adicional SPEC-17/18; su épica académica sigue siendo EP-05 |
| Estado / revisión | Alcance H3 aprobado según comunicación de Luis; diseño técnico delegado cerrado; ejecución pendiente |
| Base | main `5796622f7c039021e03dd25b2338c6a208320e05`; ajustes de este bloque aún locales |
| Contrato | v1 1.1.0; v2 2.0.0 parcial conserva límites de ADR-008 |
| Seguimiento | [Tablero único de Sergio](../../tareas/hito-3/sergio-osorio/TAREAS.md), IDs TEC-IDENTIDAD separados de EP-05 |

| Entrada | Fuente / aplicabilidad |
|---|---|
| Funcional | [SPEC-17](../../specs/funcionales/SPEC-17-tokens-servicio.md) y [SPEC-18](../../specs/funcionales/SPEC-18-consulta-identidad.md) |
| UI/UX | Sin pantalla propia: API para módulos consumidores. UI de login/sesión consumidora en [FRONT-00](../../specs/front/FRONT-00-comportamiento-comun.md)/[FRONT-01](../../specs/front/FRONT-01-inicio-sesion.md) y G5 |
| API | [OpenAPI v1](../../specs/contrato-api/openapi.yaml), esquemas/security schemes; [kit](../../specs/kit-integracion.md), [errores](../../specs/catalogo-errores.md) y [matriz de scopes](../../docs/integracion/matriz-scopes.md) |
| React | Sin nuevos componentes: [base React](../../specs/componentes-react/FRONT-00-base-compartida.md) de EP-02 consume acceso; jamás recibe client_secret o clave privada |

También gobiernan [CONTEXT](../../CONTEXT.md),
[SPEC-11](../../specs/funcionales/SPEC-11-roles-permisos.md),
[SPEC-12](../../specs/funcionales/SPEC-12-registro-auditoria.md),
[SPEC-16](../../specs/funcionales/SPEC-16-atributos.md),
[ADR-001](../../docs/arquitectura/adr/001-rs256-frente-a-hs256.md),
[ADR-002](../../docs/arquitectura/adr/002-validacion-local-frente-a-introspeccion.md),
[ADR-003](../../docs/arquitectura/adr/003-contrato-antes-que-codigo.md),
[ADR-008](../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md),
[EP-02, plan](../funcionalidades/EP-02-autenticacion-sesiones.md) y
[ficha bilateral H3](../../docs/integracion/hito-3-ventas.md).
No aplica UI/React propia es justificación explícita, no fuente omitida.

**Diseño H3 definitivo:** [composición](../hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../hito-3/DISENO-PERSISTENCIA.md) y [entorno/React](../hito-3/ENTORNO-Y-REACT.md).
Concretan los detalles internos bajo delegación de Luis; su aprobación H3
no se extiende a capacidades de hitos posteriores.

## 2. Objetivo y límites

**H3:** firma RS256 consumida por EP-02, descubrimiento y JWKS públicos con
issuer real. Ventas verifica acceso de G7 y aplica CLIENTE/propiedad por sub.
Seguridad no consulta Ventas en ese recorrido.

**Posterior:** rotación completa, tokens de servicio, scopes, introspección
de sesión y consultas SPEC-18. Catálogo aprobado no acredita cliente/secreto
creado. Christian mantiene aprovisionamiento operativo con catálogo de Sergio.
H3 no necesita adelantar ocho clientes A10 ni implementar /auth/token o
/usuarios/{id} para listar pedidos propios en G5.

El diseño detallado aquí prioriza H3. Antes de tareas posteriores se concreta
su diseño de datos/interfaces necesario. Entregar JWKS no completa SPEC-17/18
ni la épica académica EP-05 de roles y permisos.

## 3. Cobertura y criterios

| Incremento / origen | Superficie | Resultado comprobable |
|---|---|---|
| H3, RF-17.1; ESC-17.1 | GET /api/v1/auth/.well-known/jwks.json | Público, kid seleccionable, sin clave privada; verifica acceso real |
| H3, RF-17.2/17.14 | GET /api/v1/auth/.well-known/openid-configuration y firma | issuer idéntico a iss; jwks_uri absoluto del entorno; sin literal auth-service |
| H3, RF-05.6 y perfil JWT | Firmador interno usado por EP-02 | RS256, tipo acceso, sub UUID, email, roles múltiples, permisos propios, iat/exp/jti; TTL 900 s |
| H3 continuidad / posterior cierre, RF-17.3; ESC-17.2 | Custodia y rotación | H3 conserva claves tras reinicio; rotación nueva/anterior ≥24 h antes de cerrar SPEC-17 |
| Posterior, RF-17.4/17.5/17.12/17.13/17.15; ESC-17.3/17.4 | POST /api/v1/auth/token | Servicio: sub client_id, tipo servicio, scopes concedidos y audiencias dueñas, sin suplantar persona |
| Posterior, RF-17.6/17.7; ESC-17.5/17.6 | POST /api/v1/auth/introspeccion | Actor técnico autorizado; estado real de sesión/cuenta, activo:false cuando ya no utilizable |
| Posterior, RF-17.8–17.10; ESC-17.7/17.8 | Frontera de integración | Token antes de buscar usuario; 401 genérico, 403 sin revelar scope, RFC 7807 |
| Herramientas, RF-17.11; ESC-17.9 | Mocks | Formas reproducibles, sin atribuir firma/autenticación al mock |
| Posterior, RF-18.1/18.2 | Usuario individual / lote | Proyección permitida, lote ≤100 con encontrados/no encontrados; nunca hash de credencial |
| Posterior, RF-18.3/18.4 | Direcciones/documento | Campos de SPEC-16; documento en claro solo por concesión aprobada, resto enmascarado |
| Posterior, RF-18.5/18.6 | Catálogos y consultas | Catálogos EP-05, auditoría EP-06; no permisos de negocio ajenos en claim |

## 4. Decisiones técnicas

### Firma, claves y descubrimiento

Un único proveedor interno de claves firma y produce JWKS público.
El firmador recibe de Jose Luis identidad, roles/permisos ya calculados y
tiempo; no autentica contraseña ni gestiona familias. Clases/configuración
se propondrán en com.marketplace.auth al implementar.

Clave RSA privada y kid proceden de configuración protegida persistente del
entorno. Christian concreta almacenamiento/despliegue; Sergio verifica el
par/algoritmo/publicación. TEC-03 no acredita aún un almacén o clave real.
No se regenera al arrancar ni se usa el par ilustrativo del mock. La clave
privada no aparece en JWKS, frontend, logs o repositorio.

Issuer es URL exacta del entorno, normalmente base externa G7 más
/api/v1/auth; jwks_uri apunta a su JWKS HTTPS. Descubrimiento y JWKS son
URLs distintas. Proxy de nube utiliza configuración externa, no localhost
o HTTP interno. G5 recibe URLs/issuer/revisión en la ficha bilateral.

El perfil actual de usuario no exige aud api-ventas/api-postventa: tipo
acceso y sub UUID. Servicio sí exige tipo servicio, sub client_id, aud y
scopes. G5 valida firma, issuer, tiempo y perfil antes de CLIENTE/propiedad.
Decodificar acceso en React solo informa UI.

Rotación posterior: publicar clave nueva y anterior, cambiar emisión al
nuevo kid y mantener anterior ≥24 h. Reinicio no la elimina. Consumidores
cachean JWKS y recuperan claves por kid desconocido sin descargar por
petición. H3 verifica continuidad tras reinicio; rotación completa conserva
su aceptación posterior, sin declarar SPEC-17 terminada.

### Servicio y concesiones posteriores

Se reutilizan Scope/ClienteServicio/ClienteServicioScope existentes; no
hay repositorios/validadores/emisión de servicio acreditados en la base.
Christian provisiona secretos hasheados por canal privado. La matriz
API dueña → scope → aud → consumidor → aprobación determina concesiones.
Aud se calcula de las APIs de los scopes concedidos, sin lógica por módulo.

Servicio dura una hora, sin refresco, y concede exactamente lo asignado
aunque se pida más (RF-17.5). Scope del token es texto separado por espacios;
scopes de respuesta es arreglo. Cada API dueña valida audiencia/scope y
resuelve su operación de negocio.

A10 mantiene siete scopes propios G5, ocho clientes previstos en total y
retiro CSAT de Marketplace/Chatbot/Retail. Joseph también retiró la concesión
provisional usuarios:leer:documento para Ventas en
[#42](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6118843852).
El scope sigue en catálogo genérico, pero no concedido a Ventas.
A9 conserva condición de confirmación del PO de Productos.

### Introspección y datos posteriores

Se autoriza al módulo solicitante antes de consultar existencia. Después
se comprueba el acceso presentado y su sesión, estado efectivo y roles.
EP-02 entrega vínculo persistente jti → usuario/familia/vigencia (DAT-01).
Consultar por sub solamente no detecta cierre de una sesión independiente.

Cambio de roles, baja o bloqueo respetan RF-17.7 y motivos del contrato.
Firma válida no equivale a sesión activa. Ventas/Postventa no recibe
introspección en A10; usuarios:leer comprueba cuenta, no sesión específica.

SPEC-18 consume cuentas de Eva, atributos/direcciones de Valery, catálogo
de Sergio y auditoría de Christian. Devuelve DTO permitidos, no entidades
completas. Primero autorización; luego enmascaramiento/descifrado selectivo.
No hay búsqueda por correo/celular, RUC ni roles repartidor/cajero.

RF-11.9/A5 limita permisos a Seguridad: no se añade pedido.crear ni
producto.editar al claim. Modulo del catálogo no concede permisos de APIs
ajenas; cada módulo decide por rol, identidad y dominio.

### Auditoría, errores y publicación

401/403 conservan catálogo, no enumeración y no divulgación de scope
requerido. SPEC-12 registra actor técnico, operación, usuario consultado y
exposición clara/enmascarada sin copiar el documento, secretos o token.

OpenAPI publicado gobierna obligaciones. Si se genera desde código, se
contrasta y revisa con Sergio; no reemplaza silenciosamente el contrato.
Cambios incompatibles respetan versión/aviso de ADR-003/008.
Rutas MFA v1 deprecadas y bloqueadas no se implementan por seguir presentes.

## 5. Dependencias y condiciones de inicio

| Condición | Dueño / evidencia | Parte afectada |
|---|---|---|
| Revisión de plan | Sergio revisa fuentes, frontera y decisiones | Derivar tareas técnicas |
| TEC-03/04 | Clave/kid protegidos y biblioteca JOSE/build compatibles con runtime declarado | Firma/JWKS reales |
| EP-02/05 | Cuenta/familia y roles/permisos reales | Acceso emitido por login |
| TEC-01 / INT-05 | URLs/issuer externo, transporte/origen y despliegue | Prueba conjunta |
| #10/#42/#18 | Clientes/secrets/concesiones cargados y prueba de alcance | Servicio/API ajenas, posterior a demo mínima |
| #33 / A9 | Confirmación de Productos por catálogo/consumidor/aud | Concesiones condicionadas |
| DAT-01 / EP-02/04 | Acceso resuelto a familia/estado, OTP cuando aplica | Introspección/cambio MFA |
| EP-01/08/06 | Datos/proyecciones/auditoría reales | Consultas SPEC-18 |

[Registro único](../../docs/registro-inconsistencias.md) y
[vista H3](../DECISIONES-Y-BLOQUEOS.md) gobiernan estados.
Fixture firmada puede probar un validador sin credencial real de consumidor;
no demuestra aprovisionamiento ni conexión entre servicios.

## 6. Fases y entregables

| Fase | Resultado de Sergio | Verificación / dependencia |
|---|---|---|
| Diseño H3 | Plan revisado, perfil/configuración acordados con Jose Luis/Christian | Custodia/runtime concretos, sin claim/endpoint nuevo |
| Criptografía H3 | Firmador/JWKS/descubrimiento reales | Acceso de login; issuer/kid; continuidad tras reinicio |
| Conexión H3 | Parámetros entregados a Michael, kit consumible | Prueba G5 local y conjunta en nube |
| Servicio posterior | Emisión/control de actor y scopes | Provisión real; aud/scopes correctos sin elevación |
| Sesión posterior | Introspección real | Vínculo DAT-01 y cambios de cuenta/roles/logout |
| Identidad posterior | Individual/lote/direcciones/catálogos | Proyección/enmascaramiento/auditoría |

Antes de tareas posteriores se concreta su diseño de datos/interfaces.
Este documento no genera IDs de tareas ni tablero de ejecución.

## 7. Pruebas y terminado

H3: acceso real de login válido, vencido, alterado, issuer incorrecto y
servicio presentado como usuario. JWKS verifica RS256 por kid sin material
privado; descubrimiento coincide con iss/URL externa; reinicio conserva
clave y permite validar accesos ya emitidos. G5: pedidos solo del sub,
cliente sin pedidos 200 vacío, sin acceso válido 401, sin CLIENTE 403,
customerId no sustituye identidad del token.

El caso negativo de servicio puede usar una fixture firmada aislada,
identificada como tal; no obliga a provisionar A10 en H3. El acceso válido
de la demo sí procede de login real, nunca de Prism o una fixture.

Posterior: rotación con ambas claves ≥24 h; servicio sin refresco; scopes
adicionales solicitados no concedidos; audiencia/tipo incorrectos; 401 antes
de existencia y 403 sin datos; sesión/cuenta/roles actuales; lote 100/101,
duplicados y UUID inválidos; documento autorizado/enmascarado y auditoría.

Introspección: objetivo p95 <200 ms con 50 concurrentes. Mediciones identifican
runtime/entorno. [Validación previa del kit](../../docs/integracion/validacion-hito-3.md)
no acredita backend futuro.

H3 termina con firma/JWKS/configuración reales y conexión comprobada G5.
TEC-IDENTIDAD completo requiere RF-17/18, pruebas/revisión/despliegue;
no cierra la épica académica EP-05 de Sergio.

## 8. Riesgos y recuperación

Regenerar claves invalida accesos; issuer interno rompe nube; omitir tipo
confunde servicio/persona. Se prueban antes de demo. Caché de kid conocido
permite validar durante caída JWKS; un kid desconocido se obtiene de fuente
confiable o se rechaza, sin omitir firma. Recuperación de rotación preserva
las claves aún publicadas.

Catálogo se revisa con dueño de API, sin editar SQL aplicado ni usar secretos
públicos del mock. Un diseño posterior incompleto conserva bloqueo; firma
y descubrimiento H3 pueden avanzar independientemente.
