package com.marketplace.auth.proteccion.repository;

import com.marketplace.auth.proteccion.entity.IntentoLogin;
import com.marketplace.auth.proteccion.entity.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Repositorio de intentos de login.
 * Usado por SPEC-14 (bloqueo automático por intentos fallidos).
 */
@Repository
public interface IntentoLoginRepository extends JpaRepository<IntentoLogin, UUID> {

    /**
     * Cuenta los intentos fallidos de un usuario desde una fecha dada.
     * Usado por SPEC-14 para decidir el bloqueo automático.
     * @param usuarioId ID del usuario
     * @param fechaDesde fecha límite inferior (exclusiva)
     * @return número de intentos fallidos
     */
    long countByUsuarioIdAndResultadoAndFechaAfter(
            UUID usuarioId, Resultado resultado, OffsetDateTime fechaDesde);
}
