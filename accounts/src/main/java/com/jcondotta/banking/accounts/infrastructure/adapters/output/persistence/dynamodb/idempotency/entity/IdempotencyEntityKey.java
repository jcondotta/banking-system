package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity;

import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyKey;

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
