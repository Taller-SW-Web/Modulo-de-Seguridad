package com.marketplace.auth.mfa.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.mfa.entity.DesafioMfa;
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
 * Pruebas del repositorio DesafioMfa.
 * Usa PostgreSQL real vía Testcontainers (no H2).
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class DesafioMfaRepositoryTest {

    @Autowired
    private DesafioMfaRepository repository;

    @Test
    void deberiaGuardarYBuscarPorHashToken() {
        // Dado
        DesafioMfa desafio = new DesafioMfa();
        desafio.setId(UUID.randomUUID());
        desafio.setUsuarioId(UUID.randomUUID());
        desafio.setHashToken("hash-desafio-123");
        desafio.setExpiracion(OffsetDateTime.now().plusMinutes(5));
        desafio.setConsumido(false);
        desafio.setCreadoEn(OffsetDateTime.now());

        // Cuando
        DesafioMfa guardado = repository.save(desafio);
        Optional<DesafioMfa> encontrado = repository.findByHashToken("hash-desafio-123");

        // Entonces
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(guardado.getId());
        assertThat(encontrado.get().isConsumido()).isFalse();
    }

    @Test
    void deberiaRetornarVacioSiHashNoExiste() {
        // Cuando
        Optional<DesafioMfa> encontrado = repository.findByHashToken("hash-inexistente");

        // Entonces
        assertThat(encontrado).isEmpty();
    }
}
