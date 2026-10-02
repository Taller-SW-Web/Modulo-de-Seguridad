package com.marketplace.auth.usuario.entity;

/**
 * Estado efectivo de una cuenta, calculado desde usuario + bloqueo.
 * Usada por: vista usuario_estado_efectivo (RF-14.7, RF-15.5).
 */
public enum EstadoCuentaEfectivo {
    ACTIVO,
    INACTIVO,
    BLOQUEADO,
    PENDIENTE_VERIFICACION
}
