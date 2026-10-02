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

## Documentación

- [Implementación](../../docs/arquitectura/implementacion.md)
- [Esquema de datos](../../docs/arquitectura/schema.sql)
- [Contrato OpenAPI](../../specs/openapi.yaml)
