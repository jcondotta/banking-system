package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.query;

import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecipientSummaryEntityMapperTest {

  private static final UUID RECIPIENT_ID = UUID.randomUUID();
  private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

  private final RecipientSummaryEntityMapper mapper = new RecipientSummaryEntityMapper();

  @Test
  void shouldMapRecipientSummaryFromEntity() {
    var entity = AccountRecipientEntity.builder()
      .entityType(EntityType.RECIPIENT)
      .recipientId(RECIPIENT_ID)
      .recipientName("Jefferson Condotta")
      .iban("ES3801283316232166447417")
      .createdAt(CREATED_AT)
      .build();

    var summary = mapper.fromEntity(entity);

    assertAll(
      () -> assertThat(summary.recipientId()).isEqualTo(RECIPIENT_ID),
      () -> assertThat(summary.recipientName()).isEqualTo("Jefferson Condotta"),
      () -> assertThat(summary.iban()).isEqualTo("ES3801283316232166447417"),
      () -> assertThat(summary.createdAt()).isEqualTo(CREATED_AT)
    );
  }
}
