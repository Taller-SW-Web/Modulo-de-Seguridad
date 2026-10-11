package com.marketplace.auth.token.repository;

import com.marketplace.auth.token.entity.TokenUnUso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/** Repositorio de tokens de un solo uso. */
@Repository
public interface TokenUnUsoRepository extends JpaRepository<TokenUnUso, UUID> {

    /** Busca un token por su hash único. */
    Optional<TokenUnUso> findByHashToken(String hashToken);
}