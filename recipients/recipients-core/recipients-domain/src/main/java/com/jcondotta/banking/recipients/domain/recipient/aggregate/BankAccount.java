package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.core.AggregateRoot;

import java.time.Instant;
import java.util.List;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public final class BankAccount extends AggregateRoot<BankAccountId> {

  private final AccountStatus accountStatus;
  private final Recipients recipients;

  public BankAccount(BankAccountId id, AccountStatus accountStatus, Recipients recipients) {
    super(required(id, BankAccountErrors.ID_MUST_BE_PROVIDED));
    this.accountStatus = required(accountStatus, BankAccountErrors.ACCOUNT_STATUS_MUST_BE_PROVIDED);
    this.recipients = required(recipients, BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
  }

  public static BankAccount register(BankAccountId id) {
    return new BankAccount(id, AccountStatus.ACTIVE, Recipients.empty());
  }

  public static BankAccount restore(BankAccountId id, AccountStatus status, Recipients recipients) {
    return new BankAccount(id, status, recipients);
  }

  public Recipient createRecipient(RecipientName name, Iban iban, Instant now) {
    if (!accountStatus.isActive()) {
      throw new BankAccountNotActiveException(accountStatus);
    }

    return recipients.add(name, iban, now);
  }

  public void removeRecipient(RecipientId recipientId) {
    if (!accountStatus.isActive()) {
      throw new BankAccountNotActiveException(accountStatus);
    }

    recipients.remove(recipientId);
  }

  public AccountStatus getAccountStatus() {
    return accountStatus;
  }

  public List<Recipient> getActiveRecipients() {
    return recipients.active();
  }
}