package com.marketplace.auth.comun.seguridad;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuración de los tokens (prefijo {@code seguridad.jwt}).
 *
 * @param emisor          claim {@code iss}; igual al issuer del documento de descubrimiento
 * @param clavePrivada    clave RSA privada en PEM (PKCS#8). Vacía: se genera una efímera al arrancar
 * @param clavePublica    clave RSA pública en PEM (X.509), pareja de la privada
 * @param duracionAcceso  vida del accessToken (RF-05.6: 15 minutos)
 * @param duracionServicio vida del token de servicio (SPEC-17: 1 hora)
 */
@ConfigurationProperties(prefix = "seguridad.jwt")
public record PropiedadesJwt(String emisor, String clavePrivada, String clavePublica,
                             Duration duracionAcceso, Duration duracionServicio) {

    public PropiedadesJwt {
        if (emisor == null || emisor.isBlank()) {
            emisor = "http://localhost:8080/api/v1/auth";
        }
        if (duracionAcceso == null) {
            duracionAcceso = Duration.ofMinutes(15);
        }
        if (duracionServicio == null) {
            duracionServicio = Duration.ofHours(1);
        }
    }

    public boolean tieneClaves() {
        return clavePrivada != null && !clavePrivada.isBlank()
                && clavePublica != null && !clavePublica.isBlank();
    }
}
