package com.jcondotta.domain.support;

import com.jcondotta.domain.exception.InvalidDomainDataException;

public final class Preconditions {

  private Preconditions() {
    throw new UnsupportedOperationException("No instances allowed for this class");
  }

  public static <T> T required(T value, String message) {
    if (value == null) {
      throw new InvalidDomainDataException(message);
    }
    return value;
  }

  public static String requiredNotBlank(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new InvalidDomainDataException(message);
    }
    return value;
  }

  public static void checkArgument(boolean condition, String message) {
    if (!condition) {
      throw new InvalidDomainDataException(message);
    }
  }
}
