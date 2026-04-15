package com.jcondotta.banking.recipients.domain.recipient.value_objects;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientNameTest {

  private static final String RECIPIENT_NAME_JEFFERSON = "Jefferson Condotta";
  private static final String RECIPIENT_NAME_PATRIZIO = "Patrizio Condotta";

  @Test
  void shouldCreateRecipientName_whenValueIsValid() {
    assertThat(RecipientName.of(RECIPIENT_NAME_JEFFERSON))
        .isNotNull()
        .extracting(RecipientName::value)
        .isEqualTo(RECIPIENT_NAME_JEFFERSON);
  }

  @Test
  void shouldThrowException_whenNameIsNull() {
    assertThatThrownBy(() -> RecipientName.of(null))
        .isInstanceOf(DomainValidationException.class)
        .hasMessage(RecipientName.NAME_NOT_PROVIDED);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenNameIsBlank(String blankValue) {
    assertThatThrownBy(() -> RecipientName.of(blankValue))
        .isInstanceOf(DomainValidationException.class)
        .hasMessage(RecipientName.NAME_NOT_BLANK);
  }

  @Test
  void shouldCreateRecipientName_whenNameHasMaxLength() {
    var maxLengthName = "A".repeat(RecipientName.MAX_LENGTH);

    assertThat(RecipientName.of(maxLengthName).value()).isEqualTo(maxLengthName);
  }

  @Test
  void shouldThrowException_whenNameIsTooLong() {
    var longName = "A".repeat(51);

    assertThatThrownBy(() -> RecipientName.of(longName))
        .isInstanceOf(DomainValidationException.class)
        .hasMessage(RecipientName.NAME_MUST_NOT_EXCEED_LENGTH.formatted(RecipientName.MAX_LENGTH));
  }

  @Test
  void shouldBeEqual_whenNamesHaveSameValue() {
    var recipientName1 = RecipientName.of(RECIPIENT_NAME_JEFFERSON);
    var recipientName2 = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

    assertThat(recipientName1).isEqualTo(recipientName2).hasSameHashCodeAs(recipientName2);
  }

  @Test
  void shouldNotBeEqual_whenNamesHaveDifferentValues() {
    var recipientName1 = RecipientName.of(RECIPIENT_NAME_JEFFERSON);
    var recipientName2 = RecipientName.of(RECIPIENT_NAME_PATRIZIO);

    assertThat(recipientName1).isNotEqualTo(recipientName2);
  }
}
