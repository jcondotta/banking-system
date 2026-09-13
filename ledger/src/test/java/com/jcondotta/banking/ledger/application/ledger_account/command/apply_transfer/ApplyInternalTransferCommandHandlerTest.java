package com.jcondotta.banking.ledger.application.ledger_account.command.apply_transfer;

import com.jcondotta.banking.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.ledger.domain.ledger_account.entries.LedgerEntry;
import com.jcondotta.banking.ledger.domain.ledger_account.exceptions.LedgerAccountNotYetProvisionedException;
import com.jcondotta.banking.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.movement.MovementAmount;
import com.jcondotta.banking.movement.MovementType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyInternalTransferCommandHandlerTest {

    private static final UUID TRANSFER_ID = UUID.randomUUID();
    private static final UUID SENDER_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID RECIPIENT_ACCOUNT_ID = UUID.randomUUID();
    private static final MovementAmount MOVEMENT_AMOUNT = MovementAmount.of(new BigDecimal("100.00"), Currency.EUR);
    private static final Instant REQUESTED_AT = Instant.parse("2026-05-16T10:15:30Z");

    @Mock
    private LedgerAccountRepository ledgerAccountRepository;

    @Captor
    private ArgumentCaptor<List<LedgerEntry>> ledgerEntriesCaptor;

    private ApplyInternalTransferCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApplyInternalTransferCommandHandler(ledgerAccountRepository);
    }

    @Test
    void shouldApplyTransferAndSaveEntries_whenBothAccountsExist() {
        var senderAccount = LedgerAccount.provision(
                AccountReference.of(SENDER_ACCOUNT_ID), Currency.EUR, REQUESTED_AT);
        var recipientAccount = LedgerAccount.provision(
                AccountReference.of(RECIPIENT_ACCOUNT_ID), Currency.EUR, REQUESTED_AT);

        when(ledgerAccountRepository.findByAccountReference(AccountReference.of(SENDER_ACCOUNT_ID)))
                .thenReturn(Optional.of(senderAccount));
        when(ledgerAccountRepository.findByAccountReference(AccountReference.of(RECIPIENT_ACCOUNT_ID)))
                .thenReturn(Optional.of(recipientAccount));

        var command = new ApplyInternalTransferCommand(
                TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, MOVEMENT_AMOUNT, REQUESTED_AT);

        handler.handle(command);

        verify(ledgerAccountRepository, times(2)).updateBalance(any());
        verify(ledgerAccountRepository).saveLedgerEntries(ledgerEntriesCaptor.capture());

        var savedEntries = ledgerEntriesCaptor.getValue();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(savedEntries).hasSize(2);

            var debitEntry = savedEntries.stream()
                    .filter(e -> e.movementType() == MovementType.DEBIT)
                    .findFirst();
            var creditEntry = savedEntries.stream()
                    .filter(e -> e.movementType() == MovementType.CREDIT)
                    .findFirst();

            softly.assertThat(debitEntry).isPresent();
            softly.assertThat(creditEntry).isPresent();

            debitEntry.ifPresent(e -> {
                softly.assertThat(e.accountId()).isEqualTo(senderAccount.getId());
                softly.assertThat(e.transferId()).isEqualTo(TRANSFER_ID);
                softly.assertThat(e.amount()).isEqualByComparingTo(MOVEMENT_AMOUNT.amount());
                softly.assertThat(e.currency()).isEqualTo(Currency.EUR);
                softly.assertThat(e.createdAt()).isEqualTo(REQUESTED_AT);
            });

            creditEntry.ifPresent(e -> {
                softly.assertThat(e.accountId()).isEqualTo(recipientAccount.getId());
                softly.assertThat(e.transferId()).isEqualTo(TRANSFER_ID);
                softly.assertThat(e.amount()).isEqualByComparingTo(MOVEMENT_AMOUNT.amount());
                softly.assertThat(e.currency()).isEqualTo(Currency.EUR);
                softly.assertThat(e.createdAt()).isEqualTo(REQUESTED_AT);
            });
        });
    }

    @Test
    void shouldThrowException_whenSenderAccountNotFound() {
        when(ledgerAccountRepository.findByAccountReference(AccountReference.of(SENDER_ACCOUNT_ID)))
                .thenReturn(Optional.empty());

        var command = new ApplyInternalTransferCommand(
                TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, MOVEMENT_AMOUNT, REQUESTED_AT);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(LedgerAccountNotYetProvisionedException.class)
                .satisfies(ex -> assertThat(((LedgerAccountNotYetProvisionedException) ex).getAccountReference())
                        .isEqualTo(SENDER_ACCOUNT_ID));

        verify(ledgerAccountRepository, never()).updateBalance(any());
        verify(ledgerAccountRepository, never()).saveLedgerEntries(any());
    }

    @Test
    void shouldThrowException_whenRecipientAccountNotFound() {
        var senderAccount = LedgerAccount.provision(
                AccountReference.of(SENDER_ACCOUNT_ID), Currency.EUR, REQUESTED_AT);

        when(ledgerAccountRepository.findByAccountReference(AccountReference.of(SENDER_ACCOUNT_ID)))
                .thenReturn(Optional.of(senderAccount));
        when(ledgerAccountRepository.findByAccountReference(AccountReference.of(RECIPIENT_ACCOUNT_ID)))
                .thenReturn(Optional.empty());

        var command = new ApplyInternalTransferCommand(
                TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, MOVEMENT_AMOUNT, REQUESTED_AT);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(LedgerAccountNotYetProvisionedException.class)
                .satisfies(ex -> assertThat(((LedgerAccountNotYetProvisionedException) ex).getAccountReference())
                        .isEqualTo(RECIPIENT_ACCOUNT_ID));

        verify(ledgerAccountRepository, never()).updateBalance(any());
        verify(ledgerAccountRepository, never()).saveLedgerEntries(any());
    }
}
