package com.marketplace.auth.rol.repository;

import com.marketplace.auth.rol.entity.CodigoRol;
import com.marketplace.auth.rol.entity.UsuarioRol;
import com.marketplace.auth.rol.entity.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Roles asignados a cada usuario y cálculo de sus permisos efectivos (SPEC-11).
 */
@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    @Query("SELECT ur.id.rolCodigo FROM UsuarioRol ur WHERE ur.id.usuarioId = :usuarioId")
    List<CodigoRol> findRolesByUsuarioId(@Param("usuarioId") UUID usuarioId);

    /**
     * Permisos efectivos: unión sin duplicados de los permisos de cada rol (RF-11.4).
     * Una sola consulta indexada, para no sumar más de 15 ms a la firma del token.
     */
    @Query(value = """
            SELECT DISTINCT rp.permiso_codigo
            FROM usuario_rol ur
            JOIN rol_permiso rp ON rp.rol_codigo = ur.rol_codigo
            WHERE ur.usuario_id = :usuarioId
            ORDER BY rp.permiso_codigo
            """, nativeQuery = true)
    List<String> findPermisosEfectivos(@Param("usuarioId") UUID usuarioId);

    /**
     * Bloquea las asignaciones de ADMIN_SISTEMA hasta el fin de la transacción.
     * Así dos administradores que se revocan el rol a la vez no dejan el sistema
     * sin ninguno (RF-11.7): el segundo espera y ve el recuento ya actualizado.
     */
    @Query(value = """
            SELECT CAST(usuario_id AS text)
            FROM usuario_rol
            WHERE rol_codigo = 'ADMIN_SISTEMA'
            FOR UPDATE
            """, nativeQuery = true)
    List<String> bloquearAsignacionesAdmin();

    /**
     * Cuántos ADMIN_SISTEMA tienen la cuenta en estado efectivo ACTIVO
     * (sin baja ni bloqueo vigente).
     */
    @Query(value = """
            SELECT count(*)
            FROM usuario_rol ur
            JOIN usuario_estado_efectivo e ON e.usuario_id = ur.usuario_id
            WHERE ur.rol_codigo = 'ADMIN_SISTEMA' AND e.estado = 'ACTIVO'
            """, nativeQuery = true)
    long countAdminsActivos();

    /**
     * Datos de la cuenta que devuelve POST /usuarios/{id}/roles.
     * Lectura directa mientras no exista el servicio de usuario (SPEC-03);
     * cuando exista, esta consulta se reemplaza por una llamada a él.
     */
    @Query(value = """
            SELECT u.id AS id,
                   u.nombres AS nombres,
                   u.apellidos AS apellidos,
                   u.correo AS correo,
                   u.correo_verificado AS correoVerificado,
                   CAST(e.estado AS text) AS estado
            FROM usuario u
            JOIN usuario_estado_efectivo e ON e.usuario_id = u.id
            WHERE u.id = :usuarioId
            """, nativeQuery = true)
    Optional<CuentaResumen> findCuenta(@Param("usuarioId") UUID usuarioId);

    interface CuentaResumen {
        UUID getId();

        String getNombres();

        String getApellidos();

        String getCorreo();

        Boolean getCorreoVerificado();

        String getEstado();
    }
}
