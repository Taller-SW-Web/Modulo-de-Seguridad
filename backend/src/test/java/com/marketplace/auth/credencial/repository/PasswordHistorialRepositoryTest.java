package com.marketplace.auth.credencial.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.credencial.entity.Credencial;
import com.marketplace.auth.credencial.entity.PasswordHistorial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class PasswordHistorialRepositoryTest {

    @Autowired
    private PasswordHistorialRepository repository;

    @Autowired
    private CredencialRepository credencialRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deberiaDevolverSoloLasCincoContrasenasMasRecientesEnOrden() {
        UUID usuarioId = UUID.randomUUID();
        insertarUsuario(usuarioId);

        Credencial credencial = new Credencial();
        credencial.setUsuarioId(usuarioId);
        credencial.setPasswordHash("hash-actual");
        credencial.setAlgoritmo("ARGON2ID");
        credencial.setCreadoEn(OffsetDateTime.now());
        credencial.setActualizadoEn(OffsetDateTime.now());
        UUID credencialId = credencialRepository.saveAndFlush(credencial).getId();

        OffsetDateTime base = OffsetDateTime.now();
        for (int antiguedadMinutos = 1; antiguedadMinutos <= 7; antiguedadMinutos++) {
            PasswordHistorial registro = new PasswordHistorial();
            registro.setCredencialId(credencialId);
            registro.setPasswordHash("hash-anterior-" + antiguedadMinutos);
            registro.setCreadoEn(base.minusMinutes(antiguedadMinutos));
            repository.save(registro);
        }
        repository.flush();

        List<PasswordHistorial> ultimasCinco =
                repository.findTop5ByCredencialIdOrderByCreadoEnDesc(credencialId);

        assertThat(ultimasCinco)
                .hasSize(5)
            .extracting((@NonNull PasswordHistorial registro) -> registro.getPasswordHash())
                .containsExactly(
                        "hash-anterior-1",
                        "hash-anterior-2",
                        "hash-anterior-3",
                        "hash-anterior-4",
                        "hash-anterior-5");
    }

    @Test
    void deberiaDevolverListaVaciaSiNoHayHistorial() {
        assertThat(repository.findTop5ByCredencialIdOrderByCreadoEnDesc(UUID.randomUUID())).isEmpty();
    }

    private void insertarUsuario(UUID usuarioId) {
        jdbcTemplate.update(
                "INSERT INTO usuario (id, correo, nombres, apellidos, estado) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVO')",
                usuarioId, usuarioId + "@example.com", "Usuario", "Prueba");
    }
}