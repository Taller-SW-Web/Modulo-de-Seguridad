package com.marketplace.auth.credencial.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Hash de una contraseña utilizada anteriormente por una credencial.
 * Tabla: password_historial (SPEC-07).
 */
@Entity
@Table(name = "password_historial")
public class PasswordHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "credencial_id", nullable = false, updatable = false)
    private UUID credencialId;

    @Column(name = "password_hash", nullable = false, length = 255, updatable = false)
    private String passwordHash;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    // ── Constructores ──────────────────────────────────────────

    public PasswordHistorial() {
    }

    // ── Getters y Setters ──────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCredencialId() {
        return credencialId;
    }

    public void setCredencialId(UUID credencialId) {
        this.credencialId = credencialId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}