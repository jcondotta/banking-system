package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.domain.events.DomainEvent;

public interface DomainEventToIntegrationEventResolver {

  IntegrationEvent<?> toIntegrationEvent(DomainEvent<?> event, EventMetadataContext eventMetadataContext);
}