package com.jcondotta.banking.infrastructure.outbox.log;

import java.util.Locale;

public enum OutboxFailureReason {
  DISPATCH_IN_PROGRESS,
  UNHANDLED_ERROR,
  INTERNAL_ERROR,
  MAX_RETRIES_EXCEEDED,
  ALREADY_CLAIMED,
  SHARD_TIMEOUT,
  SHARD_EXECUTION_ERROR,
  PUBLISH_FAILED,
  ALREADY_PROCESSED,
  DELETE_FAILED;

  public String normalize() {
    return name().toLowerCase(Locale.ROOT);
  }
}
