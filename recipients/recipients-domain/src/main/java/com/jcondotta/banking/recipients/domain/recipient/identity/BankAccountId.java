package com.jcondotta.banking.recipients.domain.recipient.identity;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.support.DomainPreconditions;

import java.util.UUID;

public record BankAccountId(UUID value) implements AggregateId<UUID> {

  public static final String ID_NOT_PROVIDED = "bank account id value must be provided";

  public BankAccountId {
    DomainPreconditions.required(value, ID_NOT_PROVIDED);
  }

  public static BankAccountId of(UUID value) {
    return new BankAccountId(value);
  }
}