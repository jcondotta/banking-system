package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultDocumentNumberValidatorRegistryTest {

  private final DocumentNumberValidator spanishDniNumberValidator = new SpanishDniNumberValidator();
  private final DocumentNumberValidator spanishNieNumberValidator = new SpanishNieNumberValidator();

  private final List<DocumentNumberValidator> documentNumberValidators = List.of(
    spanishDniNumberValidator,
    spanishNieNumberValidator
  );

  @Test
  void shouldResolveValidator_whenValidatorExists() {
    var registry = new DefaultDocumentNumberValidatorRegistry(documentNumberValidators);

    assertThat(registry.resolve(DocumentCountry.SPAIN, DocumentType.NATIONAL_ID))
      .isSameAs(spanishDniNumberValidator);

    assertThat(registry.resolve(DocumentCountry.SPAIN, DocumentType.FOREIGNER_ID))
      .isSameAs(spanishNieNumberValidator);
  }

  @Test
  void shouldThrowException_whenValidatorNotFound() {
    var registry = new DefaultDocumentNumberValidatorRegistry(List.of(spanishDniNumberValidator));

    assertThatThrownBy(() -> registry.resolve(DocumentCountry.SPAIN, DocumentType.FOREIGNER_ID))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage("No document validator found for SPAIN - FOREIGNER_ID");
  }

  @Test
  void shouldThrowException_whenValidatorsListIsNull() {
    assertThatThrownBy(() -> new DefaultDocumentNumberValidatorRegistry(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("validators must be provided");
  }

  @Test
  void shouldThrowException_whenCountryIsNull() {
    var registry = new DefaultDocumentNumberValidatorRegistry(List.of());

    assertThatThrownBy(() -> registry.resolve(null, DocumentType.NATIONAL_ID))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("country must be provided");
  }

  @Test
  void shouldThrowException_whenTypeIsNull() {
    var registry = new DefaultDocumentNumberValidatorRegistry(List.of());

    assertThatThrownBy(() -> registry.resolve(DocumentCountry.SPAIN, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("type must be provided");
  }
}