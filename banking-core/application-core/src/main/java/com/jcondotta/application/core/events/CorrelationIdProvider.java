package com.jcondotta.application.core.events;

import java.util.UUID;

@FunctionalInterface
public interface CorrelationIdProvider {
  UUID get();
}