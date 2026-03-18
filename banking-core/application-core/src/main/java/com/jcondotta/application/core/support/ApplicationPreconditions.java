package com.jcondotta.application.core.support;

public final class ApplicationPreconditions {

  private ApplicationPreconditions() {
    throw new UnsupportedOperationException("No instances allowed for this class");
  }

  public static <T> T required(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
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