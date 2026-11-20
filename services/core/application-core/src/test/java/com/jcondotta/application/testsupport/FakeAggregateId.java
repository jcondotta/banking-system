package com.jcondotta.application.testsupport;

import com.jcondotta.domain.identity.AggregateId;

import java.util.UUID;

public record FakeAggregateId(UUID value) implements AggregateId<UUID> {

  public static FakeAggregateId newId() {
    return new FakeAggregateId(UUID.randomUUID());
  }
}