package com.jcondotta.domain.testsupport;

import com.jcondotta.domain.identity.EntityId;

import java.util.UUID;

public record FakeId(UUID value) implements EntityId<UUID> {

  public static FakeId newId() {
    return new FakeId(UUID.randomUUID());
  }
}