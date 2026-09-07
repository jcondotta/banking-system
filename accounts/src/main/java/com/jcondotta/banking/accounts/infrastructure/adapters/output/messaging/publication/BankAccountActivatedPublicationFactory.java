package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class BankAccountActivatedPublicationFactory implements EventPublicationFactory<BankAccountActivatedEvent> {

  private final String destination;

  public BankAccountActivatedPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.bankAccountActivated().topicName();
  }

  @Override
  public Class<BankAccountActivatedEvent> domainEventType() {
    return BankAccountActivatedEvent.class;
  }

  @Override
  public EventPublication<BankAccountActivatedEvent> create(BankAccountActivatedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.aggregateId().asString());
  }
}
