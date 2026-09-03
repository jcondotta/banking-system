package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import com.jcondotta.banking.accounts.domain.testsupport.AddressFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.accounts.infrastructure.config.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

class AddressRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final String VALID_STREET = "Carrer de Mallorca";
  private static final String VALID_STREET_NUMBER = "401";
  private static final String VALID_POSTAL_CODE = "08013";
  private static final String VALID_CITY = "Barcelona";

  @Test
  void shouldNotDetectConstraintViolation_whenRequestIsValid() {
    var request = new AddressRequest(VALID_STREET, VALID_STREET_NUMBER, null, VALID_POSTAL_CODE, VALID_CITY);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldNotDetectConstraintViolation_whenComplementIsProvided() {
    var request = new AddressRequest(VALID_STREET, VALID_STREET_NUMBER, "3º - 1ª", VALID_POSTAL_CODE, VALID_CITY);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenStreetIsBlank(String blankStreet) {
    var request = new AddressRequest(blankStreet, VALID_STREET_NUMBER, null, VALID_POSTAL_CODE, VALID_CITY);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("street"));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenStreetNumberIsBlank(String blankStreetNumber) {
    var request = new AddressRequest(VALID_STREET, blankStreetNumber, null, VALID_POSTAL_CODE, VALID_CITY);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("streetNumber"));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenPostalCodeIsBlank(String blankPostalCode) {
    var request = new AddressRequest(VALID_STREET, VALID_STREET_NUMBER, null, blankPostalCode, VALID_CITY);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("postalCode"));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenCityIsBlank(String blankCity) {
    var request = new AddressRequest(VALID_STREET, VALID_STREET_NUMBER, null, VALID_POSTAL_CODE, blankCity);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("city"));
  }
}
