package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

  private static final String VALID_STREET = "Gran Via";
  private static final String VALID_NUMBER = "123A";
  private static final String VALID_COMPLEMENT = "Apartment 42B";
  private static final String VALID_POSTAL_CODE = "08001";
  private static final String VALID_CITY = "Barcelona";

  @Test
  void shouldCreateAddress_whenAllRequiredFieldsAreProvided_withoutComplement() {
    var address = Address.of(
      VALID_STREET,
      VALID_NUMBER,
      null,
      VALID_POSTAL_CODE,
      VALID_CITY
    );

    assertThat(address.street().value()).isEqualTo(VALID_STREET);
    assertThat(address.streetNumber().value()).isEqualTo(VALID_NUMBER);
    assertThat(address.complement()).isNull();
    assertThat(address.postalCode().value()).isEqualTo(VALID_POSTAL_CODE);
    assertThat(address.city().value()).isEqualTo(VALID_CITY);
  }

  @Test
  void shouldCreateAddress_whenAllFieldsAreProvided_withComplement() {
    var address = Address.of(
      VALID_STREET,
      VALID_NUMBER,
      VALID_COMPLEMENT,
      VALID_POSTAL_CODE,
      VALID_CITY
    );

    assertThat(address.street().value()).isEqualTo(VALID_STREET);
    assertThat(address.streetNumber().value()).isEqualTo(VALID_NUMBER);
    assertThat(address.complement().value()).isEqualTo(VALID_COMPLEMENT);
    assertThat(address.postalCode().value()).isEqualTo(VALID_POSTAL_CODE);
    assertThat(address.city().value()).isEqualTo(VALID_CITY);
  }

  @Test
  void shouldThrowException_whenStreetIsNull() {
    assertThatThrownBy(() ->
      new Address(
        null,
        StreetNumber.of(VALID_NUMBER),
        null,
        PostalCode.of(VALID_POSTAL_CODE),
        City.of(VALID_CITY)
      )
    )
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Address.STREET_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenStreetNumberIsNull() {
    assertThatThrownBy(() ->
      new Address(
        Street.of(VALID_STREET),
        null,
        null,
        PostalCode.of(VALID_POSTAL_CODE),
        City.of(VALID_CITY)
      )
    )
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Address.NUMBER_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenPostalCodeIsNull() {
    assertThatThrownBy(() ->
      new Address(
        Street.of(VALID_STREET),
        StreetNumber.of(VALID_NUMBER),
        null,
        null,
        City.of(VALID_CITY)
      )
    )
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Address.POSTAL_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCityIsNull() {
    assertThatThrownBy(() ->
      new Address(
        Street.of(VALID_STREET),
        StreetNumber.of(VALID_NUMBER),
        null,
        PostalCode.of(VALID_POSTAL_CODE),
        null
      )
    )
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Address.CITY_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenStreetIsNull_usingFactoryMethod() {
    assertThatThrownBy(() -> Address.of(null, VALID_NUMBER, null, VALID_POSTAL_CODE, VALID_CITY))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Street.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenStreetNumberIsNull_usingFactoryMethod() {
    assertThatThrownBy(() -> Address.of(VALID_STREET, null, null, VALID_POSTAL_CODE, VALID_CITY))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(StreetNumber.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenPostalCodeIsNull_usingFactoryMethod() {
    assertThatThrownBy(() -> Address.of(VALID_STREET, VALID_NUMBER, null, null, VALID_CITY))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(PostalCode.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenCityIsNull_usingFactoryMethod() {
    assertThatThrownBy(() -> Address.of(VALID_STREET, VALID_NUMBER, null, VALID_POSTAL_CODE, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(City.MUST_NOT_BE_EMPTY);
  }
}