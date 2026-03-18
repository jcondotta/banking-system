package com.jcondotta.banking.infrastructure.web.exception;

import com.jcondotta.banking.infrastructure.web.problem.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MethodArgumentNotValidExceptionHandlerTest {

  private static final String REQUEST_URI = "/test";
  private static final String OBJECT_NAME = "request";

  private static final String FIELD_NAME_1 = "field1";
  private static final String FIELD_NAME_2 = "field2";

  private static final String ERROR_NO_NUMERIC_CHARACTERS = "must not contain any numeric characters";
  private static final String ERROR_MIN_LENGTH = "must have at least 3 characters";

  private static final String ERROR_NOT_BLANK = "must not be blank";

  private final MethodArgumentNotValidExceptionHandler handler = new MethodArgumentNotValidExceptionHandler();

  @Test
  void shouldReturnGroupedValidationErrors_whenMethodArgumentNotValidExceptionIsHandled() {
    var bindingResult = new BeanPropertyBindingResult(new Object(), OBJECT_NAME);

    bindingResult.addError(new FieldError(OBJECT_NAME, FIELD_NAME_1, ERROR_NO_NUMERIC_CHARACTERS));
    bindingResult.addError(new FieldError(OBJECT_NAME, FIELD_NAME_1, ERROR_MIN_LENGTH));
    bindingResult.addError(new FieldError(OBJECT_NAME, FIELD_NAME_2, ERROR_NOT_BLANK));

    var exception = new MethodArgumentNotValidException(null, bindingResult);

    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleValidationException(exception, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

    assertThat(response.getBody())
      .isNotNull()
      .satisfies(body -> {
        assertThat(body.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(body.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS);
        assertThat(body.getTitle()).isEqualTo(MethodArgumentNotValidExceptionHandler.TITLE_VALIDATION_FAILED);
        assertThat(body.getInstance()).isEqualTo(URI.create(REQUEST_URI));

        var properties = body.getProperties();

        assertThat(properties)
          .isNotNull()
          .hasSize(1)
          .containsKey(MethodArgumentNotValidExceptionHandler.ERRORS_PROPERTY);

        var errors = (List<?>) properties.get(MethodArgumentNotValidExceptionHandler.ERRORS_PROPERTY);

        assertThat(errors)
          .isNotNull()
          .hasSize(2)
          .anySatisfy(error ->
            assertThat(error.toString())
              .contains(FIELD_NAME_1)
              .contains(ERROR_NO_NUMERIC_CHARACTERS)
              .contains(ERROR_MIN_LENGTH)
          )
          .anySatisfy(error ->
            assertThat(error.toString())
              .contains(FIELD_NAME_2)
              .contains(ERROR_NOT_BLANK)
          );
      });
  }
}