package com.jcondotta.application.events;

import com.jcondotta.application.testsupport.argument_provider.BlankValuesArgumentProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IntegrationEventMetadataTest {

  private final UUID eventId = UUID.randomUUID();
  private final UUID correlationId = UUID.randomUUID();
  private final String eventSource = "core-service";
  private final int version = 1;
  private final Instant occurredAt = Instant.now();

  @Test
  void shouldCreateIntegrationEventMetadata_whenAllFieldsAreValid() {
    var metadata = IntegrationEventMetadata.of(eventId, correlationId, eventSource, version, occurredAt);

    assertThat(metadata.eventId()).isEqualTo(eventId);
    assertThat(metadata.correlationId()).isEqualTo(correlationId);
    assertThat(metadata.eventSource()).isEqualTo(eventSource);
    assertThat(metadata.version()).isEqualTo(version);
    assertThat(metadata.occurredAt()).isEqualTo(occurredAt);
  }

  @Test
  void shouldCreateIntegrationEventMetadataWithDefaultVersion_whenVersionIsNotProvided() {
    var metadata = IntegrationEventMetadata.of(eventId, correlationId, eventSource, occurredAt);

    assertThat(metadata.eventId()).isEqualTo(eventId);
    assertThat(metadata.correlationId()).isEqualTo(correlationId);
    assertThat(metadata.eventSource()).isEqualTo(eventSource);
    assertThat(metadata.version()).isEqualTo(IntegrationEventMetadata.DEFAULT_EVENT_VERSION);
    assertThat(metadata.occurredAt()).isEqualTo(occurredAt);
  }

  @Test
  void shouldThrowException_whenEventIdIsNull() {
    assertThatThrownBy(() -> IntegrationEventMetadata.of(null, correlationId, eventSource, version, occurredAt))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(IntegrationEventMetadata.EVENT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenCorrelationIdIsNull() {
    assertThatThrownBy(() -> IntegrationEventMetadata.of(eventId, null, eventSource, version, occurredAt))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(IntegrationEventMetadata.CORRELATION_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenEventSourceIsNull() {
    assertThatThrownBy(() -> IntegrationEventMetadata.of(eventId, correlationId, null, version, occurredAt))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(IntegrationEventMetadata.EVENT_SOURCE_REQUIRED);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenEventSourceIsBlank(String blankEventSource) {
    assertThatThrownBy(() -> IntegrationEventMetadata.of(eventId, correlationId, blankEventSource, version, occurredAt))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(IntegrationEventMetadata.EVENT_SOURCE_REQUIRED);
  }

  @Test
  void shouldThrowException_whenOccurredAtIsNull() {
    assertThatThrownBy(() -> IntegrationEventMetadata.of(eventId, correlationId, eventSource, version, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(IntegrationEventMetadata.OCCURRED_AT_REQUIRED);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -10})
  void shouldThrowException_whenVersionIsNotGreaterThanZero(int invalidVersion) {
    assertThatThrownBy(() -> IntegrationEventMetadata.of(eventId, correlationId, eventSource, invalidVersion, occurredAt))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(IntegrationEventMetadata.VERSION_MUST_BE_GREATER_THAN_ZERO);
  }
}