package com.jcondotta.banking.accounts.domain.bankaccount.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;

import java.time.Instant;

public final class AccountHolderTestFactory {

  private AccountHolderTestFactory() {}

  public static AccountHolder build(AccountHolderId id, AccountHolderFixtures fixture, HolderType holderType, Instant createdAt) {
    return AccountHolder.restore(
      id,
      fixture.personalInfo(),
      fixture.contactInfo(),
      fixture.address(),
      holderType,
      createdAt
    );
  }

  public static AccountHolder build(AccountHolderFixtures fixture, HolderType holderType, Instant createdAt) {
    return build(AccountHolderId.newId(), fixture, holderType, createdAt);
  }

  public static AccountHolder build(AccountHolderFixtures fixture, HolderType holderType) {
    return build(AccountHolderId.newId(), fixture, holderType, Instant.now());
  }

  public static AccountHolder primary(AccountHolderId id, AccountHolderFixtures fixture, Instant createdAt) {
    return build(id, fixture, HolderType.PRIMARY, createdAt);
  }

  public static AccountHolder primary(AccountHolderFixtures fixture, Instant createdAt) {
    return build(fixture, HolderType.PRIMARY, createdAt);
  }

  public static AccountHolder primary(AccountHolderFixtures fixture) {
    return build(fixture, HolderType.PRIMARY);
  }

  public static AccountHolder joint(AccountHolderId id, AccountHolderFixtures fixture, Instant createdAt) {
    return build(id, fixture, HolderType.JOINT, createdAt);
  }

  public static AccountHolder joint(AccountHolderFixtures fixture, Instant createdAt) {
    return build(fixture, HolderType.JOINT, createdAt);
  }

  public static AccountHolder joint(AccountHolderFixtures fixture) {
    return build(fixture, HolderType.JOINT);
  }
}