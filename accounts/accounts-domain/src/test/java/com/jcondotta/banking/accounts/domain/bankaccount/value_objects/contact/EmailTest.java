package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact;

import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

  @ParameterizedTest
  @CsvSource({
    "'John.Doe@Example.com', 'john.doe@example.com'",
    "'   John.Doe@Example.com   ', 'john.doe@example.com'",
    "'TEST@DOMAIN.COM', 'test@domain.com'",
    "'user+alias@Sub.Domain.Co.Uk', 'user+alias@sub.domain.co.uk'"
  })
  void shouldNormalizeEmail_whenValidEmailIsProvided(String rawValue, String expectedValue) {
    var email = Email.of(rawValue);

    assertThat(email.value())
      .isEqualTo(expectedValue);
  }

  @Test
  void shouldBeEqual_whenEmailsHaveSameNormalizedValue_afterNormalization() {
    var email1 = Email.of("Test@Example.com");
    var email2 = Email.of("test@example.com");

    assertThat(email1)
      .isEqualTo(email2)
      .hasSameHashCodeAs(email2);
  }

  @Test
  void shouldTrimEmail_whenEmailContainsLeadingOrTrailingSpaces() {
    var email = Email.of("   test@example.com   ");

    assertThat(email.value()).isEqualTo("test@example.com");
  }

  @Test
  void shouldThrowException_whenEmailIsNull() {
    assertThatThrownBy(() -> Email.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Email.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenEmailIsBlank(String blankEmailValue) {
    assertThatThrownBy(() -> Email.of(blankEmailValue))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Email.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenEmailExceedsMaxLength() {
    var localPart = "a".repeat(Email.MAX_LENGTH + 1);
    var email = localPart + "@t.com";

    assertThatThrownBy(() -> Email.of(email))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Email.MUST_NOT_EXCEED_LENGTH.formatted(Email.MAX_LENGTH));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "invalid",
    "invalid@",
    "@domain.com",
    "test@domain",
    "test@.com",
    "john doe@email.com",
    "john@do main.com",
    "john @email.com",
    "john@ email.com"
  })
  void shouldThrowException_whenEmailFormatIsInvalid(String invalidEmailValue) {
    assertThatThrownBy(() -> Email.of(invalidEmailValue))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Email.INVALID_FORMAT);
  }
}