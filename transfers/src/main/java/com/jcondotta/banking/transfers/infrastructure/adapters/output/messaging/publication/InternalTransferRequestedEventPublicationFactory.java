package com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferRequestedEvent;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class InternalTransferRequestedEventPublicationFactory
    implements EventPublicationFactory<InternalTransferRequestedEvent> {

  private final String destination;

  public InternalTransferRequestedEventPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.internalTransferRequested().topicName();
  }

  @Override
  public Class<InternalTransferRequestedEvent> domainEventType() {
    return InternalTransferRequestedEvent.class;
  }

  @Override
  public EventPublication<InternalTransferRequestedEvent> create(InternalTransferRequestedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.aggregateId().asString());
  }
}
