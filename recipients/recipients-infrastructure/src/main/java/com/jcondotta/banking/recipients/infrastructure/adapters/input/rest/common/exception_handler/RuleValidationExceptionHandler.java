package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainRuleValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class RuleValidationExceptionHandler {

  static final String TITLE_OPERATION_NOT_ALLOWED = "Operation not allowed";

  @ExceptionHandler(DomainRuleValidationException.class)
  public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(DomainRuleValidationException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    problemDetail.setType(ProblemTypes.RULE_VIOLATION);
    problemDetail.setTitle(TITLE_OPERATION_NOT_ALLOWED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
