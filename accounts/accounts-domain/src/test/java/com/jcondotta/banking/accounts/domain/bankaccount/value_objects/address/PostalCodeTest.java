package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostalCodeTest {

  private static final String VALID_POSTAL_CODE = "08001";

  @Test
  void shouldCreatePostalCode_whenValueIsValid() {
    var postalCode = PostalCode.of(VALID_POSTAL_CODE);

    assertThat(postalCode.value()).isEqualTo(VALID_POSTAL_CODE);
  }

  @Test
  void shouldCreatePostalCode_whenValueIsAtMaxLengthBoundary() {
    var postalCodeAtMaxLength = "1".repeat(PostalCode.MAX_LENGTH);

    var postalCode = PostalCode.of(postalCodeAtMaxLength);

    assertThat(postalCode.value()).hasSize(PostalCode.MAX_LENGTH);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> PostalCode.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(PostalCode.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenValueIsBlank(String blankPostalCode) {
    assertThatThrownBy(() -> PostalCode.of(blankPostalCode))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(PostalCode.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var postalCodeExceedingMaxLength = "1".repeat(PostalCode.MAX_LENGTH + 1);

    assertThatThrownBy(() -> PostalCode.of(postalCodeExceedingMaxLength))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(
        PostalCode.MUST_NOT_EXCEED_LENGTH
          .formatted(PostalCode.MAX_LENGTH)
      );
  }
}