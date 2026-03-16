package com.bookingapp.adapter.out.persistence.outbox;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop100ByStatusInOrderByCreatedAtAsc(Collection<OutboxStatus> statuses);
}