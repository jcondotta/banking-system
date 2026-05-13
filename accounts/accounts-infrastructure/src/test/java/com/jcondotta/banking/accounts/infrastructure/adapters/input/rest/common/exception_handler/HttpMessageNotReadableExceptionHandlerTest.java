package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpMessageNotReadableExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/accounts";

  private final HttpMessageNotReadableExceptionHandler handler = new HttpMessageNotReadableExceptionHandler();

  @Test
  void shouldReturnBadRequestProblemDetail_whenHttpMessageNotReadableExceptionIsThrown() {
    var exception = new HttpMessageNotReadableException("Malformed JSON request", mock(HttpInputMessage.class));
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handle(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpMessageNotReadableExceptionHandler.HTTP_STATUS_BAD_REQUEST.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(HttpMessageNotReadableExceptionHandler.TITLE_VALIDATION_FAILED),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(HttpMessageNotReadableExceptionHandler.DETAIL_UNREADABLE_MESSAGE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }
}
