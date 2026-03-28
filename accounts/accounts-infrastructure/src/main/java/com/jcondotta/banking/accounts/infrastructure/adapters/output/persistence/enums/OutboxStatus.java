package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums;

public enum OutboxStatus {
  PENDING,
  PUBLISHED,
  FAILED;

  public boolean canTransitionTo(OutboxStatus next) {
    return switch (this) {
      case PENDING -> next == PUBLISHED || next == FAILED;
      case PUBLISHED, FAILED -> false;
    };
  }
}