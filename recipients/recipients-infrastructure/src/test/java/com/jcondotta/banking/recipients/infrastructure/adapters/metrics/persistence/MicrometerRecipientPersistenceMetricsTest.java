package com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceConstraint.BANK_ACCOUNT_IBAN;
import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceOperation.CREATE;
import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceOperation.DELETE;
import static com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence.RecipientPersistenceOperation.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;

class MicrometerRecipientPersistenceMetricsTest {

  private SimpleMeterRegistry meterRegistry;
  private MicrometerRecipientPersistenceMetrics metrics;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    metrics = new MicrometerRecipientPersistenceMetrics(meterRegistry);
  }

  @Test
  void shouldRecordUniqueConstraintViolationWithConstraintTag() {
    metrics.recordUniqueConstraintViolation(CREATE, BANK_ACCOUNT_IBAN);

    var counter = meterRegistry.get("recipient.persistence.unique_constraint.violations")
      .tag("operation", "create")
      .tag("constraint", "bank_account_iban")
      .counter();

    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void shouldRecordOptimisticLockConflictWithUpdateOperationTag() {
    metrics.recordOptimisticLockConflict(UPDATE);

    var counter = meterRegistry.get("recipient.persistence.optimistic_lock.conflicts")
      .tag("operation", "update")
      .tag("repository", "postgres")
      .counter();

    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void shouldRecordOptimisticLockConflictWithDeleteOperationTag() {
    metrics.recordOptimisticLockConflict(DELETE);

    var counter = meterRegistry.get("recipient.persistence.optimistic_lock.conflicts")
      .tag("operation", "delete")
      .tag("repository", "postgres")
      .counter();

    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void shouldRecordDeleteVersionMiss() {
    metrics.recordDeleteVersionMiss();

    var counter = meterRegistry.get("recipient.persistence.delete.version_miss")
      .tag("operation", "delete")
      .tag("repository", "postgres")
      .counter();

    assertThat(counter.count()).isEqualTo(1.0);
  }
}
