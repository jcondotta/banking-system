package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public final class Recipients {

  private final List<Recipient> entries;

  Recipients(List<Recipient> entries) {
    required(entries, BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
    this.entries = new ArrayList<>(entries);
  }

  static Recipients empty() {
    return new Recipients(List.of());
  }

  Recipient add(RecipientName name, Iban iban, Instant createdAt) {
    if (entries.stream()
      .filter(Recipient::isActive)
      .anyMatch(recipient -> recipient.getIban().equals(iban))) {

      throw new DuplicateRecipientException(iban);
    }

    var recipient = Recipient.create(name, iban, createdAt);

    entries.add(recipient);
    return recipient;
  }

  void remove(RecipientId recipientId) {
    required(recipientId, RecipientError.RECIPIENT_ID_NOT_PROVIDED);

    var recipient = find(recipientId)
      .findFirst()
      .orElseThrow(() -> new RecipientNotFoundException(recipientId));

    recipient.remove();
  }

  private Stream<Recipient> find(RecipientId recipientId) {
    return entries.stream()
      .filter(recipient -> recipient.getId().equals(recipientId));
  }

  public List<Recipient> getEntries() {
    return entries.stream()
      .filter(Recipient::isActive)
      .toList();
  }
}