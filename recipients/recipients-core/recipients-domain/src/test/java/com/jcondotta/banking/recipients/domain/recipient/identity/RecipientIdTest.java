package com.jcondotta.banking.recipients.domain.recipient.identity;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientIdTest {

  private static final UUID RECIPIENT_UUID_1 = UUID.fromString("ff9cdd7f-9a2a-4b0a-9e53-6e9f1d482d4e");

  @Test
  void shouldCreateRecipientId_whenValueIsValid() {
    var recipientId = RecipientId.of(RECIPIENT_UUID_1);

    assertThat(recipientId)
        .isNotNull()
        .extracting(RecipientId::value)
        .isEqualTo(RECIPIENT_UUID_1);
  }

  @Test
  void shouldGenerateNewRecipientId_whenCallingNewId() {
    var recipientId = RecipientId.newId();

    assertThat(recipientId).isNotNull().extracting(RecipientId::value).isNotNull();
  }

  @Test
  void shouldBeEqual_whenRecipientIdsHaveSameValue() {
    var recipientId1 = RecipientId.of(RECIPIENT_UUID_1);
    var recipientId2 = RecipientId.of(RECIPIENT_UUID_1);

    assertThat(recipientId1)
        .isEqualTo(recipientId2)
        .hasSameHashCodeAs(recipientId2);
  }

  @Test
  void shouldNotBeEqual_whenRecipientIdsHaveDifferentValues() {
    var recipientId1 = RecipientId.newId();
    var recipientId2 = RecipientId.newId();

    assertThat(recipientId1).isNotEqualTo(recipientId2);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> RecipientId.of(null))
        .isInstanceOf(DomainValidationException.class)
        .hasMessage(RecipientId.ID_NOT_PROVIDED);
  }
}
