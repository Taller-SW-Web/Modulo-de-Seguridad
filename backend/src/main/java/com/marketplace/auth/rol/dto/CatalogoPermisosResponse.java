package com.marketplace.auth.rol.dto;

import java.util.List;

/**
 * Respuesta de GET /permisos.
 */
public record CatalogoPermisosResponse(List<PermisoResponse> permisos) {

    public record PermisoResponse(String codigo, String modulo, String descripcion) {
    }
}
