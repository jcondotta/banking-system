package com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MicrometerRecipientPersistenceMetrics implements RecipientPersistenceMetrics {

  private static final String OPERATION_TAG = "operation";
  private static final String REPOSITORY_TAG = "repository";
  private static final String REPOSITORY_POSTGRES = "postgres";
  private static final String CONSTRAINT_TAG = "constraint";

  private final MeterRegistry meterRegistry;

  @Override
  public void recordOptimisticLockConflict(RecipientPersistenceOperation operation) {
    meterRegistry.counter(
      "recipient.persistence.optimistic_lock.conflicts",
      OPERATION_TAG, operation.tagValue(),
      REPOSITORY_TAG, REPOSITORY_POSTGRES
    ).increment();
  }

  @Override
  public void recordUniqueConstraintViolation(
    RecipientPersistenceOperation operation,
    RecipientPersistenceConstraint constraint
  ) {
    meterRegistry.counter(
      "recipient.persistence.unique_constraint.violations",
      OPERATION_TAG, operation.tagValue(),
      CONSTRAINT_TAG, constraint.tagValue()
    ).increment();
  }

  @Override
  public void recordDeleteVersionMiss() {
    meterRegistry.counter(
      "recipient.persistence.delete.version_miss",
      OPERATION_TAG, RecipientPersistenceOperation.DELETE.tagValue(),
      REPOSITORY_TAG, REPOSITORY_POSTGRES
    ).increment();
  }
}
