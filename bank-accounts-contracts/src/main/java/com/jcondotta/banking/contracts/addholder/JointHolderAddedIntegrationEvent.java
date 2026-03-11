package com.jcondotta.banking.contracts.addholder;

import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.banking.contracts.IntegrationEventMetadata;

public record JointHolderAddedIntegrationEvent(IntegrationEventMetadata metadata, JointHolderAddedIntegrationPayload payload)
  implements IntegrationEvent<JointHolderAddedIntegrationPayload> {

}
