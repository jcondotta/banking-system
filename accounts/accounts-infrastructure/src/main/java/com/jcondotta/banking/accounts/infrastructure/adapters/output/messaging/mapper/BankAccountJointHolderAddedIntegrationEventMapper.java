package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.contracts.addholder.BankAccountJointHolderAddedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.addholder.BankAccountJointHolderAddedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountJointHolderAddedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountJointHolderAddedEvent, BankAccountJointHolderAddedIntegrationEvent> {

  @Override
  public BankAccountJointHolderAddedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountJointHolderAddedEvent event) {
    var payload = new BankAccountJointHolderAddedIntegrationPayload(
      event.aggregateId().value(),
      event.accountHolderId().value()
    );

    return new BankAccountJointHolderAddedIntegrationEvent(metadata, payload);
  }
}