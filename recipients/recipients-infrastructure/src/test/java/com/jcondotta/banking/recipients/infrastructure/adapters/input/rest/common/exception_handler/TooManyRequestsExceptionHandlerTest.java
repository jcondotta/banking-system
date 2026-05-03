package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.resilience.InvocationRejectedException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TooManyRequestsExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/recipients";

  private final TooManyRequestsExceptionHandler handler = new TooManyRequestsExceptionHandler();

  @Test
  void shouldReturnTooManyRequestsProblemDetail_whenInvocationRejectedExceptionIsThrown() {
    var exception = new InvocationRejectedException("Concurrency limit reached", new Object());
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handle(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(TooManyRequestsExceptionHandler.HTTP_STATUS_TOO_MANY_REQUESTS.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.TOO_MANY_REQUESTS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(TooManyRequestsExceptionHandler.DETAIL_CONCURRENCY_LIMIT_REACHED),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }
}
