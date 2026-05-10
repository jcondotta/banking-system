package com.jcondotta.banking.accounts.contracts.status;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;

public record BankAccountStatusChangedIntegrationEvent(
  IntegrationEventMetadata metadata,
  BankAccountStatusChangedIntegrationPayload payload
) implements IntegrationEvent<BankAccountStatusChangedIntegrationPayload> {

  public static final String EVENT_TYPE = "bank-account-status-changed";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
