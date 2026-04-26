package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.core.AggregateRoot;

import java.time.Instant;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public final class Recipient extends AggregateRoot<RecipientId> {

  private final BankAccountId bankAccountId;
  private final RecipientName recipientName;
  private final Iban iban;
  private final Instant createdAt;

  private Long version;

  private Recipient(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    Instant createdAt,
    Long version
  ) {
    super(required(recipientId, RecipientError.RECIPIENT_ID_NOT_PROVIDED));

    this.bankAccountId = required(bankAccountId, RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
    this.recipientName = required(recipientName, RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
    this.iban = required(iban, RecipientError.IBAN_NOT_PROVIDED);
    this.createdAt = required(createdAt, RecipientError.CREATED_AT_NOT_PROVIDED);
    this.version = version;
  }

  public static Recipient create(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    Instant createdAt
  ) {
    return new Recipient(
      recipientId,
      bankAccountId,
      recipientName,
      iban,
      createdAt,
      null
    );
  }

  public static Recipient restore(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    Instant createdAt,
    Long version
  ) {
    return new Recipient(
      recipientId,
      bankAccountId,
      recipientName,
      iban,
      createdAt,
      version
    );
  }

  public void assertBelongsTo(BankAccountId other) {
    required(other, RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);

    if (!bankAccountId.equals(other)) {
      throw new RecipientOwnershipMismatchException(this.getId(), other);
    }
  }

  public boolean isPersisted() {
    return version != null;
  }

  public BankAccountId getBankAccountId() {
    return bankAccountId;
  }

  public RecipientName getRecipientName() {
    return recipientName;
  }

  public Iban getIban() {
    return iban;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Long getVersion() {
    return version;
  }
}
