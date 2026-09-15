package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountsGrpcExceptionHandlerTest {

  private final AccountsGrpcExceptionHandler configuration = new AccountsGrpcExceptionHandler();

  @Test
  void shouldReturnInvalidArgument_whenBankAccountIdIsInvalid() {
    var exception = configuration.grpcExceptionHandler()
      .handleException(new InvalidGrpcRequestException(BankAccountQueryGrpcService.INVALID_BANK_ACCOUNT_ID));

    assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
    assertThat(exception.getStatus().getDescription()).isEqualTo(BankAccountQueryGrpcService.INVALID_BANK_ACCOUNT_ID);
  }

  @Test
  void shouldReturnInvalidArgument_whenIbanIsInvalid() {
    var exception = configuration.grpcExceptionHandler()
      .handleException(new InvalidGrpcRequestException(BankAccountQueryGrpcService.INVALID_IBAN));

    assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
    assertThat(exception.getStatus().getDescription()).isEqualTo(BankAccountQueryGrpcService.INVALID_IBAN);
  }

  @Test
  void shouldReturnNotFoundWithoutIdentifier_whenBankAccountDoesNotExist() {
    var exception = configuration.grpcExceptionHandler()
      .handleException(new BankAccountNotFoundException(BankAccountId.newId()));

    assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
    assertThat(exception.getStatus().getDescription()).isEqualTo(AccountsGrpcExceptionHandler.BANK_ACCOUNT_NOT_FOUND);
  }

  @Test
  void shouldReturnInternalWithoutFailureDetails_whenUnexpectedFailureOccurs() {
    var exception = configuration.grpcExceptionHandler()
      .handleException(new IllegalStateException("database credentials leaked"));

    assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
    assertThat(exception.getStatus().getDescription()).isEqualTo(AccountsGrpcExceptionHandler.INTERNAL_ERROR);
  }
}
