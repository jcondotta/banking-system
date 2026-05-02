package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecipientSummaryMapperTest {

  private static final Instant CREATED_AT = Instant.parse("2026-04-17T08:00:00Z");

  private final RecipientSummaryMapper mapper = new RecipientSummaryMapper();

  @Test
  void shouldMapEntityToRecipientSummary() {
    var entity = RecipientEntity.builder()
      .id(UUID.randomUUID())
      .bankAccountId(UUID.randomUUID())
      .name("Jefferson Condotta")
      .iban("ES3801283316232166447417")
      .createdAt(CREATED_AT)
      .version(1L)
      .build();

    var summary = mapper.fromEntity(entity);

    assertAll(
      () -> assertThat(summary.recipientId()).isEqualTo(entity.getId()),
      () -> assertThat(summary.recipientName()).isEqualTo(entity.getName()),
      () -> assertThat(summary.iban()).isEqualTo(entity.getIban()),
      () -> assertThat(summary.createdAt()).isEqualTo(entity.getCreatedAt())
    );
  }
}
