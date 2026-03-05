package com.jcondotta.domain.core;

import com.jcondotta.domain.identity.EntityId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

  private record FakeId(UUID value) implements EntityId<UUID> {}

  private static class FakeEntity extends Entity<FakeId> {
    protected FakeEntity(FakeId id) {
      super(id);
    }
  }

  @Test
  void shouldExposeEntityId() {
    FakeId id = new FakeId(UUID.randomUUID());
    FakeEntity entity = new FakeEntity(id);

    assertThat(entity.getId()).isEqualTo(id);
  }
}