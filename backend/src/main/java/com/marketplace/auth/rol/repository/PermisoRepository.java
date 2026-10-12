package com.marketplace.auth.rol.repository;

import com.marketplace.auth.rol.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Catálogo de permisos de este módulo (RF-11.9).
 */
@Repository
public interface PermisoRepository extends JpaRepository<Permiso, String> {

    List<Permiso> findAllByOrderByCodigoAsc();

    List<Permiso> findByModuloOrderByCodigoAsc(String modulo);
}
