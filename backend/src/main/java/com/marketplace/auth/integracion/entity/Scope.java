package com.marketplace.auth.integracion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scope")
public class Scope {

    @Id
    @Column(name = "codigo")
    private String codigo;

    @Column(name = "audiencia")
    private String audiencia;

    @Column(name = "descripcion")
    private String descripcion;

    public Scope() {
    }

    public Scope(String codigo, String audiencia, String descripcion) {
        this.codigo = codigo;
        this.audiencia = audiencia;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getAudiencia() {
        return audiencia;
    }

    public void setAudiencia(String audiencia) {
        this.audiencia = audiencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
