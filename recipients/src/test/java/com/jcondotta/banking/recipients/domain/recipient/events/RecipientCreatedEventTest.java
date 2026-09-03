package com.jcondotta.banking.recipients.domain.recipient.events;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientCreatedEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(java.util.UUID.randomUUID());
  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");
  private static final DomainEventMetadata<RecipientId> METADATA = DomainEventMetadata.of(
    EVENT_ID,
    RECIPIENT_ID,
    OCCURRED_AT
  );

  @Test
  void shouldCreateEvent_whenAllValuesAreValid() {
    var data = new RecipientCreatedData(
      BANK_ACCOUNT_ID.value(),
      "Jefferson Silva",
      "BR1200000000000000000000000"
    );
    var event = new RecipientCreatedEvent(
      METADATA,
      data
    );

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(RECIPIENT_ID);
    assertThat(event.data()).isEqualTo(data);
    assertThat(event.data().bankAccountId()).isEqualTo(BANK_ACCOUNT_ID.value());
    assertThat(event.data().name()).isEqualTo("Jefferson Silva");
    assertThat(event.data().iban()).isEqualTo("BR1200000000000000000000000");
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
    assertThat(event.eventType()).isEqualTo(RecipientCreatedEvent.EVENT_TYPE);
    assertThat(event.eventVersion()).isEqualTo(RecipientCreatedEvent.EVENT_VERSION);
  }

  @Test
  void shouldThrowException_whenMetadataIsNull() {
    var data = new RecipientCreatedData(BANK_ACCOUNT_ID.value(), "Name", "BR1200000000000000000000000");

    assertThatThrownBy(() -> new RecipientCreatedEvent(null, data))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new RecipientCreatedData(null, "Name", "BR1200000000000000000000000"))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsBlank() {
    assertThatThrownBy(() -> new RecipientCreatedData(BANK_ACCOUNT_ID.value(), " ", "BR1200000000000000000000000"))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIbanIsBlank() {
    assertThatThrownBy(() -> new RecipientCreatedData(BANK_ACCOUNT_ID.value(), "Name", ""))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.IBAN_NOT_PROVIDED);
  }
}
