package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseExceptionHandler {

  static final HttpStatus HTTP_STATUS_SERVICE_UNAVAILABLE = HttpStatus.SERVICE_UNAVAILABLE;
  static final HttpStatus HTTP_STATUS_GATEWAY_TIMEOUT = HttpStatus.GATEWAY_TIMEOUT;

  public static final String DETAIL_DATABASE_UNAVAILABLE = "Database is temporarily unavailable";
  public static final String DETAIL_DATABASE_TIMEOUT = "Database operation timed out";

  @ExceptionHandler(CannotCreateTransactionException.class)
  public ResponseEntity<ProblemDetail> handleDatabaseUnavailable(
    CannotCreateTransactionException ex,
    HttpServletRequest request
  ) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_SERVICE_UNAVAILABLE);
    problemDetail.setType(ProblemTypes.DATABASE_UNAVAILABLE);
    problemDetail.setTitle(HTTP_STATUS_SERVICE_UNAVAILABLE.getReasonPhrase());
    problemDetail.setDetail(DETAIL_DATABASE_UNAVAILABLE);
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }

  @ExceptionHandler(QueryTimeoutException.class)
  public ResponseEntity<ProblemDetail> handleDatabaseTimeout(QueryTimeoutException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_GATEWAY_TIMEOUT);
    problemDetail.setType(ProblemTypes.DATABASE_TIMEOUT);
    problemDetail.setTitle(HTTP_STATUS_GATEWAY_TIMEOUT.getReasonPhrase());
    problemDetail.setDetail(DETAIL_DATABASE_TIMEOUT);
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
