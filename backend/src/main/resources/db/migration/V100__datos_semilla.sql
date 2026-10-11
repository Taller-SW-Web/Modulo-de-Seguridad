-- V100__datos_semilla.sql
-- Datos semilla: 6 roles, 8 permisos (solo ADMIN_SISTEMA), usuarios de prueba (kit §6)
-- Dueña: Eva Moreno

-- ============================================================
-- 1. ROLES (SPEC-11) - Catálogo cerrado de 6 roles
-- ============================================================
INSERT INTO rol (codigo, nombre, descripcion) VALUES
('CLIENTE', 'Cliente Final', 'Usuario comprador de la plataforma'),
('VENDEDOR', 'Asesor de Ventas', 'Gestión de cotizaciones y pedidos'),
('ADMIN_VENTAS', 'Supervisor de Ventas', 'Supervisión de operaciones comerciales'),
('GESTOR_DESPACHO', 'Gestor de Logística', 'Administración de almacén y despachos'),
('GESTOR_COMERCIAL', 'Gestor Comercial', 'Gestión de catálogo de productos y precios'),
('ADMIN_SISTEMA', 'Administrador del Sistema', 'Superadministrador de la plataforma e IAM')
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- 2. PERMISOS (SPEC-11) - Solo los de este módulo, recurso.accion (punto)
-- ============================================================
INSERT INTO permiso (codigo, modulo, descripcion) VALUES
('usuario.ver', 'seguridad', 'Listar y consultar cualquier cuenta'),
('usuario.crear', 'seguridad', 'Dar de alta vendedores y roles de gestión'),
('usuario.desactivar', 'seguridad', 'Dar de baja una cuenta'),
('usuario.reactivar', 'seguridad', 'Reactivar una cuenta dada de baja'),
('rol.asignar', 'seguridad', 'Asignar y revocar roles'),
('auditoria.ver', 'seguridad', 'Consultar y exportar la auditoría'),
('cuenta.bloquear', 'seguridad', 'Bloquear y desbloquear cuentas'),
('atributos.editar', 'seguridad', 'Editar los atributos de cualquier cuenta'),
('cliente.registrar', 'seguridad', 'Registrar a un cliente en tienda (A6)'),
('cliente.buscar', 'seguridad', 'Encontrar a un cliente por su documento (A7)')
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- 3. ROL_PERMISO - Los 8 de administración a ADMIN_SISTEMA; los 2 de tienda a VENDEDOR
-- ============================================================
INSERT INTO rol_permiso (rol_codigo, permiso_codigo)
SELECT 'ADMIN_SISTEMA', codigo FROM permiso WHERE codigo NOT LIKE 'cliente.%'
ON CONFLICT (rol_codigo, permiso_codigo) DO NOTHING;

INSERT INTO rol_permiso (rol_codigo, permiso_codigo) VALUES
('VENDEDOR', 'cliente.registrar'),
('VENDEDOR', 'cliente.buscar')
ON CONFLICT (rol_codigo, permiso_codigo) DO NOTHING;

-- Los valores cifrado_* y semilla-hmac-* son marcadores: en ejecución los calcula la
-- aplicación con sus claves. Sirven para probar el esquema, no para buscar.

-- ============================================================
-- 4. USUARIOS DE PRUEBA (kit-integracion.md §6)
-- IDs fijos para que el mock y la BD real coincidan
-- ============================================================

-- Cliente activo: 11111111-1111-1111-1111-111111111111
INSERT INTO usuario (id, correo, nombres, apellidos, celular, estado, correo_verificado, celular_verificado, acepta_terminos, fecha_aceptacion, version_terminos, canal_origen)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'cliente@ejemplo.com',
    'María',
    'Quispe Rojas',
    '+51987654321',
    'ACTIVO',
    true,
    true,
    true,
    now(),
    '1.0',
    'WEB'
)
ON CONFLICT (id) DO NOTHING;

-- Perfil cliente para el cliente activo
INSERT INTO perfil_cliente (usuario_id, tipo_documento, documento_cifrado, fecha_nacimiento, documento_key_id, documento_hmac)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'DNI',
    'cifrado_12345678',
    '1990-05-15',
    'key-001',
    'semilla-hmac-dni-12345678'
)
ON CONFLICT (usuario_id) DO NOTHING;

-- Rol CLIENTE para el cliente activo
INSERT INTO usuario_rol (usuario_id, rol_codigo)
VALUES ('11111111-1111-1111-1111-111111111111', 'CLIENTE')
ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;


-- Vendedor activo: 22222222-2222-2222-2222-222222222222
INSERT INTO usuario (id, correo, nombres, apellidos, celular, estado, correo_verificado, celular_verificado, acepta_terminos, fecha_aceptacion, version_terminos, canal_origen)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'vendedor@ejemplo.com',
    'Carlos',
    'Mendoza',
    '+51987654322',
    'ACTIVO',
    true,
    true,
    true,
    now(),
    '1.0',
    'WEB'
)
ON CONFLICT (id) DO NOTHING;

-- Perfil cliente para el vendedor (también tiene documento)
-- El vendedor no lleva perfil de cliente: los atributos son excluyentes (RF-16.3).

-- Perfil vendedor
INSERT INTO perfil_vendedor (usuario_id, codigo_vendedor, tienda, fecha_ingreso)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'VEND-001',
    'Tienda Centro',
    '2023-01-15'
)
ON CONFLICT (usuario_id) DO NOTHING;

-- Rol VENDEDOR
INSERT INTO usuario_rol (usuario_id, rol_codigo)
VALUES ('22222222-2222-2222-2222-222222222222', 'VENDEDOR')
ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;


-- Usuario desactivado: 33333333-3333-3333-3333-333333333333
INSERT INTO usuario (id, correo, nombres, apellidos, celular, estado, correo_verificado, celular_verificado, acepta_terminos, fecha_aceptacion, version_terminos, canal_origen)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'desactivado@ejemplo.com',
    'Ana',
    'López',
    '+51987654323',
    'INACTIVO',
    true,
    true,
    true,
    now(),
    '1.0',
    'WEB'
)
ON CONFLICT (id) DO NOTHING;

-- Perfil cliente para usuario desactivado
INSERT INTO perfil_cliente (usuario_id, tipo_documento, documento_cifrado, fecha_nacimiento, documento_key_id, documento_hmac)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'DNI',
    'cifrado_11112222',
    '1992-03-10',
    'key-001',
    'semilla-hmac-dni-11112222'
)
ON CONFLICT (usuario_id) DO NOTHING;

-- Rol CLIENTE para usuario desactivado
INSERT INTO usuario_rol (usuario_id, rol_codigo)
VALUES ('33333333-3333-3333-3333-333333333333', 'CLIENTE')
ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;


-- Admin de prueba (para tests SPEC-04/11/15): aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
INSERT INTO usuario (id, correo, nombres, apellidos, estado, correo_verificado, acepta_terminos, fecha_aceptacion, version_terminos, canal_origen)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'admin@ejemplo.com',
    'Admin',
    'Sistema',
    'ACTIVO',
    true,
    true,
    now(),
    '1.0',
    'WEB'
)
ON CONFLICT (id) DO NOTHING;

-- Rol ADMIN_SISTEMA para el admin de prueba
INSERT INTO usuario_rol (usuario_id, rol_codigo)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ADMIN_SISTEMA')
ON CONFLICT (usuario_id, rol_codigo) DO NOTHING;