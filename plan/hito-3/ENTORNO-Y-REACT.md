# H3 — entorno, firma y composición React

**Versión:** 1.0.0, 12-oct-2026. Decisiones técnicas cerradas para los
[siete planes](README.md); configuración real y ejecución pendientes.
Complementa [persistencia](DISENO-PERSISTENCIA.md) y
[composición interna](ACUERDOS-INTERNOS.md), sin cambiar OpenAPI.

## 1. Backend y PostgreSQL en Supabase

Se conserva Java 21 y Spring Boot 3.3.5 del pom actual. Maven y el BOM de Boot
resuelven Spring Data, Hibernate, Security, pgJDBC y Flyway; no fijar versiones
transitivas tomadas del kit en JDK 23/Security 6.5. La primera comprobación
de ejecución guarda dependency:tree y verifica las APIs elegidas en ese runtime.
No se promete compatibilidad con una versión de documentación más reciente.

Se agregarán en implementación los starters Boot OAuth2 Resource Server
(incluye JOSE), Mail y AMQP; NimbusJwtEncoder/NimbusJwtDecoder para RS256,
JavaMailSender SMTP y Spring AMQP RabbitTemplate con confirms. Argon2id usa
Spring Security Crypto y el proveedor Bouncy Castle compatible resuelto/fijado
en el build. No construir JWT o criptografía mediante concatenación manual.
No se añade Spring Authorization Server ni se reemplaza la identidad G7
con Supabase Auth.

**Conexión elegida:** JDBC a PostgreSQL, session pooler Supabase puerto 5432
para el backend persistente si el host es IPv4. Directa 5432 cuando exista
conectividad IPv6 o add-on adecuado. No seleccionar transaction pooler 6543
para H3 ni introducir supabase-js/REST Data API en la SPA.
Host, usuario y base salen de Connect del proyecto correcto; no se construye
el host del pooler suponiendo región. El proveedor está confirmado, los datos
de conexión y esquema aplicado todavía no se comprobaron desde esta sesión.

pgJDBC usa `sslmode=verify-full` y CA correcta del entorno; no pasar a una
factoría SSL permisiva para eludir errores. Credencial/certificado via secretos
del backend. Hikari: máximo 5 conexiones por instancia, mínimo idle 1,
connectionTimeout 10 s, maxLifetime 10 min, keepalive 2 min; comprobar que suma
de instancias más migrador cabe en el cupo real. H3 comienza con una instancia
de backend y workers, sin autoscaling ni pools por estudiante.

Runtime `g7_runtime` separado de identidad administradora/migraciones;
Flyway de nube se ejecuta como paso controlado con esa identidad, no con la
contraseña runtime. Validación de esquema al arrancar y open-in-view=false.
El application.yml existente tiene baseline-on-migrate=true: **se debe
desactivar en el perfil de nube** antes de conectar, y no heredar sus valores
de contraseña/usuario locales. Identificar historial primero; no baseline,
repair o semillas automáticos sobre una base desconocida.

Si las tablas siguen en public/esquema expuesto, revocar acceso a anon,
authenticated y PUBLIC sobre tablas/vistas del módulo, habilitar RLS y dar
políticas/grants solamente al rol backend que lo necesita. Auditoría solo
SELECT/INSERT; rol catálogo requiere privilegio de lock, sin API de edición
del catálogo. No dar privilegios owner/superuser/BYPASSRLS al runtime.
La vista usa security_invoker cuando corresponda y grants explícitos. Las
políticas del rol servidor no sustituyen titularidad/permisos en Spring.
No usar auth.uid() de Supabase para un JWT de identidad propia G7.

La revisión read-only previa al despliegue identifica versión Postgres,
tablas, historial Flyway, grants/RLS/vista y diferencias con migrations.
Testcontainers conserva PostgreSQL 16 local del repo; si la versión efectiva
remota es otra se añade comprobación sobre esa versión antes de aplicar.
Se revisó changelog de Supabase: no se usa su HTTP Auth/Data API en este diseño;
cambios de extensiones/cifrado exigen contrastar el proyecto real, no asumir
que otra base conectada representa la de Seguridad.

## 2. Firma, validación y errores de acceso

Sergio mantiene un par RSA **3072 bits**, PKCS8 privado externo y clave pública
derivada, persistentes por entorno. kid = thumbprint SHA-256 de JWK pública,
estable mientras no cambie la clave. Cargar/validar al arrancar y denegar
emisión si falta o no coincide; no autogenerar claves efímeras de producción.
JWKS solo kty/use=sig/alg=RS256/kid/n/e, nunca parámetros privados.

Claims de persona: sub UUID, email, tipo=acceso, roles array y permisos G7
array, incluso vacíos; iss configurado, iat/exp/jti, exp=iat+900 s. No familiaId,
customerId, token en URL ni audiencia de servicio improvisada. Tiempo UTC,
Clock inyectable, validación exp<=ahora rechazada, skew=0 para esta versión;
sin ventana adicional oculta para los casos de expiración de la demo.

G7 valida con su clave pública local, sin HTTP hacia su propio JWKS. G5 usa
issuer y JWKS del entorno. Pin de RS256; rechazar firma/issuer/tiempo inválidos
y claves desconocidas. Persona se reconoce por tipo y sub UUID, nunca por
tener un scope. G7 mantiene `403 TOKEN_NO_APLICABLE` para token de servicio
criptográficamente válido presentado en una operación de persona, según
catálogo; G5 implementa el rechazo de perfil acordado en su contrato.
El converter no transforma scopes de servicio en permisos/roles de usuario.

SecurityFilterChain stateless: permitAll solo para las operaciones públicas
H3 realmente habilitadas (login/refresh/logout, OTP/desbloqueo, política,
descubrimiento/JWKS); demás rutas requieren contexto y permisos/titularidad.
No usar permitAll indiscriminado para `/auth/**`. Bearer no viaja en cookie;
CSRF se configura de acuerdo con ese transporte stateless. Las autorizaciones
de negocio permanecen en el servicio dueño de cada endpoint.

Errores de filtro/controller se serializan al ProblemDetail/catálogos existentes.
Solo 401 TOKEN_INVALIDO en petición autenticada habilita renovación en SPA;
401 CREDENCIALES_INVALIDAS, CODIGO_INVALIDO o contraseña actual incorrecta
conserva tratamiento de formulario. No revelar bloqueada/inexistente en login.

## 3. Base frontend y rutas de H3

Jose Luis coordina el arranque en `frontend/`: SPA Vite 7 + React 19.2 +
TypeScript 5.9 strict, React Router 7 en modo declarativo, Node 22 LTS ≥22.12.
Mantine **9.6.2**, ya escogido en el sistema de diseño, y sus core/hooks/form/
notifications con la misma versión. No se cambian tokens, tema ni diseño visual.
Fetch nativo para HTTP, Context/reducer para sesión y hooks propios; no añadir
Redux, Next.js ni un segundo cliente HTTP por dueño.

Al crear el manifiesto se fijan patches estables compatibles dentro de estas
líneas, package-lock y versión efectiva Node/npm, sin instalar `latest` o rangos
abiertos en siete PCs. Mantine mantiene el patch exacto del diseño aprobado.
La resolución del lockfile es trabajo mecánico de arranque, no una decisión
de arquitectura pendiente. Scripts comunes: dev, build (typecheck+bundle),
typecheck, test (Vitest/Testing Library), preview. Instalación reproducible npm ci.

Mantener ubicaciones/nombres `frontend/src/pantallas/...` de las specs React,
con shared para api/errores/sesion/tema. Cada estudiante modifica sus bloques;
Jose Luis/Eva componen rutas/contenedores sin implementar la funcionalidad ajena.

| Ruta interna elegida | Spec / dueño | Guard y disponibilidad |
|---|---|---|
| `/login` | FRONT-01 / Jose Luis | Pública; sesión válida lleva a destino permitido |
| `/otp` | FRONT-04 / Luis | Pública con desafío en memoria; sin él vuelve al login |
| `/desbloquear` | FRONT-12 / Luis | Pública; token de enlace quitado de URL al cargar; POST solo al confirmar |
| `/mi-cuenta` | FRONT-06 / Valery | Sesión; muestra/edita solo incremento propio |
| `/mi-cuenta/contrasena` | FRONT-10 / Juan José | Sesión, formulario/medidor real |
| `/mi-cuenta/actividad` | FRONT-13 / Christian | Sesión, consulta exclusivamente titular |
| `/admin/usuarios` | FRONT-07 / Eva | usuario.ver; listado/filtros, sin altas ni mutaciones de estado posteriores |
| `/admin/usuarios/:id` | FRONT-08 / Eva + bloques | usuario.ver; SeccionRoles de Sergio exige rol.asignar; lectura de protección Luis |

Estas son rutas SPA de G7, no rutas nuevas del contrato ni URLs acordadas de
otros módulos. No habilitar FRONT-02/03/05/09/11/14, acciones manuales, edición
administrativa de atributos o direcciones antes de su incremento.
Destino por defecto mantiene FRONT-00: administración permitida o Mi cuenta.
`volver` solo ruta local relativa permitida, sin // ni redirección de dominio.

## 4. Contratos de composición de componentes

| Punto compartido | Contrato interno |
|---|---|
| Sesión | `useSesion()` expone usuario/permisos para UI, aceptarSesion(SesionIniciada), terminarSesion(), estado; tokens permanecen encapsulados en memoria |
| HTTP | `api.request<T>(ruta, {method, body, signal, autenticada})` serializa Bearer/JSON, ProblemDetail y Retry-After; base URL una sola; sin secretos Supabase/client_secret |
| MFA | Login entrega DesafioMfa a estado de desafío compartido y navega OtpPage; Luis entrega SesionIniciada a aceptarSesion solo tras respuesta válida |
| Panel | Eva carga detalle una vez, entrega DTO Usuario/id/permisos al bloque del dueño y callback `onUsuarioActualizado()` para recarga. Ningún bloque recibe repositorios o privilegios persistidos desde UI |
| Roles | Sergio implementa SeccionRoles/hooks propios; operación correcta invoca recarga. No reemplazar array de roles completo ni consultar catálogo de servicio desde navegador |
| Contraseña | Juan José entrega medidor que consume política actual y CambiarContrasenaPage; 204 informa resultado sin borrar familia actual |
| Protección | Luis entrega lectura SeccionBloqueo y pantallas OTP/enlace; solo lectura de manual en H3 |
| Perfil/actividad | Valery y Christian entregan sus rutas/hooks; comparten api/errores, no un hook ajeno que asuma sus mutaciones |

Cliente HTTP mantiene **una Promise de refresh en vuelo** por pestaña. Tras
rotación actualiza ambos tokens juntos; llamadas pendientes usan resultado
nuevo. Solo una repetición de la petición original ante TOKEN_INVALIDO, con
señal de cancelación y sin bucle. El filtro rechaza antes de mutar para hacer
segura esa repetición; un timeout/respuesta perdida en mutación o refresh
no permite repetir a ciegas. Fin de sesión cancela esperas; una respuesta tardía
no restaura sesión cerrada. Recarga exige login; sin local/sessionStorage.

Sin cookies, CORS permite orígenes concretos del frontend/G5 que realmente
llaman desde navegador, Authorization/Content-Type y métodos usados; expone
Retry-After. El HTTP backend G5 no necesita CORS. HTTPS de nube, Referrer-Policy
no-referrer en pantallas de enlace y sin tokens en telemetría/consola/capturas.

## 5. Entorno y datos para demostrar

| Configuración | Dueño / entrega privada o pública |
|---|---|
| Proyecto/esquema Supabase, DB URL/CA y credenciales separadas | Christian; secreto backend, referencia pública sin contraseña |
| URL externa G7, AUTH_ISSUER, CORS_ALLOWED_ORIGINS | Christian/Sergio; pública en ficha bilateral, sin adivinar dominio |
| Clave PKCS8 y claves HMAC/AEAD/documento | Custodia Christian, reglas Sergio/Luis/Valery; secret store del host, nunca repo/browser |
| SMTP host/port/from/usuario/password con TLS, Rabbit URL/credenciales | Christian; JavaMailSender/RabbitTemplate reales, sin simulación EMAIL en demo |
| VITE_API_BASE_URL | Jose Luis; pública, única por build, nunca contraseña/key con VITE_ |
| URL/issuer/JWKS G5 consume, origen y revisión, hora | Sergio con Joseph/Michael; INT-05 |

No se elige un dominio real, proveedor de backend/frontend ni cuenta SMTP
sin evidencia. El artefacto de backend será imagen Docker del monolito y SPA
bundle estático; Christian configura esos artefactos en el host autorizado.
Proyecto correcto/secretos/URLs son insumos de ejecución, no bloqueos de diseño
ni razón para pedir otra aprobación de los siete planes.

Fixtures por capacidades: Eva crea identidades/UUID estables; Juan José hashes
e historial; Sergio roles; Luis flags/desafíos/protección mediante sus ports;
no un SQL de Eva que escriba todas las tablas. Christian coordina carga en
entorno de prueba. A/B/V mantienen propósito G5: A con pedidos, B sin pedidos,
V acceso válido sin CLIENTE para 403. Credenciales solo por canal privado.
ADMIN1/ADMIN2 y cuentas de contraseña, OTP y bloqueo están separadas de A/B.
La fixture técnica declara correo verificado/dato de prueba, sin inventar
aceptación de términos ni evento usuario.creado de registro no ejecutado.

## 6. Evidencia y fuentes

Aceptación futura por dueño: pantalla→HTTP→servicio→DB→respuesta, caso correcto,
negativo, autorización y efectos; builds Java 21/Node fijado, pruebas necesarias
y explicación del uso de IA. Integración G5 real con dos servicios de nube;
dobles/Testcontainers/fixtures acreditan su ámbito, no sustituyen esa evidencia.

Fuentes primarias de conexión: [Supabase PostgreSQL](https://supabase.com/docs/guides/database/connecting-to-postgres),
[pgJDBC SSL](https://jdbc.postgresql.org/documentation/ssl/),
[seguridad de Data API](https://supabase.com/docs/guides/api/securing-your-api).
De stack: [Spring Security JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html),
[React 19.2](https://react.dev/blog/2025/10/01/react-19-2),
[Vite 7](https://vite.dev/blog/announcing-vite7),
[TypeScript 5.9](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-5-9.html),
[Mantine con Vite](https://mantine.dev/guides/vite/) y
[Router declarativo](https://reactrouter.com/start/declarative/installation).
Las fuentes orientan el diseño; no acreditan un build aún no realizado.
