package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.mapper;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxSerializationException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.mapper.OutboxEntityMapperImpl;
import com.jcondotta.banking.infrastructure.outbox.shard.OutboxShardResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEntityMapperImplTest {

  private static final String AGGREGATE_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String EVENT_ID = "9f1c2a44-6b7e-4c1a-8d3e-2f7a9b6c5d01";
  private static final String MESSAGE_KEY = "bba3f1c2-9999-4d2a-0000-000000000001";
  private static final String EVENT_TYPE = "bank-account-opened";
  private static final String DESTINATION = "bank-account-opened";
  private static final String SERIALIZED_PAYLOAD = "{\"eventId\":\"test\"}";
  private static final UUID CORRELATION_ID = UUID.fromString("cbd765a4-90a5-4d1c-a52e-aff6ea60e27c");

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private OutboxShardResolver shardResolver;

  @Mock
  private EventEnvelope envelope;

  @InjectMocks
  private OutboxEntityMapperImpl mapper;

  private final EventRouting routing = new EventRouting(DESTINATION, MESSAGE_KEY);

  @BeforeEach
  void setUp() {
    when(envelope.aggregateId()).thenReturn(AGGREGATE_ID);
    when(envelope.eventId()).thenReturn(EVENT_ID);
    when(shardResolver.resolve(AGGREGATE_ID)).thenReturn(0);
  }

  @Test
  void shouldMapToOutboxEntity_whenValidInput() {
    when(objectMapper.writeValueAsString(envelope)).thenReturn(SERIALIZED_PAYLOAD);
    when(envelope.correlationId()).thenReturn(CORRELATION_ID);
    when(envelope.eventType()).thenReturn(EVENT_TYPE);

    OutboxEntity outboxEntity = mapper.toOutboxEntity(new EventPublication(envelope, routing));

    assertThat(outboxEntity.getAggregateId()).isEqualTo(AGGREGATE_ID);
    assertThat(outboxEntity.getMessageKey()).isEqualTo(MESSAGE_KEY);
    assertThat(outboxEntity.getEventId()).isEqualTo(UUID.fromString(EVENT_ID));
    assertThat(outboxEntity.getCorrelationId()).isEqualTo(CORRELATION_ID);
    assertThat(outboxEntity.getEventType()).isEqualTo(EVENT_TYPE);
    assertThat(outboxEntity.getDestination()).isEqualTo(DESTINATION);
    assertThat(outboxEntity.getPayload()).isEqualTo(SERIALIZED_PAYLOAD);
    assertThat(outboxEntity.getCreatedAt()).isNotNull();
    assertThat(outboxEntity.getPartitionKey()).isNotBlank();
    assertThat(outboxEntity.getSortKey()).isNotBlank();
    assertThat(outboxEntity.getGsi1pk()).isNotBlank();
    assertThat(outboxEntity.getGsi1sk()).isNotBlank();

    verify(objectMapper).writeValueAsString(envelope);
  }

  @Test
  void shouldThrowOutboxSerializationException_whenSerializationFails() {
    when(objectMapper.writeValueAsString(any(EventEnvelope.class)))
      .thenThrow(new JacksonException("serialization failed") {});

    assertThatThrownBy(() -> mapper.toOutboxEntity(new EventPublication(envelope, routing)))
      .isInstanceOf(OutboxSerializationException.class)
      .hasMessageContaining(EventEnvelope.class.getSimpleName());

    verify(objectMapper).writeValueAsString(envelope);
  }
}
