package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateOfBirthTest {

  @Test
  void shouldCreateDateOfBirth_whenValueIsValid() {
    var dateOfBirth = DateOfBirth.of(LocalDate.of(1990, 1, 1));

    assertThat(dateOfBirth)
      .extracting(DateOfBirth::value)
      .isEqualTo(LocalDate.of(1990, 1, 1));
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> DateOfBirth.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DateOfBirth.DATE_OF_BIRTH_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenDateIsInTheFuture() {
    var futureDate = LocalDate.now().plusDays(1);

    assertThatThrownBy(() -> DateOfBirth.of(futureDate))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DateOfBirth.DATE_OF_BIRTH_NOT_IN_PAST);
  }

  @Test
  void shouldAllowTodayAsValidDateOfBirth() {
    var today = LocalDate.now();
    var dateOfBirth = DateOfBirth.of(today);

    assertThat(dateOfBirth.value()).isEqualTo(today);
  }

  @Test
  void shouldBeEqual_whenDatesOfBirthHaveSameValue() {
    var date = LocalDate.of(1985, 5, 20);

    var dateOfBirth1 = DateOfBirth.of(date);
    var dateOfBirth2 = DateOfBirth.of(date);

    assertThat(dateOfBirth1)
      .isEqualTo(dateOfBirth2)
      .hasSameHashCodeAs(dateOfBirth2);
  }

  @Test
  void shouldNotBeEqual_whenDatesOfBirthHaveDifferentValues() {
    var dateOfBirth1 = DateOfBirth.of(LocalDate.of(1985, 5, 20));
    var dateOfBirth2 = DateOfBirth.of(LocalDate.of(1990, 1, 1));

    assertThat(dateOfBirth1).isNotEqualTo(dateOfBirth2);
  }
}
