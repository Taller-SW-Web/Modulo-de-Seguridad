package com.marketplace.auth.otp.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.otp.entity.Otp;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class OtpRepositoryTest {

    @Autowired
    private OtpRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deberiaBuscarOtpNoUsadosPorUsuario() {
        UUID usuarioId = UUID.randomUUID();
        UUID otroUsuarioId = UUID.randomUUID();
        insertarUsuario(usuarioId);
        insertarUsuario(otroUsuarioId);

        repository.save(crearOtp(usuarioId, "hash-pendiente", false));
        repository.save(crearOtp(usuarioId, "hash-usado", true));
        repository.save(crearOtp(otroUsuarioId, "hash-otro-usuario", false));
        repository.flush();

        assertThat(repository.findByUsuarioIdAndUsadoFalse(usuarioId))
                .extracting((@NonNull Otp otp) -> otp.getHashCodigo())
                .containsExactly("hash-pendiente");
    }

    private @NonNull Otp crearOtp(UUID usuarioId, String hashCodigo, boolean usado) {
        Otp otp = new Otp();
        otp.setUsuarioId(usuarioId);
        otp.setHashCodigo(hashCodigo);
        otp.setDestino("usuario@example.com");
        otp.setCanal("EMAIL");
        otp.setMotivo("RECUPERACION_PASSWORD");
        otp.setCreadoEn(OffsetDateTime.now());
        otp.setExpiraEn(OffsetDateTime.now().plusMinutes(10));
        otp.setUsado(usado);
        otp.setIntentos(0);
        return otp;
    }

    private void insertarUsuario(UUID usuarioId) {
        jdbcTemplate.update(
                "INSERT INTO usuario (id, correo, nombres, apellidos, estado) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVO')",
                usuarioId, usuarioId + "@example.com", "Usuario", "Prueba");
    }
}