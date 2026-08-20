package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RecipientCreateTest {

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of(RecipientTestData.JEFFERSON.getName());
  private static final RecipientName RECIPIENT_NAME_PATRIZIO = RecipientName.of(RecipientTestData.PATRIZIO.getName());

  private static final Iban IBAN_JEFFERSON = Iban.of(RecipientTestData.JEFFERSON.getIban());

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Test
  void shouldCreateRecipient_whenAllValuesAreValid() {
    var recipient = Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    assertThat(recipient)
      .satisfies(r -> {
        assertThat(r.getId()).isEqualTo(RECIPIENT_ID);
        assertThat(r.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(r.getRecipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
        assertThat(r.getIban()).isEqualTo(IBAN_JEFFERSON);
        assertThat(r.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(r.getVersion()).isNull();
      });
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNullOnCreate() {
    assertThatThrownBy(() -> Recipient.create(null, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNullOnCreate() {
    assertThatThrownBy(() -> Recipient.create(RECIPIENT_ID, null, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNullOnCreate() {
    assertThatThrownBy(() -> Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, null, IBAN_JEFFERSON, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIbanIsNullOnCreate() {
    assertThatThrownBy(() -> Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, null, CREATED_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.IBAN_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCreatedAtIsNullOnCreate() {
    assertThatThrownBy(() -> Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.CREATED_AT_NOT_PROVIDED);
  }

  @Test
  void shouldGenerateDifferentIds_whenCreatingMultipleRecipients() {
    var recipient1 = Recipient.create(RecipientId.newId(), BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);
    var recipient2 = Recipient.create(RecipientId.newId(), BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    assertThat(recipient1.getId()).isNotEqualTo(recipient2.getId());
  }

  @Test
  void shouldPassSilently_whenRecipientBelongsToGivenBankAccount() {
    var recipient = Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    assertThatCode(() -> recipient.assertBelongsTo(BANK_ACCOUNT_ID))
      .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowOwnershipMismatch_whenRecipientBelongsToADifferentBankAccount() {
    var recipient = Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);
    var otherBankAccountId = BankAccountId.of(UUID.randomUUID());

    assertThatThrownBy(() -> recipient.assertBelongsTo(otherBankAccountId))
      .isInstanceOf(RecipientOwnershipMismatchException.class)
      .hasMessage(RecipientOwnershipMismatchException.MESSAGE);
  }

  @Test
  void shouldThrowException_whenAssertingAgainstNullBankAccountId() {
    var recipient = Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    assertThatThrownBy(() -> recipient.assertBelongsTo(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldBeEqual_whenSameId() {
    var recipient1 = Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);
    var recipient2 = Recipient.create(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_PATRIZIO, IBAN_JEFFERSON, CREATED_AT);

    assertThat(recipient1).isEqualTo(recipient2).hasSameHashCodeAs(recipient2);
  }

  @Test
  void shouldNotBeEqual_whenDifferentId() {
    var recipient1 = Recipient.create(RecipientId.newId(), BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);
    var recipient2 = Recipient.create(RecipientId.newId(), BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    assertThat(recipient1).isNotEqualTo(recipient2);
  }
}
