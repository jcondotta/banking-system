package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ConflictExceptionHandler {

  public static final String TITLE_RESOURCE_ALREADY_EXISTS = "Resource already exists";

  @ExceptionHandler(DomainConflictException.class)
  public ResponseEntity<ProblemDetail> handleConflict(DomainConflictException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problemDetail.setType(ProblemTypes.CONFLICT);
    problemDetail.setTitle(TITLE_RESOURCE_ALREADY_EXISTS);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}