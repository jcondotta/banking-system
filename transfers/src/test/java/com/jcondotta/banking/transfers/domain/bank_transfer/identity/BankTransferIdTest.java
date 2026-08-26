package com.jcondotta.banking.transfers.domain.bank_transfer.identity;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankTransferIdTest {

    private static final UUID BANK_TRANSFER_UUID_1 = UUID.fromString("1fcaca1b-92ba-43c1-b45c-bacf92868d31");
    private static final UUID BANK_TRANSFER_UUID_2 = UUID.fromString("d063f4bd-dd1f-41d0-8f47-0d5b9195bfaa");

    @Test
    void shouldCreateBankTransferId_whenValueIsValid() {
        var bankTransferId = BankTransferId.of(BANK_TRANSFER_UUID_1);

        assertThat(bankTransferId)
            .isNotNull()
            .extracting(BankTransferId::value)
            .isEqualTo(BANK_TRANSFER_UUID_1);
    }

    @Test
    void shouldThrowException_whenValueIsNull() {
        assertThatThrownBy(() -> BankTransferId.of(null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferId.ID_NOT_PROVIDED);
    }

    @Test
    void shouldBeEqual_whenBankTransferIdsHaveSameValue() {
        var bankTransferId1 = BankTransferId.of(BANK_TRANSFER_UUID_1);
        var bankTransferId2 = BankTransferId.of(BANK_TRANSFER_UUID_1);

        assertThat(bankTransferId1)
            .isEqualTo(bankTransferId2)
            .hasSameHashCodeAs(bankTransferId2);
    }

    @Test
    void shouldNotBeEqual_whenBankTransferIdsHaveDifferentValues() {
        var bankTransferId1 = BankTransferId.of(BANK_TRANSFER_UUID_1);
        var bankTransferId2 = BankTransferId.of(BANK_TRANSFER_UUID_2);

        assertThat(bankTransferId1).isNotEqualTo(bankTransferId2);
    }
}
