package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedData;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEventMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaBrokerPublisherTest {

  private static final Duration PUBLISH_TIMEOUT = Duration.ofMillis(2500);
  private static final UUID CORRELATION_ID = UUID.fromString("ce75acbd-da91-4aca-ad03-e1fbb11429b6");
  private static final String EVENT_SOURCE = "recipients";

  @AfterEach
  void clearInterruptedStatus() {
    Thread.interrupted();
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldUseConfiguredPublishTimeout() throws Exception {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var sendResult = (CompletableFuture<SendResult<String, byte[]>>) mock(CompletableFuture.class);
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(sendResult);
    var publisher = publisher(kafkaTemplate, JsonMapper.builder().build());

    publisher.publish(publication(), context());

    verify(sendResult).get(PUBLISH_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldWrapSerializationFailure() throws Exception {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var objectMapper = mock(ObjectMapper.class);
    var jacksonException = mock(JacksonException.class);
    when(objectMapper.writeValueAsBytes(any())).thenThrow(jacksonException);
    var publisher = publisher(kafkaTemplate, objectMapper);

    assertThatThrownBy(() -> publisher.publish(publication(), context()))
      .isInstanceOf(RecipientEventPublishException.class)
      .hasMessage("Failed to publish recipient event: recipient-created")
      .hasCause(jacksonException);
    verify(kafkaTemplate, never()).send(any(ProducerRecord.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldRestoreInterruptedStatus_whenKafkaSendIsInterrupted() throws Exception {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var sendResult = (CompletableFuture<SendResult<String, byte[]>>) mock(CompletableFuture.class);
    var interruptedException = new InterruptedException("interrupted");
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(sendResult);
    when(sendResult.get(PUBLISH_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)).thenThrow(interruptedException);
    var publisher = publisher(kafkaTemplate, JsonMapper.builder().build());

    assertThatThrownBy(() -> publisher.publish(publication(), context()))
      .isInstanceOf(RecipientEventPublishException.class)
      .hasCause(interruptedException);
    assertThat(Thread.currentThread().isInterrupted()).isTrue();
  }

  private static KafkaBrokerPublisher publisher(
    KafkaTemplate<String, byte[]> kafkaTemplate,
    ObjectMapper objectMapper
  ) {
    return new KafkaBrokerPublisher(
      kafkaTemplate,
      objectMapper,
      new KafkaPublisherProperties(PUBLISH_TIMEOUT)
    );
  }

  private static EventPublicationContext context() {
    return new EventPublicationContext(CORRELATION_ID, EVENT_SOURCE);
  }

  private static EventPublication<RecipientCreatedEvent> publication() {
    var recipientId = RecipientId.of(UUID.fromString("1b495c23-15f8-448d-af4d-4d287f2166ec"));
    var data = new RecipientCreatedData(
      UUID.fromString("208ff308-a695-48e5-87d8-99f5da6b57ac"),
      "Erika Condotta",
      "IT57P0300203280456112655641"
    );
    var event = new RecipientCreatedEvent(
      DomainEventMetadata.of(recipientId, Instant.parse("2026-08-30T13:13:04Z")),
      data
    );
    return new DefaultEventPublication<>(event, "recipients-created", data.bankAccountId().toString());
  }
}
