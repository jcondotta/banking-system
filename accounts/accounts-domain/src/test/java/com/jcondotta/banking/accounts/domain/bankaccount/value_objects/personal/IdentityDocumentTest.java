package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityDocumentTest {

  private static final DocumentCountry SPAIN = DocumentCountry.SPAIN;

  private static final DocumentNumber VALID_SPANISH_DNI = DocumentNumber.of("12345678Z");
  private static final DocumentNumber VALID_SPANISH_NIE = DocumentNumber.of("X1234567L");

  @Test
  void shouldCreateIdentityDocument_whenSpanishDniIsValid() {
    var identityDocument = IdentityDocument.of(SPAIN, DocumentType.NATIONAL_ID, VALID_SPANISH_DNI);

    assertThat(identityDocument.country()).isEqualTo(SPAIN);
    assertThat(identityDocument.type()).isEqualTo(DocumentType.NATIONAL_ID);
    assertThat(identityDocument.number()).isEqualTo(VALID_SPANISH_DNI);
  }

  @Test
  void shouldCreateIdentityDocument_whenSpanishNieIsValid() {
    var identityDocument = IdentityDocument.of(SPAIN, DocumentType.FOREIGNER_ID, VALID_SPANISH_NIE);

    assertThat(identityDocument.country()).isEqualTo(SPAIN);
    assertThat(identityDocument.type()).isEqualTo(DocumentType.FOREIGNER_ID);
    assertThat(identityDocument.number()).isEqualTo(VALID_SPANISH_NIE);
  }

  @Test
  void shouldThrowException_whenDocumentCountryIsNull() {
    assertThatThrownBy(() -> IdentityDocument.of(null, DocumentType.NATIONAL_ID, VALID_SPANISH_DNI))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(IdentityDocument.DOCUMENT_COUNTRY_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenDocumentTypeIsNull() {
    assertThatThrownBy(() -> IdentityDocument.of(SPAIN, null, VALID_SPANISH_DNI))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(IdentityDocument.DOCUMENT_TYPE_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenDocumentNumberIsNull() {
    assertThatThrownBy(() -> IdentityDocument.of(SPAIN, DocumentType.NATIONAL_ID, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(IdentityDocument.DOCUMENT_NUMBER_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenDocumentNumberIsInvalidForType() {
    var invalidNumber = DocumentNumber.of("INVALID");

    assertThatThrownBy(() -> IdentityDocument.of(SPAIN, DocumentType.NATIONAL_ID, invalidNumber))
      .isInstanceOf(DomainValidationException.class);
  }
}