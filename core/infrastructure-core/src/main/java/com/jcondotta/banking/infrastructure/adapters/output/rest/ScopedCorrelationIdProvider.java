package com.jcondotta.banking.infrastructure.adapters.output.rest;

import com.jcondotta.application.events.CorrelationIdProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScopedCorrelationIdProvider implements CorrelationIdProvider {

  public static final ScopedValue<UUID> CORRELATION_ID = ScopedValue.newInstance();

  @Override
  public UUID get() {
    return CORRELATION_ID.get();
  }
}
