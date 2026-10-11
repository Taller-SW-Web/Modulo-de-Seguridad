package com.marketplace.auth.mfa.repository;

import com.marketplace.auth.mfa.entity.DesafioMfa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de desafíos MFA (challenge tokens).
 * Usado por SPEC-09 (segundo factor en el inicio de sesión).
 */
@Repository
public interface DesafioMfaRepository extends JpaRepository<DesafioMfa, UUID> {

    /**
     * Busca un desafío por su hash de token.
     * @param hashToken hash del token de desafío (único)
     * @return el desafío si existe
     */
    Optional<DesafioMfa> findByHashToken(String hashToken);
}
