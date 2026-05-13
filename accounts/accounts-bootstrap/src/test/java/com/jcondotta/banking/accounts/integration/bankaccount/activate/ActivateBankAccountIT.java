package com.jcondotta.banking.accounts.integration.bankaccount.activate;

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
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ActivateBankAccountIT extends BankAccountIntegrationSupport {

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn204NoContentAndActivateBankAccount_whenBankAccountIsPending(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);

    activateBankAccount(id);

    assertThat(bankAccountRepository.findById(id)).hasValueSatisfying(bankAccount ->
      assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE)
    );

    assertOutboxEvents(
      id,
      BankAccountOpenedIntegrationEvent.EVENT_TYPE,
      BankAccountStatusChangedIntegrationEvent.EVENT_TYPE
    );
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
    var nonExistentId = BankAccountId.of(UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe"));

    var response = patchBankAccount(nonExistentId, "/activate");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.as(ProblemDetail.class).getInstance())
      .isEqualTo(URI.create("/api/bank-accounts/" + nonExistentId.asString() + "/activate"));
  }
}
