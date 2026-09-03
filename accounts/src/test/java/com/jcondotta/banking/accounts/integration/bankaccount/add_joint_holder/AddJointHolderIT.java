package com.jcondotta.banking.accounts.integration.bankaccount.add_joint_holder;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.accounts.domain.testsupport.AccountTypeAndCurrencySource;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountRequestFactory;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class AddJointHolderIT extends BankAccountIntegrationSupport {

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn204NoContentAndAddJointHolder_whenBankAccountIsActive(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);
    activateBankAccount(id);

    addJointHolder(id, AccountHolderFixtures.PATRIZIO);

    var details = getBankAccount(id);
    assertThat(details.holders())
      .hasSize(2)
      .anySatisfy(holder -> {
        assertThat(holder.type().name()).isEqualTo("JOINT");
        assertThat(holder.personalInfo().firstName()).isEqualTo(AccountHolderFixtures.PATRIZIO.personalInfo().holderName().firstName());
        assertThat(holder.personalInfo().lastName()).isEqualTo(AccountHolderFixtures.PATRIZIO.personalInfo().holderName().lastName());
      });

    assertOutboxEvents(
      id,
      BankAccountOpenedEvent.EVENT_TYPE,
      BankAccountStatusChangedEvent.EVENT_TYPE,
      BankAccountJointHolderAddedEvent.EVENT_TYPE
    );
  }

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn422UnprocessableEntity_whenBankAccountIsNotActive(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);

    var response = postJointHolder(id, BankAccountRequestFactory.addJointHolder(AccountHolderFixtures.PATRIZIO));

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    assertThat(getBankAccount(id).holders()).hasSize(1);
    assertOutboxEvents(id, BankAccountOpenedEvent.EVENT_TYPE);
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountIsNotFound() {
    var response = postJointHolder(
      com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId.of(UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe")),
      BankAccountRequestFactory.addJointHolder(AccountHolderFixtures.PATRIZIO)
    );

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }
}
