package com.jcondotta.banking.recipients.infrastructure.adapters.metrics.persistence;

public enum RecipientPersistenceOperation {

  CREATE("create"),
  UPDATE("update"),
  DELETE("delete");

  private final String tagValue;

  RecipientPersistenceOperation(String tagValue) {
    this.tagValue = tagValue;
  }

  public String tagValue() {
    return tagValue;
  }
}
