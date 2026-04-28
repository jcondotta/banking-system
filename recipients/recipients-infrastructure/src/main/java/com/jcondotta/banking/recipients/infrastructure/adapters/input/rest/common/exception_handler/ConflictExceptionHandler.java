package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.application.common.exception.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientAlreadyExistsException;
import com.jcondotta.domain.exception.DomainException;
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
public class ConflictExceptionHandler {

  static final HttpStatus HTTP_STATUS_CONFLICT = HttpStatus.CONFLICT;
  static final String TITLE_RESOURCE_ALREADY_EXISTS = "Resource already exists";

  @ExceptionHandler({
    DuplicateRecipientIbanException.class,
    RecipientAlreadyExistsException.class,
    RecipientOptimisticLockException.class
  })
  public ResponseEntity<ProblemDetail> handleConflict(DomainException ex, HttpServletRequest request) {
    return ResponseEntity.of(buildConflictProblemDetail(ex, request)).build();
  }

  private ProblemDetail buildConflictProblemDetail(DomainException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_CONFLICT);
    problemDetail.setType(ProblemTypes.CONFLICT);
    problemDetail.setTitle(TITLE_RESOURCE_ALREADY_EXISTS);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
