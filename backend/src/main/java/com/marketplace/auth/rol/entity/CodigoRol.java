package com.marketplace.auth.rol.entity;

/**
 * Catálogo cerrado de los seis roles del marketplace (RF-11.1).
 * Se corresponde con el enum codigo_rol de PostgreSQL; no se crean roles en runtime.
 */
public enum CodigoRol {
    CLIENTE,
    VENDEDOR,
    ADMIN_VENTAS,
    GESTOR_DESPACHO,
    GESTOR_COMERCIAL,
    ADMIN_SISTEMA
}
