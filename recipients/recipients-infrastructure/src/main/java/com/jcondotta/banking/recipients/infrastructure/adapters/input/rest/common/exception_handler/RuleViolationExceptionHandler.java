package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainRuleViolationException;
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
public class RuleViolationExceptionHandler {

  public static final String TITLE_OPERATION_NOT_ALLOWED = "Operation not allowed";
  static final HttpStatus HTTP_STATUS_UNPROCESSABLE_CONTENT = HttpStatus.UNPROCESSABLE_CONTENT;

  @ExceptionHandler(DomainRuleViolationException.class)
  public ResponseEntity<ProblemDetail> handleRuleViolation(DomainRuleViolationException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_UNPROCESSABLE_CONTENT);
    problemDetail.setType(ProblemTypes.RULE_VIOLATION);
    problemDetail.setTitle(TITLE_OPERATION_NOT_ALLOWED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
