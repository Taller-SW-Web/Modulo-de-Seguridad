package com.marketplace.auth.sesion.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.sesion.entity.TokenRefresco;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas del repositorio TokenRefresco.
 * Usa PostgreSQL real vía Testcontainers (no H2).
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class TokenRefrescoRepositoryTest {

    @Autowired
    private TokenRefrescoRepository repository;

    @Test
    void deberiaGuardarYBuscarPorHashToken() {
        // Dado
        TokenRefresco token = new TokenRefresco();
        token.setJti(UUID.randomUUID());
        token.setUsuarioId(UUID.randomUUID());
        token.setFamiliaId(UUID.randomUUID());
        token.setHashToken("hash-unico-123");
        token.setRevocado(false);
        token.setUsado(false);
        token.setExpiracion(OffsetDateTime.now().plusDays(7));
        token.setCreadoEn(OffsetDateTime.now());

        // Cuando
        TokenRefresco guardado = repository.save(token);
        Optional<TokenRefresco> encontrado = repository.findByHashToken("hash-unico-123");

        // Entonces
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getJti()).isEqualTo(guardado.getJti());
        assertThat(encontrado.get().isRevocado()).isFalse();
        assertThat(encontrado.get().isUsado()).isFalse();
    }

    @Test
    void deberiaBuscarPorFamiliaId() {
        // Dado
        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        TokenRefresco token1 = crearToken(usuarioId, familiaId, "hash-1");
        TokenRefresco token2 = crearToken(usuarioId, familiaId, "hash-2");
        TokenRefresco token3 = crearToken(usuarioId, UUID.randomUUID(), "hash-3");

        repository.save(token1);
        repository.save(token2);
        repository.save(token3);

        // Cuando
        List<TokenRefresco> tokensFamilia = repository.findByFamiliaId(familiaId);

        // Entonces
        assertThat(tokensFamilia).hasSize(2);
        assertThat(tokensFamilia).extracting(TokenRefresco::getHashToken)
                .containsExactlyInAnyOrder("hash-1", "hash-2");
    }

    @Test
    void deberiaBuscarPorUsuarioId() {
        // Dado
        UUID usuarioId = UUID.randomUUID();

        TokenRefresco token1 = crearToken(usuarioId, UUID.randomUUID(), "hash-1");
        TokenRefresco token2 = crearToken(usuarioId, UUID.randomUUID(), "hash-2");
        TokenRefresco token3 = crearToken(UUID.randomUUID(), UUID.randomUUID(), "hash-3");

        repository.save(token1);
        repository.save(token2);
        repository.save(token3);

        // Cuando
        List<TokenRefresco> tokensUsuario = repository.findByUsuarioId(usuarioId);

        // Entonces
        assertThat(tokensUsuario).hasSize(2);
    }

    @Test
    void deberiaRetornarVacioSiHashNoExiste() {
        // Cuando
        Optional<TokenRefresco> encontrado = repository.findByHashToken("hash-inexistente");

        // Entonces
        assertThat(encontrado).isEmpty();
    }

    // ── Helper ──────────────────────────────────────────────────

    private TokenRefresco crearToken(UUID usuarioId, UUID familiaId, String hashToken) {
        TokenRefresco token = new TokenRefresco();
        token.setJti(UUID.randomUUID());
        token.setUsuarioId(usuarioId);
        token.setFamiliaId(familiaId);
        token.setHashToken(hashToken);
        token.setRevocado(false);
        token.setUsado(false);
        token.setExpiracion(OffsetDateTime.now().plusDays(7));
        token.setCreadoEn(OffsetDateTime.now());
        return token;
    }
}
