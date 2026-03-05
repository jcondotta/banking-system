package com.jcondotta.domain.identity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityIdTest {

  private record FakeEntityId(UUID value) implements EntityId<UUID> {}

  @Test
  void shouldReturnValueAsString_whenCallingAsString() {
    var uuid = UUID.randomUUID();
    EntityId<UUID> id = new FakeEntityId(uuid);

    assertThat(id.asString())
      .isEqualTo(uuid.toString());
  }
}