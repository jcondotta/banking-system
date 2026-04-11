package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.mapper;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.shard.OutboxShardResolver;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.exceptions.OutboxSerializationException;
import com.jcondotta.domain.identity.AggregateId;
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

  public OutboxEntity toOutboxEntity(AggregateId<?> aggregateId, IntegrationEvent<?> integrationEvent) {
    var metadata = integrationEvent.metadata();

    var outboxKey = OutboxKey.of(aggregateId, metadata.eventId());
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
      .eventId(metadata.eventId())
      .eventType(integrationEvent.eventType())
      .payload(serialize(integrationEvent))
      .createdAt(now)
      .build();
  }

  private String serialize(IntegrationEvent<?> event) {
    try {
      return objectMapper.writeValueAsString(event);
    }
    catch (JacksonException e) {
      throw new OutboxSerializationException(event.getClass(), e);
    }
  }
}
