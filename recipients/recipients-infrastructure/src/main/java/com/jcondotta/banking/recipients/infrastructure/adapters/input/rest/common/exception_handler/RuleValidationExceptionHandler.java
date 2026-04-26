package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.domain.exception.DomainRuleValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class RuleValidationExceptionHandler {

  static final String TITLE_OPERATION_NOT_ALLOWED = "Operation not allowed";

  @ExceptionHandler(RecipientOwnershipMismatchException.class)
  public ResponseEntity<ProblemDetail> handleRecipientOwnershipMismatch(
    RecipientOwnershipMismatchException ex,
    HttpServletRequest request
  ) {
    log.atWarn()
      .setMessage("Recipient ownership validation failed")
      .addKeyValue("event", "recipient_ownership_validation")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "ownership_mismatch")
      .addKeyValue("httpStatus", 422)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("recipientId", ex.getRecipientId())
      .addKeyValue("bankAccountId", ex.getBankAccountId())
      .log();

    return ResponseEntity.of(buildRuleViolationProblemDetail(ex, request)).build();
  }

  @ExceptionHandler(DomainRuleValidationException.class)
  public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(DomainRuleValidationException ex, HttpServletRequest request) {
    log.atWarn()
      .setMessage("Rule validation failed")
      .addKeyValue("event", "rule_validation")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "rule_violation")
      .addKeyValue("httpStatus", 422)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("exceptionType", ex.getClass().getSimpleName())
      .log();

    return ResponseEntity.of(buildRuleViolationProblemDetail(ex, request)).build();
  }

  private ProblemDetail buildRuleViolationProblemDetail(DomainRuleValidationException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);
    problemDetail.setType(ProblemTypes.RULE_VIOLATION);
    problemDetail.setTitle(TITLE_OPERATION_NOT_ALLOWED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
