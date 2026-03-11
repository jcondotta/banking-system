package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxKeyFactory;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.domain.identity.EntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEntityMapper {

  private final ObjectMapper objectMapper;

  public OutboxEntity toOutboxEntity(EntityId<?> entityId, IntegrationEvent<?> integrationEvent) {
    OutboxKey key = OutboxKeyFactory.pending(
      (UUID) entityId.value(),
      integrationEvent.metadata().eventId(),
      integrationEvent.metadata().occurredAt()
    );

    return OutboxEntity.builder()
      .partitionKey(key.partitionKey())
      .sortKey(key.sortKey())
      .gsi1pk(key.gsi1pk())
      .gsi1sk(key.gsi1sk())
      .eventId(integrationEvent.metadata().eventId())
      .aggregateId((UUID) entityId.value())
      .entityType(EntityType.OUTBOX_EVENT)
      .eventType(integrationEvent.metadata().eventType())
      .payload(serialize(integrationEvent))
      .publishedAt(null)
      .build();
  }

  private String serialize(IntegrationEvent<?> event) {
    try {
      return objectMapper.writeValueAsString(event);
    }
    catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize integration event: " + event.getClass().getName(), e);
    }
  }
}
