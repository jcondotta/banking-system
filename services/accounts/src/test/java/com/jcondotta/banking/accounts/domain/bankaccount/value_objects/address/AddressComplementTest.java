package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressComplementTest {

  private static final String VALID_COMPLEMENT = "Apartment 42B";

  @Test
  void shouldCreateAddressComplement_whenValueIsValid() {
    var complement = AddressComplement.ofNullable(VALID_COMPLEMENT);

    assertThat(complement.value()).isEqualTo(VALID_COMPLEMENT);
  }

  @Test
  void shouldCreateAddressComplement_whenValueIsAtMaxLengthBoundary() {
    var complementAtMaxLength = "a".repeat(AddressComplement.MAX_LENGTH);

    var complement = AddressComplement.ofNullable(complementAtMaxLength);

    assertThat(complement.value()).hasSize(AddressComplement.MAX_LENGTH);
  }

  @Test
  void shouldReturnNull_whenValueIsNull() {
    assertThat(AddressComplement.ofNullable(null)).isNull();
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldReturnNull_whenValueIsBlank(String blankComplement) {
    assertThat(AddressComplement.ofNullable(blankComplement)).isNull();
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var complementExceedingMaxLength = "a".repeat(AddressComplement.MAX_LENGTH + 1);

    assertThatThrownBy(() -> AddressComplement.ofNullable(complementExceedingMaxLength))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(
        AddressComplement.MUST_NOT_EXCEED_LENGTH
          .formatted(AddressComplement.MAX_LENGTH)
      );
  }
}