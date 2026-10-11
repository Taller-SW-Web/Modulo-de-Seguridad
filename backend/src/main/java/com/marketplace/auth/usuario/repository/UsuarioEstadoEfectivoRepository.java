package com.marketplace.auth.usuario.repository;

import com.marketplace.auth.usuario.entity.UsuarioEstadoEfectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de la vista usuario_estado_efectivo.
 * Estado que ven la API y los módulos (RF-14.7, RF-15.5).
 * Solo lectura: la vista se calcula desde usuario + bloqueo.
 */
@Repository
public interface UsuarioEstadoEfectivoRepository extends JpaRepository<UsuarioEstadoEfectivo, UUID> {

    /**
     * Busca el estado efectivo de un usuario.
     * @param usuarioId ID del usuario
     * @return estado efectivo si el usuario existe
     */
    Optional<UsuarioEstadoEfectivo> findByUsuarioId(UUID usuarioId);
}
