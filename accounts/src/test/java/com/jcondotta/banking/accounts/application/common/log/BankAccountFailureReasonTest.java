package com.jcondotta.banking.accounts.application.common.log;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.*;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountFailureReasonTest {

  @ParameterizedTest
  @CsvSource({
    "NOT_FOUND, not_found",
    "NOT_ACTIVE, not_active",
    "INVALID_STATE_TRANSITION, invalid_state_transition",
    "MAX_JOINT_HOLDERS_EXCEEDED, max_joint_holders_exceeded",
    "ACCOUNT_HOLDER_NOT_FOUND, account_holder_not_found",
    "CANNOT_DEACTIVATE_PRIMARY_HOLDER, cannot_deactivate_primary_holder",
    "DOMAIN_ERROR, domain_error",
    "INTERNAL_ERROR, internal_error"
  })
  void shouldNormalizeFailureReason_whenConvertingToLogFriendlyValue(
    BankAccountFailureReason failureReason,
    String expected
  ) {
    assertThat(failureReason.normalize()).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("domainExceptions")
  void shouldReturnExpectedFailureReason_whenExceptionTypeIsKnown(
    DomainException exception,
    BankAccountFailureReason expected
  ) {
    assertThat(BankAccountFailureReason.from(exception)).isEqualTo(expected);
  }

  @Test
  void shouldReturnDomainError_whenExceptionTypeIsUnknown() {
    assertThat(BankAccountFailureReason.from(new TestDomainException())).isEqualTo(BankAccountFailureReason.DOMAIN_ERROR);
  }

  @Test
  void shouldReturnDomainError_whenExceptionIsNull() {
    assertThat(BankAccountFailureReason.from(null)).isEqualTo(BankAccountFailureReason.DOMAIN_ERROR);
  }

  static Stream<Arguments> domainExceptions() {
    return Stream.of(
      Arguments.of(new BankAccountNotFoundException(BankAccountId.newId()), BankAccountFailureReason.NOT_FOUND),
      Arguments.of(new AccountHolderNotFoundException(AccountHolderId.newId()), BankAccountFailureReason.ACCOUNT_HOLDER_NOT_FOUND),
      Arguments.of(new BankAccountNotActiveException(AccountStatus.PENDING), BankAccountFailureReason.NOT_ACTIVE),
      Arguments.of(new InvalidBankAccountStateTransitionException(AccountStatus.PENDING, AccountStatus.CLOSED), BankAccountFailureReason.INVALID_STATE_TRANSITION),
      Arguments.of(new MaxJointHoldersExceededException(3), BankAccountFailureReason.MAX_JOINT_HOLDERS_EXCEEDED),
      Arguments.of(new CannotDeactivatePrimaryHolderException(), BankAccountFailureReason.CANNOT_DEACTIVATE_PRIMARY_HOLDER)
    );
  }

  private static final class TestDomainException extends DomainException {
    private TestDomainException() {
      super("domain error");
    }
  }
}
