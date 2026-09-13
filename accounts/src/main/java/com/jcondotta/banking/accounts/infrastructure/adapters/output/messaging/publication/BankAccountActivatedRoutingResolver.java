package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import org.springframework.stereotype.Component;

@Component
public class BankAccountActivatedRoutingResolver implements EventRoutingResolver<BankAccountActivatedEvent> {

  private final String destination;

  public BankAccountActivatedRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.bankAccountActivated().topicName();
  }

  @Override
  public Class<BankAccountActivatedEvent> domainEventType() {
    return BankAccountActivatedEvent.class;
  }

  @Override
  public EventRouting resolve(BankAccountActivatedEvent event) {
    return new EventRouting(destination, event.aggregateId().asString());
  }
}
