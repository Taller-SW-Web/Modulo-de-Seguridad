package com.marketplace.auth.rol.dto;

import java.util.List;

/**
 * Respuesta de GET /roles: los seis roles con los permisos de este módulo.
 */
public record CatalogoRolesResponse(List<RolResponse> roles) {

    public record RolResponse(String codigo, String nombre, String descripcion, List<String> permisos) {
    }
}
