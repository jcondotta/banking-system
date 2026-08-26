package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalTransferRequestedEventTest {

    private static final EventId EVENT_ID = EventId.newId();
    private static final BankTransferId BANK_TRANSFER_ID = BankTransferId.newId();
    private static final BankAccountId SENDER_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final BankAccountId RECIPIENT_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final MonetaryAmount AMOUNT_200_USD = MonetaryAmount.of(new BigDecimal("200.00"), Currency.USD);
    private static final String REFERENCE = "payment for invoice #123";
    private static final Instant OCCURRED_AT = Instant.now();

    @Test
    void shouldCreateEvent_whenAllParamsAreValid() {
        var event = new InternalTransferRequestedEvent(
            EVENT_ID, BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, OCCURRED_AT
        );

        assertThat(event.eventId()).isEqualTo(EVENT_ID);
        assertThat(event.aggregateId()).isEqualTo(BANK_TRANSFER_ID);
        assertThat(event.senderAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
        assertThat(event.recipientAccountId()).isEqualTo(RECIPIENT_ACCOUNT_ID);
        assertThat(event.monetaryAmount()).isEqualTo(AMOUNT_200_USD);
        assertThat(event.reference()).isEqualTo(REFERENCE);
        assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
    }

    @Test
    void shouldCreateEvent_whenReferenceIsNull() {
        var event = new InternalTransferRequestedEvent(
            EVENT_ID, BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, null, OCCURRED_AT
        );

        assertThat(event.reference()).isNull();
    }

    @Test
    void shouldThrowException_whenEventIdIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedEvent(
                null, BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, OCCURRED_AT
            )
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenAggregateIdIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedEvent(
                EVENT_ID, null, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, OCCURRED_AT
            )
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenSenderAccountIdIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedEvent(
                EVENT_ID, BANK_TRANSFER_ID, null, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, OCCURRED_AT
            )
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenRecipientAccountIdIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedEvent(
                EVENT_ID, BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, null, AMOUNT_200_USD, REFERENCE, OCCURRED_AT
            )
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenMonetaryAmountIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedEvent(
                EVENT_ID, BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, null, REFERENCE, OCCURRED_AT
            )
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.MONETARY_AMOUNT_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenOccurredAtIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedEvent(
                EVENT_ID, BANK_TRANSFER_ID, SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT_200_USD, REFERENCE, null
            )
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
    }
}
