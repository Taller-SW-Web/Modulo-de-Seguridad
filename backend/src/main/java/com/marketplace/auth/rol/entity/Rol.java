package com.marketplace.auth.rol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Rol del catálogo cerrado (SPEC-11). Se siembra en V100, no se inserta en runtime.
 * Tabla: rol.
 */
@Entity
@Immutable
@Table(name = "rol")
public class Rol {

    @Id
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "codigo", nullable = false, updatable = false)
    private CodigoRol codigo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // ── Constructores ──────────────────────────────────────────

    public Rol() {
    }

    // ── Getters ────────────────────────────────────────────────

    public CodigoRol getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
