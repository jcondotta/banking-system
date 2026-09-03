package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.recipients.application.recipient.ports.output.RecipientEventPublisher;
import com.jcondotta.domain.events.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecipientKafkaEventPublisher implements RecipientEventPublisher {

  private final EventPublicationRegistry publicationRegistry;
  private final BrokerPublisher brokerPublisher;
  private final CorrelationIdProvider correlationIdProvider;
  private final EventSourceProvider eventSourceProvider;

  public RecipientKafkaEventPublisher(
    EventPublicationRegistry publicationRegistry,
    BrokerPublisher brokerPublisher,
    CorrelationIdProvider correlationIdProvider,
    EventSourceProvider eventSourceProvider
  ) {
    this.publicationRegistry = publicationRegistry;
    this.brokerPublisher = brokerPublisher;
    this.correlationIdProvider = correlationIdProvider;
    this.eventSourceProvider = eventSourceProvider;
  }

  @Override
  public void publish(List<DomainEvent<?, ?>> events) {
    if (events.isEmpty()) {
      return;
    }

    var context = new EventPublicationContext(correlationIdProvider.get(), eventSourceProvider.get());
    events.forEach(event -> publishDomainEvent(event, context));
  }

  private void publishDomainEvent(DomainEvent<?, ?> event, EventPublicationContext context) {
    brokerPublisher.publish(publicationRegistry.publicationFor(event), context);
  }
}
