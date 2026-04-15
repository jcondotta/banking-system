package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity;

import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRecipientEntityTest {

  @Test
  void shouldIdentifyBankAccountEntity_whenEntityTypeIsBankAccount() {
    var entity = AccountRecipientEntity.builder()
      .entityType(EntityType.BANK_ACCOUNT)
      .build();

    assertThat(entity.isBankAccount()).isTrue();
    assertThat(entity.isRecipient()).isFalse();
  }

  @Test
  void shouldIdentifyRecipientEntity_whenEntityTypeIsRecipient() {
    var entity = AccountRecipientEntity.builder()
      .entityType(EntityType.RECIPIENT)
      .build();

    assertThat(entity.isRecipient()).isTrue();
    assertThat(entity.isBankAccount()).isFalse();
  }

  @Test
  void shouldNotIdentifyKnownEntityType_whenEntityTypeIsMissing() {
    var entity = AccountRecipientEntity.builder().build();

    assertThat(entity.isBankAccount()).isFalse();
    assertThat(entity.isRecipient()).isFalse();
  }
}
