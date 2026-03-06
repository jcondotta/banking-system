package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.AccountHolderNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.CannotDeactivatePrimaryHolderException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountHoldersConfigurationException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.MaxJointHoldersExceededException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;

import java.util.*;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public final class AccountHolders {

  static final String ACCOUNT_HOLDERS_MUST_BE_PROVIDED = BankAccountErrors.ACCOUNT_HOLDERS_MUST_BE_PROVIDED;

  private static final int MIN_PRIMARY = 1;
  private static final int MAX_PRIMARY = 1;
  private static final int MAX_JOINT = 1;

  private final List<AccountHolder> holders;

  private AccountHolders(List<AccountHolder> holders) {
    required(holders, ACCOUNT_HOLDERS_MUST_BE_PROVIDED);

    this.holders = new ArrayList<>(holders);
    validateConfiguration();
  }

  public static AccountHolders of(List<AccountHolder> holders) {
    return new AccountHolders(holders);
  }

  public static AccountHolders of(AccountHolder... holders) {
    required(holders, ACCOUNT_HOLDERS_MUST_BE_PROVIDED);
    return new AccountHolders(Arrays.stream(holders).toList());
  }

  private void validateConfiguration() {
    validatePrimary();
    validateJoint();
  }

  private void validatePrimary() {
    int primaryCount = (int) holders.stream()
      .filter(AccountHolder::isPrimary)
      .count();

    if (primaryCount < MIN_PRIMARY || primaryCount > MAX_PRIMARY) {
      throw new InvalidBankAccountHoldersConfigurationException(primaryCount, MIN_PRIMARY, MAX_PRIMARY);
    }
  }

  private void validateJoint() {
    int jointCount = jointCount();

    if (jointCount > MAX_JOINT) {
      throw new MaxJointHoldersExceededException(jointCount);
    }
  }

  public List<AccountHolder> active() {
    return holders.stream()
      .filter(AccountHolder::isActive)
      .sorted(Comparator.comparing(
        holder -> holder.getAccountHolderType() == HolderType.PRIMARY ? 0 : 1
      ))
      .toList();
  }

  public AccountHolder primary() {
    return holders.stream()
      .filter(AccountHolder::isPrimary)
      .findFirst()
      .orElseThrow();
  }

  public List<AccountHolder> joint() {
    return holders.stream()
      .filter(AccountHolder::isJoint)
      .filter(AccountHolder::isActive)
      .toList();
  }

  public int jointCount() {
    return joint().size();
  }

  public void add(AccountHolder holder) {
    required(holder, "Account holder must not be null");

    if (holder.isPrimary()) {
      throw new IllegalStateException("Primary account holder already exists");
    }

    if (jointCount() >= MAX_JOINT) {
      throw new MaxJointHoldersExceededException(jointCount());
    }

    holders.add(holder);
  }

  public void deactivate(AccountHolderId accountHolderId) {
    var holder = find(accountHolderId)
      .orElseThrow(() -> new AccountHolderNotFoundException(accountHolderId));

    if (holder.isPrimary()) {
      throw new CannotDeactivatePrimaryHolderException();
    }

    holder.deactivate();
  }

  public Optional<AccountHolder> find(AccountHolderId accountHolderId) {
    return holders.stream()
      .filter(holder -> holder.getId().equals(accountHolderId))
      .findFirst();
  }
}