package com.marketplace.auth.rol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad;
import com.marketplace.auth.auditoria.repository.AuditoriaSeguridadRepository;
import com.marketplace.auth.integracion.entity.Outbox;
import com.marketplace.auth.integracion.repository.OutboxRepository;
import com.marketplace.auth.rol.entity.CodigoRol;
import com.marketplace.auth.rol.entity.UsuarioRol;
import com.marketplace.auth.rol.repository.UsuarioRolRepository;
import com.marketplace.auth.rol.service.PermisosEfectivos;
import com.marketplace.auth.rol.service.PermisosEfectivosService;
import com.marketplace.auth.sesion.entity.TokenRefresco;
import com.marketplace.auth.sesion.repository.TokenRefrescoRepository;
import com.marketplace.auth.sesion.service.EmisorTokens;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Escenarios de SPEC-11 de punta a punta: HTTP real, tokens RS256 reales y el
 * esquema de las migraciones Flyway (V1 a V100) en PostgreSQL.
 *
 * <p>Cada prueba corre en una transacción que se revierte al terminar, así todas
 * parten de los datos semilla de V100 (un solo ADMIN_SISTEMA).
 */
@SpringBootTest(properties = {
        "spring.flyway.enabled=true",
        "spring.flyway.clean-disabled=false",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class Spec11IntegracionTest {

    private static final UUID ADMIN = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID VENDEDOR = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CLIENTE = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final List<String> PERMISOS_ADMIN = List.of("atributos.editar", "auditoria.ver",
            "cuenta.bloquear", "rol.asignar", "usuario.crear", "usuario.desactivar", "usuario.reactivar",
            "usuario.ver");

    /** Base limpia en cada arranque del contexto: el mismo esquema que en producción. */
    @TestConfiguration
    static class BaseLimpia {
        @Bean
        FlywayMigrationStrategy limpiarYMigrar() {
            return (Flyway flyway) -> {
                flyway.clean();
                flyway.migrate();
            };
        }
    }

    @Autowired
    private MockMvc mvc;
    @Autowired
    private EmisorTokens emisorTokens;
    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private PermisosEfectivosService permisosEfectivosService;
    @Autowired
    private UsuarioRolRepository usuarioRolRepository;
    @Autowired
    private TokenRefrescoRepository tokenRefrescoRepository;
    @Autowired
    private AuditoriaSeguridadRepository auditoriaRepository;
    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("ESC-11.1 Asignar un rol actualiza los roles, revoca las sesiones, responde 200 y publica el evento")
    void esc11_1_asignacionExitosa() throws Exception {
        TokenRefresco sesion = guardarSesion(VENDEDOR);

        mvc.perform(post("/api/v1/usuarios/{id}/roles", VENDEDOR)
                        .header("Authorization", bearer(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\":\"GESTOR_DESPACHO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VENDEDOR.toString()))
                .andExpect(jsonPath("$.roles[0]").value("GESTOR_DESPACHO"))
                .andExpect(jsonPath("$.roles[1]").value("VENDEDOR"));

        assertThat(usuarioRolRepository.findRolesByUsuarioId(VENDEDOR))
                .containsExactlyInAnyOrder(CodigoRol.VENDEDOR, CodigoRol.GESTOR_DESPACHO);
        assertThat(tokenRefrescoRepository.findById(sesion.getJti())).get()
                .extracting(TokenRefresco::isRevocado).isEqualTo(true);
        assertThat(eventos()).singleElement().satisfies(evento -> {
            assertThat(evento.getEstado()).isEqualTo("PENDIENTE");
            JsonNode sobre = json(evento.getPayload());
            assertThat(sobre.get("tipo").asText()).isEqualTo("usuario.roles_cambiados");
            assertThat(sobre.get("usuarioId").asText()).isEqualTo(VENDEDOR.toString());
            assertThat(sobre.get("datos").get("roles").toString()).isEqualTo("[\"GESTOR_DESPACHO\",\"VENDEDOR\"]");
        });
        assertThat(auditoriaRepository.findByObjetivoUsuarioIdOrderByFechaDesc(VENDEDOR))
                .singleElement().satisfies(registro -> {
                    assertThat(registro.getAccion()).isEqualTo("ROL_ASIGNADO");
                    assertThat(registro.getActorId()).isEqualTo(ADMIN.toString());
                    assertThat(registro.getDetalle()).contains("GESTOR_DESPACHO");
                });
    }

    @Test
    @DisplayName("ESC-11.2 Un rol fuera del catálogo responde 400 VALIDACION y no toca la base")
    void esc11_2_rolInexistente() throws Exception {
        for (String invalido : List.of("BUYER", "SUPER_ADMIN", "admin_sistema")) {
            mvc.perform(post("/api/v1/usuarios/{id}/roles", VENDEDOR)
                            .header("Authorization", bearer(ADMIN))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"rol\":\"" + invalido + "\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDACION"))
                    .andExpect(jsonPath("$.errores[0].campo").value("rol"));
        }
        assertThat(usuarioRolRepository.findRolesByUsuarioId(VENDEDOR)).containsExactly(CodigoRol.VENDEDOR);
        assertThat(eventos()).isEmpty();
    }

    @Test
    @DisplayName("ESC-11.3 Un ADMIN_VENTAS recibe 403 SCOPE_INSUFICIENTE y el intento queda auditado")
    void esc11_3_usuarioNoAutorizado() throws Exception {
        usuarioRolRepository.save(new UsuarioRol(CLIENTE, CodigoRol.ADMIN_VENTAS));

        mvc.perform(post("/api/v1/usuarios/{id}/roles", VENDEDOR)
                        .header("Authorization", bearer(CLIENTE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\":\"ADMIN_SISTEMA\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("SCOPE_INSUFICIENTE"));

        assertThat(usuarioRolRepository.findRolesByUsuarioId(VENDEDOR)).containsExactly(CodigoRol.VENDEDOR);
        assertThat(auditoriaRepository.findByAccionOrderByFechaDesc("ACCESO_DENEGADO"))
                .singleElement().satisfies(registro -> {
                    assertThat(registro.getActorId()).isEqualTo(CLIENTE.toString());
                    assertThat(registro.getResultado()).isEqualTo(AuditoriaSeguridad.Resultado.FALLO);
                });
    }

    @Test
    @DisplayName("Sin token responde 401 TOKEN_INVALIDO")
    void sinToken() throws Exception {
        mvc.perform(delete("/api/v1/usuarios/{id}/roles/VENDEDOR", VENDEDOR))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("TOKEN_INVALIDO"));
    }

    @Test
    @DisplayName("ESC-11.4 ADMIN_SISTEMA + VENDEDOR: diez permisos sin duplicados en el token")
    void esc11_4_permisosEfectivos() {
        usuarioRolRepository.save(new UsuarioRol(ADMIN, CodigoRol.VENDEDOR));

        Jwt token = jwtDecoder.decode(emisorTokens.emitirAcceso(ADMIN, "admin@ejemplo.com").valor());

        assertThat(token.getClaimAsStringList("roles")).containsExactly("ADMIN_SISTEMA", "VENDEDOR");
        assertThat(token.getClaimAsStringList("permisos"))
                .hasSize(10)
                .doesNotHaveDuplicates()
                .containsAll(PERMISOS_ADMIN)
                .contains("cliente.registrar", "cliente.buscar");
    }

    @Test
    @DisplayName("ESC-11.5 El único ADMIN_SISTEMA no puede quitarse su rol: 422 ADMINISTRADOR_PROTEGIDO")
    void esc11_5_ultimoAdministrador() throws Exception {
        mvc.perform(delete("/api/v1/usuarios/{id}/roles/ADMIN_SISTEMA", ADMIN)
                        .header("Authorization", bearer(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ADMINISTRADOR_PROTEGIDO"))
                .andExpect(jsonPath("$.detail").value("No se puede eliminar el único administrador del sistema"));

        assertThat(usuarioRolRepository.findRolesByUsuarioId(ADMIN)).containsExactly(CodigoRol.ADMIN_SISTEMA);
        assertThat(eventos()).isEmpty();
    }

    @Test
    @DisplayName("RF-11.7 Con dos administradores, nadie se quita su propio rol, pero sí se lo quita al otro")
    void rf11_7_autoRevocacion() throws Exception {
        usuarioRolRepository.save(new UsuarioRol(VENDEDOR, CodigoRol.ADMIN_SISTEMA));

        mvc.perform(delete("/api/v1/usuarios/{id}/roles/ADMIN_SISTEMA", ADMIN)
                        .header("Authorization", bearer(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ADMINISTRADOR_PROTEGIDO"));

        mvc.perform(delete("/api/v1/usuarios/{id}/roles/ADMIN_SISTEMA", VENDEDOR)
                        .header("Authorization", bearer(ADMIN)))
                .andExpect(status().isNoContent());
        assertThat(usuarioRolRepository.findRolesByUsuarioId(VENDEDOR)).containsExactly(CodigoRol.VENDEDOR);
    }

    @Test
    @DisplayName("ESC-11.6 Revocar un rol: conserva el resto, revoca sesiones, audita y publica la lista completa")
    void esc11_6_revocacion() throws Exception {
        usuarioRolRepository.save(new UsuarioRol(VENDEDOR, CodigoRol.GESTOR_DESPACHO));
        TokenRefresco sesion = guardarSesion(VENDEDOR);

        mvc.perform(delete("/api/v1/usuarios/{id}/roles/GESTOR_DESPACHO", VENDEDOR)
                        .header("Authorization", bearer(ADMIN)))
                .andExpect(status().isNoContent());

        assertThat(usuarioRolRepository.findRolesByUsuarioId(VENDEDOR)).containsExactly(CodigoRol.VENDEDOR);
        assertThat(tokenRefrescoRepository.findById(sesion.getJti())).get()
                .extracting(TokenRefresco::isRevocado).isEqualTo(true);
        assertThat(auditoriaRepository.findByAccionOrderByFechaDesc("ROL_REVOCADO")).hasSize(1);
        assertThat(eventos()).singleElement()
                .satisfies(evento -> assertThat(json(evento.getPayload()).get("datos").get("roles").toString())
                        .isEqualTo("[\"VENDEDOR\"]"));
    }

    @Test
    @DisplayName("ESC-11.7 El token de un ADMIN_SISTEMA lleva los ocho permisos del módulo, con punto")
    void esc11_7_permisosDelAdministrador() {
        Jwt token = jwtDecoder.decode(emisorTokens.emitirAcceso(ADMIN, "admin@ejemplo.com").valor());

        assertThat(token.getClaimAsStringList("permisos")).containsExactlyElementsOf(PERMISOS_ADMIN);
        assertThat(token.getClaimAsStringList("permisos")).allMatch(p -> p.matches("^[a-z_]+\\.[a-z_]+$"));
        assertThat(token.getClaimAsString("tipo")).isEqualTo("acceso");
    }

    @Test
    @DisplayName("Asignar a una cuenta que no existe responde 404 NO_ENCONTRADO")
    void cuentaInexistente() throws Exception {
        mvc.perform(post("/api/v1/usuarios/{id}/roles", UUID.randomUUID())
                        .header("Authorization", bearer(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\":\"VENDEDOR\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NO_ENCONTRADO"));
    }

    @Test
    @DisplayName("Asignar un rol que ya tiene no revoca sesiones ni publica eventos")
    void asignacionIdempotente() throws Exception {
        TokenRefresco sesion = guardarSesion(VENDEDOR);

        mvc.perform(post("/api/v1/usuarios/{id}/roles", VENDEDOR)
                        .header("Authorization", bearer(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\":\"VENDEDOR\"}"))
                .andExpect(status().isOk());

        assertThat(tokenRefrescoRepository.findById(sesion.getJti())).get()
                .extracting(TokenRefresco::isRevocado).isEqualTo(false);
        assertThat(eventos()).isEmpty();
    }

    @Test
    @DisplayName("SPEC-18 GET /roles con roles:leer devuelve los seis roles con sus permisos")
    void catalogoDeRoles() throws Exception {
        String servicio = "Bearer " + emisorTokens
                .emitirServicio("modulo-ventas", List.of("roles:leer"), List.of("api-seguridad")).valor();

        mvc.perform(get("/api/v1/roles").header("Authorization", servicio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles.length()").value(6))
                .andExpect(jsonPath("$.roles[5].codigo").value("ADMIN_SISTEMA"))
                .andExpect(jsonPath("$.roles[5].permisos.length()").value(8))
                .andExpect(jsonPath("$.roles[1].permisos.length()").value(2));

        mvc.perform(get("/api/v1/permisos").param("modulo", "seguridad").header("Authorization", servicio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permisos.length()").value(10));
    }

    @Test
    @DisplayName("SPEC-18 Un módulo sin el scope roles:leer recibe 403")
    void catalogoSinScope() throws Exception {
        String servicio = "Bearer " + emisorTokens
                .emitirServicio("modulo-x", List.of("usuarios:leer"), List.of("api-seguridad")).valor();

        mvc.perform(get("/api/v1/roles").header("Authorization", servicio))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RNF: el cálculo de permisos no suma más de 15 ms y el token no pasa de 4 KB")
    void requisitosNoFuncionales() {
        usuarioRolRepository.save(new UsuarioRol(ADMIN, CodigoRol.VENDEDOR));
        usuarioRolRepository.flush();
        for (int i = 0; i < 20; i++) {
            permisosEfectivosService.calcular(ADMIN);
        }

        int repeticiones = 100;
        long inicio = System.nanoTime();
        PermisosEfectivos efectivos = null;
        for (int i = 0; i < repeticiones; i++) {
            efectivos = permisosEfectivosService.calcular(ADMIN);
        }
        double msPromedio = (System.nanoTime() - inicio) / 1_000_000.0 / repeticiones;

        assertThat(efectivos.permisos()).hasSize(10);
        assertThat(msPromedio).isLessThan(15.0);
        assertThat(emisorTokens.emitirAcceso(ADMIN, "admin@ejemplo.com").valor().length()).isLessThan(4096);
    }

    // ── Ayudas ─────────────────────────────────────────────────

    private String bearer(UUID usuarioId) {
        return "Bearer " + emisorTokens.emitirAcceso(usuarioId, "prueba@ejemplo.com").valor();
    }

    private JsonNode json(String texto) {
        try {
            return objectMapper.readTree(texto);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Outbox> eventos() {
        return outboxRepository.findByTopicoOrderByCreadoEnAsc("usuario.roles_cambiados");
    }

    private TokenRefresco guardarSesion(UUID usuarioId) {
        TokenRefresco token = new TokenRefresco();
        token.setJti(UUID.randomUUID());
        token.setUsuarioId(usuarioId);
        token.setFamiliaId(UUID.randomUUID());
        token.setHashToken("hash-" + UUID.randomUUID());
        token.setRevocado(false);
        token.setUsado(false);
        token.setExpiracion(OffsetDateTime.now().plusDays(7));
        token.setCreadoEn(OffsetDateTime.now());
        return tokenRefrescoRepository.saveAndFlush(token);
    }
}
