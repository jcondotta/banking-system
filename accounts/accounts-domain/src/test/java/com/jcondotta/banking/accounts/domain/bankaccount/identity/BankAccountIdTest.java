package com.jcondotta.banking.accounts.domain.bankaccount.identity;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountIdTest {

  private static final UUID BANK_ACCOUNT_UUID_1 = UUID.fromString("1fcaca1b-92ba-43c1-b45c-bacf92868d31");
  private static final UUID BANK_ACCOUNT_UUID_2 = UUID.fromString("d063f4bd-dd1f-41d0-8f47-0d5b9195bfaa");

  @Test
  void shouldCreateBankAccountId_whenValueIsValid() {
    var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID_1);

    assertThat(bankAccountId)
      .extracting(BankAccountId::value)
      .isEqualTo(BANK_ACCOUNT_UUID_1);
  }

  @Test
  void shouldGenerateNewBankAccountId() {
    var bankAccountId = BankAccountId.newId();

    assertThat(bankAccountId)
      .extracting(BankAccountId::value)
      .isNotNull();
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> BankAccountId.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountId.BANK_ACCOUNT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldBeEqual_whenBankAccountIdsHaveSameValue() {
    var bankAccountId1 = BankAccountId.of(BANK_ACCOUNT_UUID_1);
    var bankAccountId2 = BankAccountId.of(BANK_ACCOUNT_UUID_1);

    assertThat(bankAccountId1)
      .isEqualTo(bankAccountId2)
      .hasSameHashCodeAs(bankAccountId2);
  }

  @Test
  void shouldNotBeEqual_whenBankAccountIdsHaveDifferentValues() {
    var bankAccountId1 = BankAccountId.of(BANK_ACCOUNT_UUID_1);
    var bankAccountId2 = BankAccountId.of(BANK_ACCOUNT_UUID_2);

    assertThat(bankAccountId1).isNotEqualTo(bankAccountId2);
  }
}