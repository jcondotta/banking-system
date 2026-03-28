package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.application.core.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.contracts.open.BankAccountOpenedIntegrationEvent;
import com.jcondotta.banking.contracts.open.BankAccountOpenedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountOpenedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountOpenedEvent, BankAccountOpenedIntegrationEvent> {

  @Override
  public BankAccountOpenedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountOpenedEvent event) {
    var payload = new BankAccountOpenedIntegrationPayload(
      event.aggregateId().value(),
      event.accountType().name(),
      event.currency().name(),
      event.primaryAccountHolderId().value()
    );

    return new BankAccountOpenedIntegrationEvent(metadata, payload);
  }
}