package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import com.jcondotta.banking.accounts.infrastructure.Preconditions;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxStatus;

public record OutboxQuery(int shard, int limit, OutboxStatus status) {

  static final String STATUS_REQUIRED = "status must be provided";

  public OutboxQuery {
    Preconditions.required(status, STATUS_REQUIRED);
  }
}