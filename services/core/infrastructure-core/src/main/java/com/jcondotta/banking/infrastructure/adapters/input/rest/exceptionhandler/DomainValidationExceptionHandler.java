package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.ApiProblem;
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

  static final HttpStatus HTTP_STATUS_UNPROCESSABLE_CONTENT = HttpStatus.UNPROCESSABLE_CONTENT;

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ProblemDetail> handle(DomainValidationException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_UNPROCESSABLE_CONTENT);
    problemDetail.setType(ApiProblem.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.of(problemDetail).build();
  }
}
