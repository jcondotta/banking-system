package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import org.springframework.stereotype.Component;

@Component
public class BankAccountOpenedRoutingResolver implements EventRoutingResolver<BankAccountOpenedEvent> {

  private final String destination;

  public BankAccountOpenedRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.bankAccountOpened().topicName();
  }

  @Override
  public Class<BankAccountOpenedEvent> domainEventType() {
    return BankAccountOpenedEvent.class;
  }

  @Override
  public EventRouting resolve(BankAccountOpenedEvent event) {
    return new EventRouting(destination, event.aggregateId().asString());
  }
}
