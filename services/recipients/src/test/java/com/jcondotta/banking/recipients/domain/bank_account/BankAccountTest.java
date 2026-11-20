package com.jcondotta.banking.recipients.domain.bank_account;

import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Test
  void shouldCreateActiveBankAccount() {
    var bankAccount = BankAccount.active(BANK_ACCOUNT_ID);

    assertThat(bankAccount.id()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(bankAccount.status()).isEqualTo(BankAccountStatus.ACTIVE);
    assertThat(bankAccount.isActive()).isTrue();
  }

  @ParameterizedTest
  @EnumSource(value = BankAccountStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
  void shouldReportInactive_whenStatusIsNotActive(BankAccountStatus status) {
    assertThat(new BankAccount(BANK_ACCOUNT_ID, status).isActive()).isFalse();
  }

  @Test
  void shouldRejectNullId() {
    assertThatThrownBy(() -> new BankAccount(null, BankAccountStatus.ACTIVE))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccount.ID_NOT_PROVIDED);
  }

  @Test
  void shouldRejectNullStatus() {
    assertThatThrownBy(() -> new BankAccount(BANK_ACCOUNT_ID, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccount.STATUS_NOT_PROVIDED);
  }
}
