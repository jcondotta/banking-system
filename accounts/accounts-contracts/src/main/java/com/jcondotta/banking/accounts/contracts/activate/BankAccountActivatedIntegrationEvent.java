package com.jcondotta.banking.accounts.contracts.activate;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;

public record BankAccountActivatedIntegrationEvent(IntegrationEventMetadata metadata, BankAccountActivatedIntegrationPayload payload)
  implements IntegrationEvent<BankAccountActivatedIntegrationPayload> {

  public static final String EVENT_TYPE = "bank-account-activated";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
