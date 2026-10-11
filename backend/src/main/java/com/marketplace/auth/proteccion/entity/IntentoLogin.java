package com.marketplace.auth.proteccion.entity;

import com.marketplace.auth.proteccion.entity.Resultado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Intento de login (append-only, retención 90 días).
 * Registro de hechos; el contador vive en usuario (SPEC-14).
 * Tabla: intento_login.
 */
@Entity
@Table(name = "intento_login")
public class IntentoLogin {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "usuario_id", updatable = false)
    private UUID usuarioId;

    @Column(name = "correo_intentado", updatable = false)
    private String correoIntentado;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "resultado", nullable = false, updatable = false)
    private Resultado resultado;

    @Column(name = "ip", updatable = false)
    private String ip;

    @Column(name = "user_agent", updatable = false)
    private String userAgent;

    @Column(name = "fecha", nullable = false, updatable = false)
    private OffsetDateTime fecha;

    // ── Constructores ──────────────────────────────────────────

    public IntentoLogin() {
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

    public String getCorreoIntentado() {
        return correoIntentado;
    }

    public void setCorreoIntentado(String correoIntentado) {
        this.correoIntentado = correoIntentado;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }
}
