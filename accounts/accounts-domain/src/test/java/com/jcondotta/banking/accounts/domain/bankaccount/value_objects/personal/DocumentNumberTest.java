package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentNumberTest {

  private static final String VALID_DOCUMENT_NUMBER = "ABC123456";
  private static final String VALID_SPACED_DOCUMENT_NUMBER = "  ABC123456  ";

  @Test
  void shouldCreateDocumentNumber_whenValueIsValid() {
    var documentNumber = DocumentNumber.of(VALID_DOCUMENT_NUMBER);

    assertThat(documentNumber.value()).isEqualTo(VALID_DOCUMENT_NUMBER);
  }

  @Test
  void shouldTrimValue_whenValueHasSpaces() {
    var documentNumber = DocumentNumber.of(VALID_SPACED_DOCUMENT_NUMBER);

    assertThat(documentNumber.value()).isEqualTo(VALID_DOCUMENT_NUMBER);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> DocumentNumber.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DocumentNumber.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenValueIsBlank(String blankDocumentNumber) {
    assertThatThrownBy(() -> DocumentNumber.of(blankDocumentNumber))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DocumentNumber.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var documentNumberTooLong = "A".repeat(41);

    assertThatThrownBy(() -> DocumentNumber.of(documentNumberTooLong))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(DocumentNumber.MUST_NOT_EXCEED_LENGTH.formatted(DocumentNumber.MAX_LENGTH));
  }

  @Test
  void shouldBeEqual_whenDocumentNumbersHaveSameValue() {
    var documentNumber1 = DocumentNumber.of(VALID_DOCUMENT_NUMBER);
    var documentNumber2 = DocumentNumber.of(VALID_DOCUMENT_NUMBER);

    assertThat(documentNumber1)
      .isEqualTo(documentNumber2)
      .hasSameHashCodeAs(documentNumber2);
  }

  @Test
  void shouldNotBeEqual_whenDocumentNumbersHaveDifferentValues() {
    var documentNumber1 = DocumentNumber.of(VALID_DOCUMENT_NUMBER);
    var documentNumber2 = DocumentNumber.of("XYZ999");

    assertThat(documentNumber1).isNotEqualTo(documentNumber2);
  }

  @Test
  void shouldBeEqual_whenValuesDifferOnlyBySpaces() {
    var doc1 = DocumentNumber.of(VALID_DOCUMENT_NUMBER);
    var doc2 = DocumentNumber.of(VALID_SPACED_DOCUMENT_NUMBER);

    assertThat(doc1.hashCode()).isEqualTo(doc2.hashCode());
  }
}