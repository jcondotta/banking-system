package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity;

import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_transfer_outbox")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OutboxJpaEntity implements OutboxRecord {

  @Id
  @Column(name = "event_id", nullable = false, updatable = false)
  private UUID eventId;

  @Column(name = "correlation_id", nullable = false, updatable = false)
  private UUID correlationId;

  @Column(name = "aggregate_id", nullable = false, updatable = false, length = 36)
  private String aggregateId;

  @Column(name = "message_key", nullable = false, updatable = false, length = 255)
  private String messageKey;

  @Column(name = "event_type", nullable = false, updatable = false, length = 100)
  private String eventType;

  @Column(name = "destination", nullable = false, updatable = false, length = 255)
  private String destination;

  @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(name = "shard", nullable = false)
  private int shard;

  @Column(name = "attempt_count", nullable = false)
  private int attemptCount;

  @Column(name = "next_attempt_at", nullable = false)
  private Instant nextAttemptAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;
}
