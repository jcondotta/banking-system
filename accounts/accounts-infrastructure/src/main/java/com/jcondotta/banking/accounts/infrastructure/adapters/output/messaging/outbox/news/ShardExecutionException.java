package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news;

public class ShardExecutionException extends RuntimeException {

  public static final String ERROR_MESSAGE = "Thread interrupted while acquiring semaphore for shard %s";

  public ShardExecutionException(Object shard, Throwable cause) {
    super(ERROR_MESSAGE.formatted(shard), cause);
  }
}