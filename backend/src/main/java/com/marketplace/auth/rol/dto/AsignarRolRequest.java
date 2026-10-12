package com.marketplace.auth.rol.dto;

import com.marketplace.auth.rol.entity.CodigoRol;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de POST /usuarios/{id}/roles. Un código fuera del catálogo cerrado
 * ("BUYER", "SUPER_ADMIN") no se puede deserializar y responde 400 VALIDACION (ESC-11.2).
 */
public record AsignarRolRequest(@NotNull(message = "Es obligatorio.") CodigoRol rol) {
}
