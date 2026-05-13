package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

  private static final String REQUEST_METHOD = "POST";
  private static final String REQUEST_URI = "/api/accounts";

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void shouldReturnInternalServerErrorProblemDetail_whenUnexpectedExceptionIsThrown() {
    var exception = new RuntimeException("Unexpected failure");
    var request = mock(HttpServletRequest.class);

    when(request.getMethod()).thenReturn(REQUEST_METHOD);
    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleUnexpected(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.INTERNAL_ERROR),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isNull(),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  @Test
  void shouldPreserveProblemDetailAndStatus_whenErrorResponseExceptionIsThrown() {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle("Request validation failed");
    problemDetail.setDetail("Invalid request");
    var exception = new ErrorResponseException(HttpStatus.BAD_REQUEST, problemDetail, null);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleErrorResponseException(exception, request);
    var responseProblemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
      () -> assertThat(responseProblemDetail).isNotNull(),
      () -> assertThat(responseProblemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
      () -> assertThat(responseProblemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(responseProblemDetail.getTitle()).isEqualTo("Request validation failed"),
      () -> assertThat(responseProblemDetail.getDetail()).isEqualTo("Invalid request"),
      () -> assertThat(responseProblemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }
}
