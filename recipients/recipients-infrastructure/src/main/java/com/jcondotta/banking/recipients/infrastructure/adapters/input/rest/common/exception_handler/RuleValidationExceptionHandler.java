package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
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
public class RuleValidationExceptionHandler {

  static final HttpStatus HTTP_STATUS_UNPROCESSABLE_CONTENT = HttpStatus.UNPROCESSABLE_CONTENT;
  static final String TITLE_OPERATION_NOT_ALLOWED = "Operation not allowed";

  @ExceptionHandler(RecipientOwnershipMismatchException.class)
  public ResponseEntity<ProblemDetail> handleRecipientOwnershipMismatch(
    RecipientOwnershipMismatchException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.of(buildRuleViolationProblemDetail(ex, request)).build();
  }

  private ProblemDetail buildRuleViolationProblemDetail(RecipientOwnershipMismatchException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_UNPROCESSABLE_CONTENT);
    problemDetail.setType(ProblemTypes.RULE_VIOLATION);
    problemDetail.setTitle(TITLE_OPERATION_NOT_ALLOWED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
