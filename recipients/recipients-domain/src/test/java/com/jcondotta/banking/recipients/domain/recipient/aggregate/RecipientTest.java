package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.fixtures.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientTest {

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();
  private static final RecipientStatus STATUS_ACTIVE = RecipientStatus.ACTIVE;

  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldCreateRecipient_whenUsingFactoryMethod() {
    var recipient = RecipientFixtures.JEFFERSON.create();

    assertThat(recipient.getId()).isNotNull();
    assertThat(recipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(recipient.getIban()).isEqualTo(IBAN);
    assertThat(recipient.isActive()).isTrue();
    assertThat(recipient.getCreatedAt()).isEqualTo(CREATED_AT);
  }

  @ParameterizedTest
  @EnumSource(RecipientStatus.class)
  void shouldRestoreRecipient_whenAllValuesAreProvided(RecipientStatus status) {
    var recipient = Recipient.restore(RECIPIENT_ID, RECIPIENT_NAME, IBAN, status, CREATED_AT);

    assertThat(recipient.getId()).isEqualTo(RECIPIENT_ID);
    assertThat(recipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(recipient.getIban()).isEqualTo(IBAN);
    assertThat(recipient.getStatus()).isEqualTo(status);
    assertThat(recipient.getCreatedAt()).isEqualTo(CREATED_AT);
  }

  @Test
  void shouldDeactivateRecipient_whenRemoveIsCalled() {
    var recipient = RecipientFixtures.JEFFERSON.create();
    recipient.remove();

    assertThat(recipient.isActive()).isFalse();
  }

  @Test
  void shouldKeepRecipientDeactivate_whenRemoveIsCalledTwice() {
    var recipient = RecipientFixtures.JEFFERSON.create();
    recipient.remove();
    assertThat(recipient.isActive()).isFalse();

    recipient.remove();
    assertThat(recipient.isActive()).isFalse();
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> Recipient.restore(null, RECIPIENT_NAME, IBAN, STATUS_ACTIVE, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNull() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, null, IBAN, STATUS_ACTIVE, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIbanIsNull() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, RECIPIENT_NAME, null, STATUS_ACTIVE, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.IBAN_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenStatusIsNull() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, RECIPIENT_NAME, IBAN, null, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.STATUS_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCreatedAtIsNull() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, RECIPIENT_NAME, IBAN, STATUS_ACTIVE, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.CREATED_AT_NOT_PROVIDED);
  }

  @Test
  void shouldBeEqual_whenRecipientIdIsSame() {
    var recipient1 = Recipient.restore(RECIPIENT_ID, RECIPIENT_NAME, IBAN, STATUS_ACTIVE, CREATED_AT);
    var recipient2 = Recipient.restore(RECIPIENT_ID, RecipientName.of("Different name"), IBAN, STATUS_ACTIVE, CREATED_AT);

    assertThat(recipient1)
      .isEqualTo(recipient2)
      .hasSameHashCodeAs(recipient2);
  }

  @Test
  void shouldNotBeEqual_whenRecipientIdIsDifferent() {
    var recipient1 = Recipient.restore(RecipientId.newId(), RECIPIENT_NAME, IBAN, STATUS_ACTIVE, CREATED_AT);
    var recipient2 = Recipient.restore(RecipientId.newId(), RECIPIENT_NAME, IBAN, STATUS_ACTIVE, CREATED_AT);

    assertThat(recipient1).isNotEqualTo(recipient2);
  }
}