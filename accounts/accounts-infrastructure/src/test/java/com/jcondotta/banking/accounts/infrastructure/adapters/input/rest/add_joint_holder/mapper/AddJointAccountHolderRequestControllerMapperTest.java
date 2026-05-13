package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AddJointAccountHolderRequestControllerMapperTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

  private static final String VALID_FIRST_NAME = PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName();
  private static final String VALID_LAST_NAME = PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName();
  private static final String VALID_DOCUMENT_NUMBER = IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value();
  private static final String VALID_EMAIL = ContactInfoFixtures.JEFFERSON.email().value();
  private static final String VALID_PHONE = ContactInfoFixtures.JEFFERSON.phoneNumber().value();

  private final AddJointHolderRequestControllerMapper mapper =
    Mappers.getMapper(AddJointHolderRequestControllerMapper.class);

  @Test
  void shouldMapAddJointHolderRequestToCommand_whenValuesAreValid() {
    var identityDoc = new IdentityDocumentRequest(
      DocumentTypeRequest.FOREIGNER_ID,
      DocumentCountryRequest.SPAIN,
      VALID_DOCUMENT_NUMBER
    );
    var personalInfo = new PersonalInfoRequest(
      VALID_FIRST_NAME,
      VALID_LAST_NAME,
      identityDoc,
      PersonalInfoFixtures.JEFFERSON.dateOfBirth().value()
    );
    var contactInfo = new ContactInfoRequest(VALID_EMAIL, VALID_PHONE);
    var address = new AddressRequest("Carrer de Mallorca", "401", null, "08013", "Barcelona");
    var request = new AddJointHolderRequest(personalInfo, contactInfo, address);

    AddJointHolderCommand command = mapper.toCommand(BANK_ACCOUNT_UUID, request);

    assertThat(command).isNotNull();
    assertThat(command.bankAccountId().value()).isEqualTo(BANK_ACCOUNT_UUID);
    assertThat(command.personalInfo().holderName().firstName()).isEqualTo(VALID_FIRST_NAME);
    assertThat(command.personalInfo().holderName().lastName()).isEqualTo(VALID_LAST_NAME);
    assertThat(command.personalInfo().identityDocument().number().value()).isEqualTo(VALID_DOCUMENT_NUMBER);
    assertThat(command.personalInfo().dateOfBirth().value()).isEqualTo(PersonalInfoFixtures.JEFFERSON.dateOfBirth().value());
    assertThat(command.contactInfo().email().value()).isEqualTo(VALID_EMAIL);
    assertThat(command.contactInfo().phoneNumber().value()).isEqualTo(VALID_PHONE);
    assertThat(command.address().street().value()).isEqualTo("Carrer de Mallorca");
    assertThat(command.address().postalCode().value()).isEqualTo("08013");
    assertThat(command.address().city().value()).isEqualTo("Barcelona");
  }

  @Test
  void shouldConvertUuidToBankAccountId_whenValueIsValid() {
    BankAccountId id = mapper.toBankAccountId(BANK_ACCOUNT_UUID);

    assertThat(id.value()).isEqualTo(BANK_ACCOUNT_UUID);
  }
}
