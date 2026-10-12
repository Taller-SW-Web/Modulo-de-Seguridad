# auth-service

Módulo de Seguridad y Autenticación — Marketplace Multicanal de Productos Deportivos.

## Stack

- **Java 21** + **Spring Boot 3.3**
- **Spring Data JPA** (Hibernate 6)
- **Flyway** — migraciones versionadas
- **PostgreSQL 13+**
- **Testcontainers** — pruebas con PostgreSQL real

## Estructura de paquetes

```
com.marketplace.auth
├── comun/            # Transversal: errores RFC 7807 y seguridad (JWT RS256)
│   ├── error/
│   └── seguridad/
├── rol/              # Roles y permisos (SPEC-11)
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── dto/
│   └── controller/
├── usuario/          # Dominio usuario (SPEC-01, 03, 04, 16)
│   ├── entity/
│   └── repository/
├── credencial/       # Dominio credenciales (SPEC-07, 08)
│   ├── entity/
│   └── repository/
├── sesion/           # Dominio sesión y tokens (SPEC-05, 06)
│   ├── entity/
│   └── repository/
├── mfa/              # Dominio segundo factor (SPEC-09, 10)
│   ├── entity/
│   └── repository/
├── proteccion/       # Dominio protección y bloqueo (SPEC-14, 15)
│   ├── entity/
│   └── repository/
├── auditoria/        # Dominio auditoría (SPEC-12, 13)
│   ├── entity/
│   └── repository/
└── integracion/      # Dominio integración (SPEC-17, 18)
    ├── entity/
    └── repository/
```

## Migraciones Flyway

| Versión | Responsable | Contenido |
|---------|-------------|-----------|
| V1 | Eva | usuario, perfiles, direccion, rol, permiso, rol_permiso, usuario_rol |
| V2 | Juan José | credencial, password_historial, otp, token_un_uso, solicitud_limitada |
| **V3** | **Jose Luis** | **token_refresco, desafio_mfa, intento_login, bloqueo, vista usuario_estado_efectivo** |
| V4 | Christian | auditoria_seguridad, cliente_servicio, scope, cliente_servicio_scope, outbox |
| V100 | Eva | Semilla de datos |

## Cómo ejecutar

### Requisitos

- Java 21+
- Maven 3.9+
- Docker (para PostgreSQL vía Testcontainers o docker-compose)

### Desarrollo

```bash
# Compilar
mvn compile

# Ejecutar (requiere PostgreSQL en localhost:5432)
mvn spring-boot:run

# Ejecutar pruebas (usa Testcontainers, no requiere PostgreSQL local)
mvn test
```

### Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_HOST` | localhost | Host de PostgreSQL |
| `DB_PORT` | 5432 | Puerto de PostgreSQL |
| `DB_NAME` | auth_service | Nombre de la base de datos |
| `DB_USER` | auth_user | Usuario de PostgreSQL |
| `DB_PASSWORD` | auth_password | Contraseña de PostgreSQL |
| `RABBITMQ_HOST` | localhost | Host de RabbitMQ |
| `RABBITMQ_PORT` | 5672 | Puerto AMQP de RabbitMQ |
| `RABBITMQ_USER` | auth_rabbit | Usuario de RabbitMQ |
| `RABBITMQ_PASSWORD` | auth_rabbit_password | Contraseña de RabbitMQ |
| `RABBITMQ_MANAGEMENT_PORT` | 15672 | Puerto de la UI de administración de RabbitMQ |
| `PGADMIN_EMAIL` | admin@example.com | Email de login de pgAdmin |
| `PGADMIN_PASSWORD` | admin | Contraseña de pgAdmin |
| `PGADMIN_PORT` | 5050 | Puerto de pgAdmin |
| `BACKEND_PORT` | 8080 | Puerto del backend |
| `SPRING_PROFILES_ACTIVE` | docker | Perfil de Spring Boot activo |
| `JWT_EMISOR` | http://localhost:8080/api/v1/auth | Claim `iss` de los tokens |
| `JWT_CLAVE_PRIVADA` | *(vacía)* | Clave RSA privada en PEM (PKCS#8). Vacía: par efímero, solo para desarrollo |
| `JWT_CLAVE_PUBLICA` | *(vacía)* | Clave RSA pública en PEM (X.509) |

### Probar SPEC-11 (roles y permisos) en local

Con el perfil `dev` existe `/dev/token`, que emite un token sin contraseña mientras
no esté el inicio de sesión (SPEC-05). **Nunca activar `dev` en la nube.**

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Token del administrador semilla
TOKEN=$(curl -s -X POST localhost:8080/dev/token -H 'Content-Type: application/json'   -d '{"usuarioId":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"}' | jq -r .accessToken)

# Asignar y revocar un rol al vendedor semilla
curl -X POST localhost:8080/api/v1/usuarios/22222222-2222-2222-2222-222222222222/roles   -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"rol":"GESTOR_DESPACHO"}'
curl -X DELETE localhost:8080/api/v1/usuarios/22222222-2222-2222-2222-222222222222/roles/GESTOR_DESPACHO   -H "Authorization: Bearer $TOKEN"

# Token de servicio de otro módulo y catálogo de roles (SPEC-18)
SERV=$(curl -s -X POST localhost:8080/dev/token-servicio -H 'Content-Type: application/json'   -d '{"clientId":"modulo-ventas","scopes":["roles:leer"]}' | jq -r .accessToken)
curl localhost:8080/api/v1/roles -H "Authorization: Bearer $SERV"
```

Los escenarios ESC-11.1 a 11.7 están automatizados en `Spec11IntegracionTest`.

## Documentación

- [Implementación](../../docs/arquitectura/implementacion.md)
- [Esquema de datos](../../docs/arquitectura/schema.sql)
- [Contrato OpenAPI](../../specs/openapi.yaml)
