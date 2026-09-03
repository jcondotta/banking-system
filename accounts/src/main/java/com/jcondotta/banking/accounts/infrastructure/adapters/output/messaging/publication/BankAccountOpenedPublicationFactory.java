package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import org.springframework.stereotype.Component;

@Component
public class BankAccountOpenedPublicationFactory implements EventPublicationFactory<BankAccountOpenedEvent> {

  static final String DESTINATION = "bank-account-opened";

  @Override
  public Class<BankAccountOpenedEvent> domainEventType() {
    return BankAccountOpenedEvent.class;
  }

  @Override
  public EventPublication<BankAccountOpenedEvent> create(BankAccountOpenedEvent event) {
    return new DefaultEventPublication<>(event, DESTINATION, event.aggregateId().asString());
  }
}
