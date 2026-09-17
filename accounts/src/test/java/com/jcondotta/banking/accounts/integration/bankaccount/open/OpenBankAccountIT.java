package com.jcondotta.banking.accounts.integration.bankaccount.open;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.testsupport.AccountTypeAndCurrencySource;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.AccountHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.AddressRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.AccountTypeRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.CurrencyRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountRequestFactory;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class OpenBankAccountIT extends BankAccountIntegrationSupport {

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn201CreatedWithValidLocationHeader_whenRequestIsValid(AccountType accountType, Currency currency) {
    var response = postOpenBankAccount(
      BankAccountRequestFactory.openBankAccount(accountType, currency, AccountHolderFixtures.JEFFERSON)
    );

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getHeader("Content-Type")).isNullOrEmpty();

    var bankAccountId = response.getHeader("Location").substring(response.getHeader("Location").lastIndexOf('/') + 1);
    var bankAccount = bankAccountRepository
      .findById(BankAccountId.of(UUID.fromString(bankAccountId)))
      .orElseThrow();

    assertAll(
      () -> assertThat(bankAccount.getAccountType()).isEqualTo(accountType),
      () -> assertThat(bankAccount.getCurrency()).isEqualTo(currency),
      () -> assertThat(bankAccount.getIban()).isEmpty(),
      () -> assertThat(bankAccount.getAccountStatus()).isEqualTo(BankAccount.ACCOUNT_STATUS_ON_OPENING),
      () -> assertThat(bankAccount.getCreatedAt()).isNotNull(),
      () -> assertThat(bankAccount.getActiveHolders())
        .hasSize(1)
        .singleElement()
        .satisfies(holder -> {
          assertThat(holder.getPersonalInfo()).isEqualTo(AccountHolderFixtures.JEFFERSON.personalInfo());
          assertThat(holder.getContactInfo()).isEqualTo(AccountHolderFixtures.JEFFERSON.contactInfo());
          assertThat(holder.getAddress()).isEqualTo(AccountHolderFixtures.JEFFERSON.address());
          assertThat(holder.isPrimary()).isTrue();
        })
    );

    assertOutboxEvents(bankAccount.getId(), BankAccountOpenedEvent.EVENT_TYPE);
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenPrimaryHolderIsMissing() {
    var request = new OpenBankAccountRequest(AccountTypeRequest.CHECKING, CurrencyRequest.EUR, null);

    var response = postOpenBankAccount(request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenPersonalInfoIsMissing() {
    var request = new OpenBankAccountRequest(
      AccountTypeRequest.CHECKING,
      CurrencyRequest.EUR,
      new AccountHolderRequest(null, null, null)
    );

    var response = postOpenBankAccount(request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  @Test
  void shouldDescribeExpectedCountryFormat_whenCountryFormatIsInvalid() {
    var validRequest = BankAccountRequestFactory.openBankAccount(
      AccountType.CHECKING,
      Currency.EUR,
      AccountHolderFixtures.JEFFERSON
    );
    var validHolder = validRequest.primaryHolder();
    var validAddress = validHolder.address();
    var invalidAddress = new AddressRequest(
      validAddress.street(),
      validAddress.streetNumber(),
      validAddress.complement(),
      validAddress.postalCode(),
      validAddress.city(),
      "es"
    );
    var request = new OpenBankAccountRequest(
      validRequest.accountType(),
      validRequest.currency(),
      new AccountHolderRequest(validHolder.personalInfo(), validHolder.contactInfo(), invalidAddress)
    );

    var response = postOpenBankAccount(request);

    assertAll(
      () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(response.jsonPath().getString("errors[0].field"))
        .isEqualTo("primaryHolder.address.country"),
      () -> assertThat(response.jsonPath().getList("errors[0].messages", String.class))
        .containsExactly("must contain exactly two uppercase letters (for example, ES)")
    );
  }
}
