package com.marketplace.auth.sesion.repository;

import com.marketplace.auth.sesion.entity.TokenRefresco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de refresh tokens.
 * Usado por SPEC-06 (renovación y cierre de sesión).
 */
@Repository
public interface TokenRefrescoRepository extends JpaRepository<TokenRefresco, UUID> {

    /**
     * Busca un refresh token por su hash.
     * @param hashToken hash del token (único)
     * @return el token si existe
     */
    Optional<TokenRefresco> findByHashToken(String hashToken);

    /**
     * Busca todos los tokens de una familia de sesión.
     * Usado para revocar la familia completa ante reutilización (RF-06.3).
     * @param familiaId ID de la familia de sesión
     * @return tokens de la familia
     */
    List<TokenRefresco> findByFamiliaId(UUID familiaId);

    /**
     * Busca todos los tokens de un usuario.
     * Usado para revocar todas las sesiones de una cuenta.
     * @param usuarioId ID del usuario
     * @return tokens del usuario
     */
    List<TokenRefresco> findByUsuarioId(UUID usuarioId);

    /**
     * Revoca de una vez todas las sesiones vigentes de una cuenta.
     * Usado al cambiar sus roles (RF-11.5), y por baja y bloqueo (SPEC-04, SPEC-15).
     * @param usuarioId ID del usuario
     * @return cuántos tokens se revocaron
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE TokenRefresco t SET t.revocado = true WHERE t.usuarioId = :usuarioId AND t.revocado = false")
    int revocarTodosPorUsuarioId(@Param("usuarioId") UUID usuarioId);
}
