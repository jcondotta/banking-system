package com.jcondotta.application.events;

import java.util.UUID;

@FunctionalInterface
public interface CorrelationIdProvider {
  UUID get();
}