package com.jcondotta.banking.contracts.unblock;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.application.core.events.IntegrationEventMetadata;

public record BankAccountUnblockedIntegrationEvent(IntegrationEventMetadata metadata, BankAccountUnblockedIntegrationPayload payload)
  implements IntegrationEvent<BankAccountUnblockedIntegrationPayload> {

  public static final String EVENT_TYPE = "bank-account-unblocked";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
