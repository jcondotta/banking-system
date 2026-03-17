package com.jcondotta.application.core.testsupport;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.application.core.events.IntegrationEventMetadata;

public record FakeIntegrationEvent(IntegrationEventMetadata metadata, FakeIntegrationPayload payload)
  implements IntegrationEvent<FakeIntegrationPayload> {

  static final String EVENT_TYPE = "fake-event-type";

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}