package com.jcondotta.application.testsupport;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.events.mapper.AbstractDomainEventMapper;

public class FakeDomainEventMapper extends AbstractDomainEventMapper<FakeDomainEvent, FakeIntegrationEvent> {

  @Override
  public FakeIntegrationEvent map(IntegrationEventMetadata metadata, FakeDomainEvent event) {
    return new FakeIntegrationEvent(metadata, new FakeIntegrationPayload(event.aggregateId().value()));
  }
}