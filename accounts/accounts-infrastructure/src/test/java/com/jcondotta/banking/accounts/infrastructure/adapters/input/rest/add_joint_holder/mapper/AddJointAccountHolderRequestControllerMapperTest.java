package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.mapper;

class AddJointAccountHolderRequestControllerMapperTest {

//  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();
//
//  private static final String VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName().value();
//  private static final String VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber().value();
//  private static final LocalDate VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth().value();
//  private static final String VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail().value();
//
//  private final AddJointAccountHolderRequestControllerMapper mapper =
//    Mappers.getMapper(AddJointAccountHolderRequestControllerMapper.class);
//
//  @Test
//  void shouldMapAddJointAccountHolderRequestToCommand_whenValuesAreValid() {
//    AddJointAccountHolderRequest request =
//      new AddJointAccountHolderRequest(
//        VALID_NAME,
//        VALID_PASSPORT,
//        VALID_DATE_OF_BIRTH,
//        VALID_EMAIL
//      );
//
//    AddJointAccountHolderCommand command =
//      mapper.toCommand(BANK_ACCOUNT_UUID, request);
//
//    assertThat(command).isNotNull();
//    assertThat(command.id().value()).isEqualTo(BANK_ACCOUNT_UUID);
//    assertThat(command.holderName().value()).isEqualTo(VALID_NAME);
//    assertThat(command.passportNumber().value()).isEqualTo(VALID_PASSPORT);
//    assertThat(command.dateOfBirth().value()).isEqualTo(VALID_DATE_OF_BIRTH);
//    assertThat(command.email().value()).isEqualTo(VALID_EMAIL);
//  }
//
//  @Test
//  void shouldConvertUuidToBankAccountId_whenValueIsValid() {
//    BankAccountId id = mapper.toBankAccountId(BANK_ACCOUNT_UUID);
//
//    assertThat(id.value()).isEqualTo(BANK_ACCOUNT_UUID);
//  }
//
//  @Test
//  void shouldConvertStringToAccountHolderName_whenValueIsValid() {
//    AccountHolderName accountHolderName = mapper.toAccountHolderName(VALID_NAME);
//
//    assertThat(accountHolderName.value()).isEqualTo(VALID_NAME);
//  }
//
//  @Test
//  void shouldConvertStringToPassportNumber_whenValueIsValid() {
//    PassportNumber passportNumber = mapper.toPassportNumber(VALID_PASSPORT);
//
//    assertThat(passportNumber.value()).isEqualTo(VALID_PASSPORT);
//  }
//
//  @Test
//  void shouldConvertStringToEmail_whenValueIsValid() {
//    Email email = mapper.toEmail(VALID_EMAIL);
//
//    assertThat(email.value()).isEqualTo(VALID_EMAIL);
//  }
//
//  @Test
//  void shouldConvertLocalDateToDateOfBirth_whenValueIsValid() {
//    DateOfBirth dateOfBirth = mapper.toDateOfBirth(VALID_DATE_OF_BIRTH);
//
//    assertThat(dateOfBirth.value()).isEqualTo(VALID_DATE_OF_BIRTH);
//  }
}