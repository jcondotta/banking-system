package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.model;

import com.jcondotta.banking.recipients.infrastructure.testsupport.factory.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListRecipientsRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  @Test
  void shouldNotDetectAnyViolation_whenAllParamsAreValid() {
    var request = new ListRecipientsRequest(1, 10, "jef");

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldApplyDefaultPage_whenPageIsNull() {
    var request = new ListRecipientsRequest(null, 10, null);

    assertThat(request.page()).isEqualTo(ListRecipientsRequest.DEFAULT_PAGE);
    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldApplyDefaultSize_whenSizeIsNull() {
    var request = new ListRecipientsRequest(0, null, null);

    assertThat(request.size()).isEqualTo(ListRecipientsRequest.DEFAULT_SIZE);
    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldApplyBothDefaults_whenPageAndSizeAreNull() {
    var request = new ListRecipientsRequest(null, null, null);

    assertThat(request.page()).isEqualTo(ListRecipientsRequest.DEFAULT_PAGE);
    assertThat(request.size()).isEqualTo(ListRecipientsRequest.DEFAULT_SIZE);
    assertThat(request.name()).isNull();
    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldDetectConstraintViolation_whenPageIsNegative() {
    var request = new ListRecipientsRequest(-1, 10, null);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(v -> {
        assertThat(v.getPropertyPath()).hasToString("page");
        assertThat(v.getMessage()).isEqualTo("must be greater than or equal to 0");
      });
  }

  @Test
  void shouldDetectConstraintViolation_whenSizeIsZero() {
    var request = new ListRecipientsRequest(0, 0, null);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(v -> {
        assertThat(v.getPropertyPath()).hasToString("size");
        assertThat(v.getMessage()).isEqualTo("must be greater than or equal to 1");
      });
  }

  @Test
  void shouldDetectConstraintViolation_whenSizeExceedsMax() {
    var request = new ListRecipientsRequest(0, 101, null);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(v -> {
        assertThat(v.getPropertyPath()).hasToString("size");
        assertThat(v.getMessage()).isEqualTo("must be less than or equal to 100");
      });
  }

  @Test
  void shouldDetectConstraintViolation_whenNameExceedsMaxLength() {
    var nameLongerThan50 = "a".repeat(51);
    var request = new ListRecipientsRequest(0, 20, nameLongerThan50);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(v -> {
        assertThat(v.getPropertyPath()).hasToString("name");
        assertThat(v.getMessage()).isEqualTo("size must be between 0 and 50");
      });
  }

  @Test
  void shouldNotDetectAnyViolation_whenNameIsNull() {
    var request = new ListRecipientsRequest(0, 20, null);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldNotDetectAnyViolation_whenNameIsBlank() {
    var request = new ListRecipientsRequest(0, 20, "   ");

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }
}
