package com.marketplace.auth.auditoria.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_seguridad")
public class AuditoriaSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Column(name = "accion")
    private String accion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "resultado")
    private Resultado resultado;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "actor_tipo")
    private ActorTipo actorTipo;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "objetivo_usuario_id")
    private UUID objetivoUsuarioId;

    @Column(name = "ip")
    private String ip;

    @Column(name = "agente")
    private String agente;

    @Column(name = "detalle", columnDefinition = "jsonb")
    private String detalle;

    public AuditoriaSeguridad() {
    }

    public AuditoriaSeguridad(Long id, OffsetDateTime fecha, String accion, Resultado resultado, ActorTipo actorTipo, String actorId, UUID objetivoUsuarioId, String ip, String agente, String detalle) {
        this.id = id;
        this.fecha = fecha;
        this.accion = accion;
        this.resultado = resultado;
        this.actorTipo = actorTipo;
        this.actorId = actorId;
        this.objetivoUsuarioId = objetivoUsuarioId;
        this.ip = ip;
        this.agente = agente;
        this.detalle = detalle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public ActorTipo getActorTipo() {
        return actorTipo;
    }

    public void setActorTipo(ActorTipo actorTipo) {
        this.actorTipo = actorTipo;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public UUID getObjetivoUsuarioId() {
        return objetivoUsuarioId;
    }

    public void setObjetivoUsuarioId(UUID objetivoUsuarioId) {
        this.objetivoUsuarioId = objetivoUsuarioId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public enum Resultado {
        EXITO, FALLO
    }

    public enum ActorTipo {
        USUARIO, MODULO, SISTEMA
    }
}
