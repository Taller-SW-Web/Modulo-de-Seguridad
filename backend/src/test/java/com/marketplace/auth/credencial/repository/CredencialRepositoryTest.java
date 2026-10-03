package com.marketplace.auth.credencial.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.credencial.entity.Credencial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class CredencialRepositoryTest {

    @Autowired
    private CredencialRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deberiaGuardarYBuscarPorUsuarioId() {
        UUID usuarioId = UUID.randomUUID();
        insertarUsuario(usuarioId);

        Credencial credencial = new Credencial();
        credencial.setUsuarioId(usuarioId);
        credencial.setPasswordHash("hash-seguro");
        credencial.setAlgoritmo("ARGON2ID");
        credencial.setRequiereCambio(false);
        credencial.setIntentosFallidos(0);
        credencial.setCreadoEn(OffsetDateTime.now());
        credencial.setActualizadoEn(OffsetDateTime.now());

        repository.saveAndFlush(credencial);
        Optional<Credencial> encontrada = repository.findByUsuarioId(usuarioId);

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getPasswordHash()).isEqualTo("hash-seguro");
        assertThat(encontrada.get().getAlgoritmo()).isEqualTo("ARGON2ID");
    }

    private void insertarUsuario(UUID usuarioId) {
        jdbcTemplate.update(
                "INSERT INTO usuario (id, correo, nombres, apellidos, estado) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVO')",
                usuarioId, usuarioId + "@example.com", "Usuario", "Prueba");
    }
}