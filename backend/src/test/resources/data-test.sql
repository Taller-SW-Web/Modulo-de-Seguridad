-- Datos de prueba para UsuarioEstadoEfectivoRepositoryTest.

-- Usuario ACTIVO sin bloqueo
INSERT INTO "usuario" (id, correo, nombres, apellidos, estado)
VALUES ('11111111-1111-1111-1111-111111111111', 'activo@correo.com', 'Activo', 'SinBloqueo', 'ACTIVO');

-- Usuario ACTIVO con bloqueo automático vigente
INSERT INTO "usuario" (id, correo, nombres, apellidos, estado)
VALUES ('22222222-2222-2222-2222-222222222222', 'bloqueado@correo.com', 'Bloqueado', 'Vigente', 'ACTIVO');

INSERT INTO "bloqueo" (usuario_id, tipo, bloqueado_hasta, motivo)
VALUES ('22222222-2222-2222-2222-222222222222', 'AUTOMATICO', now() + interval '5 minutes', null);

-- Usuario ACTIVO con bloqueo manual (sin vencimiento)
INSERT INTO "usuario" (id, correo, nombres, apellidos, estado)
VALUES ('33333333-3333-3333-3333-333333333333', 'manual@correo.com', 'Manual', 'SinVencimiento', 'ACTIVO');

INSERT INTO "bloqueo" (usuario_id, tipo, bloqueado_hasta, motivo)
VALUES ('33333333-3333-3333-3333-333333333333', 'MANUAL', null, 'Bloqueo por administrador');

-- Usuario ACTIVO con bloqueo vencido
INSERT INTO "usuario" (id, correo, nombres, apellidos, estado)
VALUES ('44444444-4444-4444-4444-444444444444', 'vencido@correo.com', 'Vencido', 'Bloqueo', 'ACTIVO');

INSERT INTO "bloqueo" (usuario_id, tipo, bloqueado_hasta, motivo)
VALUES ('44444444-4444-4444-4444-444444444444', 'AUTOMATICO', now() - interval '1 minute', null);
