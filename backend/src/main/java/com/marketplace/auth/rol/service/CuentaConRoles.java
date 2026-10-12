package com.marketplace.auth.rol.service;

import java.util.List;
import java.util.UUID;

/**
 * Estado de la cuenta tras cambiar sus roles: lo que responde POST /usuarios/{id}/roles.
 */
public record CuentaConRoles(UUID id, String nombres, String apellidos, String correo,
                             boolean correoVerificado, String estado, List<String> roles) {
}
