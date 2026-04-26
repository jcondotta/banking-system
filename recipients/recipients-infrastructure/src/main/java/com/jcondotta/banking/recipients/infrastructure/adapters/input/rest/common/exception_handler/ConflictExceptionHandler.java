package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.domain.exception.DomainConflictException;
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
public class ConflictExceptionHandler {

  public static final String TITLE_RESOURCE_ALREADY_EXISTS = "Resource already exists";

  @ExceptionHandler(DuplicateRecipientIbanException.class)
  public ResponseEntity<ProblemDetail> handleDuplicateRecipientIbanConflict(
    DuplicateRecipientIbanException ex, HttpServletRequest request) {

    log.atWarn()
      .setMessage("Recipient creation failed")
      .addKeyValue("event", "recipient_creation")
      .addKeyValue("operation", "create")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "duplicate_iban")
      .addKeyValue("httpStatus", 409)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("bankAccountId", ex.getBankAccountId())
      .addKeyValue("maskedIban", ex.getMaskedIban())
      .log();

    return ResponseEntity.of(buildConflictProblemDetail(ex, request)).build();
  }

  @ExceptionHandler(DomainConflictException.class)
  public ResponseEntity<ProblemDetail> handleConflict(DomainConflictException ex, HttpServletRequest request) {
    log.atWarn()
      .setMessage("Recipient conflict detected")
      .addKeyValue("event", "recipient_conflict")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "conflict")
      .addKeyValue("httpStatus", 409)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("exceptionType", ex.getClass().getSimpleName())
      .log();

    return ResponseEntity.of(buildConflictProblemDetail(ex, request)).build();
  }

  private ProblemDetail buildConflictProblemDetail(DomainConflictException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problemDetail.setType(ProblemTypes.CONFLICT);
    problemDetail.setTitle(TITLE_RESOURCE_ALREADY_EXISTS);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return problemDetail;
  }
}
