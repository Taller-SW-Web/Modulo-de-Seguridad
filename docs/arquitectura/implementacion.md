# Arquitectura de implementación — auth-service

**Versión:** 1.0 — Hito 2 (Semana 6)
**Responsable:** Jose Luis Limachi Sarmiento (Tech Lead — Backend)
**Co-autor:** Sergio Osorio Montenegro (Product Owner — Arquitecto de solución)

---

## 1. Stack tecnológico

| Capa | Tecnología | Justificación |
|------|------------|---------------|
| Lenguaje | Java 21 | LTS actual, requisito del curso |
| Framework | Spring Boot 3.3 | Estándar de industria, ecosistema maduro |
| ORM | Spring Data JPA (Hibernate 6) | Productividad, integración con Flyway |
| Migraciones | Flyway 10 | Versionado de esquema, orden garantizado |
| Base de datos | PostgreSQL 13+ | Enums nativos, JSONB, robustez |
| Pruebas | Testcontainers + JUnit 5 | PostgreSQL real en tests, no H2 |
| Seguridad | Spring Security 6 | Filtros, autenticación, autorización |

---

## 2. Estructura de paquetes

El proyecto usa **paquetes por bloque de dominio** (no por capa técnica). Cada dominio contiene sus entidades, repositorios, servicios y controladores.

```
com.marketplace.auth
├── usuario/              # SPEC-01, 03, 04, 16
│   ├── entity/           # Usuario, PerfilCliente, PerfilVendedor, Direccion
│   ├── repository/       # Repositorios JPA
│   ├── dto/              # Data Transfer Objects
│   ├── service/          # Servicios de dominio
│   └── controller/       # Controladores REST
├── credencial/           # SPEC-07, 08
│   ├── entity/           # Credencial, PasswordHistorial
│   ├── repository/
│   ├── dto/
│   ├── service/
│   └── controller/
├── sesion/               # SPEC-05, 06
│   ├── entity/           # TokenRefresco, Resultado
│   ├── repository/       # TokenRefrescoRepository
│   ├── dto/
│   ├── service/
│   └── controller/
├── mfa/                  # SPEC-09, 10
│   ├── entity/           # DesafioMfa, Otp
│   ├── repository/       # DesafioMfaRepository
│   ├── dto/
│   ├── service/
│   └── controller/
├── proteccion/           # SPEC-14, 15
│   ├── entity/           # Bloqueo, IntentoLogin, TipoBloqueo
│   ├── repository/       # BloqueoRepository, IntentoLoginRepository
│   ├── dto/
│   ├── service/
│   └── controller/
├── auditoria/            # SPEC-12, 13
│   ├── entity/           # AuditoriaSeguridad
│   ├── repository/
│   ├── dto/
│   ├── service/
│   └── controller/
└── integracion/          # SPEC-17, 18
    ├── entity/           # ClienteServicio, Scope
    ├── repository/
    ├── dto/
    ├── service/
    └── controller/
```

**Regla:** Cada dominio es un paquete cerrado. Las dependencias entre dominios se hacen a través de servicios o eventos, nunca accediendo directamente a las entidades de otro dominio.

---

## 3. Migraciones Flyway

Las migraciones viven en `src/main/resources/db/migration/` y se ejecutan en orden alfabético.

| Versión | Responsable | Tablas | Estado |
|---------|-------------|--------|--------|
| V1 | Eva | usuario, perfil_cliente, perfil_vendedor, direccion, rol, permiso, rol_permiso, usuario_rol | Pendiente |
| V2 | Juan José | credencial, password_historial, otp, token_un_uso, solicitud_limitada | Pendiente |
| **V3** | **Jose Luis** | **token_refresco, desafio_mfa, intento_login, bloqueo, vista usuario_estado_efectivo** | ✅ Implementada |
| V4 | Christian | auditoria_seguridad, cliente_servicio, scope, cliente_servicio_scope, outbox | Pendiente |
| V100 | Eva | Semilla de datos (roles, permisos, scopes) | Pendiente |

**Convenciones:**
- Cada migración crea sus propios enums si es la primera tabla que los usa.
- Las FKs a tablas de otras migraciones se añaden con `ALTER TABLE` al final.
- No se crea otra versión sin avisar al equipo.

---

## 4. Entidades JPA de V3

### 4.1 TokenRefresco (`token_refresco`)

Refresh tokens con familia de sesión. Rotación en cada uso; reutilización revoca la familia.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `jti` | UUID (PK) | Identificador único del token |
| `usuarioId` | UUID | Usuario propietario |
| `familiaId` | UUID | Familia de sesión (una sesión = una familia) |
| `hashToken` | String (único) | Hash del token (nunca el token en texto plano) |
| `revocado` | boolean | Si fue revocado |
| `usado` | boolean | Si ya fue usado (rotación) |
| `expiracion` | OffsetDateTime | Fecha de expiración |
| `creadoEn` | OffsetDateTime | Fecha de creación |

### 4.2 DesafioMfa (`desafio_mfa`)

Challenge token del login con segundo factor.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | UUID (PK) | Identificador único |
| `usuarioId` | UUID | Usuario propietario |
| `hashToken` | String (único) | Hash del token de desafío |
| `expiracion` | OffsetDateTime | Fecha de expiración |
| `consumido` | boolean | Si ya fue consumido |
| `creadoEn` | OffsetDateTime | Fecha de creación |

### 4.3 IntentoLogin (`intento_login`)

Registro de intentos de login (append-only, retención 90 días).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | UUID (PK) | Identificador único |
| `usuarioId` | UUID (nullable) | Usuario (null si el correo no existe) |
| `correoIntentado` | String | Correo usado en el intento |
| `resultado` | Resultado (enum) | EXITO o FALLO |
| `ip` | String | IP del cliente |
| `userAgent` | String | User-Agent del cliente |
| `fecha` | OffsetDateTime | Fecha del intento |

### 4.4 Bloqueo (`bloqueo`)

Bloqueo vigente de cuenta (1:1 con usuario). Fuente de verdad del estado BLOQUEADO efectivo.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `usuarioId` | UUID (PK) | Usuario bloqueado |
| `tipo` | TipoBloqueo (enum) | AUTOMATICO o MANUAL |
| `bloqueadoHasta` | OffsetDateTime (nullable) | Vencimiento (null = no vence) |
| `motivo` | String (nullable) | Motivo (solo manual) |
| `creadoEn` | OffsetDateTime | Fecha de creación |

### 4.5 UsuarioEstadoEfectivo (vista `usuario_estado_efectivo`)

Vista de solo lectura que calcula el estado efectivo de una cuenta.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `usuarioId` | UUID (PK) | Usuario |
| `estado` | EstadoCuentaEfectivo (enum) | ACTIVO, INACTIVO, BLOQUEADO, PENDIENTE_VERIFICACION |

---

## 5. Enums JPA

Los enums de PostgreSQL se mapean con `@Enumerated(EnumType.STRING)` + `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` (Hibernate 6.2+, Spring Boot 3.1+).

| Enum | Valores | Tabla |
|------|---------|-------|
| `Resultado` | EXITO, FALLO | intento_login |
| `TipoBloqueo` | AUTOMATICO, MANUAL | bloqueo |
| `EstadoCuentaEfectivo` | ACTIVO, INACTIVO, BLOQUEADO, PENDIENTE_VERIFICACION | vista usuario_estado_efectivo |

---

## 6. Configuración

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:auth_service}
    username: ${DB_USER:auth_user}
    password: ${DB_PASSWORD:auth_password}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### Pruebas con Testcontainers

Las pruebas usan PostgreSQL real vía Testcontainers (no H2). La configuración está en `TestcontainersConfig.java` y el esquema de pruebas en `schema-test.sql`.

---

## 7. Cómo ejecutar

### Compilar

```bash
cd backend
mvn compile
```

### Ejecutar la aplicación

Requiere PostgreSQL en localhost:5432 (o configurar variables de entorno).

```bash
mvn spring-boot:run
```

### Ejecutar pruebas

Las pruebas usan Testcontainers, no requieren PostgreSQL local.

```bash
mvn test
```

### Docker Compose (próximamente)

```bash
docker-compose up -d
```

---

## 8. Decisiones de diseño

| Decisión | Justificación |
|----------|---------------|
| Paquetes por dominio, no por capa | Mejor cohesión, más fácil de navegar |
| `ddl-auto: validate` | Flyway es la fuente de verdad del esquema |
| Enums nativos de PostgreSQL | Integridad de datos, validación en BD |
| `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` | Hibernate 6 envía enums como varchar y PostgreSQL rechaza el INSERT |
| Testcontainers para pruebas | Las reglas del Hito 2 prohíben H2 |
| Vista para estado efectivo | BLOQUEADO es un estado calculado, no almacenado |

---

## 9. Próximos pasos

- [ ] V1 (Eva): usuario, perfiles, direcciones, roles
- [ ] V2 (Juan José): credenciales, OTP, tokens de un solo uso
- [ ] V4 (Christian): auditoría, clientes de servicio, outbox
- [ ] V100 (Eva): semilla de datos
- [ ] Implementación de servicios y controladores (Hito 3)
- [ ] Configuración de Spring Security + JWT (Hito 3)
