package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class BankAccountOpenedPublicationFactory implements EventPublicationFactory<BankAccountOpenedEvent> {

  private final String destination;

  public BankAccountOpenedPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.bankAccountOpened().topicName();
  }

  @Override
  public Class<BankAccountOpenedEvent> domainEventType() {
    return BankAccountOpenedEvent.class;
  }

  @Override
  public EventPublication<BankAccountOpenedEvent> create(BankAccountOpenedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.aggregateId().asString());
  }
}
