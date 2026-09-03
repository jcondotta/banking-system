package com.jcondotta.banking.recipients.domain.recipient.events;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientDeletedEventTest {

  private static final EventId EVENT_ID = EventId.of(UUID.fromString("90854175-5da4-4775-82a0-243a602a59df"));
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.fromString("10d723ea-fe73-4d58-9ed0-97c248955496"));
  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("c328e5b7-0bf8-4acb-b09a-1dd0ed475c22");
  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  void shouldCreateDeletedEvent() {
    var metadata = DomainEventMetadata.of(EVENT_ID, RECIPIENT_ID, OCCURRED_AT);
    var data = new RecipientDeletedData(BANK_ACCOUNT_ID);

    var event = new RecipientDeletedEvent(metadata, data);

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(RECIPIENT_ID);
    assertThat(event.eventType()).isEqualTo(RecipientDeletedEvent.EVENT_TYPE);
    assertThat(event.eventVersion()).isEqualTo(RecipientDeletedEvent.EVENT_VERSION);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
    assertThat(event.data()).isEqualTo(data);
  }

  @Test
  void shouldRejectNullMetadata() {
    assertThatThrownBy(() -> new RecipientDeletedEvent(null, new RecipientDeletedData(BANK_ACCOUNT_ID)))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldRejectNullData() {
    assertThatThrownBy(() -> new RecipientDeletedEvent(
      DomainEventMetadata.of(RECIPIENT_ID, OCCURRED_AT),
      null
    )).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldRejectNullBankAccountId() {
    assertThatThrownBy(() -> new RecipientDeletedData(null))
      .isInstanceOf(DomainValidationException.class);
  }
}
