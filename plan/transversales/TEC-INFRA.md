# Plan TEC-INFRA — entorno y entrega H3

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueño técnico | Christian Gabriel Arancivia Salas |
| Capacidad | Infraestructura adicional; su épica académica es auditoría |
| Versión / corte | 0.1.0 / 12-oct-2026 |
| Estado / revisión | Alcance H3 aprobado según comunicación de Luis; diseño técnico delegado cerrado; ejecución pendiente |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 + complemento documental local |
| Pendientes | TEC-01/03/04, configuración y prueba conjunta INT-05 |

| Entrada | Fuente / aplicabilidad |
|---|---|
| Funcional | [SPEC-12](../../specs/funcionales/SPEC-12-registro-auditoria.md) y reglas de entrega de [09](../../specs/funcionales/SPEC-09-mfa-inicio-sesion.md)/[14](../../specs/funcionales/SPEC-14-bloqueo-automatico.md)/[17](../../specs/funcionales/SPEC-17-tokens-servicio.md); [plan H3](../PLAN-IMPLEMENTACION.md) |
| UI/UX | Sin pantalla propia; se despliegan las de los [siete dueños](../hito-3/README.md) según sus specs |
| API | [OpenAPI v1](../../specs/contrato-api/openapi.yaml), descubrimiento/JWKS y [catálogo de eventos](../../specs/catalogo-eventos.md); no añade API Management pública en H3 |
| React | Sin componentes nuevos; arranque/publicación de la SPA de los dueños, [base](../../specs/componentes-react/FRONT-00-base-compartida.md) |

Punto de partida: [backend independiente nuevo](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend)
y [arquitectura hexagonal](../hito-3/BACKEND-HEXAGONAL.md). El pom, Dockerfile y
Compose antiguos son antecedentes, no base de build/despliegue del nuevo repo.
[Revisión de datos](../../docs/integracion/revision-datos.md) conserva evidencia
histórica; no acredita esquema/historial Supabase actual.

**Diseño H3 definitivo:** [composición](../hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../hito-3/DISENO-PERSISTENCIA.md) y [entorno/React](../hito-3/ENTORNO-Y-REACT.md).
Concretan los detalles internos bajo delegación de Luis; su aprobación H3
no se extiende a capacidades de hitos posteriores.

## 2. Objetivo y límites

Entregar entorno compartido de desarrollo/prueba y servicios G7 en nube para
demo individual y conexión G5. PostgreSQL propio, HTTPS, claves persistentes,
SMTP real para OTP/desbloqueo/avisos y transporte de mutaciones seleccionadas.

Christian mantiene infraestructura/adaptadores compartidos, no controla la
lógica de las épicas ni implementa por otro sus controladores/React.
Los productores crean hechos/payload según contrato y transacción propios.

H3 incluye un procedimiento reproducible de build/prueba/despliegue y reversión.
Automatización completa CI/CD H4, revisión seguridad pipeline/API Management/
rendimiento/observabilidad de hitos siguientes conservan su alcance. No afirmar
que CI ya existe: .github/workflows/ci.yml está ausente en el corte consultado.

## 3. Cobertura y criterios

| Fuente / referencia | Resultado H3 verificable |
|---|---|
| TEC-01; rúbrica H3 | Backend/frontend accesibles por HTTPS y DB propia, sin acceso DB entre módulos |
| TEC-04 | Build/pruebas con Java 21/Boot 3.5.16/Gradle 8.14.3 efectivos; frontend React/TypeScript con scripts/versiones registrados |
| TEC-03; RF-17.1/17.2 | Custodia de RSA/kid, issuer/JWKS correctos y estables tras reinicio |
| RF-09.4; RF-14.8; RF-07.9 | Correo SMTP real asíncrono, sin OTP/enlace/contraseña en logs |
| Catálogo eventos; RF-11.6/14.9/16.9 | Mutaciones H3 con outbox/publicación después de commit, eventoId y reintentos |
| RF-12.4/12.6/12.7 | Grants de solo-anexado, auditoría crítica transaccional/no crítica con reintento, sin purga prematura |
| INT-05; H3-C01–C12 | Prueba conjunta con SHA/configuración/evidencia, no mock |

## 4. Decisiones técnicas

### Desarrollo y runtime

Conservar versiones declaradas del backend y comprobar dependencias reales
antes de implementar firma/verificación. Jose Luis/Sergio revisan selección
JOSE y frontend; no certificar Java 21 por validación del kit con JDK 23.

El nuevo entorno local se prepara en el repo backend con PostgreSQL 16.6 y
RabbitMQ 3.13.7-management para tests, sin latest. Verificar build Gradle y
configuración antes de desplegar; no copiar el Compose antiguo como base. No aplicar el puerto/credencial de administración
local como configuración pública de nube.

PostgreSQL ya está en Supabase, confirmado por Luis; conexión JDBC/TLS y
roles quedan fijados en ENTORNO-Y-REACT. Host del backend/SPA y broker/SMTP
reales todavía se documentan por Christian/Jose Luis,
variables reales y responsables de acceso. No crear cuentas ni recursos de
pago en este bloque. La plataforma no se inventa para rellenar el plan.

### Datos y secretos

Migrations Flyway conocidas se contrastan contra el entorno aplicado.
Credenciales de migración separadas del usuario de aplicación; runtime sin
UPDATE/DELETE de auditoria_seguridad. Datos técnicos de prueba sintéticos con
UUID acordados, sin aceptación inventada de términos ni mezcla con producción.

RSA/kid e issuer de Sergio, claves para HMAC/entrega sensible y DB/SMTP/broker
se guardan fuera de Git. Persisten entre reinicios/instancias. No usar defaults
de Compose como secreto real de nube. Mantener copia de configuración y
custodia para rollback sin exponer contenido en evidencia.

### Outbox y transporte

V4 outbox del backend antiguo es una referencia de esquema; el nuevo adaptador
y su migración se implementan después de revisar el historial real. Productor persiste
mensaje en la misma transacción del hecho; worker procesa solo confirmados,
evita dos entregas simultáneas por locks/reclamo y confirma envío antes de
marcar ENVIADO. Rabbit publisher confirm; at-least-once y deduplicación por
eventoId. Reintentos/DLQ según catálogo de eventos.

Correos OTP/enlace llevan payload sensible cifrado en outbox con clave externa;
tabla OTP/token conserva hash. No registrar cuerpo secreto ni devolverlo al
frontend. El worker descifra solo para SMTP y elimina contenido sensible tras
entrega. Propósito/vencimiento acompañan el mensaje; no enviar un código ya
caducado ni extender la vigencia porque hubo una caída.

Correo aceptado por SMTP no prueba lectura en bandeja; probar recepción real
en una cuenta controlada. Duplicación de entrega conserva mismo token/código
y nunca crea una segunda autorización o sesión.

Las mutaciones incluidas adelantan eventos de roles/bloqueo/atributos de su
alcance; las restantes y suscripciones externas siguen después. Registrar
ese ajuste de calendario en el catálogo sin cambiar payload ni afirmar
aceptación de los seis consumidores.

Auditoría no crítica utiliza la cola interna segura definida en el diseño cerrado del plan
de Christian, con política cerrada en DISENO-PERSISTENCIA. No confundir sus registros
con los eventos públicos. No crítica fallida no revierte sesión ya confirmada;
crítica sigue dentro de la transacción del productor.

### HTTP y despliegue

HTTPS, URL G7, issuer exacto, jwks_uri y origen SPA/G5 explícitos. CORS se
configura para orígenes concretos y expone Retry-After cuando lo requiere UI;
no es un sustituto de Authorization. Transportar tokens en cabecera/cuerpo
según contrato, nunca en URL de regreso de login.

Endpoint opcional anunciado en descubrimiento debe estar realmente disponible
en el entorno. No habilitar rutas históricas MFA ni anunciar /auth/token/
introspección/altas/recuperación terminados por generar OpenAPI.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Antes de |
|---|---|---|
| Plataforma/DB/configuración TEC-01 | Christian con Jose Luis/Sergio | Despliegue y prueba de entorno |
| Implementaciones/builds propios | Cada estudiante | Publicar servicio/SPA; no sustituirlos por código de infraestructura |
| Claves/firma y runtime | Sergio/Jose Luis/Christian | Imagen real con JWT válido y continuidad |
| Payloads/mapping/fixtures | Productores y Eva/Juan José | Outbox/migraciones y escenarios reproducibles |
| Datos/aceptación G5 | Sergio/Michael | URL, rama, orígenes, revisión, memoria H-11 y hora |
| Diseño técnico/mensajería cerrado | Christian y productores | Implementación y comprobación en tareas posteriores |

## 6. Fases y entregables

| Fase | Entregable | Comprobación |
|---|---|---|
| Entorno | Configuración y acceso locales/nube identificados | Dependencias sin secretos publicados |
| Datos/runtime | Cadena y grants aplicados; build/test reales | Arranque reproducible sin alterar historial aplicado |
| Transporte | Worker/adaptadores y confirmación de entregas | SMTP y broker reales, caída/reintento sin duplicar efectos |
| Nube | Versiones identificadas de backend/SPA disponibles | HTTPS, issuer/JWKS, CORS y persistencia al reiniciar |
| Prueba/ensayo | Recorridos individuales y G7/G5 | Evidencia sanitizada y correcciones de sus dueños |

## 7. Pruebas y terminado

Verificar arranque, DB/migraciones, grants de auditoría, secretos externos,
keys persistentes, exactitud issuer/JWKS, CORS y build con el runtime real.
Caída SMTP/broker conserva mensajes pendientes; recuperación confirma entrega;
outbox no filtra OTP o enlaces. Confirmar rollback del productor no publica.

No basta docker compose config válido: ejecutar builds y pruebas autorizadas,
recorridos HTTP/DB/UI reales, prueba bilateral y reinicio del servicio. Cada
dueño valida sus casos; Christian acredita entorno y transporte.

Terminado H3 técnico: ambos servicios disponibles y prueba conjunta con evidencia;
el backend G5 pertenece a su equipo. No declara CI/CD completo, auditoría
académica terminada ni todos los eventos/conexiones implementados.

## 8. Riesgos y recuperación

Conservar revisión desplegada anterior y datos de prueba para revertir código,
sin borrar base compartida ni rotar claves accidentalmente. Migración nueva
se revisa por compatibilidad antes de desplegar; rollback de imagen no
deshace automáticamente cambios de esquema.

Ante secreto expuesto, el dueño coordina rotación sin compartirlo en el issue.
Ante falla de integración, registrar caso, entorno y dueño; corregir/repetir,
sin presentar Prism o JWT sintético como reemplazo del recorrido real.
