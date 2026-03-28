package com.jcondotta.banking.accounts.infrastructure;

public final class Preconditions {

  private Preconditions() {
  }

  public static <T> T required(T value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  public static String requiredNotBlank(String value, String message) {
    required(value, message);
    if (value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
}