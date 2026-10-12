package com.marketplace.auth.comun.seguridad;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Par de claves RS256 con que se firman y verifican los tokens (ADR de RS256 + JWKS).
 *
 * <p>Sin claves configuradas se genera un par efímero: sirve en desarrollo y en
 * pruebas, pero cada reinicio invalida los tokens emitidos. En la nube las claves
 * llegan por variables de entorno ({@code JWT_CLAVE_PRIVADA}, {@code JWT_CLAVE_PUBLICA}).
 */
@Configuration
@EnableConfigurationProperties(PropiedadesJwt.class)
public class ClavesJwtConfig {

    private static final Logger log = LoggerFactory.getLogger(ClavesJwtConfig.class);

    @Bean
    public RSAKey claveFirma(PropiedadesJwt propiedades) throws GeneralSecurityException, JOSEException {
        KeyPair par;
        if (propiedades.tieneClaves()) {
            KeyFactory fabrica = KeyFactory.getInstance("RSA");
            par = new KeyPair(
                    fabrica.generatePublic(new X509EncodedKeySpec(leerPem(propiedades.clavePublica()))),
                    fabrica.generatePrivate(new PKCS8EncodedKeySpec(leerPem(propiedades.clavePrivada()))));
        } else {
            log.warn("seguridad.jwt sin claves: se genera un par RSA efímero. No usar así en producción.");
            KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
            generador.initialize(2048);
            par = generador.generateKeyPair();
        }
        RSAKey clave = new RSAKey.Builder((RSAPublicKey) par.getPublic())
                .privateKey((RSAPrivateKey) par.getPrivate())
                .build();
        return new RSAKey.Builder(clave).keyID(clave.computeThumbprint().toString()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey claveFirma) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(claveFirma)));
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey claveFirma, PropiedadesJwt propiedades) throws JOSEException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(claveFirma.toRSAPublicKey()).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(propiedades.emisor()));
        return decoder;
    }

    /** Acepta el PEM completo o solo su cuerpo en base64 (más cómodo en variables de entorno). */
    private static byte[] leerPem(String pem) {
        String cuerpo = pem.replaceAll("-----(BEGIN|END) [A-Z ]+-----", "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(cuerpo);
    }
}
