package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
      .isInstanceOf(InvalidDomainDataException.class)
      .hasMessage(DefaultDocumentNumberValidatorRegistry.VALIDATORS_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenValidatorIsNull() {
    var validators = new ArrayList<>(Arrays.asList(spanishDniNumberValidator, null));

    assertThatThrownBy(() -> new DefaultDocumentNumberValidatorRegistry(validators))
      .isInstanceOf(InvalidDomainDataException.class)
      .hasMessage(DefaultDocumentNumberValidatorRegistry.VALIDATOR_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCountryIsNull() {
    var registry = new DefaultDocumentNumberValidatorRegistry(List.of());

    assertThatThrownBy(() -> registry.resolve(null, DocumentType.NATIONAL_ID))
      .isInstanceOf(InvalidDomainDataException.class)
      .hasMessage(DefaultDocumentNumberValidatorRegistry.COUNTRY_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenTypeIsNull() {
    var registry = new DefaultDocumentNumberValidatorRegistry(List.of());

    assertThatThrownBy(() -> registry.resolve(DocumentCountry.SPAIN, null))
      .isInstanceOf(InvalidDomainDataException.class)
      .hasMessage(DefaultDocumentNumberValidatorRegistry.TYPE_NOT_PROVIDED);
  }
}
