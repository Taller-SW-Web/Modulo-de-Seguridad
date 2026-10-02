package com.marketplace.auth.sesion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Refresh token con familia de sesión.
 * Rotación en cada uso; reutilización revoca la familia (SPEC-06).
 * Tabla: token_refresco.
 */
@Entity
@Table(name = "token_refresco")
public class TokenRefresco {

    @Id
    @Column(name = "jti", nullable = false, updatable = false)
    private UUID jti;

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Column(name = "familia_id", nullable = false, updatable = false)
    private UUID familiaId;

    @Column(name = "hash_token", nullable = false, unique = true, updatable = false)
    private String hashToken;

    @Column(name = "revocado", nullable = false)
    private boolean revocado;

    @Column(name = "usado", nullable = false)
    private boolean usado;

    @Column(name = "expiracion", nullable = false, updatable = false)
    private OffsetDateTime expiracion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    // ── Constructores ──────────────────────────────────────────

    public TokenRefresco() {
    }

    // ── Getters y Setters ──────────────────────────────────────

    public UUID getJti() {
        return jti;
    }

    public void setJti(UUID jti) {
        this.jti = jti;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(UUID familiaId) {
        this.familiaId = familiaId;
    }

    public String getHashToken() {
        return hashToken;
    }

    public void setHashToken(String hashToken) {
        this.hashToken = hashToken;
    }

    public boolean isRevocado() {
        return revocado;
    }

    public void setRevocado(boolean revocado) {
        this.revocado = revocado;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public OffsetDateTime getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(OffsetDateTime expiracion) {
        this.expiracion = expiracion;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
