package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalAccountRecipientTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    @Test
    void shouldCreateRecipientCorrectly_whenUsingBankAccountId() {
        var recipient = InternalAccountRecipient.of(BANK_ACCOUNT_ID);

        assertThat(recipient.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    }

    @Test
    void shouldCreateRecipientCorrectly_whenUsingUUID() {
        var recipient = InternalAccountRecipient.of(BANK_ACCOUNT_ID.value());

        assertThat(recipient.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    }

    @Test
    void shouldCreateRecipientCorrectly_whenUsingRawString() {
        var recipient = InternalAccountRecipient.of(BANK_ACCOUNT_ID.value().toString());

        assertThat(recipient.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    }

    @Test
    void shouldThrowException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> InternalAccountRecipient.of((BankAccountId) null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(InternalAccountRecipient.RECIPIENT_ACCOUNT_ID_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenUUIDIsNull() {
        assertThatThrownBy(() -> InternalAccountRecipient.of((UUID) null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankAccountId.ID_NOT_PROVIDED);
    }
}
