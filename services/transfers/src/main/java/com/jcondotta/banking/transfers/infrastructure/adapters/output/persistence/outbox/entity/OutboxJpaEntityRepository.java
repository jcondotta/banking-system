package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxJpaEntityRepository extends JpaRepository<OutboxJpaEntity, UUID> {

  @Query("""
    SELECT o FROM OutboxJpaEntity o
     WHERE o.shard = :shard
       AND o.nextAttemptAt <= :now
     ORDER BY o.nextAttemptAt ASC
    """)
  List<OutboxJpaEntity> findPendingByShard(
    @Param("shard") int shard,
    @Param("now") Instant now,
    Pageable pageable
  );

  @Modifying
  @Query("""
    UPDATE OutboxJpaEntity o
       SET o.nextAttemptAt = :newVisibility,
           o.attemptCount  = o.attemptCount + 1
     WHERE o.eventId = :eventId
       AND o.nextAttemptAt <= :now
    """)
  int tryClaimById(
    @Param("eventId") UUID eventId,
    @Param("now") Instant now,
    @Param("newVisibility") Instant newVisibility
  );

  @Modifying
  @Query("""
    DELETE FROM OutboxJpaEntity o
     WHERE o.eventId = :eventId
       AND o.nextAttemptAt = :claimedAt
    """)
  int deleteByEventIdAndNextAttemptAt(
    @Param("eventId") UUID eventId,
    @Param("claimedAt") Instant claimedAt
  );
}
