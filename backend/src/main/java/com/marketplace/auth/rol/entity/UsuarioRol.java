package com.marketplace.auth.rol.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Asignación de un rol a un usuario (N:N). Se asigna y se revoca de uno en uno
 * (RF-11.3), así dos administradores que editan a la vez no se pisan.
 * Tabla: usuario_rol.
 */
@Entity
@Table(name = "usuario_rol")
public class UsuarioRol {

    @EmbeddedId
    private UsuarioRolId id;

    // ── Constructores ──────────────────────────────────────────

    public UsuarioRol() {
    }

    public UsuarioRol(UUID usuarioId, CodigoRol rol) {
        this.id = new UsuarioRolId(usuarioId, rol);
    }

    // ── Getters ────────────────────────────────────────────────

    public UsuarioRolId getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return id.getUsuarioId();
    }

    public CodigoRol getRol() {
        return id.getRolCodigo();
    }
}
