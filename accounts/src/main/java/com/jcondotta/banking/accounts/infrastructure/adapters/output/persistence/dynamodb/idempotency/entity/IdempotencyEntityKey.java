package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity;

import com.jcondotta.application.idempotency.IdempotencyKey;

public final class IdempotencyEntityKey {

  private IdempotencyEntityKey() {
  }

  public static String partitionKey(IdempotencyKey key) {
    return "IDEMPOTENCY#" + key.value();
  }

  public static String sortKey() {
    return "IDEMPOTENCY";
  }
}
