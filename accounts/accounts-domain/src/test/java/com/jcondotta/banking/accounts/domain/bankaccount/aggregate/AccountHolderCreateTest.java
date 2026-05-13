package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.AccountHolderErrors;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHolderCreateTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER = AccountHolderFixtures.PATRIZIO;

  private static final Instant CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  @ParameterizedTest
  @EnumSource(HolderType.class)
  void shouldCreateAccountHolder_whenValuesAreValid(HolderType holderType) {
    var accountHolder = AccountHolder.create(
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      holderType,
      CREATED_AT
    );

    assertHolderMatchesFixture(accountHolder, PRIMARY_ACCOUNT_HOLDER);
    assertThat(accountHolder.getAccountHolderType()).isEqualTo(holderType);
    assertThat(accountHolder.isPrimary()).isEqualTo(holderType.isPrimary());
    assertThat(accountHolder.isJoint()).isEqualTo(holderType.isJoint());
    assertThat(accountHolder.isActive()).isTrue();
    assertThat(accountHolder.getDeactivatedAt()).isNull();
  }

  @Test
  void shouldCreatePrimaryAccountHolder_whenValuesAreValid() {
    var accountHolder = AccountHolder.createPrimary(
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      CREATED_AT
    );

    assertHolderMatchesFixture(accountHolder, PRIMARY_ACCOUNT_HOLDER);
    assertThat(accountHolder.isPrimary()).isTrue();
    assertThat(accountHolder.isJoint()).isFalse();
    assertThat(accountHolder.isActive()).isTrue();
  }

  @Test
  void shouldCreateJointAccountHolder_whenValuesAreValid() {
    var accountHolder = AccountHolder.createJoint(
      JOINT_ACCOUNT_HOLDER.personalInfo(),
      JOINT_ACCOUNT_HOLDER.contactInfo(),
      JOINT_ACCOUNT_HOLDER.address(),
      CREATED_AT
    );

    assertHolderMatchesFixture(accountHolder, JOINT_ACCOUNT_HOLDER);
    assertThat(accountHolder.isJoint()).isTrue();
    assertThat(accountHolder.isPrimary()).isFalse();
    assertThat(accountHolder.isActive()).isTrue();
  }

  @Test
  void shouldThrowException_whenPersonalInfoIsNull() {
    assertThatThrownBy(() -> AccountHolder.create(
      null,
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.PERSONAL_INFO_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenContactInfoIsNull() {
    assertThatThrownBy(() -> AccountHolder.create(
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      null,
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.CONTACT_INFO_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenAddressIsNull() {
    assertThatThrownBy(() -> AccountHolder.create(
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      null,
      HolderType.PRIMARY,
      CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.ADDRESS_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenAccountHolderTypeIsNull() {
    assertThatThrownBy(() -> AccountHolder.create(
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      null,
      CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.ACCOUNT_HOLDER_TYPE_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCreatedAtIsNull() {
    assertThatThrownBy(() -> AccountHolder.create(
      PRIMARY_ACCOUNT_HOLDER.personalInfo(),
      PRIMARY_ACCOUNT_HOLDER.contactInfo(),
      PRIMARY_ACCOUNT_HOLDER.address(),
      HolderType.PRIMARY,
      null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderErrors.CREATED_AT_MUST_BE_PROVIDED);
  }

  private void assertHolderMatchesFixture(AccountHolder actual, AccountHolderFixtures expected) {
    assertThat(actual.getPersonalInfo()).isEqualTo(expected.personalInfo());
    assertThat(actual.getContactInfo()).isEqualTo(expected.contactInfo());
    assertThat(actual.getAddress()).isEqualTo(expected.address());
    assertThat(actual.getCreatedAt()).isEqualTo(CREATED_AT);
  }
}