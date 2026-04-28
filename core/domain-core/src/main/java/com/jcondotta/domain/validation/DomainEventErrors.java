package com.jcondotta.domain.validation;

public final class DomainEventErrors {

  public static final String EVENT_ID_MUST_BE_PROVIDED = "event id must be provided";
  public static final String AGGREGATE_ID_MUST_BE_PROVIDED = "aggregate id must be provided";
  public static final String EVENT_OCCURRED_AT_MUST_BE_PROVIDED = "event occurred at must be provided";

  private DomainEventErrors() {
  }
}