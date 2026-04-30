package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.FieldMessageError;
import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ValidationErrorMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.filter.CorrelationFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerMapping;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentNotValidExceptionHandler {

  public static final String TITLE_VALIDATION_FAILED = "Request validation failed";
  static final String ERRORS_PROPERTY = "errors";

  static final HttpStatus HTTP_STATUS_UNPROCESSABLE_CONTENT = HttpStatus.UNPROCESSABLE_CONTENT;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationException(
    MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<FieldMessageError> fieldErrors = ValidationErrorMapper.map(ex.getBindingResult().getFieldErrors());
    var invalidFields = fieldErrors.stream()
      .map(FieldMessageError::field)
      .collect(Collectors.joining(","));

    var logEvent = log.atWarn()
      .setMessage("Request validation failed")
      .addKeyValue("event_type", "recipient.operation.failed")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "validation_error")
      .addKeyValue("duration_ms", CorrelationFilter.durationMs(request))
      .addKeyValue("invalid_fields", invalidFields);

    var bankAccountId = bankAccountId(request);
    if (bankAccountId != null) {
      logEvent.addKeyValue("bank_account_id", bankAccountId);
    }

    logEvent.log();

    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_UNPROCESSABLE_CONTENT);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty(ERRORS_PROPERTY, fieldErrors);

    return ResponseEntity.of(problemDetail).build();
  }

  private static String bankAccountId(HttpServletRequest request) {
    var variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (variables instanceof Map<?, ?> pathVariables) {
      var bankAccountId = pathVariables.get("bank-account-id");
      if (bankAccountId != null) {
        return bankAccountId.toString();
      }
    }

    return null;
  }
}
