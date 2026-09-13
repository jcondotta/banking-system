package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.mapper;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxKey;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxSerializationException;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.outbox.shard.OutboxShardResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEntityMapperImpl implements OutboxEntityMapper<OutboxEntity> {

  private final ObjectMapper objectMapper;
  private final OutboxShardResolver shardResolver;

  @Override
  public OutboxEntity toOutboxEntity(EventPublication publication) {
    var envelope = publication.envelope();
    var routing = publication.routing();
    var outboxKey = OutboxKey.of(envelope.aggregateId(), envelope.eventId());
    var now = Instant.now();

    var shard = shardResolver.resolve(envelope.aggregateId());
    var gsi1pk = "OUTBOX#" + shard;

    return OutboxEntity.builder()
      .partitionKey(outboxKey.partitionKey())
      .sortKey(outboxKey.sortKey())
      .gsi1pk(gsi1pk)
      .gsi1sk(now.toString())
      .shard(shard)
      .nextAttemptAt(now)
      .aggregateId(envelope.aggregateId())
      .messageKey(routing.key())
      .eventId(UUID.fromString(envelope.eventId()))
      .correlationId(envelope.correlationId())
      .eventType(envelope.eventType())
      .destination(routing.destination())
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
