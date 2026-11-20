package com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferRequestedEvent;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class InternalTransferRequestedEventRoutingResolver
    implements EventRoutingResolver<InternalTransferRequestedEvent> {

  private final String destination;

  public InternalTransferRequestedEventRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.internalTransferRequested().topicName();
  }

  @Override
  public Class<InternalTransferRequestedEvent> domainEventType() {
    return InternalTransferRequestedEvent.class;
  }

  @Override
  public EventRouting resolve(InternalTransferRequestedEvent event) {
    return new EventRouting(destination, event.aggregateId().asString());
  }
}
