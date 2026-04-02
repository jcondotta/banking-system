package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

public enum OutboxStatus {
  PENDING,
  PUBLISHED,
  PROCESSING,
  FAILED;

  public boolean canTransitionTo(OutboxStatus next) {
    return switch (this) {
      case PENDING -> next == PROCESSING;
      case PROCESSING -> next == PUBLISHED || next == FAILED;
      case PUBLISHED, FAILED -> false;
    };
  }
}