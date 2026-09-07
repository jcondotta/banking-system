package com.jcondotta.banking.accounts.integration.bankaccount.lookup;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.testsupport.AccountTypeAndCurrencySource;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class BankAccountLookupIT extends BankAccountIntegrationSupport {

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn200OkWithBankAccountDetails_whenBankAccountIsFound(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);

    var response = getBankAccount(id);

    assertAll(
      () -> assertThat(response.id()).isEqualTo(id.value()),
      () -> assertThat(response.accountType().name()).isEqualTo(accountType.name()),
      () -> assertThat(response.currency().name()).isEqualTo(currency.name()),
      () -> assertThat(response.iban()).isNull(),
      () -> assertThat(response.createdAt()).isNotNull(),
      () -> assertThat(response.holders())
        .hasSize(1)
        .singleElement()
        .satisfies(holder -> {
          assertThat(holder.type().name()).isEqualTo("PRIMARY");
          assertThat(holder.personalInfo().firstName()).isEqualTo(AccountHolderFixtures.JEFFERSON.personalInfo().holderName().firstName());
          assertThat(holder.personalInfo().lastName()).isEqualTo(AccountHolderFixtures.JEFFERSON.personalInfo().holderName().lastName());
          assertThat(holder.createdAt()).isNotNull();
        })
    );

    assertOutboxEvents(id, BankAccountOpenedEvent.EVENT_TYPE);
  }

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn200OkWithPrimaryAndJointAccountHolders_whenBankAccountHasMultipleHolders(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);
    activateBankAccount(id);
    addJointHolder(id, AccountHolderFixtures.PATRIZIO);

    var response = getBankAccount(id);

    assertThat(response.holders())
      .hasSize(2)
      .anySatisfy(holder -> {
        assertThat(holder.type().name()).isEqualTo("PRIMARY");
        assertThat(holder.personalInfo().firstName()).isEqualTo(AccountHolderFixtures.JEFFERSON.personalInfo().holderName().firstName());
      })
      .anySatisfy(holder -> {
        assertThat(holder.type().name()).isEqualTo("JOINT");
        assertThat(holder.personalInfo().firstName()).isEqualTo(AccountHolderFixtures.PATRIZIO.personalInfo().holderName().firstName());
      });

    assertOutboxEvents(
      id,
      BankAccountOpenedEvent.EVENT_TYPE,
      BankAccountActivatedEvent.EVENT_TYPE,
      BankAccountJointHolderAddedEvent.EVENT_TYPE
    );
  }

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn200OkWithBankAccountDetails_whenBankAccountIsFoundByIban(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);
    activateBankAccount(id);
    var bankAccount = bankAccountRepository.findById(id).orElseThrow();
    var ibanValue = bankAccount.getIban().orElseThrow().value();

    var response = getBankAccountByIban(ibanValue);

    assertAll(
      () -> assertThat(response.id()).isEqualTo(id.value()),
      () -> assertThat(response.iban()).isEqualTo(ibanValue),
      () -> assertThat(response.accountType().name()).isEqualTo(accountType.name()),
      () -> assertThat(response.currency().name()).isEqualTo(currency.name())
    );
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountIsNotFound() {
    var nonExistentId = UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe");

    var response = getBankAccount(nonExistentId);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.as(ProblemDetail.class).getInstance())
      .isEqualTo(uriProperties.bankAccountURI(nonExistentId));
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountWithIbanIsNotFound() {
    var nonExistentIban = "GB29NWBK60161331926819";

    var response = getBankAccountByIbanResponse(nonExistentIban);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.as(ProblemDetail.class).getInstance())
      .isEqualTo(URI.create(uriProperties.rootPath()));
  }
}
