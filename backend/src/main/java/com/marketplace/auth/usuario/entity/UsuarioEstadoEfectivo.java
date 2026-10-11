package com.marketplace.auth.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Subselect;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

/**
 * Vista usuario_estado_efectivo.
 * Estado que ven la API y los módulos (RF-14.7, RF-15.5).
 * Una cuenta ACTIVO con un bloqueo sin vencer es BLOQUEADO.
 * Las cuentas INACTIVO o PENDIENTE_VERIFICACION nunca se muestran BLOQUEADO.
 *
 * <p>Entidad de solo lectura mapeada a la vista SQL.</p>
 */
@Entity
@Immutable
@Subselect("""
        SELECT
            u.id AS usuario_id,
            CASE
                WHEN u.estado = 'ACTIVO'
                    AND b.usuario_id IS NOT NULL
                    AND (b.bloqueado_hasta IS NULL OR b.bloqueado_hasta > now())
                THEN 'BLOQUEADO'
                ELSE u.estado
            END AS estado
        FROM usuario u
        LEFT JOIN bloqueo b ON b.usuario_id = u.id
        """)
public class UsuarioEstadoEfectivo {

    @Id
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    private EstadoCuentaEfectivo estado;

    // ── Constructores ──────────────────────────────────────────

    public UsuarioEstadoEfectivo() {
    }

    // ── Getters ────────────────────────────────────────────────

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public EstadoCuentaEfectivo getEstado() {
        return estado;
    }
}
