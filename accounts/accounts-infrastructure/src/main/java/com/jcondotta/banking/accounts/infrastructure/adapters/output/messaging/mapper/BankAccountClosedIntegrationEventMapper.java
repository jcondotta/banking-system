package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.application.core.events.mapper.AbstractDomainEventMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountClosedEvent;
import com.jcondotta.banking.contracts.close.BankAccountClosedIntegrationEvent;
import com.jcondotta.banking.contracts.close.BankAccountClosedIntegrationPayload;
import org.springframework.stereotype.Component;

@Component
public class BankAccountClosedIntegrationEventMapper
  extends AbstractDomainEventMapper<BankAccountClosedEvent, BankAccountClosedIntegrationEvent> {

  @Override
  public BankAccountClosedIntegrationEvent map(IntegrationEventMetadata metadata, BankAccountClosedEvent event) {
    var payload = new BankAccountClosedIntegrationPayload(
      event.aggregateId().value()
    );

    return new BankAccountClosedIntegrationEvent(metadata, payload);
  }
}