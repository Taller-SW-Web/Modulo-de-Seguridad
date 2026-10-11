package com.marketplace.auth.usuario.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.usuario.entity.EstadoCuentaEfectivo;
import com.marketplace.auth.usuario.entity.UsuarioEstadoEfectivo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas del repositorio UsuarioEstadoEfectivo (vista).
 * Usa PostgreSQL real vía Testcontainers (no H2).
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UsuarioEstadoEfectivoRepositoryTest {

    @Autowired
    private UsuarioEstadoEfectivoRepository repository;

    @Test
    void deberiaRetornarEstadoActivoCuandoNoHayBloqueo() {
        // Dado un usuario ACTIVO sin bloqueo (creado en data-test.sql)
        UUID usuarioId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // Cuando
        Optional<UsuarioEstadoEfectivo> estado = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(estado).isPresent();
        assertThat(estado.get().getEstado()).isEqualTo(EstadoCuentaEfectivo.ACTIVO);
    }

    @Test
    void deberiaRetornarEstadoBloqueadoCuandoHayBloqueoVigente() {
        // Dado un usuario ACTIVO con bloqueo vigente (creado en data-test.sql)
        UUID usuarioId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // Cuando
        Optional<UsuarioEstadoEfectivo> estado = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(estado).isPresent();
        assertThat(estado.get().getEstado()).isEqualTo(EstadoCuentaEfectivo.BLOQUEADO);
    }

    @Test
    void deberiaRetornarEstadoBloqueadoCuandoBloqueoManualSinVencimiento() {
        // Dado un usuario ACTIVO con bloqueo manual (creado en data-test.sql)
        UUID usuarioId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // Cuando
        Optional<UsuarioEstadoEfectivo> estado = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(estado).isPresent();
        assertThat(estado.get().getEstado()).isEqualTo(EstadoCuentaEfectivo.BLOQUEADO);
    }

    @Test
    void deberiaRetornarEstadoActivoCuandoBloqueoEstaVencido() {
        // Dado un usuario ACTIVO con bloqueo vencido (creado en data-test.sql)
        UUID usuarioId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        // Cuando
        Optional<UsuarioEstadoEfectivo> estado = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(estado).isPresent();
        assertThat(estado.get().getEstado()).isEqualTo(EstadoCuentaEfectivo.ACTIVO);
    }

    @Test
    void deberiaRetornarVacioSiUsuarioNoExiste() {
        // Cuando
        Optional<UsuarioEstadoEfectivo> estado = repository.findByUsuarioId(UUID.randomUUID());

        // Entonces
        assertThat(estado).isEmpty();
    }
}
