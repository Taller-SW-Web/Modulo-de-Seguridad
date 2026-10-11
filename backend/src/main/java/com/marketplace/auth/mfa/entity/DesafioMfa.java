package com.marketplace.auth.mfa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Challenge token del login con segundo factor (SPEC-09).
 * Tabla: desafio_mfa.
 */
@Entity
@Table(name = "desafio_mfa")
public class DesafioMfa {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Column(name = "hash_token", nullable = false, unique = true, updatable = false)
    private String hashToken;

    @Column(name = "expiracion", nullable = false, updatable = false)
    private OffsetDateTime expiracion;

    @Column(name = "consumido", nullable = false)
    private boolean consumido;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    // ── Constructores ──────────────────────────────────────────

    public DesafioMfa() {
    }

    // ── Getters y Setters ──────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getHashToken() {
        return hashToken;
    }

    public void setHashToken(String hashToken) {
        this.hashToken = hashToken;
    }

    public OffsetDateTime getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(OffsetDateTime expiracion) {
        this.expiracion = expiracion;
    }

    public boolean isConsumido() {
        return consumido;
    }

    public void setConsumido(boolean consumido) {
        this.consumido = consumido;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
