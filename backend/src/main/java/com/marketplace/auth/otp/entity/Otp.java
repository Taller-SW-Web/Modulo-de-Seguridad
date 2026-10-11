package com.marketplace.auth.otp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Código OTP de un solo uso asociado a un usuario.
 * Tabla: otp.
 */
@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Column(name = "hash_codigo", nullable = false, length = 255, updatable = false)
    private String hashCodigo;

    @Column(name = "destino", nullable = false, length = 255, updatable = false)
    private String destino;

    @Column(name = "canal", nullable = false, length = 50, updatable = false)
    private String canal;

    @Column(name = "motivo", nullable = false, length = 50, updatable = false)
    private String motivo;

    @Column(name = "expira_en", nullable = false, updatable = false)
    private OffsetDateTime expiraEn;

    @Column(name = "usado", nullable = false)
    private boolean usado;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    // ── Constructores ──────────────────────────────────────────

    public Otp() {
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

    public String getHashCodigo() {
        return hashCodigo;
    }

    public void setHashCodigo(String hashCodigo) {
        this.hashCodigo = hashCodigo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public OffsetDateTime getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(OffsetDateTime expiraEn) {
        this.expiraEn = expiraEn;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}