package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.core.Entity;

import java.time.Instant;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public final class Recipient extends Entity<RecipientId> {

  private final RecipientName recipientName;
  private final Iban iban;
  private final Instant createdAt;

  private RecipientStatus status;

  Recipient(
    RecipientId recipientId,
    RecipientName recipientName,
    Iban iban,
    RecipientStatus status,
    Instant createdAt
  ) {
    super(required(recipientId, RecipientError.RECIPIENT_ID_NOT_PROVIDED));
    this.recipientName = required(recipientName, RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
    this.iban = required(iban, RecipientError.IBAN_NOT_PROVIDED);
    this.status = required(status, RecipientError.STATUS_NOT_PROVIDED);
    this.createdAt = required(createdAt, RecipientError.CREATED_AT_NOT_PROVIDED);
  }

  static Recipient create(RecipientName recipientName, Iban iban, Instant createdAt) {
    return new Recipient(RecipientId.newId(), recipientName, iban, RecipientStatus.ACTIVE, createdAt);
  }

  void remove() {
    if (RecipientStatus.REMOVED.equals(status)) {
      return;
    }

    this.status = RecipientStatus.REMOVED;
  }

  public static Recipient restore(
    RecipientId recipientId,
    RecipientName recipientName,
    Iban iban,
    RecipientStatus status,
    Instant createdAt
  ) {
    return new Recipient(recipientId, recipientName, iban, status, createdAt);
  }

  public RecipientName getRecipientName() {
    return recipientName;
  }

  public Iban getIban() {
    return iban;
  }

  public RecipientStatus getStatus() {
    return status;
  }

  public boolean isActive() {
    return RecipientStatus.ACTIVE.equals(status);
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}