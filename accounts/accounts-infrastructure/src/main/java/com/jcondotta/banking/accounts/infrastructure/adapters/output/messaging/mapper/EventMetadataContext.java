package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import java.util.UUID;

public interface EventMetadataContext {
  UUID correlationId();
}

