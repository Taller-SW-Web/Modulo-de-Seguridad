package com.marketplace.auth.rol.dto;

import com.marketplace.auth.rol.service.CuentaConRoles;

import java.util.List;
import java.util.UUID;

/**
 * Esquema Usuario del contrato, con los campos que conoce SPEC-11.
 * Nunca incluye el hash de contraseña ni el documento.
 */
public record UsuarioResponse(UUID id, String nombres, String apellidos, String nombreCompleto,
                              String correo, String estado, boolean correoVerificado,
                              List<String> roles) {

    public static UsuarioResponse de(CuentaConRoles cuenta) {
        return new UsuarioResponse(cuenta.id(), cuenta.nombres(), cuenta.apellidos(),
                cuenta.nombres() + " " + cuenta.apellidos(), cuenta.correo(), cuenta.estado(),
                cuenta.correoVerificado(), cuenta.roles());
    }
}
