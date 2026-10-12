package com.marketplace.auth.integracion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.auth.integracion.entity.Outbox;
import com.marketplace.auth.integracion.repository.OutboxRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Publica eventos de usuario con el sobre común de specs/catalogo-eventos.md.
 *
 * <p>No habla con RabbitMQ: escribe en el outbox dentro de la transacción de
 * quien llama. Si la transacción se revierte, el evento tampoco existe.
 */
@Service
public class PublicadorEventos {

    private static final int VERSION_ESQUEMA = 1;

    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    public PublicadorEventos(OutboxRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * @param tipo      nombre del evento, igual a la routing key (p. ej. usuario.roles_cambiados)
     * @param usuarioId cuenta afectada
     * @param datos     carga específica del evento
     */
    public void publicar(String tipo, UUID usuarioId, Map<String, ?> datos) {
        OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        UUID eventoId = UUID.randomUUID();

        Map<String, Object> sobre = new LinkedHashMap<>();
        sobre.put("eventoId", eventoId);
        sobre.put("tipo", tipo);
        sobre.put("version", VERSION_ESQUEMA);
        sobre.put("fecha", DateTimeFormatter.ISO_INSTANT.format(ahora));
        sobre.put("usuarioId", usuarioId);
        sobre.put("datos", datos);

        Outbox fila = new Outbox();
        fila.setId(eventoId);
        fila.setTopico(tipo);
        fila.setPayload(aJson(sobre));
        fila.setEstado("PENDIENTE");
        fila.setIntentos(0);
        fila.setCreadoEn(ahora);
        repository.save(fila);
    }

    private String aJson(Map<String, Object> sobre) {
        try {
            return objectMapper.writeValueAsString(sobre);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el evento", e);
        }
    }
}
