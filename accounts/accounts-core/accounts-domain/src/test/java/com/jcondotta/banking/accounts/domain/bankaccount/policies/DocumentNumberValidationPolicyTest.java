package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentNumberValidationPolicyTest {

  @Mock
  private DocumentNumberValidatorRegistry registry;

  @Mock
  private DocumentNumberValidator validator;

  private DocumentNumberValidationPolicy policy;

  private final DocumentCountry COUNTRY = DocumentCountry.SPAIN;
  private final DocumentType TYPE = DocumentType.NATIONAL_ID;
  private final DocumentNumber NUMBER = DocumentNumber.of("X1234567L");

  @BeforeEach
  void setUp() {
    policy = new DocumentNumberValidationPolicy(registry);
  }

  @Test
  void shouldDelegateToResolvedValidator_whenAllParametersAreValid() {
    when(registry.resolve(COUNTRY, TYPE)).thenReturn(validator);

    assertThatCode(() -> policy.validate(COUNTRY, TYPE, NUMBER)).doesNotThrowAnyException();

    verify(registry).resolve(COUNTRY, TYPE);
    verify(validator).validate(NUMBER);
  }

  @Test
  void shouldThrowException_whenRegistryIsNull() {
    assertThatThrownBy(() -> new DocumentNumberValidationPolicy(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(DocumentNumberValidationPolicy.REGISTRY_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCountryIsNull() {
    assertThatThrownBy(() -> policy.validate(null, TYPE, NUMBER))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(DocumentNumberValidationPolicy.COUNTRY_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenTypeIsNull() {
    assertThatThrownBy(() -> policy.validate(COUNTRY, null, NUMBER))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(DocumentNumberValidationPolicy.TYPE_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenNumberIsNull() {
    assertThatThrownBy(() -> policy.validate(COUNTRY, TYPE, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(DocumentNumberValidationPolicy.NUMBER_NOT_PROVIDED);
  }
}