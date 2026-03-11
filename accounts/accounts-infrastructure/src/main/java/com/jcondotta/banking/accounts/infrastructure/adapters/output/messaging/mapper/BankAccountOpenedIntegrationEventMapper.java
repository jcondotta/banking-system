package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.open.BankAccountOpenedIntegrationEvent;
import com.jcondotta.banking.contracts.open.BankAccountOpenedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountOpenedIntegrationEventMapper extends AbstractDomainEventMapper<BankAccountOpenedEvent> {

  public BankAccountOpenedIntegrationEventMapper() {
    super(BankAccountOpenedEvent.class);
  }

  @Override
  protected IntegrationEvent<?> buildIntegrationEvent(BankAccountOpenedEvent event, IntegrationEventMetadata metadata) {
    var payload = new BankAccountOpenedIntegrationPayload(
      event.aggregateId().value(),
      event.accountType().name(),
      event.currency().name(),
      event.primaryAccountHolderId().value()
    );

    return new BankAccountOpenedIntegrationEvent(metadata, payload);
  }
}