package com.marketplace.auth.proteccion.repository;

import com.marketplace.auth.TestcontainersConfig;
import com.marketplace.auth.proteccion.entity.IntentoLogin;
import com.marketplace.auth.proteccion.entity.Resultado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas del repositorio IntentoLogin.
 * Usa PostgreSQL real vía Testcontainers (no H2).
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@Sql("/schema-test.sql")
class IntentoLoginRepositoryTest {

    @Autowired
    private IntentoLoginRepository repository;

    @Test
    void deberiaGuardarIntentoFallido() {
        // Dado
        IntentoLogin intento = new IntentoLogin();
        intento.setId(UUID.randomUUID());
        intento.setUsuarioId(UUID.randomUUID());
        intento.setCorreoIntentado("juan@correo.com");
        intento.setResultado(Resultado.FALLO);
        intento.setIp("192.168.1.1");
        intento.setUserAgent("Mozilla/5.0");
        intento.setFecha(OffsetDateTime.now());

        // Cuando
        IntentoLogin guardado = repository.save(intento);

        // Entonces
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getResultado()).isEqualTo(Resultado.FALLO);
    }

    @Test
    void deberiaContarIntentosFallidosRecientes() {
        // Dado
        UUID usuarioId = UUID.randomUUID();
        OffsetDateTime ahora = OffsetDateTime.now();

        repository.save(crearIntento(usuarioId, Resultado.FALLO, ahora.minusMinutes(1)));
        repository.save(crearIntento(usuarioId, Resultado.FALLO, ahora.minusMinutes(2)));
        repository.save(crearIntento(usuarioId, Resultado.EXITO, ahora.minusMinutes(3)));
        repository.save(crearIntento(usuarioId, Resultado.FALLO, ahora.minusMinutes(10)));

        // Cuando
        long count = repository.countByUsuarioIdAndResultadoAndFechaAfter(
                usuarioId, Resultado.FALLO, ahora.minusMinutes(5));

        // Entonces
        assertThat(count).isEqualTo(2);
    }

    @Test
    void deberiaRetornarCeroSiNoHayIntentosFallidos() {
        // Dado
        UUID usuarioId = UUID.randomUUID();

        // Cuando
        long count = repository.countByUsuarioIdAndResultadoAndFechaAfter(
                usuarioId, Resultado.FALLO, OffsetDateTime.now().minusMinutes(5));

        // Entonces
        assertThat(count).isZero();
    }

    // ── Helper ──────────────────────────────────────────────────

    private IntentoLogin crearIntento(UUID usuarioId, Resultado resultado, OffsetDateTime fecha) {
        IntentoLogin intento = new IntentoLogin();
        intento.setId(UUID.randomUUID());
        intento.setUsuarioId(usuarioId);
        intento.setCorreoIntentado("juan@correo.com");
        intento.setResultado(resultado);
        intento.setIp("192.168.1.1");
        intento.setUserAgent("Mozilla/5.0");
        intento.setFecha(fecha);
        return intento;
    }
}
