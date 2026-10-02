package com.marketplace.auth.token.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.token.entity.TokenUnUso;
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
class TokenUnUsoRepositoryTest {

    @Autowired
    private TokenUnUsoRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deberiaGuardarYBuscarPorHash() {
        UUID usuarioId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO usuario (id, correo, nombres, apellidos, estado) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVO')",
                usuarioId, usuarioId + "@example.com", "Usuario", "Prueba");

        TokenUnUso token = new TokenUnUso();
        token.setUsuarioId(usuarioId);
        token.setHashToken("hash-token-unico");
        token.setMotivo("RECUPERACION_PASSWORD");
        token.setCreadoEn(OffsetDateTime.now());
        token.setExpiraEn(OffsetDateTime.now().plusHours(1));
        token.setUsado(false);

        repository.saveAndFlush(token);
        Optional<TokenUnUso> encontrado = repository.findByHashToken("hash-token-unico");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getUsuarioId()).isEqualTo(usuarioId);
        assertThat(encontrado.get().isUsado()).isFalse();
    }
}