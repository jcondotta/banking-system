package com.jcondotta.domain.identity;

import com.jcondotta.domain.support.Preconditions;

import java.util.UUID;

public record EventId(UUID value) {

  public static final String EVENT_ID_NOT_PROVIDED = "Event id must be provided.";

  public EventId {
    Preconditions.required(value, EVENT_ID_NOT_PROVIDED);
  }

  public static EventId newId() {
    return new EventId(UUID.randomUUID());
  }

  public static EventId of(UUID value) {
    return new EventId(value);
  }
}
