package com.marketplace.auth.integracion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "cliente_servicio_scope")
public class ClienteServicioScope {

    @EmbeddedId
    private ClienteServicioScopeId id;

    public ClienteServicioScope() {
    }

    public ClienteServicioScope(ClienteServicioScopeId id) {
        this.id = id;
    }

    public ClienteServicioScopeId getId() {
        return id;
    }

    public void setId(ClienteServicioScopeId id) {
        this.id = id;
    }

    @jakarta.persistence.Embeddable
    public static class ClienteServicioScopeId implements Serializable {

        @Column(name = "client_id")
        private String clientId;

        @Column(name = "scope")
        private String scope;

        public ClienteServicioScopeId() {
        }

        public ClienteServicioScopeId(String clientId, String scope) {
            this.clientId = clientId;
            this.scope = scope;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClienteServicioScopeId that = (ClienteServicioScopeId) o;
            return Objects.equals(clientId, that.clientId) && Objects.equals(scope, that.scope);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId, scope);
        }
    }
}
