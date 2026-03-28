package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper.OutboxEntityMapperImpl;
import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEntityMapperImplTest {

//  private static final String AGGREGATE_ID = "123e4567-e89b-12d3-a456-426614174000";
//  private static final UUID EVENT_ID = UUID.randomUUID();
//  private static final Instant OCCURRED_AT = Instant.now();
//  private static final String EVENT_TYPE = "BankAccountOpened";
//  private static final String SERIALIZED_PAYLOAD = "{\"event\":\"test\"}";
//
//  @Mock
//  private ObjectMapper objectMapper;
//
//  @Mock
//  private AggregateId<?> aggregateId;
//
//  @Mock
//  private IntegrationEvent<?> integrationEvent;
//
//  @Mock
//  private IntegrationEventMetadata metadata;
//
//  @InjectMocks
//  private OutboxEntityMapperImpl mapper;
//
//  @BeforeEach
//  void setUp() {
//    when(aggregateId.asString()).thenReturn(AGGREGATE_ID);
//
//    when(integrationEvent.metadata()).thenReturn(metadata);
//    when(integrationEvent.eventType()).thenReturn(EVENT_TYPE);
//
//    when(metadata.eventId()).thenReturn(EVENT_ID);
//    when(metadata.occurredAt()).thenReturn(OCCURRED_AT);
//  }
//
//  @Test
//  void shouldMapToOutboxEntity_whenValidInput() throws Exception {
//    when(objectMapper.writeValueAsString(integrationEvent)).thenReturn(SERIALIZED_PAYLOAD);
//
//    OutboxEntity outboxEntity = mapper.toOutboxEntity(aggregateId, integrationEvent);
//
//    assertThat(outboxEntity).isNotNull();
//    assertThat(outboxEntity.getAggregateId()).isEqualTo(AGGREGATE_ID);
//    assertThat(outboxEntity.getEventId()).isEqualTo(EVENT_ID);
//    assertThat(outboxEntity.getEventType()).isEqualTo(EVENT_TYPE);
//    assertThat(outboxEntity.getPayload()).isEqualTo(SERIALIZED_PAYLOAD);
//    assertThat(outboxEntity.getCreatedAt()).isNotNull();
//
//    assertThat(outboxEntity.getPartitionKey()).isNotBlank();
//    assertThat(outboxEntity.getSortKey()).isNotBlank();
//    assertThat(outboxEntity.getGsi1pk()).isNotBlank();
//    assertThat(outboxEntity.getGsi1sk()).isNotBlank();
//
//    verify(objectMapper).writeValueAsString(integrationEvent);
//  }
//
//  @Test
//  void shouldThrowOutboxSerializationException_whenSerializationFails() throws Exception {
//    when(objectMapper.writeValueAsString(integrationEvent))
//      .thenThrow(new JsonProcessingException("error") {});
//
//    assertThatThrownBy(() -> mapper.toOutboxEntity(aggregateId, integrationEvent))
//      .isInstanceOf(OutboxSerializationException.class)
//      .hasMessageContaining(integrationEvent.getClass().getSimpleName());
//
//    verify(objectMapper).writeValueAsString(integrationEvent);
//  }
}