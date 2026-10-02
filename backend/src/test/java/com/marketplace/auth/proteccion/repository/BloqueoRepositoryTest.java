package com.marketplace.auth.proteccion.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.proteccion.entity.Bloqueo;
import com.marketplace.auth.proteccion.entity.TipoBloqueo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas del repositorio Bloqueo.
 * Usa PostgreSQL real vía Testcontainers (no H2).
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class BloqueoRepositoryTest {

    @Autowired
    private BloqueoRepository repository;

    @Test
    void deberiaGuardarBloqueoManual() {
        // Dado
        UUID usuarioId = UUID.randomUUID();
        Bloqueo bloqueo = new Bloqueo();
        bloqueo.setUsuarioId(usuarioId);
        bloqueo.setTipo(TipoBloqueo.MANUAL);
        bloqueo.setMotivo("Bloqueo por administrador");
        bloqueo.setBloqueadoHasta(null);
        bloqueo.setCreadoEn(OffsetDateTime.now());

        // Cuando
        Bloqueo guardado = repository.save(bloqueo);
        Optional<Bloqueo> encontrado = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTipo()).isEqualTo(TipoBloqueo.MANUAL);
        assertThat(encontrado.get().getMotivo()).isEqualTo("Bloqueo por administrador");
        assertThat(encontrado.get().getBloqueadoHasta()).isNull();
    }

    @Test
    void deberiaGuardarBloqueoAutomatico() {
        // Dado
        UUID usuarioId = UUID.randomUUID();
        Bloqueo bloqueo = new Bloqueo();
        bloqueo.setUsuarioId(usuarioId);
        bloqueo.setTipo(TipoBloqueo.AUTOMATICO);
        bloqueo.setMotivo(null);
        bloqueo.setBloqueadoHasta(OffsetDateTime.now().plusMinutes(5));
        bloqueo.setCreadoEn(OffsetDateTime.now());

        // Cuando
        repository.save(bloqueo);
        Optional<Bloqueo> encontrado = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTipo()).isEqualTo(TipoBloqueo.AUTOMATICO);
        assertThat(encontrado.get().getMotivo()).isNull();
        assertThat(encontrado.get().getBloqueadoHasta()).isNotNull();
    }

    @Test
    void deberiaRetornarVacioSiNoExisteBloqueo() {
        // Cuando
        Optional<Bloqueo> encontrado = repository.findByUsuarioId(UUID.randomUUID());

        // Entonces
        assertThat(encontrado).isEmpty();
    }
}
