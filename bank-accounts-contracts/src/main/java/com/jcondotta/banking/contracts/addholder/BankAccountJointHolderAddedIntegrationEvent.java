package com.jcondotta.banking.contracts.addholder;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;

public record BankAccountJointHolderAddedIntegrationEvent(IntegrationEventMetadata metadata, BankAccountJointHolderAddedIntegrationPayload payload)
  implements IntegrationEvent<BankAccountJointHolderAddedIntegrationPayload> {

  public static final String EVENT_TYPE = "joint-holder-added";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
