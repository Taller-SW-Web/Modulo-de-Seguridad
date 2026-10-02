package com.marketplace.auth.proteccion.repository;

import com.marketplace.auth.proteccion.entity.Bloqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de bloqueos de cuenta.
 * Usado por SPEC-14 (bloqueo automático) y SPEC-15 (bloqueo manual).
 */
@Repository
public interface BloqueoRepository extends JpaRepository<Bloqueo, UUID> {

    /**
     * Busca el bloqueo vigente de un usuario.
     * @param usuarioId ID del usuario
     * @return el bloqueo si existe
     */
    Optional<Bloqueo> findByUsuarioId(UUID usuarioId);
}
