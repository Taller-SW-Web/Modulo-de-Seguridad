-- Modelo de datos — Módulo de Seguridad y Autenticación (G7)
-- PostgreSQL. Generado desde drawdb (anatoly-lab/drawdb-mcp).
-- 20 tablas, 17 relaciones, 9 enums (+ estado efectivo), índices y notas de diseño.
--
-- NOTAS DE DISEÑO (seguridad / integridad, ver specs):
--  * BLOQUEADO es estado EFECTIVO, calculado desde "bloqueo" (RF-14.7). "usuario.estado"
--    persiste solo el ciclo de vida; el estado expuesto (estado_cuenta_efectivo) se
--    deriva: bloqueado_hasta IS NULL OR > now(). El login lee usuario + bloqueo en una
--    misma consulta.
--  * FK COMPUESTA otp(desafio_id, usuario_id) -> desafio_mfa(id, usuario_id): obliga a
--    que un OTP y su desafío sean del mismo usuario (ESC-09.9). Con MATCH SIMPLE, si
--    desafio_id es NULL no se evalúa (OTP de verificación de contacto).
--  * otp.hash_codigo: HMAC-SHA256 con clave de servidor (6 dígitos = baja entropía),
--    comparación en tiempo constante. Nunca hash simple.
--  * documento: cifrado AES con clave fuera de la BD; documento_key_id permite rotar la
--    clave sin re-cifrar. (Blind index HMAC para búsqueda/unicidad: opcional, no requerido.)
--  * Retención: auditoria_seguridad e intento_login 90 días (configurable, SPEC-12);
--    otp/desafio_mfa/token_un_uso/token_refresco se purgan por job.

CREATE TYPE "estado_cuenta" AS ENUM (
	'ACTIVO',
	'INACTIVO',
	'PENDIENTE_VERIFICACION'
);

CREATE TYPE "codigo_rol" AS ENUM (
	'CLIENTE',
	'VENDEDOR',
	'ADMIN_VENTAS',
	'GESTOR_DESPACHO',
	'GESTOR_COMERCIAL',
	'ADMIN_SISTEMA'
);

CREATE TYPE "tipo_documento" AS ENUM (
	'DNI',
	'CE',
	'PASAPORTE'
);

CREATE TYPE "canal_origen" AS ENUM (
	'WEB',
	'CHATBOT',
	'RETAIL',
	'MARKETPLACE'
);

CREATE TYPE "canal_otp" AS ENUM (
	'EMAIL',
	'SMS'
);

CREATE TYPE "tipo_bloqueo" AS ENUM (
	'AUTOMATICO',
	'MANUAL'
);

CREATE TYPE "resultado" AS ENUM (
	'EXITO',
	'FALLO'
);

CREATE TYPE "actor_tipo" AS ENUM (
	'USUARIO',
	'MODULO',
	'SISTEMA'
);

CREATE TYPE "proposito_token" AS ENUM (
	'VERIFICAR_CORREO',
	'CAMBIO_CORREO',
	'RECUPERACION',
	'DESBLOQUEO'
);

CREATE TYPE "estado_cuenta_efectivo" AS ENUM (
	'ACTIVO',
	'INACTIVO',
	'BLOQUEADO',
	'PENDIENTE_VERIFICACION'
);

CREATE TABLE IF NOT EXISTS "usuario" (
	"id" uuid NOT NULL,
	"correo" varchar(254) NOT NULL,
	"nombres" varchar(100) NOT NULL,
	"apellidos" varchar(100) NOT NULL,
	"celular" varchar(15),
	"estado" estado_cuenta NOT NULL,
	"correo_verificado" boolean NOT NULL DEFAULT false,
	"celular_verificado" boolean NOT NULL DEFAULT false,
	"mfa_habilitado" boolean NOT NULL DEFAULT false,
	"acepta_terminos" boolean NOT NULL,
	"fecha_aceptacion" timestamptz,
	"version_terminos" varchar(20),
	"canal_origen" canal_origen NOT NULL DEFAULT 'WEB',
	"intentos_fallidos_consecutivos" integer NOT NULL DEFAULT 0,
	"bloqueos_seguidos" integer NOT NULL DEFAULT 0,
	"creado_en" timestamptz NOT NULL,
	"actualizado_en" timestamptz,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "usuario" IS 'Entidad central (dueña SPEC-01). Un solo dueño de identidad en el marketplace.';
COMMENT ON COLUMN "usuario"."celular" IS '+51 + 9 dígitos';
COMMENT ON COLUMN "usuario"."estado" IS 'Ciclo de vida persistido (sin BLOQUEADO). BLOQUEADO es estado efectivo, derivado de la tabla bloqueo.';
COMMENT ON COLUMN "usuario"."intentos_fallidos_consecutivos" IS 'Fuente de verdad del contador (SPEC-14); se actualiza en la misma transacción que intento_login.';
COMMENT ON COLUMN "usuario"."bloqueos_seguidos" IS 'Escalera de bloqueos 1/2/4/null; solo vuelve a cero con login correcto (RF-14.4).';

-- Unicidad insensible a mayúsculas (correo normalizado a minúsculas).
CREATE UNIQUE INDEX "uq_usuario_correo_lower" ON "usuario" (LOWER("correo"));

CREATE TABLE IF NOT EXISTS "credencial" (
	"usuario_id" uuid NOT NULL,
	"hash_contrasena" varchar(255) NOT NULL,
	"ultimo_cambio" timestamptz NOT NULL,
	"requiere_cambio" boolean NOT NULL DEFAULT false,
	PRIMARY KEY("usuario_id")
);
COMMENT ON TABLE "credencial" IS 'Hash de contraseña, separado para no exponerlo en lecturas de perfil (SPEC-07).';
COMMENT ON COLUMN "credencial"."hash_contrasena" IS 'bcrypt/argon2id, nunca texto plano';

CREATE TABLE IF NOT EXISTS "password_historial" (
	"id" uuid NOT NULL,
	"usuario_id" uuid NOT NULL,
	"hash_contrasena" varchar(255) NOT NULL,
	"creado_en" timestamptz NOT NULL,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "password_historial" IS 'Últimas 5 contraseñas para impedir reutilización (SPEC-07).';

CREATE TABLE IF NOT EXISTS "perfil_cliente" (
	"usuario_id" uuid NOT NULL,
	"tipo_documento" tipo_documento,
	"documento_cifrado" text,
	"fecha_nacimiento" date,
	"documento_key_id" varchar(50),
	PRIMARY KEY("usuario_id")
);
COMMENT ON TABLE "perfil_cliente" IS 'Atributos de CLIENTE (SPEC-16). Documento cifrado AES con clave fuera de BD.';
COMMENT ON COLUMN "perfil_cliente"."documento_cifrado" IS 'AES; se expone enmascarado *****234';
COMMENT ON COLUMN "perfil_cliente"."documento_key_id" IS 'key_id de la clave AES, para rotación sin re-cifrar.';

CREATE TABLE IF NOT EXISTS "perfil_vendedor" (
	"usuario_id" uuid NOT NULL,
	"codigo_vendedor" varchar(50),
	"tienda" varchar(100),
	"fecha_ingreso" date,
	PRIMARY KEY("usuario_id")
);
COMMENT ON TABLE "perfil_vendedor" IS 'Atributos de VENDEDOR (SPEC-16). "tienda" es un nombre, no una FK (la entidad tienda vive en otro módulo).';

CREATE TABLE IF NOT EXISTS "direccion" (
	"id" uuid NOT NULL,
	"usuario_id" uuid NOT NULL,
	"etiqueta" varchar(50),
	"departamento" varchar(100) NOT NULL,
	"provincia" varchar(100) NOT NULL,
	"distrito" varchar(100) NOT NULL,
	"direccion" varchar(255) NOT NULL,
	"referencia" varchar(255),
	"es_predeterminada" boolean NOT NULL DEFAULT false,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "direccion" IS 'Direcciones de entrega del cliente (SPEC-16, expuesta por SPEC-18).';
COMMENT ON COLUMN "direccion"."es_predeterminada" IS 'Solo una por usuario (índice único parcial).';

-- Una sola predeterminada por usuario (ESC-16.9). Al cambiarla: en la MISMA
-- transacción, primero quitar la anterior y luego marcar la nueva.
CREATE UNIQUE INDEX "uq_direccion_predeterminada" ON "direccion" ("usuario_id") WHERE "es_predeterminada" = true;

CREATE TABLE IF NOT EXISTS "bloqueo" (
	"usuario_id" uuid NOT NULL,
	"tipo" tipo_bloqueo NOT NULL,
	"bloqueado_hasta" timestamptz,
	"motivo" varchar(500),
	"creado_en" timestamptz NOT NULL,
	PRIMARY KEY("usuario_id")
);
COMMENT ON TABLE "bloqueo" IS 'Bloqueo vigente (1:1). Fuente de verdad del estado BLOQUEADO (efectivo). Manual no vence; automático según escalera 1/2/4/null (SPEC-14/15).';
COMMENT ON COLUMN "bloqueo"."bloqueado_hasta" IS 'null = no vence (manual, o automático desde el 4º)';
COMMENT ON COLUMN "bloqueo"."motivo" IS 'Solo manual; nunca a token de servicio';

CREATE TABLE IF NOT EXISTS "intento_login" (
	"id" uuid NOT NULL,
	"usuario_id" uuid,
	"correo_intentado" varchar(254),
	"resultado" resultado NOT NULL,
	"ip" varchar(45),
	"user_agent" varchar(255),
	"fecha" timestamptz NOT NULL,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "intento_login" IS 'Intentos de login (append-only, 90 días). Registro de hechos; el contador vive en usuario (SPEC-14).';
COMMENT ON COLUMN "intento_login"."usuario_id" IS 'null si el correo no existe';
CREATE INDEX "idx_intento_login_usuario_fecha" ON "intento_login" ("usuario_id", "fecha");
CREATE INDEX "idx_intento_login_ip_fecha" ON "intento_login" ("ip", "fecha");

CREATE TABLE IF NOT EXISTS "token_refresco" (
	"jti" uuid NOT NULL,
	"usuario_id" uuid NOT NULL,
	"familia_id" uuid NOT NULL,
	"hash_token" varchar(255) NOT NULL,
	"revocado" boolean NOT NULL DEFAULT false,
	"usado" boolean NOT NULL DEFAULT false,
	"expiracion" timestamptz NOT NULL,
	"creado_en" timestamptz NOT NULL,
	PRIMARY KEY("jti")
);
COMMENT ON TABLE "token_refresco" IS 'Refresh tokens con familia. Rotación en cada uso; reutilización revoca la familia (SPEC-06).';
COMMENT ON COLUMN "token_refresco"."familia_id" IS 'una sesión = una familia';
CREATE INDEX "idx_token_refresco_familia" ON "token_refresco" ("familia_id");

CREATE TABLE IF NOT EXISTS "desafio_mfa" (
	"id" uuid NOT NULL,
	"usuario_id" uuid NOT NULL,
	"hash_token" varchar(255) NOT NULL,
	"expiracion" timestamptz NOT NULL,
	"consumido" boolean NOT NULL DEFAULT false,
	"creado_en" timestamptz NOT NULL,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "desafio_mfa" IS 'challengeToken del login con segundo factor (SPEC-09).';
-- UNIQUE(id, usuario_id) habilita la FK compuesta desde otp.
CREATE UNIQUE INDEX "uq_desafio_id_usuario" ON "desafio_mfa" ("id", "usuario_id");

CREATE TABLE IF NOT EXISTS "otp" (
	"id" uuid NOT NULL,
	"usuario_id" uuid NOT NULL,
	"desafio_id" uuid,
	"hash_codigo" varchar(255) NOT NULL,
	"canal" canal_otp NOT NULL,
	"intentos" integer NOT NULL DEFAULT 0,
	"expiracion" timestamptz NOT NULL,
	"usado" boolean NOT NULL DEFAULT false,
	"creado_en" timestamptz NOT NULL,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "otp" IS 'Código OTP de 6 dígitos, solo hash, 5 min, máx 3 intentos (SPEC-09).';
COMMENT ON COLUMN "otp"."desafio_id" IS 'null para validación de contacto sin login';
COMMENT ON COLUMN "otp"."hash_codigo" IS 'HMAC-SHA256 con clave de servidor (6 dígitos = baja entropía). Nunca hash simple.';

CREATE TABLE IF NOT EXISTS "token_un_uso" (
	"id" uuid NOT NULL,
	"proposito" proposito_token NOT NULL,
	"usuario_id" uuid NOT NULL,
	"hash_token" varchar(255) NOT NULL,
	"destino" varchar(254),
	"expiracion" timestamptz NOT NULL,
	"usado" boolean NOT NULL DEFAULT false,
	"creado_en" timestamptz NOT NULL,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "token_un_uso" IS 'Tokens de un solo uso: verificación (24h), recuperación (30m), desbloqueo (30m) (SPEC-02/08/14).';
COMMENT ON COLUMN "token_un_uso"."destino" IS 'nuevo correo en CAMBIO_CORREO';

CREATE TABLE IF NOT EXISTS "rol" (
	"codigo" codigo_rol NOT NULL,
	"nombre" varchar(50) NOT NULL,
	"descripcion" varchar(255),
	PRIMARY KEY("codigo")
);
COMMENT ON TABLE "rol" IS 'Catálogo cerrado de 6 roles (SPEC-11). Se siembra, no se inserta en runtime.';

CREATE TABLE IF NOT EXISTS "permiso" (
	"codigo" varchar(50) NOT NULL,
	"modulo" varchar(50) NOT NULL,
	"descripcion" varchar(255),
	PRIMARY KEY("codigo")
);
COMMENT ON TABLE "permiso" IS 'Permisos recurso.accion (SPEC-11). Hoy los del módulo; se añaden los de consumidores.';
COMMENT ON COLUMN "permiso"."codigo" IS 'recurso.accion';

CREATE TABLE IF NOT EXISTS "rol_permiso" (
	"rol_codigo" codigo_rol NOT NULL,
	"permiso_codigo" varchar(50) NOT NULL,
	PRIMARY KEY("rol_codigo", "permiso_codigo")
);
COMMENT ON TABLE "rol_permiso" IS 'N:N rol-permiso (fuente de permisos efectivos).';

CREATE TABLE IF NOT EXISTS "usuario_rol" (
	"usuario_id" uuid NOT NULL,
	"rol_codigo" codigo_rol NOT NULL,
	PRIMARY KEY("usuario_id", "rol_codigo")
);
COMMENT ON TABLE "usuario_rol" IS 'N:N usuario-rol. Asignación de a uno en uno (SPEC-11).';

CREATE TABLE IF NOT EXISTS "auditoria_seguridad" (
	"id" bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	"fecha" timestamptz NOT NULL,
	"accion" varchar(50) NOT NULL,
	"resultado" resultado NOT NULL,
	"actor_tipo" actor_tipo NOT NULL,
	"actor_id" varchar(64),
	"objetivo_usuario_id" uuid,
	"ip" varchar(45),
	"agente" varchar(255),
	"detalle" jsonb,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "auditoria_seguridad" IS 'Solo-anexado, sin UPDATE/DELETE para la app, retención 90 días configurable (SPEC-12).';
COMMENT ON COLUMN "auditoria_seguridad"."actor_id" IS 'uuid usuario o client_id';
CREATE INDEX "idx_auditoria_objetivo" ON "auditoria_seguridad" ("objetivo_usuario_id");
CREATE INDEX "idx_auditoria_actor" ON "auditoria_seguridad" ("actor_id");
CREATE INDEX "idx_auditoria_accion" ON "auditoria_seguridad" ("accion");
CREATE INDEX "idx_auditoria_fecha" ON "auditoria_seguridad" ("fecha");

CREATE TABLE IF NOT EXISTS "cliente_servicio" (
	"client_id" varchar(50) NOT NULL,
	"client_secret_hash" varchar(255) NOT NULL,
	"nombre" varchar(100),
	"activo" boolean NOT NULL DEFAULT true,
	PRIMARY KEY("client_id")
);
COMMENT ON TABLE "cliente_servicio" IS 'Los seis módulos consumidores (SPEC-17). client_secret siempre hasheado.';

CREATE TABLE IF NOT EXISTS "cliente_servicio_scope" (
	"client_id" varchar(50) NOT NULL,
	"scope" varchar(50) NOT NULL,
	PRIMARY KEY("client_id", "scope")
);
COMMENT ON TABLE "cliente_servicio_scope" IS 'Scopes concedidos por módulo (SPEC-17).';

CREATE TABLE IF NOT EXISTS "outbox" (
	"id" uuid NOT NULL,
	"topico" varchar(100) NOT NULL,
	"payload" jsonb NOT NULL,
	"estado" varchar(20) NOT NULL DEFAULT 'PENDIENTE',
	"creado_en" timestamptz NOT NULL,
	"intentos" integer NOT NULL DEFAULT 0,
	"procesado_en" timestamptz,
	PRIMARY KEY("id")
);
COMMENT ON TABLE "outbox" IS 'Outbox transaccional: correos asíncronos y eventos after-commit (SPEC-02/12/14).';
COMMENT ON COLUMN "outbox"."intentos" IS 'reintentos de envío del mensaje';
COMMENT ON COLUMN "outbox"."procesado_en" IS 'cuándo se despachó el mensaje';
CREATE INDEX "idx_outbox_estado_creado" ON "outbox" ("estado", "creado_en");

ALTER TABLE "credencial"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "password_historial"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "perfil_cliente"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "perfil_vendedor"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "direccion"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "bloqueo"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "intento_login"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE SET NULL;
ALTER TABLE "token_refresco"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "desafio_mfa"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "otp"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
-- FK compuesta: el OTP y su desafío deben ser del mismo usuario (ESC-09.9).
-- MATCH SIMPLE (default): si desafio_id es NULL, no se evalúa.
ALTER TABLE "otp"
ADD FOREIGN KEY("desafio_id", "usuario_id") REFERENCES "desafio_mfa"("id", "usuario_id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "token_un_uso"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "rol_permiso"
ADD FOREIGN KEY("rol_codigo") REFERENCES "rol"("codigo")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "rol_permiso"
ADD FOREIGN KEY("permiso_codigo") REFERENCES "permiso"("codigo")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "usuario_rol"
ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "usuario_rol"
ADD FOREIGN KEY("rol_codigo") REFERENCES "rol"("codigo")
ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE "cliente_servicio_scope"
ADD FOREIGN KEY("client_id") REFERENCES "cliente_servicio"("client_id")
ON UPDATE NO ACTION ON DELETE CASCADE;
