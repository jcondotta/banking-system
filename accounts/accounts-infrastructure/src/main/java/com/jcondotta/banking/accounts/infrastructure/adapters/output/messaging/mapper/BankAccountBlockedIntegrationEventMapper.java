package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountBlockedEvent;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.block.BankAccountBlockedIntegrationEvent;
import com.jcondotta.banking.contracts.block.BankAccountBlockedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountBlockedIntegrationEventMapper extends AbstractDomainEventMapper<BankAccountBlockedEvent> {

  public BankAccountBlockedIntegrationEventMapper() {
    super(BankAccountBlockedEvent.class);
  }

  @Override
  protected IntegrationEvent<?> buildIntegrationEvent(BankAccountBlockedEvent event, IntegrationEventMetadata metadata) {
    var payload = new BankAccountBlockedIntegrationPayload(event.aggregateId().value());

    return new BankAccountBlockedIntegrationEvent(metadata, payload);
  }
}