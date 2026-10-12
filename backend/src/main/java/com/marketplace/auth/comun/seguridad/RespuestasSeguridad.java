package com.marketplace.auth.comun.seguridad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad.ActorTipo;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad.Resultado;
import com.marketplace.auth.auditoria.service.AuditoriaService;
import com.marketplace.auth.comun.error.ErrorApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Respuestas 401 y 403 con el formato del catálogo de errores.
 *
 * <p>El 401 es idéntico exista o no el recurso, para no permitir enumerar cuentas.
 * El 403 no revela qué permiso faltaba y queda en auditoría como ACCESO_DENEGADO
 * (ESC-11.3).
 */
@Component
public class RespuestasSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(RespuestasSeguridad.class);

    private final ObjectMapper objectMapper;
    private final AuditoriaService auditoriaService;

    public RespuestasSeguridad(ObjectMapper objectMapper, AuditoriaService auditoriaService) {
        this.objectMapper = objectMapper;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void commence(HttpServletRequest peticion, HttpServletResponse respuesta,
                         AuthenticationException ex) throws IOException {
        escribir(respuesta, ErrorApi.tokenInvalido());
    }

    @Override
    public void handle(HttpServletRequest peticion, HttpServletResponse respuesta,
                       AccessDeniedException ex) throws IOException {
        auditarAccesoDenegado(peticion);
        escribir(respuesta, ErrorApi.scopeInsuficiente());
    }

    private void auditarAccesoDenegado(HttpServletRequest peticion) {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken auth) {
                Jwt token = auth.getToken();
                ActorTipo tipo = "servicio".equals(token.getClaimAsString("tipo")) ? ActorTipo.MODULO : ActorTipo.USUARIO;
                auditoriaService.registrar("ACCESO_DENEGADO", Resultado.FALLO, tipo, token.getSubject(), null,
                        peticion.getRemoteAddr(), peticion.getHeader(HttpHeaders.USER_AGENT),
                        Map.of("metodo", peticion.getMethod(), "ruta", peticion.getRequestURI()));
            }
        } catch (RuntimeException e) {
            // ACCESO_DENEGADO no es crítico (SPEC-12): el 403 se responde igual.
            log.warn("No se pudo auditar el acceso denegado: {}", e.getMessage());
        }
    }

    private void escribir(HttpServletResponse respuesta, ErrorApi error) throws IOException {
        respuesta.setStatus(error.getEstado().value());
        respuesta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(respuesta.getOutputStream(), error.aProblemDetail());
    }
}
