package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.domain.events.DomainEvent;

public interface EventPublication<E extends DomainEvent<?, ?>> {

  E event();

  /**
   * Logical destination interpreted by the selected broker adapter.
   */
  String destination();

  /**
   * Stable key that the selected broker adapter may use for routing or partitioning.
   */
  String key();
}
