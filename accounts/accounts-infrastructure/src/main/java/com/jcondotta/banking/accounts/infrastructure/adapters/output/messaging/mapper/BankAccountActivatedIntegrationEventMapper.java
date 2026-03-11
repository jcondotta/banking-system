package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;
import org.springframework.stereotype.Component;

@Component
public class BankAccountActivatedIntegrationEventMapper extends AbstractDomainEventMapper<BankAccountActivatedEvent> {

  public BankAccountActivatedIntegrationEventMapper() {
    super(BankAccountActivatedEvent.class);
  }

  @Override
  protected IntegrationEvent<?> buildIntegrationEvent(BankAccountActivatedEvent event, IntegrationEventMetadata metadata) {
//    BankAccountActivatedIntegrationPayload payload = new BankAccountActivatedIntegrationPayload(
//      event.aggregateId().value()
//    );
//
//    return new BankAccountActivatedIntegrationEvent(metadata, payload);

    return null;
  }
}