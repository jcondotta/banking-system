package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecipientSummaryMapperTest {

  private static final String RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName().value();
  private static final String RECIPIENT_IBAN = RecipientFixtures.JEFFERSON.toIban().value();

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  private final RecipientSummaryMapper mapper = new RecipientSummaryMapper();

  @Test
  void shouldMapEntityToRecipientSummary() {
    var entity = RecipientEntity.builder()
      .id(UUID.randomUUID())
      .bankAccountId(UUID.randomUUID())
      .name(RECIPIENT_NAME)
      .iban(RECIPIENT_IBAN)
      .createdAt(CREATED_AT)
      .version(1L)
      .build();

    var summary = mapper.fromEntity(entity);

    assertAll(
      () -> assertThat(summary.recipientId()).isEqualTo(entity.getId()),
      () -> assertThat(summary.recipientName()).isEqualTo(RECIPIENT_NAME),
      () -> assertThat(summary.iban()).isEqualTo(IbanMasker.mask(RECIPIENT_IBAN)),
      () -> assertThat(summary.createdAt()).isEqualTo(CREATED_AT)
    );
  }
}
