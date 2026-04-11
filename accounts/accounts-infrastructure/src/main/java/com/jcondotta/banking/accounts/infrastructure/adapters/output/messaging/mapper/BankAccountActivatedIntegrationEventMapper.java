package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountActivatedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountActivatedEvent, BankAccountActivatedIntegrationEvent> {

  @Override
  public BankAccountActivatedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountActivatedEvent event) {
    var payload = new BankAccountActivatedIntegrationPayload(
      event.aggregateId().value()
    );

    return new BankAccountActivatedIntegrationEvent(metadata, payload);
  }
}