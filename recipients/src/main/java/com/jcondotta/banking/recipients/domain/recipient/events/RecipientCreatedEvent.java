package com.jcondotta.banking.recipients.domain.recipient.events;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.validation.DomainEventErrors;

import static com.jcondotta.domain.support.Preconditions.required;

public record RecipientCreatedEvent(DomainEventMetadata<RecipientId> metadata, RecipientCreatedData data)
  implements DomainEvent<RecipientId, RecipientCreatedData> {

  public static final String EVENT_TYPE = "recipient-created";
  public static final int EVENT_VERSION = 1;

  public RecipientCreatedEvent {
    required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
    required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
  }

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }

  @Override
  public int eventVersion() {
    return EVENT_VERSION;
  }
}
