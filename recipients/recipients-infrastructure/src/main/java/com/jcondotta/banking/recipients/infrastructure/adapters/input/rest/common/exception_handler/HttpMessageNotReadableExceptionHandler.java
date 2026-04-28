package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.filter.CorrelationFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerMapping;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpMessageNotReadableExceptionHandler {

  static final HttpStatus HTTP_STATUS_BAD_REQUEST = HttpStatus.BAD_REQUEST;
  static final String TITLE_VALIDATION_FAILED = "Request validation failed";
  static final String DETAIL_UNREADABLE_MESSAGE = "Request body could not be read";

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handle(HttpMessageNotReadableException ex, HttpServletRequest request) {
    var logEvent = log.atWarn()
      .setMessage("Request body could not be read")
      .addKeyValue("event_type", "recipient.operation.failed")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "unreadable_message")
      .addKeyValue("duration_ms", CorrelationFilter.durationMs(request));

    var bankAccountId = bankAccountId(request);
    if (bankAccountId != null) {
      logEvent.addKeyValue("bank_account_id", bankAccountId);
    }

    logEvent.log();

    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_BAD_REQUEST);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setDetail(DETAIL_UNREADABLE_MESSAGE);
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
  private static String bankAccountId(HttpServletRequest request) {
    var variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (variables instanceof Map<?, ?> pathVariables) {
      var bankAccountId = pathVariables.get("bank-account-id");
      if (bankAccountId != null) {
        return bankAccountId.toString();
      }
    }

    return null;
  }
}
