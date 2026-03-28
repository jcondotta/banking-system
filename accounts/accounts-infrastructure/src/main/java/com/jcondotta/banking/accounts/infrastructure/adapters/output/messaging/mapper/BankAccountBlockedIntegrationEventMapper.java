package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.application.core.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountBlockedEvent;
import com.jcondotta.banking.contracts.block.BankAccountBlockedIntegrationEvent;
import com.jcondotta.banking.contracts.block.BankAccountBlockedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountBlockedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountBlockedEvent, BankAccountBlockedIntegrationEvent> {

  @Override
  public BankAccountBlockedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountBlockedEvent event) {
    var payload = new BankAccountBlockedIntegrationPayload(
      event.aggregateId().value()
    );

    return new BankAccountBlockedIntegrationEvent(metadata, payload);
  }
}