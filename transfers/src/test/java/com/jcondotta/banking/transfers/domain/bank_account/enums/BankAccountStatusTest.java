package com.jcondotta.banking.transfers.domain.bank_account.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountStatusTest {

    @Test
    void shouldReturnTrue_whenBankAccountStatusIsActive() {
        assertThat(BankAccountStatus.ACTIVE.isActive()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenBankAccountStatusIsPending() {
        assertThat(BankAccountStatus.PENDING.isActive()).isFalse();
    }

    @Test
    void shouldReturnFalse_whenBankAccountStatusIsBlocked() {
        assertThat(BankAccountStatus.BLOCKED.isActive()).isFalse();
    }

    @Test
    void shouldReturnFalse_whenBankAccountStatusIsClosed() {
        assertThat(BankAccountStatus.CLOSED.isActive()).isFalse();
    }
}
