package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.publisher;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProcessingProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaOutboxEventPublisherTest {

  private static final String TOPIC = "bank-account-opened";
  private static final String AGGREGATE_ID = "6a3a7a45-21ee-4110-9d9a-b619fccd88a6";
  private static final String PAYLOAD_JSON = "{\"eventType\":\"BankAccountOpened\"}";

  @Mock
  private KafkaTemplate<String, byte[]> kafkaTemplate;

  @Mock
  private OutboxProcessingProperties properties;

  @Mock
  private OutboxEntity outboxEntity;

  private KafkaOutboxEventPublisher publisher;

  @BeforeEach
  void setUp() {
    publisher = new KafkaOutboxEventPublisher(kafkaTemplate, properties);

    when(outboxEntity.getEventType()).thenReturn(TOPIC);
    when(outboxEntity.getAggregateId()).thenReturn(AGGREGATE_ID);
    when(outboxEntity.getPayload()).thenReturn(PAYLOAD_JSON);
  }

  @Test
  void shouldSendMessageToKafka_whenPublishingOutboxEntity() {
    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(eq(TOPIC), eq(AGGREGATE_ID), any(byte[].class));
  }

  @Test
  void shouldUseEventTypeAsTopic_whenPublishingOutboxEntity() {
    var topicCaptor = ArgumentCaptor.forClass(String.class);

    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(topicCaptor.capture(), anyString(), any(byte[].class));
    assertThat(topicCaptor.getValue()).isEqualTo(TOPIC);
  }

  @Test
  void shouldUseAggregateIdAsMessageKey_whenPublishingOutboxEntity() {
    var keyCaptor = ArgumentCaptor.forClass(String.class);

    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(anyString(), keyCaptor.capture(), any(byte[].class));
    assertThat(keyCaptor.getValue()).isEqualTo(AGGREGATE_ID);
  }

  @Test
  void shouldEncodePayloadAsUtf8Bytes_whenPublishingOutboxEntity() {
    var valueCaptor = ArgumentCaptor.forClass(byte[].class);

    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(anyString(), anyString(), valueCaptor.capture());
    assertThat(valueCaptor.getValue()).isEqualTo(PAYLOAD_JSON.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void shouldInvokeKafkaTemplateExactlyOnce_whenPublishingOutboxEntity() {
    publisher.send(outboxEntity);

    verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(byte[].class));
    verifyNoMoreInteractions(kafkaTemplate);
  }
}
