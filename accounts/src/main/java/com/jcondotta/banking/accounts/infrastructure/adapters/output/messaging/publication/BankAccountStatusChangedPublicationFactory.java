package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import org.springframework.stereotype.Component;

@Component
public class BankAccountStatusChangedPublicationFactory implements EventPublicationFactory<BankAccountStatusChangedEvent> {

  private final String destination;

  public BankAccountStatusChangedPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.bankAccountStatusChanged().topicName();
  }

  @Override
  public Class<BankAccountStatusChangedEvent> domainEventType() {
    return BankAccountStatusChangedEvent.class;
  }

  @Override
  public EventPublication<BankAccountStatusChangedEvent> create(BankAccountStatusChangedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.aggregateId().asString());
  }
}
