package com.marketplace.auth.credencial.repository;

import com.marketplace.auth.credencial.entity.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/** Repositorio de credenciales de acceso. */
@Repository
public interface CredencialRepository extends JpaRepository<Credencial, UUID> {

    /** Busca la credencial única asociada a un usuario. */
    Optional<Credencial> findByUsuarioId(UUID usuarioId);
}