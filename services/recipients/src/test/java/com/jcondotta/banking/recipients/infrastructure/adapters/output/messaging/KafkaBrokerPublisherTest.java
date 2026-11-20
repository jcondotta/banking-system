package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessage;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessageSender;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.BrokerMessageSendException;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedData;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEventMetadata;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaBrokerPublisherTest {

  private static final Duration PUBLISH_TIMEOUT = Duration.ofMillis(2500);
  private static final UUID CORRELATION_ID = UUID.fromString("ce75acbd-da91-4aca-ad03-e1fbb11429b6");
  private static final String EVENT_SOURCE = "recipients";
  private static final String DESTINATION = "recipients-created";
  private static final String MESSAGE_KEY = UUID.randomUUID().toString();

  @Test
  void shouldUseSharedSenderWithConfiguredPublishTimeout() {
    var messageSender = mock(BrokerMessageSender.class);
    var publisher = publisher(messageSender, JsonMapper.builder().build());

    publisher.publish(publication());

    verify(messageSender).send(any(BrokerMessage.class), eq(PUBLISH_TIMEOUT));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldWrapSerializationFailure() throws Exception {
    var messageSender = mock(BrokerMessageSender.class);
    var objectMapper = mock(ObjectMapper.class);
    var jacksonException = mock(JacksonException.class);
    when(objectMapper.writeValueAsBytes(any())).thenThrow(jacksonException);
    var publisher = publisher(messageSender, objectMapper);

    assertThatThrownBy(() -> publisher.publish(publication()))
      .isInstanceOf(RecipientEventPublishException.class)
      .hasMessage("Failed to publish recipient event: recipient-created")
      .hasCause(jacksonException);
    verifyNoInteractions(messageSender);
  }

  @Test
  void shouldPreserveOriginalCause_whenSharedSenderFails() {
    var messageSender = mock(BrokerMessageSender.class);
    var interruptedException = new InterruptedException("interrupted");
    doThrow(new BrokerMessageSendException(
      DESTINATION,
      MESSAGE_KEY,
      interruptedException
    )).when(messageSender).send(any(), any());
    var publisher = publisher(messageSender, JsonMapper.builder().build());

    assertThatThrownBy(() -> publisher.publish(publication()))
      .isInstanceOf(RecipientEventPublishException.class)
      .hasCause(interruptedException);
  }

  private static KafkaBrokerPublisher publisher(
    BrokerMessageSender messageSender,
    ObjectMapper objectMapper
  ) {
    return new KafkaBrokerPublisher(
      messageSender,
      objectMapper,
      new KafkaPublisherProperties(PUBLISH_TIMEOUT)
    );
  }

  private static EventPublication publication() {
    return new EventPublication(envelope(), new EventRouting(DESTINATION, MESSAGE_KEY));
  }

  private static EventEnvelope envelope() {
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
    return EventEnvelope.from(event, new EventPublicationContext(CORRELATION_ID, EVENT_SOURCE));
  }
}
