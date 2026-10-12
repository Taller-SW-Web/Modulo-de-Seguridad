# H3 — backend nuevo, arquitectura hexagonal y repositorios

**Versión:** 1.1.0, 12-oct-2026. Luis confirma un backend nuevo e independiente,
JPA dentro del adaptador, Gradle y Spring Boot 3.5; descarta el backend Maven
anterior. Este complemento sustituye las rutas/runtime anteriores, no el
alcance funcional aprobado ni el contrato. Implementaciones de negocio pendientes.

## 1. Bases y fuentes

| Elemento | Decisión |
|---|---|
| Documentación | Este repositorio `Modulo-de-Seguridad`: specs, contrato, planes y tareas oficiales |
| Backend | [Modulo-de-Seguridad-Backend](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend); clon local actual `Backend-auth-service/`, Git independiente e ignorado por el padre |
| Revisión inicial inspeccionada | `ae53b42e5a109a3a813acad3d43d3327e83e822f`, generado por Initializr; contenía Boot 4.1.1/Gradle 9.7.1, sustituidos por instrucción de Luis |
| Runtime decidido | Java 21, Spring Boot **3.5.16**, Gradle wrapper **8.14.3**, plugin dependency-management 1.1.7; sin Maven ni Boot 4 |
| Paquete raíz | `com.marketplace.auth_service`, conserva el generado; aplicación en la raíz para escanear los adaptadores/configuración |
| Frontend | Repositorio independiente React/TypeScript con Vite; URL y nombre de clon aún no comunicados. Las rutas `src/...` se resuelven dentro de ese clon |
| Base antigua | `backend/`, su pom, Dockerfile, entidades y tests quedan como antecedentes; no se ejecutan ni se copian como base del nuevo proyecto |

Para trabajar, abrir en el editor **los clones de documentación y código**.
También pueden quedar anidados como ahora: el backend no depende de estar en
esa ubicación. No agregar el clon como archivos del padre ni como gitlink.
Registrar en la evidencia SHA de documentación y SHA de backend/frontend;
el último commit público no se supone automáticamente la revisión aprobada.
Sin acceso a las fuentes de una tarea, obtener el clon de documentación antes
de implementar; no completar el contrato por memoria ni duplicar specs editables.

## 2. Estructura y dirección de dependencias

Todas las rutas siguientes son relativas a la raíz del **repo backend**:

```text
src/main/java/com/marketplace/auth_service/
  AuthServiceApplication.java
  <capacidad>/
    domain/model/                  # Modelo, enums y reglas propios
    domain/exception/              # Fallos de negocio sin HTTP
    application/port/in/           # Casos de uso y capacidades exportadas
    application/port/out/          # Lo que el caso de uso necesita
    application/dto/               # Commands, resultados y snapshots inmutables
    application/service/           # Casos de uso en Java puro
    adapter/in/web/                # Controller, DTO HTTP y WebMapper
    adapter/in/transaction/        # Decorador Spring del puerto de entrada
    adapter/out/persistence/       # JpaAdapter, entity/, repository/, mapper/
    adapter/out/capacidad/         # Puentes hacia puertos de otro dueño
    adapter/out/crypto/            # Cuando esa capacidad requiere criptografía
  shared/domain/                   # Actor verificado y tipos mínimos comunes
  configuration/                  # Wiring, DataSource, Security, propiedades
src/main/resources/db/migration/   # Flyway del backend nuevo
src/test/java/com/marketplace/auth_service/
```

Dominio depende de Java y de los tipos mínimos compartidos. Aplicación depende
de su dominio, DTO y puertos; no importa Spring, Hibernate, Jakarta Persistence,
Servlet, Jackson, HTTP, repositorios Spring Data ni entidades JPA.
Adaptadores dependen hacia dentro; `configuration` construye los casos de uso
y los conecta a los adaptadores. No añadir `@Service`/`@Transactional` al núcleo.

El controller llama al puerto de entrada **decorado**, nunca al repositorio ni
al caso de uso sin frontera transaccional. Un puente del consumidor implementa
su propio puerto de salida y delega únicamente a `application.port.in` y DTO
exportados del proveedor. No importa su dominio, servicios internos o adaptadores.
Las capacidades antes descritas como subpaquete `api` se alojan ahora en esos
puertos/DTO exportados; se conservan sus firmas y semántica de
[composición](ACUERDOS-INTERNOS.md), sin añadir un segundo puerto equivalente.

### Distribución por dueño

| Dueño | Paquetes de capacidad | Puertos de entrada/capacidades existentes |
|---|---|---|
| Eva | `usuario` | CuentasPort, CuentaEscrituraInterna; casos de consulta administrativa |
| Jose Luis | `sesion` | SesionesPort; casos de login, refresh y logout |
| Juan José | `credencial`, `token` | CredencialesPort, TokensUnUsoPort; política y cambio de contraseña |
| Luis David | `mfa`, `proteccion` | MfaPort, ProteccionPort; OTP y bloqueo/desbloqueo separados |
| Sergio | `autorizacion`, `identidad` | AutorizacionPort, FirmaPort; roles, RS256, descubrimiento/JWKS y filtro de acceso |
| Christian | `auditoria`, `mensajeria`; configuración de infraestructura | AuditoriaPort, MensajeriaPort; consulta propia, outbox, SMTP y RabbitMQ |
| Valery | `perfil` | PerfilPort; consulta/edición propia y proyecciones autorizadas |

Cada dueño implementa su núcleo, puertos, adaptadores y pruebas. Christian
coordina el build y el wiring compartido; no crea las clases funcionales ajenas.
Sergio aporta configuración de seguridad; Jose Luis aporta la base React.
La [propiedad física](DISENO-PERSISTENCIA.md) no cambia por mover los paquetes.

## 3. DTO, entidades y mappers

| Pieza | Convención |
|---|---|
| Dominio | Clases/value objects y enums sin `@Entity`; UUID escalares para identidades ajenas |
| DTO de aplicación | Records inmutables `...Command`, `...Query`, `...Result`/snapshots; sin anotaciones HTTP/JPA |
| DTO HTTP | `...Request`/`...Response` en `adapter.in.web.dto`; nombres/campos exactos OpenAPI, Jakarta Validation en request |
| Entidad JPA | `...JpaEntity` en `adapter.out.persistence.entity`; solo mapping, sin reglas funcionales ni serialización pública |
| Spring Data | `...JpaRepository` en `adapter.out.persistence.repository`; solo usado por el adaptador de su dueño |
| Mapper HTTP | `...WebMapper`: request→command y result→response; no decide autorización ni consulta DB |
| Mapper persistencia | `...PersistenceMapper`: entidad/proyección↔modelo/snapshot propio; no accede a otros repositorios |

**Mappers manuales explícitos para H3**, con pruebas de campos y exclusión de
secretos; no reflexión/BeanUtils ni copia masiva. No añadir MapStruct en este
incremento. Lombok opcional dentro de adaptadores; no `@Data` en entidades ni
`toString` que incluya hashes, códigos, documento o tokens. PATCH distingue
omitido/null/valor según contrato antes de invocar al dueño físico.
No devolver `Page`, entidades Hibernate o `Authentication` desde el núcleo:
DTO propio de página y Actor verificado. El adaptador traduce al contrato.

## 4. Persistencia y transacciones

JPA/Hibernate trabaja sobre pgJDBC y un DataSource HikariCP compartido. JDBC
es el transporte a PostgreSQL; no implica sustituir JPA por Spring Data JDBC.
En el adaptador se permiten consultas nativas parametrizadas para locks,
SKIP LOCKED y proyecciones del dueño; no interpolar SQL ni consultar tablas ajenas.
Conservar mappings, constraints y orden de locks de los documentos comunes.

Los decoradores en `adapter.in.transaction` implementan el mismo puerto de
entrada que el caso de uso puro. Operaciones principales usan REQUIRED y
READ_COMMITTED; escrituras participantes usan MANDATORY. Consultas sin mutación
usan readOnly. Wiring explícito evita dos beans ambiguos y bypass del decorador.
Un solo transaction manager/DataSource permite atomicidad entre capacidades.
Mantener guard→cuentas UUID ordenadas→familias UUID ordenadas→filas propias.

Los rechazos esperados que registran intentos, reúso, cuotas o bloqueo vuelven
como resultado tipado: el decorador confirma y **después** el controller traduce
el error contractual. Fallos de infraestructura/integridad revierten. Auditoría
crítica y outbox participan en el mismo commit; envío SMTP/RabbitMQ ocurre fuera
del lock, después del commit, con los reintentos y deduplicación acordados.
Los callbacks Spring y los workers quedan en adaptadores, fuera del núcleo.

Configuración al implementar TEC-INFRA-T001/T002:

- `spring.datasource.url/username/password` desde `DB_JDBC_URL`, `DB_USERNAME`,
  `DB_PASSWORD`; TLS `sslmode=verify-full` y CA del proyecto real.
- Session pooler 5432 para IPv4; conexión directa 5432 si la red lo permite.
  Tomar host/usuario desde Connect; no adivinar región ni usar pooler 6543 en H3.
- Hikari maximumPoolSize 5, `spring.jpa.hibernate.ddl-auto=validate`,
  `spring.jpa.open-in-view=false`; sin generación automática del esquema.
- `spring.flyway.enabled=false` en runtime y `baseline-on-migrate=false`.
  Perfil `migration`: Flyway habilitado, JPA sin inicialización de esquema y
  credencial migradora aparte; ejecutar migración validada y luego arrancar
  runtime con su rol limitado. No fallbacks a contraseña local/Supabase Auth.
- Revisar primero esquema e historial reales. V101–V106 siguen como reservas,
  no como SQL ya aplicado. Las migraciones históricas solo se incorporan al
  nuevo historial cuando Christian comprueba checksums/versiones y condiciones
  de arranque con cada dueño. No copiar V100/semillas ni hacer baseline/repair
  para esconder diferencias. El backend nuevo no implica una base nueva/vacía.

## 5. Dependencias, arranque y comprobación

El Gradle nuevo declara starters Boot 3: Web, Validation, Security, OAuth2
Resource Server, Data JPA, Mail, AMQP y Actuator; Flyway core + PostgreSQL module
y driver PostgreSQL. Tests: starter-test, security-test, boot-testcontainers y
Testcontainers junit-jupiter/postgresql/rabbitmq. Versiones transitivas del BOM
3.5.16; sin starters modulares exclusivos de Boot 4 ni Testcontainers 2.
DevTools es solo desarrollo; no se añade Authorization Server ni Supabase Auth.
Juan añade el proveedor Bouncy Castle requerido por Argon2 con versión exacta
compatible y evidencia de resolución antes de cerrar EP-03-T002.

Christian añade `com.tngtech.archunit:archunit:1.5.1` en testImplementation y
ejecuta reglas desde JUnit Jupiter, sin otro engine. Comprueba núcleo sin
frameworks, dirección de dependencias, entidades/repositorios solo en persistencia,
consumo ajeno únicamente a través de puertos exportados y ausencia de ciclos.
No considerar una comprobación sin clases importadas evidencia de conformidad.

Orden: **TEC-INFRA-T001** prepara build/convenciones/wiring y prueba mínima de
arquitectura; revisión inicial T002 comprueba DB; cada dueño entrega su primer
modelo/puerto/adaptador con sus mappers. Se pueden diseñar/probar núcleos sin DB,
pero no cerrar aceptación integrada con dobles. No generar todas las entidades
por un único integrante ni introducir tareas nuevas de funcionalidad futura.

Desde el repo backend: `./gradlew compileJava compileTestJava`, `./gradlew test`,
`./gradlew bootJar` y `./gradlew dependencies --configuration runtimeClasspath`.
En Windows usar `gradlew.bat`. Las pruebas con contenedores requieren Docker;
no omitirlas silenciosamente ni usar Supabase como DB de tests automáticos.
El frontend mantiene `npm ci`, typecheck, tests y build en su propio clon.

El cierre de cada tarea incluye núcleo, adaptadores/mappers que correspondan,
pruebas correctas/negativas, evidencia y revisión. Una entidad aislada o un
mapper no constituye el recorrido académico completo.

Fuentes primarias: [arquitectura hexagonal original](https://alistair.cockburn.us/hexagonal-architecture),
[runtime Boot 3.5](https://docs.spring.io/spring-boot/3.5/system-requirements.html),
[plugin Gradle](https://docs.spring.io/spring-boot/3.5/gradle-plugin/index.html),
[dependencias del BOM](https://docs.spring.io/spring-boot/3.5/appendix/dependency-versions/coordinates.html),
[transacciones Spring](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html),
[conexión Supabase](https://supabase.com/docs/guides/database/connecting-to-postgres),
[ArchUnit](https://www.archunit.org/userguide/html/000_Index.html).
