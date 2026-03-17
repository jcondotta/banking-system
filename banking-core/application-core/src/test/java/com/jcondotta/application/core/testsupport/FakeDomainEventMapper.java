package com.jcondotta.application.core.testsupport;

import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.application.core.events.mapper.AbstractDomainEventMapper;

public class FakeDomainEventMapper extends AbstractDomainEventMapper<FakeDomainEvent, FakeIntegrationEvent> {

  @Override
  public FakeIntegrationEvent map(IntegrationEventMetadata metadata, FakeDomainEvent event) {
    return new FakeIntegrationEvent(metadata, new FakeIntegrationPayload(event.aggregateId().value()));
  }
}