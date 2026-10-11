package com.marketplace.auth.proteccion.entity;

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
 * Bloqueo vigente de cuenta (1:1 con usuario).
 * Fuente de verdad del estado BLOQUEADO efectivo.
 * Manual no vence; automático según escalera 1/2/4/null (SPEC-14/15).
 * Tabla: bloqueo.
 */
@Entity
@Table(name = "bloqueo")
public class Bloqueo {

    @Id
    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo", nullable = false, updatable = false)
    private TipoBloqueo tipo;

    @Column(name = "bloqueado_hasta", updatable = false)
    private OffsetDateTime bloqueadoHasta;

    @Column(name = "motivo", updatable = false)
    private String motivo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    // ── Constructores ──────────────────────────────────────────

    public Bloqueo() {
    }

    // ── Getters y Setters ──────────────────────────────────────

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoBloqueo getTipo() {
        return tipo;
    }

    public void setTipo(TipoBloqueo tipo) {
        this.tipo = tipo;
    }

    public OffsetDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public void setBloqueadoHasta(OffsetDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
