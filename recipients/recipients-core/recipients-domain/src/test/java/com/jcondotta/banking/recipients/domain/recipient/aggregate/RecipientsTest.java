package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientsTest {

  private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of(RecipientTestData.JEFFERSON.getName());
  private static final RecipientName RECIPIENT_NAME_PATRIZIO = RecipientName.of(RecipientTestData.PATRIZIO.getName());

  private static final Iban IBAN_JEFFERSON = Iban.of(RecipientTestData.JEFFERSON.getIban());
  private static final Iban IBAN_PATRIZIO = Iban.of(RecipientTestData.PATRIZIO.getIban());

  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldCreateRecipients_whenUsingListFactoryMethod() {
    var recipient = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);

    var recipients = Recipients.of(List.of(recipient));

    assertThat(recipients.values())
      .containsExactly(recipient);
  }

  @Test
  void shouldThrowException_whenUsingListFactoryMethod_givenNullList() {
    assertThatThrownBy(() -> Recipients.of((Collection<Recipient>) null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldCreateRecipients_whenUsingVarargsFactoryMethod() {
    var recipient = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);

    var recipients = Recipients.of(recipient);

    assertThat(recipients.values())
      .containsExactly(recipient);
  }

  @Test
  void shouldCreateRecipientsFromMultipleRecipients_whenUsingVarargsFactoryMethod() {
    var recipient1 = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);
    var recipient2 = createRecipient(RECIPIENT_NAME_PATRIZIO, IBAN_PATRIZIO);

    var recipients = Recipients.of(recipient1, recipient2);

    assertThat(recipients.values())
      .containsExactly(recipient1, recipient2);
  }

  @Test
  void shouldThrowException_whenUsingVarargsFactoryMethod_givenNullRecipientsArray() {
    assertThatThrownBy(() -> Recipients.of((Recipient[]) null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldCreateEmptyRecipients_whenUsingFactoryMethod() {
    var recipients = Recipients.empty();

    assertThat(recipients.values()).isEmpty();
  }

  @Test
  void shouldReturnEmptyCollection_whenPassingEmptyList() {
    var recipients = Recipients.of(List.of());

    assertThat(recipients.values()).isEmpty();
  }

  @Test
  void shouldReturnEmptyCollection_whenPassingEmptyVarargs() {
    var recipients = Recipients.of(new Recipient[0]);

    assertThat(recipients.values()).isEmpty();
  }

  @Test
  void shouldAddRecipient_whenNoRecipientActiveExists() {
    var recipients = Recipients.empty();
    var recipient = recipients.add(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    assertThat(recipients.values())
      .containsExactly(recipient);
  }

  @Test
  void shouldAddRecipient_whenActiveRecipientWithDifferentIbanExists() {
    var recipient1 = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);
    var recipients = Recipients.of(recipient1);

    var recipient2 = recipients.add(RECIPIENT_NAME_PATRIZIO, IBAN_PATRIZIO, CREATED_AT);

    assertThat(recipients.active())
      .containsExactly(recipient1, recipient2);
  }

  @Test
  void shouldThrowException_whenAddRecipientWithExistingIban() {
    var recipient1 = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);
    var recipients = Recipients.of(recipient1);

    assertThatThrownBy(() -> recipients.add(RECIPIENT_NAME_PATRIZIO, IBAN_JEFFERSON, CREATED_AT))
      .isInstanceOf(DuplicateRecipientIbanException.class)
      .hasMessage(DuplicateRecipientIbanException.RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(IBAN_JEFFERSON.value()));
  }

  @Test
  void shouldAddRecipient_whenRecipientWithExistingIbanIsDeactivated() {
    var recipient1 = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);
    var recipients = Recipients.of(recipient1);

    recipients.remove(recipient1.getId());

    Recipient recipient2 = recipients.add(RECIPIENT_NAME_PATRIZIO, IBAN_JEFFERSON, CREATED_AT);

    assertThat(recipients.active())
      .containsExactly(recipient2);
  }

  @Test
  void shouldMarkRecipientAsRemoved_whenRecipientExists() {
    var recipients = Recipients.empty();
    var recipient = recipients.add(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, CREATED_AT);

    recipients.remove(recipient.getId());

    assertThat(recipient.isActive()).isFalse();
    assertThat(recipients.active()).isEmpty();
  }

  @Test
  void shouldThrowException_whenRemovingNonExistentRecipient() {
    var recipients = Recipients.empty();
    var recipientId = RecipientId.newId();

    assertThatThrownBy(() -> recipients.remove(recipientId))
      .isInstanceOf(RecipientNotFoundException.class)
      .hasMessage(RecipientNotFoundException.RECIPIENT_NOT_FOUND.formatted(recipientId));
  }

  @Test
  void shouldThrowException_whenRemovingRecipientThatDoesNotExist() {
    var recipient = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);

    var recipients = Recipients.of(recipient);

    var unknownId = RecipientId.newId();

    assertThatThrownBy(() -> recipients.remove(unknownId))
      .isInstanceOf(RecipientNotFoundException.class);
  }

  @Test
  void shouldThrowException_whenRemovingRecipient_givenNullRecipientId() {
    var recipients = Recipients.empty();

    assertThatThrownBy(() -> recipients.remove(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldReturnRecipient_whenRecipientExists() {
    var recipient1 = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);
    var recipient2 = createRecipient(RECIPIENT_NAME_PATRIZIO, IBAN_PATRIZIO);

    var recipients = Recipients.of(recipient1, recipient2);

    recipients.remove(recipient1.getId());

    assertThat(recipients.active())
      .containsExactly(recipient2);
  }

  @Test
  void shouldReturnActiveRecipients_whenRecipientsContainRemovedOnes() {
    var recipient1 = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);
    var recipient2 = createRecipient(RECIPIENT_NAME_PATRIZIO, IBAN_PATRIZIO);

    var recipients = Recipients.of(recipient1, recipient2);
    recipients.remove(recipient1.getId());

    assertThat(recipients.active())
      .containsExactly(recipient2);
  }

  private static Recipient createRecipient(RecipientName recipientName, Iban iban) {
    return Recipient.create(recipientName, iban, CREATED_AT);
  }

}
