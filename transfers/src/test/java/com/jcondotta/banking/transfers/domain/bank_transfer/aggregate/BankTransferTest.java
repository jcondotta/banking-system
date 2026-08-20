package com.jcondotta.banking.transfers.domain.bank_transfer.aggregate;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferType;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferCompletedEvent;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferRequestedEvent;
import com.jcondotta.banking.transfers.domain.bank_transfer.exceptions.InvalidTransferStatusTransitionException;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.banking.transfers.domain.monetary_movement.enums.MovementType;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankTransferTest {

    private static final BankTransferId BANK_TRANSFER_ID = BankTransferId.newId();
    private static final BankAccountId SENDER_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final BankAccountId RECIPIENT_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final MonetaryAmount AMOUNT_200_USD = MonetaryAmount.of(new BigDecimal("200.00"), Currency.USD);
    private static final String REFERENCE = "payment for invoice #123";
    private static final Instant REQUESTED_AT = Instant.parse("2026-05-16T10:15:30Z");
    private static final Instant COMPLETED_AT = Instant.parse("2026-05-16T10:16:30Z");

    @Test
    void shouldRequestInternalTransfer_whenParamsAreValid() {
        var transfer = requestInternalTransfer();

        assertThat(transfer.getId()).isEqualTo(BANK_TRANSFER_ID);
        assertThat(transfer.getTransferType()).isEqualTo(TransferType.INTERNAL);
        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.PENDING);
        assertThat(transfer.getReference()).isEqualTo(REFERENCE);
        assertThat(transfer.getCreatedAt()).isEqualTo(REQUESTED_AT);
        assertThat(transfer.getTransferEntries()).hasSize(2);
    }

    @Test
    void shouldRequestInternalTransfer_withNullReference() {
        var transfer = BankTransfer.requestInternalTransfer(
            BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, null, REQUESTED_AT
        );

        assertThat(transfer.getReference()).isNull();
        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.PENDING);
    }

    @Test
    void shouldCreateDebitAndCreditEntries_whenInternalTransferRequested() {
        var transfer = requestInternalTransfer();

        var entries = transfer.getTransferEntries();
        assertThat(entries).hasSize(2);
        assertThat(entries).anyMatch(e -> e.movementType() == MovementType.DEBIT);
        assertThat(entries).anyMatch(e -> e.movementType() == MovementType.CREDIT);
    }

    @Test
    void shouldRegisterInternalTransferRequestedEvent_whenInternalTransferRequested() {
        var transfer = requestInternalTransfer();

        var events = transfer.pullEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(InternalTransferRequestedEvent.class);

        var event = (InternalTransferRequestedEvent) events.getFirst();
        assertThat(event.aggregateId()).isEqualTo(BANK_TRANSFER_ID);
        assertThat(event.senderAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
        assertThat(event.recipientAccountId()).isEqualTo(RECIPIENT_ACCOUNT_ID);
        assertThat(event.monetaryAmount()).isEqualTo(AMOUNT_200_USD);
        assertThat(event.reference()).isEqualTo(REFERENCE);
        assertThat(event.occurredAt()).isEqualTo(REQUESTED_AT);
    }

    @Test
    void shouldThrowException_whenBankTransferIdIsNull() {
        assertThatThrownBy(() ->
            BankTransfer.requestInternalTransfer(null, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, REQUESTED_AT)
        )
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankTransferErrors.ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenSenderAccountIdIsNull() {
        assertThatThrownBy(() ->
            BankTransfer.requestInternalTransfer(BANK_TRANSFER_ID, null, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, REQUESTED_AT)
        )
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenRecipientAccountIdIsNull() {
        assertThatThrownBy(() ->
            BankTransfer.requestInternalTransfer(BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, null, AMOUNT_200_USD, REFERENCE, REQUESTED_AT)
        )
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenMonetaryAmountIsNull() {
        assertThatThrownBy(() ->
            BankTransfer.requestInternalTransfer(BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, null, REFERENCE, REQUESTED_AT)
        )
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankTransferErrors.MONETARY_AMOUNT_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenRequestedAtIsNull() {
        assertThatThrownBy(() ->
            BankTransfer.requestInternalTransfer(BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, null)
        )
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankTransferErrors.REQUESTED_AT_MUST_BE_PROVIDED);
    }

    @Test
    void shouldCompleteTransfer_whenStatusIsPending() {
        var transfer = requestInternalTransfer();
        transfer.pullEvents();

        transfer.complete(COMPLETED_AT);

        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.COMPLETED);
    }

    @Test
    void shouldRegisterInternalTransferCompletedEvent_whenTransferCompleted() {
        var transfer = requestInternalTransfer();
        transfer.pullEvents();

        transfer.complete(COMPLETED_AT);

        var events = transfer.pullEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(InternalTransferCompletedEvent.class);

        var event = (InternalTransferCompletedEvent) events.getFirst();
        assertThat(event.aggregateId()).isEqualTo(BANK_TRANSFER_ID);
        assertThat(event.occurredAt()).isEqualTo(COMPLETED_AT);
    }

    @Test
    void shouldBeIdempotent_whenCompleteCalledOnAlreadyCompletedTransfer() {
        var transfer = requestInternalTransfer();
        transfer.pullEvents();
        transfer.complete(COMPLETED_AT);
        transfer.pullEvents();

        transfer.complete(COMPLETED_AT);

        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(transfer.pullEvents()).isEmpty();
    }

    @Test
    void shouldThrowException_whenCompleteCalledOnFailedTransfer() {
        var transfer = requestInternalTransfer();
        transfer.fail();

        assertThatThrownBy(() -> transfer.complete(COMPLETED_AT))
            .isInstanceOf(InvalidTransferStatusTransitionException.class)
            .hasMessageContaining(TransferStatus.FAILED.name())
            .hasMessageContaining(TransferStatus.COMPLETED.name());
    }

    @Test
    void shouldThrowException_whenCompletedAtIsNull() {
        var transfer = requestInternalTransfer();

        assertThatThrownBy(() -> transfer.complete(null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(BankTransferErrors.COMPLETED_AT_MUST_BE_PROVIDED);
    }

    @Test
    void shouldFailTransfer_whenStatusIsPending() {
        var transfer = requestInternalTransfer();

        transfer.fail();

        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.FAILED);
    }

    @Test
    void shouldNotRegisterEvent_whenTransferFailed() {
        var transfer = requestInternalTransfer();
        transfer.pullEvents();

        transfer.fail();

        assertThat(transfer.pullEvents()).isEmpty();
    }

    @Test
    void shouldBeIdempotent_whenFailCalledOnAlreadyFailedTransfer() {
        var transfer = requestInternalTransfer();
        transfer.fail();

        transfer.fail();

        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.FAILED);
    }

    @Test
    void shouldThrowException_whenFailCalledOnCompletedTransfer() {
        var transfer = requestInternalTransfer();
        transfer.complete(COMPLETED_AT);

        assertThatThrownBy(transfer::fail)
            .isInstanceOf(InvalidTransferStatusTransitionException.class)
            .hasMessageContaining(TransferStatus.COMPLETED.name())
            .hasMessageContaining(TransferStatus.FAILED.name());
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldRequestInternalTransfer_forAllCurrencies(Currency currency) {
        var amount = MonetaryAmount.of(new BigDecimal("100.00"), currency);
        var transfer = BankTransfer.requestInternalTransfer(
            BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, amount, null, REQUESTED_AT
        );

        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.PENDING);
        assertThat(transfer.getTransferEntries()).hasSize(2);
        assertThat(transfer.getTransferEntries()).allMatch(e -> e.currency() == currency);
    }

    private static BankTransfer requestInternalTransfer() {
        return BankTransfer.requestInternalTransfer(
            BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, REQUESTED_AT
        );
    }
}
