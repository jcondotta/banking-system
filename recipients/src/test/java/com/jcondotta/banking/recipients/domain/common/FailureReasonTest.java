package com.jcondotta.banking.recipients.domain.common;

import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FailureReasonTest {

  @ParameterizedTest
  @CsvSource({
    "DUPLICATE_IBAN,duplicate_iban",
    "NOT_FOUND,not_found",
    "ALREADY_EXISTS,already_exists",
    "OWNERSHIP_MISMATCH,ownership_mismatch",
    "OPTIMISTIC_LOCK_CONFLICT,optimistic_lock_conflict",
    "DOMAIN_ERROR,domain_error",
    "INTERNAL_ERROR,internal_error"
  })
  void shouldNormalizeFailureReason_whenConvertingToLogFriendlyValue(FailureReason failureReason, String expected) {
    assertThat(failureReason.normalize()).isEqualTo(expected);
  }

  @Test
  void shouldReturnProvidedFailureReason_whenExceptionImplementsFailureReasonProvider() {
    var exception = new FailureReasonAwareDomainException(FailureReason.OPTIMISTIC_LOCK_CONFLICT);

    assertThat(FailureReason.from(exception)).isEqualTo(FailureReason.OPTIMISTIC_LOCK_CONFLICT);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = "plain domain error")
  void shouldReturnDomainError_whenExceptionDoesNotProvideFailureReason(String message) {
    DomainException exception = message == null ? null : new PlainDomainException(message);

    assertThat(FailureReason.from(exception)).isEqualTo(FailureReason.DOMAIN_ERROR);
  }

  private static final class FailureReasonAwareDomainException extends DomainException implements FailureReasonProvider {

    private final FailureReason failureReason;

    private FailureReasonAwareDomainException(FailureReason failureReason) {
      super("failure reason aware domain exception");
      this.failureReason = failureReason;
    }

    @Override
    public FailureReason reason() {
      return failureReason;
    }
  }

  private static final class PlainDomainException extends DomainException {

    private PlainDomainException(String message) {
      super(message);
    }
  }
}
