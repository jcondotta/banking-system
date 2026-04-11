package com.jcondotta.banking.accounts.contracts.block;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;

public record BankAccountBlockedIntegrationEvent(IntegrationEventMetadata metadata, BankAccountBlockedIntegrationPayload payload)
  implements IntegrationEvent<BankAccountBlockedIntegrationPayload> {

  public static final String EVENT_TYPE = "bank-account-blocked";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
