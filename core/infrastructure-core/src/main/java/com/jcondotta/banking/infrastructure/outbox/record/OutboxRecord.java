package com.jcondotta.banking.infrastructure.outbox.record;

import java.time.Instant;
import java.util.UUID;

public interface OutboxRecord {

  UUID getEventId();

  UUID getCorrelationId();

  String getAggregateId();

  /**
   * The routing/partition key captured from {@code EventPublication.key()} at write time.
   * May differ from {@code aggregateId} — e.g. recipients keys by bank account ID, not recipient ID.
   */
  String getMessageKey();

  String getEventType();

  /**
   * The Kafka topic (or logical destination) to publish this event to.
   * Populated from {@code EventPublicationFactory.destination()} at write time,
   * ensuring the routing decision made by the factory is honoured at publish time.
   */
  String getDestination();

  String getPayload();

  int getShard();

  int getAttemptCount();

  Instant getNextAttemptAt();
}
