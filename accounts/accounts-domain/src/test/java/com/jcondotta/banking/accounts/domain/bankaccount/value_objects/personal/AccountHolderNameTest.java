package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHolderNameTest {

  private static final String FIRST_NAME_JEFFERSON = "Jefferson";
  private static final String FIRST_NAME_PATRIZIO = "Patrizio";
  private static final String LAST_NAME_CONDOTTA = "Condotta";

  private static final String FULL_NAME = FIRST_NAME_JEFFERSON + " " + LAST_NAME_CONDOTTA;

  @Test
  void shouldCreateAccountHolderName_whenValuesAreValid() {
    var name = AccountHolderName.of(FIRST_NAME_JEFFERSON, LAST_NAME_CONDOTTA);

    assertThat(name.firstName()).isEqualTo(FIRST_NAME_JEFFERSON);
    assertThat(name.lastName()).isEqualTo(LAST_NAME_CONDOTTA);
    assertThat(name.fullName()).isEqualTo(FULL_NAME);
  }

  @Test
  void shouldThrowException_whenFirstNameIsNull() {
    assertThatThrownBy(() -> AccountHolderName.of(null, LAST_NAME_CONDOTTA))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderName.FIRST_NAME_MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenLastNameIsNull() {
    assertThatThrownBy(() -> AccountHolderName.of(FIRST_NAME_JEFFERSON, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderName.LAST_NAME_MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenFirstNameIsBlank(String blankValue) {
    assertThatThrownBy(() -> AccountHolderName.of(blankValue, LAST_NAME_CONDOTTA))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderName.FIRST_NAME_MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenLastNameIsBlank(String blankValue) {
    assertThatThrownBy(() -> AccountHolderName.of(FIRST_NAME_JEFFERSON, blankValue))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderName.LAST_NAME_MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenFirstNameExceedsMaxLength() {
    var longFirstName = "A".repeat(AccountHolderName.MAX_LENGTH + 1);

    assertThatThrownBy(() -> AccountHolderName.of(longFirstName, LAST_NAME_CONDOTTA))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderName.FIRST_NAME_MUST_NOT_EXCEED_LENGTH.formatted(AccountHolderName.MAX_LENGTH));
  }

  @Test
  void shouldThrowException_whenLastNameExceedsMaxLength() {
    var longLastName = "A".repeat(AccountHolderName.MAX_LENGTH + 1);

    assertThatThrownBy(() -> AccountHolderName.of(FIRST_NAME_JEFFERSON, longLastName))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolderName.LAST_NAME_MUST_NOT_EXCEED_LENGTH.formatted(AccountHolderName.MAX_LENGTH));
  }

  @Test
  void shouldCreateAccountHolderName_whenFirstNameIsExactlyMaxLength() {
    var firstName = "A".repeat(AccountHolderName.MAX_LENGTH);

    var name = AccountHolderName.of(firstName, LAST_NAME_CONDOTTA);

    assertThat(name.firstName()).isEqualTo(firstName);
  }

  @Test
  void shouldNormalizeWhitespace_whenNamesContainExtraSpaces() {
    var name = AccountHolderName.of("  Jefferson  ", "  Condotta  ");

    assertThat(name.firstName()).isEqualTo(FIRST_NAME_JEFFERSON);
    assertThat(name.lastName()).isEqualTo(LAST_NAME_CONDOTTA);
    assertThat(name.fullName()).isEqualTo(FULL_NAME);
  }

  @Test
  void shouldBeEqual_whenNamesHaveSameValues() {
    var name1 = AccountHolderName.of(FIRST_NAME_JEFFERSON, LAST_NAME_CONDOTTA);
    var name2 = AccountHolderName.of(FIRST_NAME_JEFFERSON, LAST_NAME_CONDOTTA);

    assertThat(name1)
      .isEqualTo(name2)
      .hasSameHashCodeAs(name2);
  }

  @Test
  void shouldNotBeEqual_whenNamesHaveDifferentValues() {
    var name1 = AccountHolderName.of(FIRST_NAME_JEFFERSON, LAST_NAME_CONDOTTA);
    var name2 = AccountHolderName.of(FIRST_NAME_PATRIZIO, LAST_NAME_CONDOTTA);

    assertThat(name1).isNotEqualTo(name2);
  }
}