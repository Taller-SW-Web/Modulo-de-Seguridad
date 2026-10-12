package com.marketplace.auth.rol.repository;

import com.marketplace.auth.rol.entity.CodigoRol;
import com.marketplace.auth.rol.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Catálogo cerrado de roles (SPEC-11) y su relación con los permisos.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, CodigoRol> {

    /**
     * Pares rol → permiso de rol_permiso, para armar el catálogo de GET /roles.
     */
    @Query(value = """
            SELECT CAST(rol_codigo AS text) AS rol, permiso_codigo AS permiso
            FROM rol_permiso
            ORDER BY permiso_codigo
            """, nativeQuery = true)
    List<RolPermisoFila> findParesRolPermiso();

    interface RolPermisoFila {
        String getRol();

        String getPermiso();
    }
}
