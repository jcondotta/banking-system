package com.jcondotta.banking.transfers.domain.bank_transfer.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransferTypeTest {

    @Test
    void shouldAssertInfoCorrectly_whenTransferTypeIsInternal() {
        assertThat(TransferType.INTERNAL)
            .satisfies(transferType -> {
                assertThat(transferType.isInternal()).isTrue();
                assertThat(transferType.isIncomingExternal()).isFalse();
                assertThat(transferType.isOutgoingExternal()).isFalse();
            });
    }

    @Test
    void shouldAssertInfoCorrectly_whenTransferTypeIsIncomingExternal() {
        assertThat(TransferType.INCOMING_EXTERNAL)
            .satisfies(transferType -> {
                assertThat(transferType.isIncomingExternal()).isTrue();
                assertThat(transferType.isInternal()).isFalse();
                assertThat(transferType.isOutgoingExternal()).isFalse();
            });
    }

    @Test
    void shouldAssertInfoCorrectly_whenTransferTypeIsOutgoingExternal() {
        assertThat(TransferType.OUTGOING_EXTERNAL)
            .satisfies(transferType -> {
                assertThat(transferType.isOutgoingExternal()).isTrue();
                assertThat(transferType.isIncomingExternal()).isFalse();
                assertThat(transferType.isInternal()).isFalse();
            });
    }
}
