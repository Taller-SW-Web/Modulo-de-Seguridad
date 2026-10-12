package com.marketplace.auth.rol.controller;

import com.marketplace.auth.rol.dto.AsignarRolRequest;
import com.marketplace.auth.rol.dto.UsuarioResponse;
import com.marketplace.auth.rol.entity.CodigoRol;
import com.marketplace.auth.rol.service.Actor;
import com.marketplace.auth.rol.service.RolService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Asignación y revocación de roles (SPEC-11). Solo con el permiso {@code rol.asignar},
 * que se exige en {@code SeguridadConfig} antes de leer el cuerpo: un usuario sin
 * permiso recibe 403 aunque mande un rol inválido.
 */
@RestController
@RequestMapping("/api/v1/usuarios/{id}/roles")
public class RolUsuarioController {

    private final RolService rolService;

    public RolUsuarioController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> asignar(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody AsignarRolRequest cuerpo,
                                                   @AuthenticationPrincipal Jwt token,
                                                   HttpServletRequest peticion) {
        var cuenta = rolService.asignar(id, cuerpo.rol(), actor(token, peticion));
        return ResponseEntity.ok(UsuarioResponse.de(cuenta));
    }

    @DeleteMapping("/{rol}")
    public ResponseEntity<Void> revocar(@PathVariable("id") UUID id,
                                        @PathVariable("rol") CodigoRol rol,
                                        @AuthenticationPrincipal Jwt token,
                                        HttpServletRequest peticion) {
        rolService.revocar(id, rol, actor(token, peticion));
        return ResponseEntity.noContent().build();
    }

    private static Actor actor(Jwt token, HttpServletRequest peticion) {
        return new Actor(UUID.fromString(token.getSubject()), peticion.getRemoteAddr(),
                peticion.getHeader(HttpHeaders.USER_AGENT));
    }
}
