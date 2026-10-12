package com.marketplace.auth.comun.seguridad;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Traduce los claims del token a autoridades de Spring Security:
 * <ul>
 *   <li>{@code permisos} → tal cual ({@code rol.asignar}). Token de usuario (SPEC-11).</li>
 *   <li>{@code scope} → {@code SCOPE_roles:leer}. Token de servicio (SPEC-17).</li>
 *   <li>{@code roles} → {@code ROL_ADMIN_SISTEMA}.</li>
 * </ul>
 * Permisos con punto y scopes con dos puntos nunca se confunden.
 */
public class ConvertidorAutoridades implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt token) {
        List<GrantedAuthority> autoridades = new ArrayList<>();
        lista(token, "permisos").forEach(p -> autoridades.add(new SimpleGrantedAuthority(p)));
        lista(token, "roles").forEach(r -> autoridades.add(new SimpleGrantedAuthority("ROL_" + r)));

        String scope = token.getClaimAsString("scope");
        if (scope != null && !scope.isBlank()) {
            for (String s : scope.trim().split("\\s+")) {
                autoridades.add(new SimpleGrantedAuthority("SCOPE_" + s));
            }
        }
        return autoridades;
    }

    private static List<String> lista(Jwt token, String claim) {
        List<String> valores = token.getClaimAsStringList(claim);
        return valores != null ? valores : List.of();
    }
}
