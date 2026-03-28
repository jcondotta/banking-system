package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxStatusKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxKey;
import com.jcondotta.domain.identity.AggregateId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxEntityMapperImpl implements OutboxEntityMapper {

  private final ObjectMapper objectMapper;

  public OutboxEntity toOutboxEntity(AggregateId<?> aggregateId, IntegrationEvent<?> integrationEvent) {
    var metadata = integrationEvent.metadata();

    var outboxKey = OutboxKey.of(
      aggregateId.asString(),
      metadata.eventId().toString()
    );

    var instant = Instant.now();
    var outboxGsi1Key = OutboxStatusKey.pending(
      aggregateId.asString(),
      instant
    );

    return OutboxEntity.builder()
      .partitionKey(outboxKey.partitionKey())
      .sortKey(outboxKey.sortKey())
      .gsi1pk(outboxGsi1Key.partitionKey())
      .gsi1sk(outboxGsi1Key.sortKey())
      .aggregateId(aggregateId.asString())
      .eventId(metadata.eventId())
      .eventType(integrationEvent.eventType())
      .payload(serialize(integrationEvent))
      .createdAt(instant)
      .build();
  }

  private String serialize(IntegrationEvent<?> event) {
    return objectMapper.writeValueAsString(event);
  }
}
