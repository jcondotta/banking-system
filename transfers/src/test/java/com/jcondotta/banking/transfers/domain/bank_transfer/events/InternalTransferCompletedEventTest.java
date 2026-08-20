package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalTransferCompletedEventTest {

    private static final EventId EVENT_ID = EventId.newId();
    private static final BankTransferId BANK_TRANSFER_ID = BankTransferId.newId();
    private static final Instant OCCURRED_AT = Instant.now();

    @Test
    void shouldCreateEvent_whenAllParamsAreValid() {
        var event = new InternalTransferCompletedEvent(EVENT_ID, BANK_TRANSFER_ID, OCCURRED_AT);

        assertThat(event.eventId()).isEqualTo(EVENT_ID);
        assertThat(event.aggregateId()).isEqualTo(BANK_TRANSFER_ID);
        assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
    }

    @Test
    void shouldThrowException_whenEventIdIsNull() {
        assertThatThrownBy(() -> new InternalTransferCompletedEvent(null, BANK_TRANSFER_ID, OCCURRED_AT))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenAggregateIdIsNull() {
        assertThatThrownBy(() -> new InternalTransferCompletedEvent(EVENT_ID, null, OCCURRED_AT))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenOccurredAtIsNull() {
        assertThatThrownBy(() -> new InternalTransferCompletedEvent(EVENT_ID, BANK_TRANSFER_ID, null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
    }
}
