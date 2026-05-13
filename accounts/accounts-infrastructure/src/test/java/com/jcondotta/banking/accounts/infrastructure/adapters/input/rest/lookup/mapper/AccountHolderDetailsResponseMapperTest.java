package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.*;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.testsupport.AddressFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.AccountHolderDetailsResponse;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHolderDetailsResponseMapperTest {

  private static final UUID ACCOUNT_HOLDER_ID = UUID.randomUUID();
  private static final Instant CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  private final AccountHolderDetailsResponseMapper mapper =
    Mappers.getMapper(AccountHolderDetailsResponseMapper.class);

  @ParameterizedTest
  @EnumSource(HolderType.class)
  void shouldMapAccountHolderSummaryToResponse_whenValuesAreValid(HolderType type) {
    var identityDocument = new IdentityDocumentSummary(
      IdentityDocumentFixtures.SPANISH_NIE.country().name(),
      IdentityDocumentFixtures.SPANISH_NIE.documentType().name(),
      IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value()
    );
    var personalInfo = new PersonalInfoSummary(
      PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName(),
      PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName(),
      identityDocument,
      PersonalInfoFixtures.JEFFERSON.dateOfBirth().value()
    );
    var contactInfo = new ContactInfoSummary(
      ContactInfoFixtures.JEFFERSON.email().value(),
      ContactInfoFixtures.JEFFERSON.phoneNumber().value()
    );
    var address = new AddressSummary(
      AddressFixtures.BARCELONA_APT.address().street().value(),
      AddressFixtures.BARCELONA_APT.address().streetNumber().value(),
      AddressFixtures.BARCELONA_APT.address().addressComplement().value(),
      AddressFixtures.BARCELONA_APT.address().postalCode().value(),
      AddressFixtures.BARCELONA_APT.address().city().value()
    );

    var summary = new AccountHolderSummary(ACCOUNT_HOLDER_ID, personalInfo, contactInfo, address, type, CREATED_AT);

    AccountHolderDetailsResponse response = mapper.toResponse(summary);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(ACCOUNT_HOLDER_ID);
    assertThat(response.type().name()).isEqualTo(type.name());
    assertThat(response.personalInfo().firstName()).isEqualTo(personalInfo.firstName());
    assertThat(response.personalInfo().lastName()).isEqualTo(personalInfo.lastName());
    assertThat(response.personalInfo().identityDocument().number()).isEqualTo(identityDocument.number());
    assertThat(response.personalInfo().dateOfBirth()).isEqualTo(personalInfo.dateOfBirth());
    assertThat(response.contactInfo().email()).isEqualTo(contactInfo.email());
    assertThat(response.contactInfo().phoneNumber()).isEqualTo(contactInfo.phoneNumber());
    assertThat(response.address().street()).isEqualTo(address.street());
    assertThat(response.address().postalCode()).isEqualTo(address.postalCode());
    assertThat(response.address().city()).isEqualTo(address.city());
    assertThat(response.createdAt()).isEqualTo(CREATED_AT);
  }

  @Test
  void shouldReturnNull_whenAccountHolderSummaryIsNull() {
    AccountHolderDetailsResponse response = mapper.toResponse(null);

    assertThat(response).isNull();
  }
}
