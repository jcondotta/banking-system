package com.jcondotta.banking.contracts.open;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;

public record BankAccountOpenedIntegrationEvent(IntegrationEventMetadata metadata, BankAccountOpenedIntegrationPayload payload)
  implements IntegrationEvent<BankAccountOpenedIntegrationPayload> {

  public static final String EVENT_TYPE = "bank-account-opened";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
