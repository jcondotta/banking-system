package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientsTest {

  private static final RecipientName RECIPIENT_NAME = RecipientName.of("Jefferson Condotta");
  private static final Iban IBAN = Iban.of("GB82WEST12345698765432");
  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldCreateEmptyRecipients_whenUsingFactoryMethod() {
    var recipients = Recipients.empty();

    assertThat(recipients.getEntries()).isEmpty();
  }

  @Test
  void shouldCreateRecipientsWithInitialValues_whenRestoringCollection() {
    var recipient = Recipient.create(RECIPIENT_NAME, IBAN, CREATED_AT);

    var recipients = new Recipients(List.of(recipient));

    assertThat(recipients.getEntries())
      .hasSize(1)
      .contains(recipient);
  }

  @Test
  void shouldAddRecipient_whenRecipientDoesNotExist() {
    var recipients = Recipients.empty();

    var recipient = recipients.add(RECIPIENT_NAME, IBAN, CREATED_AT);

    assertThat(recipients.getEntries())
      .hasSize(1)
      .containsExactly(recipient);
  }

  @Test
  void shouldThrowException_whenAddingRecipientWithDuplicateIban() {
    var recipients = Recipients.empty();

    recipients.add(RECIPIENT_NAME, IBAN, CREATED_AT);

    assertThatThrownBy(() -> recipients.add(RecipientName.of("Another Name"), IBAN, CREATED_AT))
      .isInstanceOf(DuplicateRecipientException.class)
      .hasMessage(DuplicateRecipientException.RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(IBAN.value()));
  }

  @Test
  void shouldAllowAddingRecipientWithSameIban_whenPreviousRecipientIsRemoved() {
    var recipients = Recipients.empty();

    var recipient = recipients.add(RECIPIENT_NAME, IBAN, CREATED_AT);

    recipients.remove(recipient.getId());

    var newRecipient = recipients.add(RECIPIENT_NAME, IBAN, CREATED_AT);

    assertThat(recipients.getEntries())
      .hasSize(1)
      .containsExactly(newRecipient);
  }

  @Test
  void shouldMarkRecipientAsRemoved_whenRecipientExists() {
    var recipients = Recipients.empty();
    var recipient = recipients.add(RECIPIENT_NAME, IBAN, CREATED_AT);

    recipients.remove(recipient.getId());

    assertThat(recipient.isActive()).isFalse();
    assertThat(recipients.getEntries()).isEmpty();
  }

  @Test
  void shouldThrowException_whenRemovingRecipientThatDoesNotExist() {
    var recipients = Recipients.empty();
    var recipientId = RecipientId.newId();

    assertThatThrownBy(() -> recipients.remove(recipientId))
      .isInstanceOf(RecipientNotFoundException.class)
      .hasMessage(RecipientNotFoundException.RECIPIENT_NOT_FOUND.formatted(recipientId));
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNull() {
    var recipients = Recipients.empty();

    assertThatThrownBy(() -> recipients.remove(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldReturnOnlyActiveRecipients_whenCallingGetEntries() {
    var recipients = Recipients.empty();

    var active = recipients.add(RECIPIENT_NAME, IBAN, CREATED_AT);
    recipients.remove(active.getId());

    recipients.add(RecipientName.of("Another Recipient"), Iban.of("GB33BUKB20201555555555"), CREATED_AT);

    assertThat(recipients.getEntries())
      .hasSize(1)
      .allMatch(Recipient::isActive);
  }

  @Test
  void shouldThrowException_whenRecipientsListIsNull() {
    assertThatThrownBy(() -> new Recipients(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
  }
}