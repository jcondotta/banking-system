package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedData;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEventMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RecipientKafkaEventPublisherContextTest {

  private static final UUID CORRELATION_ID = UUID.fromString("ce75acbd-da91-4aca-ad03-e1fbb11429b6");
  private static final String EVENT_SOURCE = "recipients";

  @Test
  void shouldReusePublicationContext_whenPublishingMultipleEvents() {
    var event = recipientCreatedEvent();
    var publication = new RecipientCreatedPublicationFactory().create(event);
    var publicationRegistry = mock(EventPublicationRegistry.class);
    var brokerPublisher = mock(BrokerPublisher.class);
    var correlationIdProvider = mock(CorrelationIdProvider.class);
    var eventSourceProvider = mock(EventSourceProvider.class);
    doReturn(publication).when(publicationRegistry).publicationFor(event);
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    var publisher = new RecipientKafkaEventPublisher(
      publicationRegistry,
      brokerPublisher,
      correlationIdProvider,
      eventSourceProvider
    );
    var contextCaptor = ArgumentCaptor.forClass(EventPublicationContext.class);

    publisher.publish(List.of(event, event));

    verify(correlationIdProvider).get();
    verify(eventSourceProvider).get();
    verify(brokerPublisher, times(2)).publish(eq(publication), contextCaptor.capture());
    assertThat(contextCaptor.getAllValues())
      .hasSize(2)
      .allSatisfy(context -> {
        assertThat(context).isSameAs(contextCaptor.getAllValues().getFirst());
        assertThat(context.correlationId()).isEqualTo(CORRELATION_ID);
        assertThat(context.eventSource()).isEqualTo(EVENT_SOURCE);
      });
  }

  @Test
  void shouldNotResolveContext_whenEventListIsEmpty() {
    var publicationRegistry = mock(EventPublicationRegistry.class);
    var brokerPublisher = mock(BrokerPublisher.class);
    var correlationIdProvider = mock(CorrelationIdProvider.class);
    var eventSourceProvider = mock(EventSourceProvider.class);
    var publisher = new RecipientKafkaEventPublisher(
      publicationRegistry,
      brokerPublisher,
      correlationIdProvider,
      eventSourceProvider
    );

    publisher.publish(List.of());

    verifyNoInteractions(publicationRegistry, brokerPublisher, correlationIdProvider, eventSourceProvider);
  }

  @Test
  void shouldNotPublishEvents_whenContextResolutionFails() {
    var event = recipientCreatedEvent();
    var publicationRegistry = mock(EventPublicationRegistry.class);
    var brokerPublisher = mock(BrokerPublisher.class);
    var correlationIdProvider = mock(CorrelationIdProvider.class);
    var eventSourceProvider = mock(EventSourceProvider.class);
    var contextFailure = new IllegalStateException("event source unavailable");
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenThrow(contextFailure);
    var publisher = new RecipientKafkaEventPublisher(
      publicationRegistry,
      brokerPublisher,
      correlationIdProvider,
      eventSourceProvider
    );

    assertThatThrownBy(() -> publisher.publish(List.of(event)))
      .isSameAs(contextFailure);
    verifyNoInteractions(publicationRegistry, brokerPublisher);
  }

  private static RecipientCreatedEvent recipientCreatedEvent() {
    var recipientId = RecipientId.of(UUID.fromString("1b495c23-15f8-448d-af4d-4d287f2166ec"));
    var data = new RecipientCreatedData(
      UUID.fromString("208ff308-a695-48e5-87d8-99f5da6b57ac"),
      "Erika Condotta",
      "IT57P0300203280456112655641"
    );
    return new RecipientCreatedEvent(
      DomainEventMetadata.of(recipientId, Instant.parse("2026-08-30T13:13:04Z")),
      data
    );
  }
}
