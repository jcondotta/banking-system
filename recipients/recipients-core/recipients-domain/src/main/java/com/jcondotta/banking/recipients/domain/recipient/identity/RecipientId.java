package com.jcondotta.banking.recipients.domain.recipient.identity;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.support.DomainPreconditions;

import java.util.UUID;

public record RecipientId(UUID value) implements AggregateId<UUID> {

  public static final String ID_NOT_PROVIDED = "recipient id value must be provided";

  public RecipientId {
    DomainPreconditions.required(value, ID_NOT_PROVIDED);
  }

  public static RecipientId of(UUID value) {
    return new RecipientId(value);
  }

  public static RecipientId newId() {
    return new RecipientId(UUID.randomUUID());
  }
}
