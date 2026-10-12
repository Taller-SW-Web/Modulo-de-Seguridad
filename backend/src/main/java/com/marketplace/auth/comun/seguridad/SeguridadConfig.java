package com.marketplace.auth.comun.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Reglas de acceso del servicio. API sin estado: cada petición trae su Bearer token.
 *
 * <p>Los permisos se exigen aquí, por ruta, y no en el método: así se deciden antes
 * de leer el cuerpo y un usuario sin permiso nunca recibe un 400 que le dé pistas.
 */
@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http, RespuestasSeguridad respuestas) throws Exception {
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(new ConvertidorAutoridades());

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers("/actuator/health/**", "/actuator/health").permitAll()
                        // Solo existe con el perfil dev (TokenDesarrolloController).
                        .requestMatchers("/dev/**").permitAll()
                        // SPEC-11: asignar y revocar roles.
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/*/roles").hasAuthority("rol.asignar")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/*/roles/*").hasAuthority("rol.asignar")
                        // SPEC-18: catálogo para los módulos (scope) y para el panel de administración.
                        .requestMatchers(HttpMethod.GET, "/api/v1/roles", "/api/v1/permisos")
                        .hasAnyAuthority("SCOPE_roles:leer", "rol.asignar")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(rs -> rs
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(convertidor))
                        .authenticationEntryPoint(respuestas)
                        .accessDeniedHandler(respuestas))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(respuestas)
                        .accessDeniedHandler(respuestas));
        return http.build();
    }
}
