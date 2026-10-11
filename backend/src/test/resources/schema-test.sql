-- Esquema mínimo para pruebas de repositorios V3.
-- Crea las tablas necesarias para probar token_refresco, desafio_mfa,
-- intento_login y bloqueo sin depender de las migraciones V1 y V2.

CREATE TYPE "estado_cuenta" AS ENUM (
	'ACTIVO',
	'INACTIVO',
	'PENDIENTE_VERIFICACION'
);

CREATE TYPE "resultado" AS ENUM (
	'EXITO',
	'FALLO'
);

CREATE TYPE "tipo_bloqueo" AS ENUM (
	'AUTOMATICO',
	'MANUAL'
);

CREATE TYPE "estado_cuenta_efectivo" AS ENUM (
	'ACTIVO',
	'INACTIVO',
	'BLOQUEADO',
	'PENDIENTE_VERIFICACION'
);

CREATE TABLE IF NOT EXISTS "usuario" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"correo" varchar(254) NOT NULL,
	"nombres" varchar(100) NOT NULL,
	"apellidos" varchar(100) NOT NULL,
	"celular" varchar(12),
	"estado" estado_cuenta NOT NULL,
	"correo_verificado" boolean NOT NULL DEFAULT false,
	"celular_verificado" boolean NOT NULL DEFAULT false,
	"mfa_habilitado" boolean NOT NULL DEFAULT false,
	"acepta_terminos" boolean NOT NULL DEFAULT false,
	"fecha_aceptacion" timestamptz,
	"version_terminos" varchar(20),
	"canal_origen" varchar(20) NOT NULL DEFAULT 'WEB',
	"intentos_fallidos_consecutivos" integer NOT NULL DEFAULT 0,
	"bloqueos_seguidos" integer NOT NULL DEFAULT 0,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	"actualizado_en" timestamptz,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "token_refresco" (
	"jti" uuid NOT NULL DEFAULT gen_random_uuid(),
	"usuario_id" uuid NOT NULL,
	"familia_id" uuid NOT NULL,
	"hash_token" varchar(255) NOT NULL,
	"revocado" boolean NOT NULL DEFAULT false,
	"usado" boolean NOT NULL DEFAULT false,
	"expiracion" timestamptz NOT NULL,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	PRIMARY KEY("jti"),
	CONSTRAINT "ck_token_refresco_expiracion" CHECK ("expiracion" > "creado_en")
);

CREATE UNIQUE INDEX "uq_token_refresco_hash" ON "token_refresco" ("hash_token");
CREATE INDEX "idx_token_refresco_familia" ON "token_refresco" ("familia_id");
CREATE INDEX "idx_token_refresco_usuario" ON "token_refresco" ("usuario_id");

ALTER TABLE "token_refresco"
	ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
	ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS "desafio_mfa" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"usuario_id" uuid NOT NULL,
	"hash_token" varchar(255) NOT NULL,
	"expiracion" timestamptz NOT NULL,
	"consumido" boolean NOT NULL DEFAULT false,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	PRIMARY KEY("id"),
	CONSTRAINT "ck_desafio_mfa_expiracion" CHECK ("expiracion" > "creado_en")
);

CREATE UNIQUE INDEX "uq_desafio_id_usuario" ON "desafio_mfa" ("id", "usuario_id");
CREATE UNIQUE INDEX "uq_desafio_mfa_hash" ON "desafio_mfa" ("hash_token");
CREATE INDEX "idx_desafio_mfa_usuario" ON "desafio_mfa" ("usuario_id");

ALTER TABLE "desafio_mfa"
	ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
	ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS "intento_login" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"usuario_id" uuid,
	"correo_intentado" varchar(254),
	"resultado" resultado NOT NULL,
	"ip" varchar(45),
	"user_agent" varchar(255),
	"fecha" timestamptz NOT NULL DEFAULT now(),
	PRIMARY KEY("id")
);

CREATE INDEX "idx_intento_login_usuario_fecha" ON "intento_login" ("usuario_id", "fecha");
CREATE INDEX "idx_intento_login_ip_fecha" ON "intento_login" ("ip", "fecha");
CREATE INDEX "idx_intento_login_fecha" ON "intento_login" ("fecha");

ALTER TABLE "intento_login"
	ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
	ON UPDATE NO ACTION ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS "bloqueo" (
	"usuario_id" uuid NOT NULL,
	"tipo" tipo_bloqueo NOT NULL,
	"bloqueado_hasta" timestamptz,
	"motivo" varchar(500),
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	PRIMARY KEY("usuario_id"),
	CONSTRAINT "ck_bloqueo_tipo" CHECK (
		("tipo" = 'MANUAL' AND "motivo" IS NOT NULL AND "bloqueado_hasta" IS NULL)
		OR ("tipo" = 'AUTOMATICO' AND "motivo" IS NULL)
	)
);

ALTER TABLE "bloqueo"
	ADD FOREIGN KEY("usuario_id") REFERENCES "usuario"("id")
	ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE OR REPLACE VIEW "usuario_estado_efectivo" AS
SELECT
	u."id" AS "usuario_id",
	CASE
		WHEN u."estado" = 'ACTIVO'
			AND b."usuario_id" IS NOT NULL
			AND (b."bloqueado_hasta" IS NULL OR b."bloqueado_hasta" > now())
		THEN 'BLOQUEADO'::estado_cuenta_efectivo
		ELSE u."estado"::text::estado_cuenta_efectivo
	END AS "estado"
FROM "usuario" u
LEFT JOIN "bloqueo" b ON b."usuario_id" = u."id";

CREATE TABLE IF NOT EXISTS "credencial" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"usuario_id" uuid NOT NULL,
	"password_hash" varchar(255) NOT NULL,
	"salt" varchar(255),
	"algoritmo" varchar(50) NOT NULL,
	"requiere_cambio" boolean NOT NULL DEFAULT FALSE,
	"intentos_fallidos" integer NOT NULL DEFAULT 0,
	"bloqueada_hasta" timestamptz,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	"actualizado_en" timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT "pk_credencial" PRIMARY KEY ("id"),
	CONSTRAINT "uq_credencial_usuario" UNIQUE ("usuario_id"),
	CONSTRAINT "ck_credencial_intentos_fallidos" CHECK ("intentos_fallidos" >= 0),
	CONSTRAINT "fk_credencial_usuario" FOREIGN KEY ("usuario_id")
		REFERENCES "usuario" ("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "password_historial" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"credencial_id" uuid NOT NULL,
	"password_hash" varchar(255) NOT NULL,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT "pk_password_historial" PRIMARY KEY ("id"),
	CONSTRAINT "fk_password_historial_credencial" FOREIGN KEY ("credencial_id")
		REFERENCES "credencial" ("id") ON DELETE CASCADE
);

CREATE INDEX "idx_password_historial_credencial_creado_en"
	ON "password_historial" ("credencial_id", "creado_en" DESC);

CREATE TABLE IF NOT EXISTS "otp" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"usuario_id" uuid NOT NULL,
	"hash_codigo" varchar(255) NOT NULL,
	"destino" varchar(255) NOT NULL,
	"canal" varchar(50) NOT NULL,
	"motivo" varchar(50) NOT NULL,
	"expira_en" timestamptz NOT NULL,
	"usado" boolean NOT NULL DEFAULT FALSE,
	"intentos" integer NOT NULL DEFAULT 0,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT "pk_otp" PRIMARY KEY ("id"),
	CONSTRAINT "ck_otp_intentos" CHECK ("intentos" >= 0),
	CONSTRAINT "ck_otp_expira_en" CHECK ("expira_en" > "creado_en"),
	CONSTRAINT "fk_otp_usuario" FOREIGN KEY ("usuario_id")
		REFERENCES "usuario" ("id") ON DELETE CASCADE
);

CREATE INDEX "idx_otp_usuario_id" ON "otp" ("usuario_id");
CREATE INDEX "idx_otp_expira_en" ON "otp" ("expira_en");

CREATE TABLE IF NOT EXISTS "token_un_uso" (
	"id" uuid NOT NULL DEFAULT gen_random_uuid(),
	"usuario_id" uuid NOT NULL,
	"hash_token" varchar(255) NOT NULL,
	"motivo" varchar(50) NOT NULL,
	"expira_en" timestamptz NOT NULL,
	"usado" boolean NOT NULL DEFAULT FALSE,
	"creado_en" timestamptz NOT NULL DEFAULT now(),
	CONSTRAINT "pk_token_un_uso" PRIMARY KEY ("id"),
	CONSTRAINT "uq_token_un_uso_hash" UNIQUE ("hash_token"),
	CONSTRAINT "ck_token_un_uso_expira_en" CHECK ("expira_en" > "creado_en"),
	CONSTRAINT "fk_token_un_uso_usuario" FOREIGN KEY ("usuario_id")
		REFERENCES "usuario" ("id") ON DELETE CASCADE
);

CREATE INDEX "idx_token_un_uso_usuario_id" ON "token_un_uso" ("usuario_id");
CREATE INDEX "idx_token_un_uso_expira_en" ON "token_un_uso" ("expira_en");
