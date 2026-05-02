package com.jcondotta.banking.recipients.infrastructure.adapters.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipientMetrics {

  private static final String AGGREGATE_TAG = "aggregate";
  private static final String AGGREGATE_RECIPIENT = "recipient";
  private static final String OPERATION_TAG = "operation";
  private static final String CATEGORY_TAG = "category";
  private static final String EXCEPTION_TAG = "exception";
  private static final String HTTP_STATUS_TAG = "http_status";

  private final MeterRegistry meterRegistry;

  public void recordException(String operation, String category, Throwable exception, HttpStatus httpStatus) {
    meterRegistry.counter(
      "recipients.exceptions",
      AGGREGATE_TAG, AGGREGATE_RECIPIENT,
      OPERATION_TAG, operation,
      CATEGORY_TAG, category,
      EXCEPTION_TAG, exception.getClass().getSimpleName(),
      HTTP_STATUS_TAG, String.valueOf(httpStatus.value())
    ).increment();
  }

  public void recordValidationFailure(String source, String category) {
    meterRegistry.counter(
      "recipients.validation.failures",
      AGGREGATE_TAG, AGGREGATE_RECIPIENT,
      "source", source,
      CATEGORY_TAG, category
    ).increment();
  }

  public void recordListResultSize(int size) {
    meterRegistry.summary(
      "recipients.list.result.size",
      AGGREGATE_TAG, AGGREGATE_RECIPIENT,
      OPERATION_TAG, "list"
    ).record(size);
  }
}
