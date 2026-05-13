package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AddressSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.AddressComplement;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.City;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.PostalCode;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Street;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.StreetNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressSummaryMapperTest {

  private static final String STREET = "Carrer de Mallorca";
  private static final String NUMBER = "456";
  private static final String COMPLEMENT = "2º Andar";
  private static final String POSTAL_CODE = "08013";
  private static final String CITY = "Barcelona";

  private final AddressSummaryMapper mapper = new AddressSummaryMapperImpl();

  @Test
  void shouldMapAddressDetails_whenComplementIsPresent() {
    Address address = Address.of(STREET, NUMBER, COMPLEMENT, POSTAL_CODE, CITY);

    AddressSummary details = mapper.toSummary(address);

    assertThat(details.street()).isEqualTo(STREET);
    assertThat(details.streetNumber()).isEqualTo(NUMBER);
    assertThat(details.addressComplement()).isEqualTo(COMPLEMENT);
    assertThat(details.postalCode()).isEqualTo(POSTAL_CODE);
    assertThat(details.city()).isEqualTo(CITY);
  }

  @Test
  void shouldMapAddressDetails_whenComplementIsNull() {
    var address = Address.of(STREET, NUMBER, null, POSTAL_CODE, CITY);

    AddressSummary details = mapper.toSummary(address);

    assertThat(details.street()).isEqualTo(STREET);
    assertThat(details.streetNumber()).isEqualTo(NUMBER);
    assertThat(details.addressComplement()).isNull();
    assertThat(details.postalCode()).isEqualTo(POSTAL_CODE);
    assertThat(details.city()).isEqualTo(CITY);
  }

  @Test
  void shouldReturnNull_whenAddressIsNull() {
    var details = mapper.toSummary(null);

    assertThat(details).isNull();
  }

  @Test
  void shouldReturnNull_whenAddressValueObjectsAreNull() {
    assertThat(mapper.map((Street) null)).isNull();
    assertThat(mapper.map((StreetNumber) null)).isNull();
    assertThat(mapper.map((AddressComplement) null)).isNull();
    assertThat(mapper.map((PostalCode) null)).isNull();
    assertThat(mapper.map((City) null)).isNull();
  }
}
