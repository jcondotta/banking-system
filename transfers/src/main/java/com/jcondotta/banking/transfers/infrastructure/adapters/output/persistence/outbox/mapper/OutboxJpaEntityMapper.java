package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.mapper;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxSerializationException;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.outbox.shard.OutboxShardResolver;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntity;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxJpaEntityMapper implements OutboxEntityMapper<OutboxJpaEntity> {

  private final ObjectMapper objectMapper;
  private final OutboxShardResolver shardResolver;

  @Override
  public OutboxJpaEntity toOutboxEntity(DomainEvent<?, ?> event, EventEnvelope envelope) {
    var aggregateId = event.aggregateId();
    var now = Instant.now();
    var shard = shardResolver.resolve(aggregateId);

    return OutboxJpaEntity.builder()
      .eventId(event.eventId().value())
      .correlationId(envelope.correlationId())
      .aggregateId(aggregateId.asString())
      .messageKey(envelope.messageKey())
      .eventType(event.eventType())
      .destination(envelope.destination())
      .payload(serialize(envelope))
      .shard(shard)
      .attemptCount(0)
      .nextAttemptAt(now)
      .createdAt(now)
      .build();
  }

  private String serialize(EventEnvelope envelope) {
    try {
      return objectMapper.writeValueAsString(envelope);
    }
    catch (JacksonException e) {
      throw new OutboxSerializationException(EventEnvelope.class, e);
    }
  }
}
