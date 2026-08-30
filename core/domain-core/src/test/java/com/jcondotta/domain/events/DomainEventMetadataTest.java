package com.jcondotta.domain.events;

import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.testsupport.FakeAggregateId;
import com.jcondotta.domain.validation.DomainEventErrors;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainEventMetadataTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final FakeAggregateId AGGREGATE_ID = FakeAggregateId.newId();
  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  void shouldCreateMetadata_whenAllValuesAreValid() {
    var metadata = new DomainEventMetadata<>(EVENT_ID, AGGREGATE_ID, OCCURRED_AT);

    assertThat(metadata.eventId()).isEqualTo(EVENT_ID);
    assertThat(metadata.aggregateId()).isEqualTo(AGGREGATE_ID);
    assertThat(metadata.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  @Test
  void shouldCreateMetadataWithGeneratedEventId_whenUsingCreateFactory() {
    var metadata = DomainEventMetadata.of(AGGREGATE_ID, OCCURRED_AT);

    assertThat(metadata.eventId()).isNotNull();
    assertThat(metadata.aggregateId()).isEqualTo(AGGREGATE_ID);
    assertThat(metadata.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  @Test
  void shouldThrowException_whenEventIdIsNull() {
    assertThatThrownBy(() -> new DomainEventMetadata<>(null, AGGREGATE_ID, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenAggregateIdIsNull() {
    assertThatThrownBy(() -> new DomainEventMetadata<>(EVENT_ID, null, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenOccurredAtIsNull() {
    assertThatThrownBy(() -> new DomainEventMetadata<>(EVENT_ID, AGGREGATE_ID, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}
