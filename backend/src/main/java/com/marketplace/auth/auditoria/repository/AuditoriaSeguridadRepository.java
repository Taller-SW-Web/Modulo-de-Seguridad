package com.marketplace.auth.auditoria.repository;

import com.marketplace.auth.auditoria.entity.AuditoriaSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Registro de auditoría, solo-anexado (SPEC-12).
 */
@Repository
public interface AuditoriaSeguridadRepository extends JpaRepository<AuditoriaSeguridad, Long> {

    List<AuditoriaSeguridad> findByObjetivoUsuarioIdOrderByFechaDesc(UUID objetivoUsuarioId);

    List<AuditoriaSeguridad> findByAccionOrderByFechaDesc(String accion);
}
