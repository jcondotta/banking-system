package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.OutboxStatus;

import java.util.Objects;

public record OutboxQuery(int shard, int limit, OutboxStatus status) {

  static final String STATUS_REQUIRED = "status must be provided";

  public OutboxQuery {
    Objects.requireNonNull(status, STATUS_REQUIRED);
  }
}