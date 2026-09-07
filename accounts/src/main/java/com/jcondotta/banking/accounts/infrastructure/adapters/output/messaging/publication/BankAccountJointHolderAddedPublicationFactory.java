package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import org.springframework.stereotype.Component;

@Component
public class BankAccountJointHolderAddedPublicationFactory implements EventPublicationFactory<BankAccountJointHolderAddedEvent> {

  private final String destination;

  public BankAccountJointHolderAddedPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.jointAccountHolderAdded().topicName();
  }

  @Override
  public Class<BankAccountJointHolderAddedEvent> domainEventType() {
    return BankAccountJointHolderAddedEvent.class;
  }

  @Override
  public EventPublication<BankAccountJointHolderAddedEvent> create(BankAccountJointHolderAddedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.aggregateId().asString());
  }
}
