package com.jcondotta.banking.accounts.domain.bankaccount.identity;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHolderIdTest {

  private static final UUID ACCOUNT_HOLDER_UUID_1 = UUID.fromString("8b9f0f3a-7f3d-4b7f-9a21-3b1c9c1f8a11");
  private static final UUID ACCOUNT_HOLDER_UUID_2 = UUID.fromString("4e0d6c7b-5b1a-4b62-9a4d-2e6a4c8f3e55");

  @Test
  void shouldCreateAccountHolderId_whenValueIsValid() {
    var accountHolderId = AccountHolderId.of(ACCOUNT_HOLDER_UUID_1);

    assertThat(accountHolderId)
      .extracting(AccountHolderId::value)
      .isEqualTo(ACCOUNT_HOLDER_UUID_1);
  }

  @Test
  void shouldGenerateNewAccountHolderId() {
    var accountHolderId = AccountHolderId.newId();

    assertThat(accountHolderId)
      .extracting(AccountHolderId::value)
      .isNotNull();
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> AccountHolderId.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderId.ACCOUNT_HOLDER_ID_NOT_PROVIDED);
  }

  @Test
  void shouldBeEqual_whenAccountHolderIdsHaveSameValue() {
    var accountHolderId1 = AccountHolderId.of(ACCOUNT_HOLDER_UUID_1);
    var accountHolderId2 = AccountHolderId.of(ACCOUNT_HOLDER_UUID_1);

    assertThat(accountHolderId1)
      .isEqualTo(accountHolderId2)
      .hasSameHashCodeAs(accountHolderId2);
  }

  @Test
  void shouldNotBeEqual_whenAccountHolderIdsHaveDifferentValues() {
    var accountHolderId1 = AccountHolderId.of(ACCOUNT_HOLDER_UUID_1);
    var accountHolderId2 = AccountHolderId.of(ACCOUNT_HOLDER_UUID_2);

    assertThat(accountHolderId1).isNotEqualTo(accountHolderId2);
  }
}