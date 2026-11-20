package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import org.springframework.stereotype.Component;

@Component
public class BankAccountJointHolderAddedRoutingResolver implements EventRoutingResolver<BankAccountJointHolderAddedEvent> {

  private final String destination;

  public BankAccountJointHolderAddedRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.jointAccountHolderAdded().topicName();
  }

  @Override
  public Class<BankAccountJointHolderAddedEvent> domainEventType() {
    return BankAccountJointHolderAddedEvent.class;
  }

  @Override
  public EventRouting resolve(BankAccountJointHolderAddedEvent event) {
    return new EventRouting(destination, event.aggregateId().asString());
  }
}
