package com.marketplace.auth.integracion.repository;

import com.marketplace.auth.integracion.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Outbox transaccional: los eventos se guardan en la misma transacción que el
 * cambio y un despachador los publica en RabbitMQ después del commit.
 */
@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    List<Outbox> findByEstadoOrderByCreadoEnAsc(String estado);

    List<Outbox> findByTopicoOrderByCreadoEnAsc(String topico);
}
