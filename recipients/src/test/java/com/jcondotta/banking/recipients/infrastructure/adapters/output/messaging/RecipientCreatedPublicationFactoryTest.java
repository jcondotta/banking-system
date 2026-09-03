package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedData;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientCreatedPublicationFactoryTest {

  private static final EventId EVENT_ID = EventId.of(UUID.fromString("90854175-5da4-4775-82a0-243a602a59df"));
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.fromString("10d723ea-fe73-4d58-9ed0-97c248955496"));
  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("c328e5b7-0bf8-4acb-b09a-1dd0ed475c22");
  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

  private final RecipientCreatedPublicationFactory factory = new RecipientCreatedPublicationFactory();

  @Test
  void shouldMapRecipientCreatedEventToPublication() {
    var event = recipientCreatedEvent();

    var publication = factory.create(event);

    assertThat(factory.domainEventType()).isEqualTo(RecipientCreatedEvent.class);
    assertThat(publication.event()).isSameAs(event);
    assertThat(publication.destination()).isEqualTo("recipients-created");
    assertThat(publication.key()).isEqualTo(BANK_ACCOUNT_ID.toString());
  }

  private static RecipientCreatedEvent recipientCreatedEvent() {
    return new RecipientCreatedEvent(
      DomainEventMetadata.of(EVENT_ID, RECIPIENT_ID, OCCURRED_AT),
      new RecipientCreatedData(BANK_ACCOUNT_ID, "Isabella Condotta", "BE68539007547034")
    );
  }
}
