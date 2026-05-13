package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxSerializationExceptionTest {

  @Test
  void shouldIncludeEventClassNameAndCause_whenCreated() {
    var cause = new RuntimeException("boom");

    var exception = new OutboxSerializationException(TestEvent.class, cause);

    assertThat(exception)
      .hasMessage("Failed to serialize integration event: " + TestEvent.class.getName())
      .hasCause(cause);
  }

  private static final class TestEvent {
  }
}
