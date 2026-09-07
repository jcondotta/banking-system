package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderDeactivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountIbanConfigurationException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountStateTransitionException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.identity.EventId;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.jcondotta.domain.support.Preconditions.required;

public final class BankAccount extends AggregateRoot<BankAccountId> {

  public static final AccountStatus ACCOUNT_STATUS_ON_OPENING = AccountStatus.PENDING;
  public static final Iban IBAN_ON_OPENING = null;

  private final AccountType accountType;
  private final Currency currency;

  @Nullable
  private final Iban iban;
  private final Instant createdAt;
  private final AccountHolders accountHolders;

  private AccountStatus accountStatus;

  private BankAccount(
    BankAccountId id,
    AccountType accountType,
    Currency currency,
    @Nullable Iban iban,
    AccountStatus accountStatus,
    Instant createdAt,
    AccountHolders accountHolders
  ) {
    super(required(id, BankAccountErrors.ID_MUST_BE_PROVIDED));
    this.accountType = required(accountType, BankAccountErrors.ACCOUNT_TYPE_MUST_BE_PROVIDED);
    this.currency = required(currency, BankAccountErrors.CURRENCY_MUST_BE_PROVIDED);
    this.accountStatus = required(accountStatus, BankAccountErrors.ACCOUNT_STATUS_MUST_BE_PROVIDED);
    validateIbanConfiguration(iban, this.accountStatus);
    this.iban = iban;
    this.createdAt = required(createdAt, BankAccountErrors.CREATED_AT_MUST_BE_PROVIDED);
    this.accountHolders = required(accountHolders, BankAccountErrors.ACCOUNT_HOLDERS_MUST_BE_PROVIDED);
  }

  private static void validateIbanConfiguration(@Nullable Iban iban, AccountStatus accountStatus) {
    var hasIban = iban != null;

    if (accountStatus.isPending() == hasIban) {
      throw new InvalidBankAccountIbanConfigurationException(accountStatus);
    }
  }

  public static BankAccount open(
    BankAccountId bankAccountId,
    PersonalInfo personalInfo,
    ContactInfo contactInfo,
    Address address,
    AccountType accountType,
    Currency currency
  ) {
    Instant now = Instant.now();
    var primaryHolder = AccountHolder.createPrimary(personalInfo, contactInfo, address, now);

    var bankAccount = new BankAccount(
      bankAccountId,
      accountType,
      currency,
      IBAN_ON_OPENING,
      ACCOUNT_STATUS_ON_OPENING,
      now,
      AccountHolders.of(primaryHolder)
    );

    bankAccount.registerEvent(
      new BankAccountOpenedEvent(
        EventId.newId(),
        bankAccount.getId(),
        bankAccount.getAccountType(),
        bankAccount.getCurrency(),
        primaryHolder.getId(),
        now
      )
    );

    return bankAccount;
  }

  public static BankAccount restore(
    BankAccountId bankAccountId,
    AccountType accountType,
    Currency currency,
    @Nullable Iban iban,
    AccountStatus accountStatus,
    Instant createdAt,
    AccountHolders accountHolders
  ) {
    return new BankAccount(
      bankAccountId,
      accountType,
      currency,
      iban,
      accountStatus,
      createdAt,
      accountHolders
    );
  }

  public BankAccount activate(Iban iban) {
    required(iban, BankAccountErrors.IBAN_MUST_BE_PROVIDED);

    if (accountStatus == AccountStatus.ACTIVE) {
      return this;
    }

    if (accountStatus != AccountStatus.PENDING) {
      throw new InvalidBankAccountStateTransitionException(accountStatus, AccountStatus.ACTIVE);
    }

    var activated = new BankAccount(getId(), accountType, currency, iban, AccountStatus.ACTIVE, createdAt, accountHolders);
    activated.registerEvent(new BankAccountActivatedEvent(EventId.newId(), activated.getId(), iban, currency, Instant.now()));
    return activated;
  }

  public void block() {
    if (accountStatus == AccountStatus.BLOCKED) {
      return;
    }

    if (accountStatus != AccountStatus.ACTIVE) {
      throw new InvalidBankAccountStateTransitionException(accountStatus, AccountStatus.BLOCKED);
    }

    var previousStatus = this.accountStatus;
    this.accountStatus = AccountStatus.BLOCKED;
    registerEvent(new BankAccountStatusChangedEvent(EventId.newId(), getId(), previousStatus, AccountStatus.BLOCKED, Instant.now()));
  }

  public void unblock() {
    if (accountStatus == AccountStatus.ACTIVE) {
      return;
    }

    if (accountStatus != AccountStatus.BLOCKED) {
      throw new InvalidBankAccountStateTransitionException(accountStatus, AccountStatus.ACTIVE);
    }

    var previousStatus = this.accountStatus;
    this.accountStatus = AccountStatus.ACTIVE;
    registerEvent(new BankAccountStatusChangedEvent(EventId.newId(), getId(), previousStatus, AccountStatus.ACTIVE, Instant.now()));
  }

  public void addJointHolder(PersonalInfo personalInfo, ContactInfo contactInfo, Address address) {
    if (!accountStatus.isActive()) {
      throw new BankAccountNotActiveException(accountStatus);
    }

    Instant now = Instant.now();
    var accountHolder = AccountHolder.createJoint(personalInfo, contactInfo, address, now);
    accountHolders.add(accountHolder);

    this.registerEvent(new BankAccountJointHolderAddedEvent(EventId.newId(), this.getId(), accountHolder.getId(), now));
  }

  public void deactivateHolder(AccountHolderId accountHolderId) {
    accountHolders.deactivate(accountHolderId);
  }

  public void close() {
    if (accountStatus == AccountStatus.CLOSED) {
      return;
    }

    if (accountStatus != AccountStatus.ACTIVE) {
      throw new InvalidBankAccountStateTransitionException(accountStatus, AccountStatus.CLOSED);
    }

    var previousStatus = this.accountStatus;
    this.accountStatus = AccountStatus.CLOSED;
    registerEvent(new BankAccountStatusChangedEvent(EventId.newId(), getId(), previousStatus, AccountStatus.CLOSED, Instant.now()));
  }

  public AccountHolder getPrimaryHolder() {
    return accountHolders.primary();
  }

  public List<AccountHolder> getJointHolders() {
    return accountHolders.joint();
  }

  public AccountType getAccountType() {
    return accountType;
  }

  public Currency getCurrency() {
    return currency;
  }

  public Optional<Iban> getIban() {
    return Optional.ofNullable(iban);
  }

  public AccountStatus getAccountStatus() {
    return accountStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<AccountHolder> getActiveHolders() {
    return accountHolders.active();
  }
}
