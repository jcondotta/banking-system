package com.jcondotta.domain.identity;

import com.jcondotta.domain.testsupport.FakeId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityIdTest {

  @Test
  void shouldReturnValueAsString_whenCallingAsString() {
    EntityId<?> id = FakeId.newId();

    assertThat(id.asString())
      .isEqualTo(id.value().toString());
  }
}
