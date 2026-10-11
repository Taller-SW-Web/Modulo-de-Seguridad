package com.marketplace.auth.credencial.repository;

import com.marketplace.auth.credencial.entity.PasswordHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/** Repositorio del historial de contraseñas. */
@Repository
public interface PasswordHistorialRepository extends JpaRepository<PasswordHistorial, UUID> {

    /** Busca hasta los cinco hashes más recientes de una credencial. */
    List<PasswordHistorial> findTop5ByCredencialIdOrderByCreadoEnDesc(UUID credencialId);
}