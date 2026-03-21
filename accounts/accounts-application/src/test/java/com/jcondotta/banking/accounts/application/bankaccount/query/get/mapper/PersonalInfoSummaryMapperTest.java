package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalInfoSummaryMapperTest {

  private static final String FIRST_NAME = "Jefferson";
  private static final String LAST_NAME = "Condotta";
  private static final String DOCUMENT_NUMBER = "12345678Z";

  private final IdentityDocumentSummaryMapper identityMapper = new IdentityDocumentSummaryMapperImpl();

  private final PersonalInfoSummaryMapper mapper = new PersonalInfoSummaryMapperImpl(identityMapper);

  @Test
  void shouldMapPersonalInfoDetails_whenPersonalInfoIsValid() {
    var identityDocument = IdentityDocument.of(
      DocumentCountry.SPAIN,
      DocumentType.NATIONAL_ID,
      DocumentNumber.of(DOCUMENT_NUMBER));

    var personalInfo = new PersonalInfo(
        AccountHolderName.of(FIRST_NAME, LAST_NAME),
        identityDocument,
        DateOfBirth.of(LocalDate.of(1988, 6, 24))
      );

    var details = mapper.toDetails(personalInfo);

    assertThat(details.firstName()).isEqualTo(FIRST_NAME);
    assertThat(details.lastName()).isEqualTo(LAST_NAME);
    assertThat(details.identityDocument().number()).isEqualTo(DOCUMENT_NUMBER);
    assertThat(details.dateOfBirth()).isEqualTo(LocalDate.of(1988, 6, 24));
  }

  @Test
  void shouldReturnNull_whenPersonalInfoIsNull() {
    var details = mapper.toDetails(null);

    assertThat(details).isNull();
  }
}