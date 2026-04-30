package com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler;

import java.net.URI;

public final class ProblemTypes {

  public static final URI RESOURCE_NOT_FOUND = uri("/resource-not-found");
  public static final URI VALIDATION_ERRORS = uri("/validation-errors");
  public static final URI RULE_VIOLATION = uri("/rule-violation");
  public static final URI CONFLICT = uri("/conflict");
  public static final URI TOO_MANY_REQUESTS = uri("/too-many-requests");
  public static final URI INTERNAL_ERROR = uri("/internal-error");

  @SuppressWarnings("all")
  private static final String BASE_PATH = "https://api.jcondotta.com/problems";

  private ProblemTypes() {
  }

  private static URI uri(String path) {
    return URI.create(BASE_PATH.concat(path));
  }
}
