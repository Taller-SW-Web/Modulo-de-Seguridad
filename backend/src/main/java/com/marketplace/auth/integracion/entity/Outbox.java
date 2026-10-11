package com.marketplace.auth.integracion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class Outbox {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "topico")
    private String topico;

    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Column(name = "estado")
    private String estado;

    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;

    @Column(name = "intentos")
    private Integer intentos;

    @Column(name = "procesado_en")
    private OffsetDateTime procesadoEn;

    public Outbox() {
    }

    public Outbox(UUID id, String topico, String payload, String estado, OffsetDateTime creadoEn, Integer intentos, OffsetDateTime procesadoEn) {
        this.id = id;
        this.topico = topico;
        this.payload = payload;
        this.estado = estado;
        this.creadoEn = creadoEn;
        this.intentos = intentos;
        this.procesadoEn = procesadoEn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTopico() {
        return topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public OffsetDateTime getProcesadoEn() {
        return procesadoEn;
    }

    public void setProcesadoEn(OffsetDateTime procesadoEn) {
        this.procesadoEn = procesadoEn;
    }
}
