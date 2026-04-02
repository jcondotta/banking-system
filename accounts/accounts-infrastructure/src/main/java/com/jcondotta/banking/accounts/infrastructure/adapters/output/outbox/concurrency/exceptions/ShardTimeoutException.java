package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.exceptions;

public class ShardTimeoutException extends RuntimeException {

  public static final String ERROR_MESSAGE = "Timeout acquiring permit for shard %s within %d ms";

  public ShardTimeoutException(Object shard, long timeoutMs) {
    super(ERROR_MESSAGE.formatted(shard, timeoutMs));
  }
}