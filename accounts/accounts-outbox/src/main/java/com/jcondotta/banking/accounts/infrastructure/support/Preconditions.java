package com.jcondotta.banking.accounts.infrastructure.support;

import java.time.Duration;

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

  public static void checkArgument(boolean condition, String message) {
    if (!condition) {
      throw new IllegalArgumentException(message);
    }
  }

  public static Duration positive(Duration value, String message) {
    required(value, message);

    if (value.compareTo(Duration.ZERO) <= 0) {
      throw new IllegalArgumentException(message);
    }

    return value;
  }
}
