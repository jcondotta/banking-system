package com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence;

public enum RecipientPersistenceConstraint {

  BANK_ACCOUNT_IBAN("bank_account_iban");

  private final String tagValue;

  RecipientPersistenceConstraint(String tagValue) {
    this.tagValue = tagValue;
  }

  public String tagValue() {
    return tagValue;
  }
}
