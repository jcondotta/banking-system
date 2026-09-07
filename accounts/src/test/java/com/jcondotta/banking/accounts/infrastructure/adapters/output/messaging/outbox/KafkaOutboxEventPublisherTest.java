package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox.KafkaOutboxEventPublisher;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaOutboxEventPublisherTest {

  private static final String DESTINATION = "bank-account-opened";
  private static final String MESSAGE_KEY = "bba3f1c2-0000-4d2a-9999-111111111111";
  private static final String PAYLOAD_JSON = "{\"eventId\":\"9f1c2a44-6b7e-4c1a-8d3e-2f7a9b6c5d01\",\"eventType\":\"bank-account-opened\"}";

  @Mock
  private KafkaTemplate<String, byte[]> kafkaTemplate;

  @Mock
  private OutboxProperties outboxProperties;

  @Mock
  private OutboxEntity outboxEntity;

  private KafkaOutboxEventPublisher<OutboxEntity> publisher;

  @BeforeEach
  void setUp() {
    publisher = new KafkaOutboxEventPublisher<>(kafkaTemplate, outboxProperties);

    when(outboxEntity.getDestination()).thenReturn(DESTINATION);
    when(outboxEntity.getMessageKey()).thenReturn(MESSAGE_KEY);
    when(outboxEntity.getPayload()).thenReturn(PAYLOAD_JSON);

    var processing = mock(OutboxProperties.Worker.Processing.class);
    var worker = mock(OutboxProperties.Worker.class);
    when(outboxProperties.worker()).thenReturn(worker);
    when(worker.processing()).thenReturn(processing);
    when(processing.publishTimeout()).thenReturn(Duration.ofSeconds(5));

    var recordMetadata = new RecordMetadata(new TopicPartition(DESTINATION, 0), 0L, 0, 0L, 0, 0);
    var sendResult = new SendResult<String, byte[]>(null, recordMetadata);
    when(kafkaTemplate.send(anyString(), anyString(), any(byte[].class)))
      .thenReturn(CompletableFuture.completedFuture(sendResult));
  }

  @Test
  void shouldSendMessageToKafka_whenPublishingOutboxEntity() {
    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(eq(DESTINATION), eq(MESSAGE_KEY), any(byte[].class));
  }

  @Test
  void shouldUseDestinationAsTopic_whenPublishingOutboxEntity() {
    var topicCaptor = ArgumentCaptor.forClass(String.class);

    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(topicCaptor.capture(), anyString(), any(byte[].class));
    assertThat(topicCaptor.getValue()).isEqualTo(DESTINATION);
  }

  @Test
  void shouldUseMessageKeyForPartitioning_whenPublishingOutboxEntity() {
    var keyCaptor = ArgumentCaptor.forClass(String.class);

    publisher.send(outboxEntity);

    verify(kafkaTemplate).send(anyString(), keyCaptor.capture(), any(byte[].class));
    assertThat(keyCaptor.getValue()).isEqualTo(MESSAGE_KEY);
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
