package com.marketplace.auth.rol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/**
 * Permiso de este módulo, escrito recurso.accion con punto (RF-11.9).
 * Los dos puntos quedan para los scopes de SPEC-17. Tabla: permiso.
 */
@Entity
@Immutable
@Table(name = "permiso")
public class Permiso {

    @Id
    @Column(name = "codigo", nullable = false, updatable = false)
    private String codigo;

    @Column(name = "modulo", nullable = false)
    private String modulo;

    @Column(name = "descripcion")
    private String descripcion;

    // ── Constructores ──────────────────────────────────────────

    public Permiso() {
    }

    // ── Getters ────────────────────────────────────────────────

    public String getCodigo() {
        return codigo;
    }

    public String getModulo() {
        return modulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
