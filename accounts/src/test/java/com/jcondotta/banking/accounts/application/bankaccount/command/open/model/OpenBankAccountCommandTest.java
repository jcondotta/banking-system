package com.jcondotta.banking.accounts.application.bankaccount.command.open.model;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenBankAccountCommandTest {

  private static final PersonalInfo PERSONAL_INFO = AccountHolderFixtures.JEFFERSON.personalInfo();
  private static final ContactInfo CONTACT_INFO = AccountHolderFixtures.JEFFERSON.contactInfo();
  private static final Address ADDRESS = AccountHolderFixtures.JEFFERSON.address();

  private static final AccountType ACCOUNT_TYPE = AccountType.CHECKING;
  private static final Currency CURRENCY = Currency.EUR;

  @Test
  void shouldCreateCommand_whenAllFieldsAreProvided() {
    var command = new OpenBankAccountCommand(
      PERSONAL_INFO,
      CONTACT_INFO,
      ADDRESS,
      ACCOUNT_TYPE,
      CURRENCY
    );

    assertThat(command.personalInfo()).isEqualTo(PERSONAL_INFO);
    assertThat(command.contactInfo()).isEqualTo(CONTACT_INFO);
    assertThat(command.address()).isEqualTo(ADDRESS);
    assertThat(command.accountType()).isEqualTo(ACCOUNT_TYPE);
    assertThat(command.currency()).isEqualTo(CURRENCY);
  }

  @Test
  void shouldThrowNullPointerException_whenPersonalInfoIsNull() {
    assertThatThrownBy(() -> new OpenBankAccountCommand(null, CONTACT_INFO, ADDRESS, ACCOUNT_TYPE, CURRENCY))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(OpenBankAccountCommand.PERSONAL_INFO_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenContactInfoIsNull() {
    assertThatThrownBy(() -> new OpenBankAccountCommand(PERSONAL_INFO, null, ADDRESS, ACCOUNT_TYPE, CURRENCY))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(OpenBankAccountCommand.CONTACT_INFO_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenAddressIsNull() {
    assertThatThrownBy(() -> new OpenBankAccountCommand(PERSONAL_INFO, CONTACT_INFO, null, ACCOUNT_TYPE, CURRENCY))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(OpenBankAccountCommand.ADDRESS_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenAccountTypeIsNull() {
    assertThatThrownBy(() -> new OpenBankAccountCommand(PERSONAL_INFO, CONTACT_INFO, ADDRESS, null, CURRENCY))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(OpenBankAccountCommand.ACCOUNT_TYPE_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenCurrencyIsNull() {
    assertThatThrownBy(() -> new OpenBankAccountCommand(PERSONAL_INFO, CONTACT_INFO, ADDRESS, ACCOUNT_TYPE, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(OpenBankAccountCommand.CURRENCY_REQUIRED);
  }
}