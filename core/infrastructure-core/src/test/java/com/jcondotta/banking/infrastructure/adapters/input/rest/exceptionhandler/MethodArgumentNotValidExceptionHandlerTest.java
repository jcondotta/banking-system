package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.ApiProblem;
import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MethodArgumentNotValidExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/recipients";
  private static final String FIELD_NAME = "recipientName";
  private static final String FIELD_MESSAGE = "must not be blank";

  private final MethodArgumentNotValidExceptionHandler handler = new MethodArgumentNotValidExceptionHandler();

  @Test
  void shouldReturnUnprocessableContentProblemDetail_whenMethodArgumentNotValidExceptionIsThrown() {
    var exception = methodArgumentNotValidException();
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handle(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus())
        .isEqualTo(MethodArgumentNotValidExceptionHandler.HTTP_STATUS_UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ApiProblem.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle())
        .isEqualTo(MethodArgumentNotValidExceptionHandler.TITLE_VALIDATION_FAILED),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI)),
      () -> assertThat(problemDetail.getProperties())
        .containsEntry(
          MethodArgumentNotValidExceptionHandler.ERRORS_PROPERTY,
          List.of(new FieldErrorDetail(FIELD_NAME, List.of(FIELD_MESSAGE)))
        )
    );
  }

  private static MethodArgumentNotValidException methodArgumentNotValidException() {
    var bindingResult = new BeanPropertyBindingResult(new TestRequest(null), "request");
    bindingResult.addError(new FieldError("request", FIELD_NAME, null, false, null, null, FIELD_MESSAGE));

    return new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);
  }

  private record TestRequest(String recipientName) {
  }
}
