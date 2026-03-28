package com.jcondotta.banking.contracts.close;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.application.core.events.IntegrationEventMetadata;

public record BankAccountClosedIntegrationEvent(IntegrationEventMetadata metadata, BankAccountClosedIntegrationPayload payload)
  implements IntegrationEvent<BankAccountClosedIntegrationPayload> {

  public static final String EVENT_TYPE = "bank-account-closed";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
