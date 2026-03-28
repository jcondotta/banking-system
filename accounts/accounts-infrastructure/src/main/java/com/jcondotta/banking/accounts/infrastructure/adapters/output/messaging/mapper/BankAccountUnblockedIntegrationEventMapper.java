package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.application.core.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountUnblockedEvent;
import com.jcondotta.banking.contracts.unblock.BankAccountUnblockedIntegrationEvent;
import com.jcondotta.banking.contracts.unblock.BankAccountUnblockedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountUnblockedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountUnblockedEvent, BankAccountUnblockedIntegrationEvent> {

  @Override
  public BankAccountUnblockedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountUnblockedEvent event) {
    var payload = new BankAccountUnblockedIntegrationPayload(
      event.aggregateId().value()
    );

    return new BankAccountUnblockedIntegrationEvent(metadata, payload);
  }
}