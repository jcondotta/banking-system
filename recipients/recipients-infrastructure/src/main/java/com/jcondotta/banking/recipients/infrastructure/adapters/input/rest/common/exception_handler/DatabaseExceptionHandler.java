package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseExceptionHandler {

  static final HttpStatus HTTP_STATUS_SERVICE_UNAVAILABLE = HttpStatus.SERVICE_UNAVAILABLE;
  static final HttpStatus HTTP_STATUS_GATEWAY_TIMEOUT = HttpStatus.GATEWAY_TIMEOUT;

  @ExceptionHandler(DataAccessResourceFailureException.class)
  public ResponseEntity<ProblemDetail> handleDatabaseUnavailable(
    DataAccessResourceFailureException ex,
    HttpServletRequest request
  ) {
    var problemDetail = ProblemDetail.forStatusAndDetail(HTTP_STATUS_SERVICE_UNAVAILABLE, ex.getMessage());
    problemDetail.setTitle(HTTP_STATUS_SERVICE_UNAVAILABLE.getReasonPhrase());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }

  @ExceptionHandler(QueryTimeoutException.class)
  public ResponseEntity<ProblemDetail> handleDatabaseTimeout(QueryTimeoutException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatusAndDetail(HTTP_STATUS_GATEWAY_TIMEOUT, ex.getMessage());
    problemDetail.setTitle(HTTP_STATUS_GATEWAY_TIMEOUT.getReasonPhrase());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
