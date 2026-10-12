package com.marketplace.auth.rol.service;

import java.util.UUID;

/**
 * Quién ejecuta una acción administrativa y desde dónde, para la auditoría (SPEC-12).
 *
 * @param id     el {@code sub} del token del administrador
 * @param ip     IP de origen
 * @param agente User-Agent de la petición
 */
public record Actor(UUID id, String ip, String agente) {
}
