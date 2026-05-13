package com.jcondotta.banking.accounts.integration.bankaccount.close;

import com.jcondotta.banking.accounts.contracts.open.BankAccountOpenedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.status.BankAccountStatusChangedIntegrationEvent;
import com.jcondotta.banking.accounts.domain.testsupport.AccountTypeAndCurrencySource;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class CloseBankAccountIT extends BankAccountIntegrationSupport {

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn204NoContentAndCloseBankAccount_whenBankAccountIsActive(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);
    activateBankAccount(id);

    closeBankAccount(id);

    assertThat(bankAccountRepository.findById(id)).hasValueSatisfying(bankAccount ->
      assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.CLOSED)
    );

    assertOutboxEvents(
      id,
      BankAccountOpenedIntegrationEvent.EVENT_TYPE,
      BankAccountStatusChangedIntegrationEvent.EVENT_TYPE,
      BankAccountStatusChangedIntegrationEvent.EVENT_TYPE
    );
  }

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn422UnprocessableEntity_whenBankAccountIsNotActive(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);

    var response = patchBankAccount(id, "/close");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    assertThat(bankAccountRepository.findById(id)).hasValueSatisfying(bankAccount ->
      assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.PENDING)
    );
    assertOutboxEvents(id, BankAccountOpenedIntegrationEvent.EVENT_TYPE);
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
    var response = patchBankAccount(BankAccountId.of(UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe")), "/close");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }
}
