package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.AccountHolderErrors;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHolderRestoreTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final Instant CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  @ParameterizedTest
  @EnumSource(HolderType.class)
  void shouldRestoreAccountHolderWithAllAttributes(HolderType holderType) {
    var accountHolderId = AccountHolderId.newId();

    var accountHolder = AccountHolder.restore(
      accountHolderId,
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      holderType,
      CREATED_AT
    );

    assertThat(accountHolder.getId()).isEqualTo(accountHolderId);
    assertThat(accountHolder.getPersonalInfo()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.personalInfo());
    assertThat(accountHolder.getContactInfo()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.contactInfo());
    assertThat(accountHolder.getAddress()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.address());
    assertThat(accountHolder.getAccountHolderType()).isEqualTo(holderType);
    assertThat(accountHolder.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(accountHolder.isPrimary()).isEqualTo(holderType.isPrimary());
    assertThat(accountHolder.isJoint()).isEqualTo(holderType.isJoint());
  }

  @Test
  void shouldThrowException_whenIdIsNull() {
    assertThatThrownBy(() -> AccountHolder.restore(
      null,
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      CREATED_AT
    ))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.ID_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenPersonalInfoIsNull() {
    assertThatThrownBy(() -> AccountHolder.restore(
      AccountHolderId.newId(),
      null,
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      CREATED_AT
    ))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.PERSONAL_INFO_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenContactInfoIsNull() {
    assertThatThrownBy(() -> AccountHolder.restore(
      AccountHolderId.newId(),
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      null,
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      CREATED_AT
    ))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.CONTACT_INFO_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenAddressIsNull() {
    assertThatThrownBy(() -> AccountHolder.restore(
      AccountHolderId.newId(),
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      null,
      HolderType.PRIMARY,
      CREATED_AT
    ))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.ADDRESS_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenAccountHolderTypeIsNull() {
    assertThatThrownBy(() -> AccountHolder.restore(
      AccountHolderId.newId(),
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      null,
      CREATED_AT
    ))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.ACCOUNT_HOLDER_TYPE_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCreatedAtIsNull() {
    assertThatThrownBy(() -> AccountHolder.restore(
      AccountHolderId.newId(),
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      null
    ))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.CREATED_AT_MUST_BE_PROVIDED);
  }
}