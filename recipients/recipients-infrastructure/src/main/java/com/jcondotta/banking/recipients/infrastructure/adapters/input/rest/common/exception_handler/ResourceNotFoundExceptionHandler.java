package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
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
public class ResourceNotFoundExceptionHandler {

  static final HttpStatus HTTP_STATUS_NOT_FOUND = HttpStatus.NOT_FOUND;
  static final String TITLE_RESOURCE_NOT_FOUND = "Not Found";

  @ExceptionHandler(RecipientNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleRecipientNotFound(RecipientNotFoundException ex, HttpServletRequest request) {
    return ResponseEntity.of(buildNotFoundProblemDetail(ex, request))
      .build();
  }

  private ProblemDetail buildNotFoundProblemDetail(RecipientNotFoundException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_NOT_FOUND);
    problemDetail.setType(ProblemTypes.RESOURCE_NOT_FOUND);
    problemDetail.setTitle(TITLE_RESOURCE_NOT_FOUND);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
