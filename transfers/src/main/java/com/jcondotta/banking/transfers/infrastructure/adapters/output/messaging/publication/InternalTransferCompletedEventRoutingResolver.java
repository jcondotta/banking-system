package com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferCompletedEvent;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class InternalTransferCompletedEventRoutingResolver
    implements EventRoutingResolver<InternalTransferCompletedEvent> {

  private final String destination;

  public InternalTransferCompletedEventRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.internalTransferCompleted().topicName();
  }

  @Override
  public Class<InternalTransferCompletedEvent> domainEventType() {
    return InternalTransferCompletedEvent.class;
  }

  @Override
  public EventRouting resolve(InternalTransferCompletedEvent event) {
    return new EventRouting(destination, event.aggregateId().asString());
  }
}
