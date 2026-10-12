package com.marketplace.auth.comun.seguridad;

import com.marketplace.auth.comun.error.ErrorApi;
import com.marketplace.auth.rol.repository.UsuarioRolRepository;
import com.marketplace.auth.sesion.service.EmisorTokens;
import com.marketplace.auth.sesion.service.EmisorTokens.TokenEmitido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SOLO DESARROLLO (perfil {@code dev}). Emite tokens sin contraseña para probar los
 * endpoints protegidos mientras no existan el inicio de sesión (SPEC-05) y el
 * token de servicio (SPEC-17). Con cualquier otro perfil este controlador no existe
 * y {@code /dev/**} responde 404.
 */
@Profile("dev")
@RestController
@RequestMapping("/dev")
public class TokenDesarrolloController {

    private final EmisorTokens emisorTokens;
    private final UsuarioRolRepository usuarioRolRepository;

    public TokenDesarrolloController(EmisorTokens emisorTokens, UsuarioRolRepository usuarioRolRepository) {
        this.emisorTokens = emisorTokens;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    /** accessToken de un usuario existente, con sus roles y permisos reales. */
    @PostMapping("/token")
    public Map<String, Object> tokenUsuario(@Valid @RequestBody SolicitudUsuario solicitud) {
        var cuenta = usuarioRolRepository.findCuenta(solicitud.usuarioId()).orElseThrow(ErrorApi::noEncontrado);
        return respuesta(emisorTokens.emitirAcceso(cuenta.getId(), cuenta.getCorreo()));
    }

    /** Token de servicio de un módulo, p. ej. modulo-ventas con roles:leer. */
    @PostMapping("/token-servicio")
    public Map<String, Object> tokenServicio(@Valid @RequestBody SolicitudServicio solicitud) {
        return respuesta(emisorTokens.emitirServicio(solicitud.clientId(), solicitud.scopes(), List.of("api-seguridad")));
    }

    private static Map<String, Object> respuesta(TokenEmitido token) {
        return Map.of("accessToken", token.valor(), "tokenType", "Bearer", "expiresIn", token.segundos());
    }

    public record SolicitudUsuario(@NotNull UUID usuarioId) {
    }

    public record SolicitudServicio(@NotBlank String clientId, @NotEmpty List<String> scopes) {
    }
}
