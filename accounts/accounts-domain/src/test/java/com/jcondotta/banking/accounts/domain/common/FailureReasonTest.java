package com.jcondotta.banking.accounts.domain.common;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.*;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FailureReasonTest {

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
    FailureReason failureReason,
    String expected
  ) {
    assertThat(failureReason.normalize()).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("domainExceptions")
  void shouldReturnProvidedFailureReason_whenExceptionImplementsFailureReasonProvider(
    DomainException exception,
    FailureReason expected
  ) {
    assertThat(FailureReason.from(exception)).isEqualTo(expected);
  }

  @Test
  void shouldReturnDomainError_whenExceptionDoesNotProvideFailureReason() {
    var exception = new TestDomainException();

    assertThat(FailureReason.from(exception)).isEqualTo(FailureReason.DOMAIN_ERROR);
  }

  static Stream<org.junit.jupiter.params.provider.Arguments> domainExceptions() {
    return Stream.of(
      org.junit.jupiter.params.provider.Arguments.of(
        new BankAccountNotFoundException(BankAccountId.newId()),
        FailureReason.NOT_FOUND
      ),
      org.junit.jupiter.params.provider.Arguments.of(
        new BankAccountNotActiveException(AccountStatus.PENDING),
        FailureReason.NOT_ACTIVE
      ),
      org.junit.jupiter.params.provider.Arguments.of(
        new InvalidBankAccountStateTransitionException(AccountStatus.PENDING, AccountStatus.CLOSED),
        FailureReason.INVALID_STATE_TRANSITION
      ),
      org.junit.jupiter.params.provider.Arguments.of(
        new MaxJointHoldersExceededException(3),
        FailureReason.MAX_JOINT_HOLDERS_EXCEEDED
      ),
      org.junit.jupiter.params.provider.Arguments.of(
        new AccountHolderNotFoundException(AccountHolderId.newId()),
        FailureReason.ACCOUNT_HOLDER_NOT_FOUND
      ),
      org.junit.jupiter.params.provider.Arguments.of(
        new CannotDeactivatePrimaryHolderException(),
        FailureReason.CANNOT_DEACTIVATE_PRIMARY_HOLDER
      )
    );
  }

  private static final class TestDomainException extends DomainException {

    private TestDomainException() {
      super("domain error");
    }
  }
}
