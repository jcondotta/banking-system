package com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence;

public interface RecipientPersistenceMetrics {

  void recordOptimisticLockConflict(RecipientPersistenceOperation operation);

  void recordUniqueConstraintViolation(RecipientPersistenceOperation operation, RecipientPersistenceConstraint constraint);

  void recordDeleteVersionMiss();
}
