package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.core.DomainCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public final class Recipients extends DomainCollection<Recipient> {

  Recipients(Collection<Recipient> values) {
    super(required(values, BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL));
  }

  public static Recipients of(Collection<Recipient> recipients) {
    required(recipients, BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
    return new Recipients(recipients);
  }

  public static Recipients of(Recipient... recipients) {
    required(recipients, BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
    return new Recipients(List.of(recipients));
  }

  public static Recipients empty() {
    return new Recipients(List.of());
  }

  Recipient add(RecipientName name, Iban iban, Instant createdAt) {
    if (existsActiveRecipientWithIban(iban)) {
      throw new DuplicateRecipientIbanException(iban.value());
    }

    var recipient = Recipient.create(name, iban, createdAt);

    super.add(recipient);
    return recipient;
  }

  void remove(RecipientId recipientId) {
    required(recipientId, RecipientError.RECIPIENT_ID_NOT_PROVIDED);

    var recipient = find(recipientId)
      .orElseThrow(() -> new RecipientNotFoundException(recipientId));

    recipient.remove();
  }

  private Optional<Recipient> find(RecipientId recipientId) {
    return stream()
      .filter(recipient -> recipient.getId().equals(recipientId))
      .findFirst();
  }

  private boolean existsActiveRecipientWithIban(Iban iban) {
    return stream()
      .filter(Recipient::isActive)
      .anyMatch(recipient -> recipient.getIban().equals(iban));
  }

  public List<Recipient> active() {
    return stream()
      .filter(Recipient::isActive)
      .toList();
  }
}
