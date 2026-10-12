package com.marketplace.auth.auditoria.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad.ActorTipo;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad.Resultado;
import com.marketplace.auth.auditoria.repository.AuditoriaSeguridadRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Punto único para escribir en la auditoría (SPEC-12).
 *
 * <p>Se ejecuta dentro de la transacción de quien llama: si la acción es crítica
 * (p. ej. ROL_ASIGNADO) y el registro falla, la acción también se revierte.
 */
@Service
public class AuditoriaService {

    private final AuditoriaSeguridadRepository repository;
    private final ObjectMapper objectMapper;

    public AuditoriaService(AuditoriaSeguridadRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void registrar(String accion, Resultado resultado, ActorTipo actorTipo, String actorId,
                          UUID objetivoUsuarioId, String ip, String agente, Map<String, ?> detalle) {
        AuditoriaSeguridad registro = new AuditoriaSeguridad();
        registro.setFecha(OffsetDateTime.now());
        registro.setAccion(accion);
        registro.setResultado(resultado);
        registro.setActorTipo(actorTipo);
        registro.setActorId(actorId);
        registro.setObjetivoUsuarioId(objetivoUsuarioId);
        registro.setIp(ip);
        registro.setAgente(recortar(agente, 255));
        registro.setDetalle(aJson(detalle));
        repository.save(registro);
    }

    private String aJson(Map<String, ?> detalle) {
        if (detalle == null || detalle.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(detalle);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el detalle de auditoría", e);
        }
    }

    private static String recortar(String valor, int maximo) {
        return valor == null || valor.length() <= maximo ? valor : valor.substring(0, maximo);
    }
}
