package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;
import org.junit.jupiter.api.Test;

import static com.jcondotta.banking.recipients.domain.testsupport.AccountHolderFixtures.JEFFERSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddJointHolderCommandTest {

  private static final PersonalInfo PERSONAL_INFO = JEFFERSON.personalInfo();
  private static final ContactInfo CONTACT_INFO = JEFFERSON.contactInfo();
  private static final Address ADDRESS = JEFFERSON.address();

  @Test
  void shouldCreateCommand_whenAllFieldsAreProvided() {
    var bankAccountId = BankAccountId.newId();

    var command = new AddJointHolderCommand(
      bankAccountId,
      PERSONAL_INFO,
      CONTACT_INFO,
      ADDRESS
    );

    assertThat(command.bankAccountId()).isEqualTo(bankAccountId);
    assertThat(command.personalInfo()).isEqualTo(PERSONAL_INFO);
    assertThat(command.contactInfo()).isEqualTo(CONTACT_INFO);
    assertThat(command.address()).isEqualTo(ADDRESS);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(
      () -> new AddJointHolderCommand(
        null,
        PERSONAL_INFO,
        CONTACT_INFO,
        ADDRESS
      ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(AddJointHolderCommand.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenPersonalInfoIsNull() {
    assertThatThrownBy(
      () -> new AddJointHolderCommand(
        BankAccountId.newId(),
        null,
        CONTACT_INFO,
        ADDRESS
      ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(AddJointHolderCommand.PERSONAL_INFO_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenContactInfoIsNull() {
    assertThatThrownBy(
      () -> new AddJointHolderCommand(
        BankAccountId.newId(),
        PERSONAL_INFO,
        null,
        ADDRESS
      ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(AddJointHolderCommand.CONTACT_INFO_REQUIRED);
  }

  @Test
  void shouldThrowNullPointerException_whenAddressIsNull() {
    assertThatThrownBy(
      () -> new AddJointHolderCommand(
        BankAccountId.newId(),
        PERSONAL_INFO,
        CONTACT_INFO,
        null
      ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(AddJointHolderCommand.ADDRESS_REQUIRED);
  }
}