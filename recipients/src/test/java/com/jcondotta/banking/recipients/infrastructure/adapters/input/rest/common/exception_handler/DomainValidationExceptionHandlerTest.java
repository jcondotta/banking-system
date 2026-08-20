package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DomainValidationExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/recipients";
  private static final String EXCEPTION_MESSAGE = "Recipient name must not be blank";

  private final DomainValidationExceptionHandler handler = new DomainValidationExceptionHandler();

  @Test
  void shouldReturnBadRequestProblemDetail_whenDomainValidationExceptionIsThrown() {
    var exception = new TestDomainValidationException(EXCEPTION_MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handle(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(DomainValidationExceptionHandler.HTTP_STATUS_BAD_REQUEST.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(DomainValidationExceptionHandler.TITLE_VALIDATION_FAILED),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(EXCEPTION_MESSAGE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  private static final class TestDomainValidationException extends DomainValidationException {

    private TestDomainValidationException(String message) {
      super(message);
    }
  }
}
