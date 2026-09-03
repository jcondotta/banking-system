package com.jcondotta.banking.recipients.application.common.log;

import com.jcondotta.banking.recipients.application.common.exception.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientFailureReasonTest {

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @ParameterizedTest
  @CsvSource({
    "DUPLICATE_IBAN,duplicate_iban",
    "NOT_FOUND,not_found",
    "OPTIMISTIC_LOCK_CONFLICT,optimistic_lock_conflict",
    "DOMAIN_ERROR,domain_error",
    "INTERNAL_ERROR,internal_error"
  })
  void shouldNormalizeFailureReason_whenConvertingToLogFriendlyValue(
    RecipientFailureReason failureReason,
    String expected
  ) {
    assertThat(failureReason.normalize()).isEqualTo(expected);
  }

  @Test
  void shouldResolveDuplicateIbanReason_whenDuplicateRecipientIbanExceptionIsProvided() {
    var exception = new DuplicateRecipientIbanException(RecipientFixtures.JEFFERSON.toIban(), BANK_ACCOUNT_ID);

    assertThat(RecipientFailureReason.from(exception)).isEqualTo(RecipientFailureReason.DUPLICATE_IBAN);
  }

  @Test
  void shouldResolveNotFoundReason_whenRecipientNotFoundExceptionIsProvided() {
    var exception = new RecipientNotFoundException(RECIPIENT_ID, BANK_ACCOUNT_ID);

    assertThat(RecipientFailureReason.from(exception)).isEqualTo(RecipientFailureReason.NOT_FOUND);
  }

  @Test
  void shouldResolveOptimisticLockReason_whenRecipientOptimisticLockExceptionIsProvided() {
    var exception = new RecipientOptimisticLockException(RECIPIENT_ID);

    assertThat(RecipientFailureReason.from(exception)).isEqualTo(RecipientFailureReason.OPTIMISTIC_LOCK_CONFLICT);
  }

  @Test
  void shouldResolveDomainErrorReason_whenDomainExceptionHasNoSpecificMapping() {
    assertThat(RecipientFailureReason.from(new PlainDomainException()))
      .isEqualTo(RecipientFailureReason.DOMAIN_ERROR);
  }

  @Test
  void shouldResolveDomainErrorReason_whenExceptionIsNull() {
    assertThat(RecipientFailureReason.from(null)).isEqualTo(RecipientFailureReason.DOMAIN_ERROR);
  }

  private static final class PlainDomainException extends DomainException {

    private PlainDomainException() {
      super("plain domain error");
    }
  }
}
