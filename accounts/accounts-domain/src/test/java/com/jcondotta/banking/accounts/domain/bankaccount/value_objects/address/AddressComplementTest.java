package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressComplementTest {

  private static final String VALID_COMPLEMENT = "Apartment 42B";

  @Test
  void shouldCreateAddressComplement_whenValueIsValid() {
    var complement = AddressComplement.of(VALID_COMPLEMENT);

    assertThat(complement.value()).isEqualTo(VALID_COMPLEMENT);
  }

  @Test
  void shouldCreateAddressComplement_whenValueIsAtMaxLengthBoundary() {
    var complementAtMaxLength = "a".repeat(AddressComplement.MAX_LENGTH);

    var complement = AddressComplement.of(complementAtMaxLength);

    assertThat(complement.value()).hasSize(AddressComplement.MAX_LENGTH);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> AddressComplement.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AddressComplement.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenValueIsBlank(String blankComplement) {
    assertThatThrownBy(() -> AddressComplement.of(blankComplement))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AddressComplement.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var complementExceedingMaxLength = "a".repeat(AddressComplement.MAX_LENGTH + 1);

    assertThatThrownBy(() -> AddressComplement.of(complementExceedingMaxLength))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(
        AddressComplement.MUST_NOT_EXCEED_LENGTH
          .formatted(AddressComplement.MAX_LENGTH)
      );
  }
}