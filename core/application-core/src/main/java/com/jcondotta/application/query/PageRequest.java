package com.jcondotta.application.query;

public record PageRequest(int page, int size) {

  public static final String PAGE_MUST_NOT_BE_NEGATIVE = "page must be greater than or equal to zero";
  public static final String SIZE_MUST_BE_POSITIVE = "size must be greater than zero";
  public static final String SIZE_MUST_NOT_EXCEED_MAXIMUM = "size must not exceed 100";

  public static final int MAX_SIZE = 100;

  public PageRequest {
    if (page < 0) {
      throw new IllegalArgumentException(PAGE_MUST_NOT_BE_NEGATIVE);
    }

    if (size < 1) {
      throw new IllegalArgumentException(SIZE_MUST_BE_POSITIVE);
    }

    if (size > MAX_SIZE) {
      throw new IllegalArgumentException(SIZE_MUST_NOT_EXCEED_MAXIMUM);
    }
  }
}
