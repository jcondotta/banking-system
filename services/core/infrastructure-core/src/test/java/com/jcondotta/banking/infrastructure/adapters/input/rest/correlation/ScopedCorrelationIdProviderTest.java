package com.jcondotta.banking.infrastructure.adapters.input.rest.correlation;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScopedCorrelationIdProviderTest {

  private final ScopedCorrelationIdProvider provider = new ScopedCorrelationIdProvider();

  @Test
  void shouldReturnScopedCorrelationId_whenValueIsBound() throws Exception {
    var correlationId = UUID.fromString("ce75acbd-da91-4aca-ad03-e1fbb11429b6");

    var result = ScopedValue.where(ScopedCorrelationIdProvider.CORRELATION_ID, correlationId)
      .call(provider::get);

    assertThat(result).isEqualTo(correlationId);
  }

  @Test
  void shouldGenerateCorrelationId_whenValueIsNotBound() {
    assertThat(provider.get()).isNotNull();
  }
}
