package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountClosedEvent;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.close.BankAccountClosedIntegrationEvent;
import com.jcondotta.banking.contracts.close.BankAccountClosedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountClosedIntegrationEventMapper extends AbstractDomainEventMapper<BankAccountClosedEvent> {

  public BankAccountClosedIntegrationEventMapper() {
    super(BankAccountClosedEvent.class);
  }

  @Override
  protected IntegrationEvent<?> buildIntegrationEvent(BankAccountClosedEvent event, IntegrationEventMetadata metadata) {
    var payload = new BankAccountClosedIntegrationPayload(event.aggregateId().value());

    return new BankAccountClosedIntegrationEvent(metadata, payload);
  }
}