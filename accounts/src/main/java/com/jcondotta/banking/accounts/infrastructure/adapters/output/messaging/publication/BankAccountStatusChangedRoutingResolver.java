package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import org.springframework.stereotype.Component;

@Component
public class BankAccountStatusChangedRoutingResolver implements EventRoutingResolver<BankAccountStatusChangedEvent> {

  private final String destination;

  public BankAccountStatusChangedRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.bankAccountStatusChanged().topicName();
  }

  @Override
  public Class<BankAccountStatusChangedEvent> domainEventType() {
    return BankAccountStatusChangedEvent.class;
  }

  @Override
  public EventRouting resolve(BankAccountStatusChangedEvent event) {
    return new EventRouting(destination, event.aggregateId().asString());
  }
}
