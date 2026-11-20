package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecipientEntityMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final Instant CREATED_AT = Instant.parse("2026-04-17T08:00:00Z");

  private final RecipientEntityMapper mapper = new RecipientEntityMapper();

  @Test
  void shouldMapRecipientToEntity() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(BANK_ACCOUNT_ID);

    var entity = mapper.toEntity(recipient);

    assertAll(
      () -> assertThat(entity.getId()).isEqualTo(recipient.getId().value()),
      () -> assertThat(entity.getBankAccountId()).isEqualTo(recipient.getBankAccountId().value()),
      () -> assertThat(entity.getName()).isEqualTo(recipient.getRecipientName().value()),
      () -> assertThat(entity.getIban()).isEqualTo(recipient.getIban().value()),
      () -> assertThat(entity.getCreatedAt()).isEqualTo(recipient.getCreatedAt()),
      () -> assertThat(entity.getVersion()).isNull()
    );
  }

  @Test
  void shouldMapEntityToRecipient() {
    var recipientId = UUID.randomUUID();
    var bankAccountId = UUID.randomUUID();
    var entity = RecipientEntity.builder()
      .id(recipientId)
      .bankAccountId(bankAccountId)
      .name("Jefferson Condotta")
      .iban("ES3801283316232166447417")
      .createdAt(CREATED_AT)
      .version(3L)
      .build();

    var recipient = mapper.toDomain(entity);

    assertAll(
      () -> assertThat(recipient.getId()).isEqualTo(RecipientId.of(recipientId)),
      () -> assertThat(recipient.getBankAccountId()).isEqualTo(BankAccountId.of(bankAccountId)),
      () -> assertThat(recipient.getRecipientName()).isEqualTo(RecipientName.of(entity.getName())),
      () -> assertThat(recipient.getIban()).isEqualTo(Iban.of(entity.getIban())),
      () -> assertThat(recipient.getCreatedAt()).isEqualTo(entity.getCreatedAt()),
      () -> assertThat(recipient.getVersion()).isEqualTo(entity.getVersion())
    );
  }
}
