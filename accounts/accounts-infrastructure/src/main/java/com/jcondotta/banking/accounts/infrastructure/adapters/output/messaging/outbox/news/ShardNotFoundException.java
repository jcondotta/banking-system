package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news;

public class ShardNotFoundException extends RuntimeException {

  public static final String ERROR_MESSAGE = "Shard not configured: %s";

  public ShardNotFoundException(Object shard) {
    super(ERROR_MESSAGE.formatted(shard));
  }
}