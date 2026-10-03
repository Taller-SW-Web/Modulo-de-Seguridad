package com.marketplace.auth.integracion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente_servicio")
public class ClienteServicio {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret_hash")
    private String clientSecretHash;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    public ClienteServicio() {
    }

    public ClienteServicio(String clientId, String clientSecretHash, String nombre, Boolean activo) {
        this.clientId = clientId;
        this.clientSecretHash = clientSecretHash;
        this.nombre = nombre;
        this.activo = activo;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecretHash() {
        return clientSecretHash;
    }

    public void setClientSecretHash(String clientSecretHash) {
        this.clientSecretHash = clientSecretHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
