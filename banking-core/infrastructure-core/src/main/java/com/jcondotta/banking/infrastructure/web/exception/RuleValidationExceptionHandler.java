package com.jcondotta.banking.infrastructure.web.exception;

import com.jcondotta.banking.infrastructure.web.problem.ProblemTypes;
import com.jcondotta.domain.exception.DomainRuleValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class RuleValidationExceptionHandler {

  static final String TITLE_OPERATION_NOT_ALLOWED = "Operation not allowed";

  @ExceptionHandler(DomainRuleValidationException.class)
  public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(DomainRuleValidationException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    problemDetail.setType(ProblemTypes.RULE_VIOLATION);
    problemDetail.setTitle(TITLE_OPERATION_NOT_ALLOWED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity
      .status(HttpStatus.UNPROCESSABLE_ENTITY)
      .body(problemDetail);
  }
}
