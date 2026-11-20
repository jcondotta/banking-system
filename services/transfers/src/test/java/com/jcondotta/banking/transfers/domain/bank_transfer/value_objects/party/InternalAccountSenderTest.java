package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalAccountSenderTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    @Test
    void shouldCreateSenderCorrectly_whenUsingBankAccountId() {
        var sender = InternalAccountSender.of(BANK_ACCOUNT_ID);

        assertThat(sender.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    }

    @Test
    void shouldCreateSenderCorrectly_whenUsingUUID() {
        var sender = InternalAccountSender.of(BANK_ACCOUNT_ID.value());

        assertThat(sender.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    }

    @Test
    void shouldThrowException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> InternalAccountSender.of((BankAccountId) null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(InternalAccountSender.SENDER_ACCOUNT_ID_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenUUIDIsNull() {
        assertThatThrownBy(() -> InternalAccountSender.of((UUID) null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankAccountId.ID_NOT_PROVIDED);
    }
}
