package com.jcondotta.banking.accounts.infrastructure.adapters;

import java.util.UUID;

public interface CorrelationIdProvider {
  UUID get();
}