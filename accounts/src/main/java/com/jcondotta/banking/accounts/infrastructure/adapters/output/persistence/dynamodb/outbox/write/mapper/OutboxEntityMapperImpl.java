package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.mapper;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxKey;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxSerializationException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.shard.OutboxShardResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxEntityMapperImpl implements OutboxEntityMapper {

  private final ObjectMapper objectMapper;
  private final OutboxShardResolver shardResolver;

  @Override
  public OutboxEntity toOutboxEntity(DomainEvent<?, ?> event, EventEnvelope envelope) {
    var aggregateId = event.aggregateId();
    var eventId = event.eventId().value();

    var outboxKey = OutboxKey.of(aggregateId, eventId);
    var now = Instant.now();

    var shard = shardResolver.resolve(aggregateId);
    var gsi1pk = "OUTBOX#" + shard;

    return OutboxEntity.builder()
      .partitionKey(outboxKey.partitionKey())
      .sortKey(outboxKey.sortKey())
      .gsi1pk(gsi1pk)
      .gsi1sk(now.toString())
      .shard(shard)
      .nextAttemptAt(now)
      .aggregateId(aggregateId.asString())
      .eventId(eventId)
      .eventType(event.eventType())
      .payload(serialize(envelope))
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
