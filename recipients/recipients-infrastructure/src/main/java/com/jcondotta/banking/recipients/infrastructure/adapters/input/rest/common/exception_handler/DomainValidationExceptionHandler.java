package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainValidationExceptionHandler {

  static final String TITLE_VALIDATION_FAILED = "Request validation failed";

  static final HttpStatus HTTP_STATUS_BAD_REQUEST = HttpStatus.BAD_REQUEST;

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ProblemDetail> handle(DomainValidationException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_BAD_REQUEST);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.of(problemDetail).build();
  }
}
