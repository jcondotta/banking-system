package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.ApiProblem;
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

  private static final String REQUEST_URI = "/api/resources";
  private static final String EXCEPTION_MESSAGE = "Resource value must not be blank";

  private final DomainValidationExceptionHandler handler = new DomainValidationExceptionHandler();

  @Test
  void shouldReturnUnprocessableContentProblemDetail_whenDomainValidationExceptionIsThrown() {
    var exception = new TestDomainValidationException(EXCEPTION_MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handle(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(DomainValidationExceptionHandler.HTTP_STATUS_UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ApiProblem.VALIDATION_ERRORS),
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
