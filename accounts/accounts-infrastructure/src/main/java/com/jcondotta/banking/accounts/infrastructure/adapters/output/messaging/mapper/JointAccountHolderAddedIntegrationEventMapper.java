package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.addholder.JointHolderAddedIntegrationEvent;
import com.jcondotta.banking.contracts.addholder.JointHolderAddedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class JointAccountHolderAddedIntegrationEventMapper extends AbstractDomainEventMapper<BankAccountJointHolderAddedEvent> {

  public JointAccountHolderAddedIntegrationEventMapper() {
    super(BankAccountJointHolderAddedEvent.class);
  }

  @Override
  protected IntegrationEvent<?> buildIntegrationEvent(BankAccountJointHolderAddedEvent event, IntegrationEventMetadata metadata) {
    var payload = new JointHolderAddedIntegrationPayload(
      event.aggregateId().value(),
      event.accountHolderId().value()
    );

    return new JointHolderAddedIntegrationEvent(metadata, payload);
  }
}