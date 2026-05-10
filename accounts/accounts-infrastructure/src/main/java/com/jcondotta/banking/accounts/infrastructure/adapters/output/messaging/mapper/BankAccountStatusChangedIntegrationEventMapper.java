package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.contracts.status.BankAccountStatus;
import com.jcondotta.banking.accounts.contracts.status.BankAccountStatusChangedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.status.BankAccountStatusChangedIntegrationPayload;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import org.springframework.stereotype.Component;

@Component
public class BankAccountStatusChangedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountStatusChangedEvent, BankAccountStatusChangedIntegrationEvent> {

  @Override
  public BankAccountStatusChangedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountStatusChangedEvent event) {
    var payload = new BankAccountStatusChangedIntegrationPayload(
      event.aggregateId().value(),
      BankAccountStatus.valueOf(event.previousStatus().name()),
      BankAccountStatus.valueOf(event.currentStatus().name())
    );

    return new BankAccountStatusChangedIntegrationEvent(metadata, payload);
  }
}
