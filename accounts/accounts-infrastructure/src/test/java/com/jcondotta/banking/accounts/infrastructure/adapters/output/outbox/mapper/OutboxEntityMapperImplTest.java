package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.mapper;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.shard.OutboxShardResolver;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.exceptions.OutboxSerializationException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.mapper.OutboxEntityMapperImpl;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEntityMapperImplTest {

  private static final String AGGREGATE_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String EVENT_TYPE = "BankAccountOpened";
  private static final String SERIALIZED_PAYLOAD = "{\"event\":\"test\"}";

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private OutboxShardResolver shardResolver;

  @Mock
  private AggregateId<?> aggregateId;

  @Mock
  private IntegrationEvent<?> integrationEvent;

  @InjectMocks
  private OutboxEntityMapperImpl mapper;

  private IntegrationEventMetadata metadata;

  @BeforeEach
  void setUp() {
    metadata = IntegrationEventMetadataFixture.create();

    when(aggregateId.asString()).thenReturn(AGGREGATE_ID);
    when(integrationEvent.metadata()).thenReturn(metadata);
    when(integrationEvent.eventType()).thenReturn(EVENT_TYPE);
    when(shardResolver.resolve(aggregateId)).thenReturn(0);
  }

  @Test
  void shouldMapToOutboxEntity_whenValidInput() {
    when(objectMapper.writeValueAsString(integrationEvent)).thenReturn(SERIALIZED_PAYLOAD);

    OutboxEntity outboxEntity = mapper.toOutboxEntity(aggregateId, integrationEvent);

    assertThat(outboxEntity.getAggregateId()).isEqualTo(AGGREGATE_ID);
    assertThat(outboxEntity.getEventId()).isEqualTo(metadata.eventId());
    assertThat(outboxEntity.getEventType()).isEqualTo(EVENT_TYPE);
    assertThat(outboxEntity.getPayload()).isEqualTo(SERIALIZED_PAYLOAD);
    assertThat(outboxEntity.getCreatedAt()).isNotNull();
    assertThat(outboxEntity.getPartitionKey()).isNotBlank();
    assertThat(outboxEntity.getSortKey()).isNotBlank();
    assertThat(outboxEntity.getGsi1pk()).isNotBlank();
    assertThat(outboxEntity.getGsi1sk()).isNotBlank();

    verify(objectMapper).writeValueAsString(integrationEvent);
  }

  @Test
  void shouldThrowOutboxSerializationException_whenSerializationFails() {
    when(objectMapper.writeValueAsString(integrationEvent))
      .thenThrow(new JacksonException("serialization failed") {});

    assertThatThrownBy(() -> mapper.toOutboxEntity(aggregateId, integrationEvent))
      .isInstanceOf(OutboxSerializationException.class)
      .hasMessageContaining(integrationEvent.getClass().getSimpleName());

    verify(objectMapper).writeValueAsString(integrationEvent);
  }
}