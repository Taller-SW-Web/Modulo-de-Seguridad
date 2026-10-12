package com.marketplace.auth.rol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Clave compuesta de usuario_rol: (usuario_id, rol_codigo).
 */
@Embeddable
public class UsuarioRolId implements Serializable {

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rol_codigo", nullable = false, updatable = false)
    private CodigoRol rolCodigo;

    // ── Constructores ──────────────────────────────────────────

    public UsuarioRolId() {
    }

    public UsuarioRolId(UUID usuarioId, CodigoRol rolCodigo) {
        this.usuarioId = usuarioId;
        this.rolCodigo = rolCodigo;
    }

    // ── Getters ────────────────────────────────────────────────

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public CodigoRol getRolCodigo() {
        return rolCodigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioRolId otro)) return false;
        return Objects.equals(usuarioId, otro.usuarioId) && rolCodigo == otro.rolCodigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, rolCodigo);
    }
}
