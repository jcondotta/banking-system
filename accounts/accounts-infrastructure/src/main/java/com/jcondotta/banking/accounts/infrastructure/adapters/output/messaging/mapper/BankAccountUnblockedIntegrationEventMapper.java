package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountUnblockedEvent;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.unblock.BankAccountUnblockedIntegrationEvent;
import com.jcondotta.banking.contracts.unblock.BankAccountUnblockedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountUnblockedIntegrationEventMapper extends AbstractDomainEventMapper<BankAccountUnblockedEvent> {

  public BankAccountUnblockedIntegrationEventMapper() {
    super(BankAccountUnblockedEvent.class);
  }

  @Override
  protected IntegrationEvent<?> buildIntegrationEvent(BankAccountUnblockedEvent event, IntegrationEventMetadata metadata) {
    var payload = new BankAccountUnblockedIntegrationPayload(event.aggregateId().value());

    return new BankAccountUnblockedIntegrationEvent(metadata, payload);
  }
}