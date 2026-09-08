package com.jcondotta.banking.transfers.application.common.log;

import com.jcondotta.banking.transfers.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BankTransferFailureReasonTest {

  @ParameterizedTest
  @CsvSource({
    "NOT_FOUND, not_found",
    "DOMAIN_ERROR, domain_error",
    "INTERNAL_ERROR, internal_error"
  })
  void shouldNormalizeFailureReason_whenConvertingToLogFriendlyValue(
    BankTransferFailureReason failureReason,
    String expected
  ) {
    assertThat(failureReason.normalize()).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("domainExceptions")
  void shouldReturnExpectedFailureReason_whenExceptionTypeIsKnown(
    DomainException exception,
    BankTransferFailureReason expected
  ) {
    assertThat(BankTransferFailureReason.from(exception)).isEqualTo(expected);
  }

  @Test
  void shouldReturnDomainError_whenExceptionTypeIsUnknown() {
    assertThat(BankTransferFailureReason.from(new TestDomainException()))
      .isEqualTo(BankTransferFailureReason.DOMAIN_ERROR);
  }

  @Test
  void shouldReturnDomainError_whenExceptionIsNull() {
    assertThat(BankTransferFailureReason.from(null)).isEqualTo(BankTransferFailureReason.DOMAIN_ERROR);
  }

  static Stream<Arguments> domainExceptions() {
    return Stream.of(
      Arguments.of(
        new RecipientBankAccountNotFoundException(Iban.of("ES9121000418450200051332")),
        BankTransferFailureReason.NOT_FOUND
      ),
      Arguments.of(
        new RecipientBankAccountNotActiveException(BankAccountStatus.BLOCKED),
        BankTransferFailureReason.DOMAIN_ERROR
      )
    );
  }

  private static final class TestDomainException extends DomainException {
    private TestDomainException() {
      super("domain error");
    }
  }
}
