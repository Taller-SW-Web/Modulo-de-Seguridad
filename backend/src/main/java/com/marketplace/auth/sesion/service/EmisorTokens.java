package com.marketplace.auth.sesion.service;

import com.marketplace.auth.comun.seguridad.PropiedadesJwt;
import com.marketplace.auth.rol.service.PermisosEfectivos;
import com.marketplace.auth.rol.service.PermisosEfectivosService;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Firma los tokens RS256 con los claims del contrato (componente tokenUsuario).
 *
 * <p>SPEC-05 (inicio de sesión) y SPEC-06 (renovación) lo llaman para el accessToken;
 * SPEC-17 para el token de servicio. Los claims {@code roles} y {@code permisos}
 * los calcula SPEC-11 en cada emisión: un cambio de roles se refleja en el
 * siguiente token, y las sesiones previas ya se revocaron (RF-11.5).
 */
@Service
public class EmisorTokens {

    private final JwtEncoder jwtEncoder;
    private final RSAKey claveFirma;
    private final PropiedadesJwt propiedades;
    private final PermisosEfectivosService permisosEfectivosService;

    public EmisorTokens(JwtEncoder jwtEncoder, RSAKey claveFirma, PropiedadesJwt propiedades,
                        PermisosEfectivosService permisosEfectivosService) {
        this.jwtEncoder = jwtEncoder;
        this.claveFirma = claveFirma;
        this.propiedades = propiedades;
        this.permisosEfectivosService = permisosEfectivosService;
    }

    /** accessToken de un usuario: 15 minutos, con sus roles y permisos efectivos (RF-05.6, RF-11.4). */
    public TokenEmitido emitirAcceso(UUID usuarioId, String correo) {
        PermisosEfectivos efectivos = permisosEfectivosService.calcular(usuarioId);
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(propiedades.emisor())
                .subject(usuarioId.toString())
                .claim("email", correo)
                .claim("roles", efectivos.roles())
                .claim("permisos", efectivos.permisos())
                .claim("tipo", "acceso")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(propiedades.duracionAcceso()))
                .id(UUID.randomUUID().toString())
                .build();
        return firmar(claims, propiedades.duracionAcceso().toSeconds());
    }

    /** Token de servicio de un módulo consumidor: 1 hora, con sus scopes (SPEC-17). */
    public TokenEmitido emitirServicio(String clientId, Collection<String> scopes, List<String> audiencias) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(propiedades.emisor())
                .subject(clientId)
                .audience(audiencias)
                .claim("tipo", "servicio")
                .claim("scope", String.join(" ", scopes))
                .issuedAt(ahora)
                .expiresAt(ahora.plus(propiedades.duracionServicio()))
                .id(UUID.randomUUID().toString())
                .build();
        return firmar(claims, propiedades.duracionServicio().toSeconds());
    }

    private TokenEmitido firmar(JwtClaimsSet claims, long segundos) {
        JwsHeader cabecera = JwsHeader.with(SignatureAlgorithm.RS256).keyId(claveFirma.getKeyID()).build();
        String valor = jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
        return new TokenEmitido(valor, segundos);
    }

    /** Token firmado y su vida en segundos ({@code expiresIn}). */
    public record TokenEmitido(String valor, long segundos) {
    }
}
