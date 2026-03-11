package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.mapper;

class OpenBankAccountRequestControllerMapperTest {

//  private static final String VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName().value();
//  private static final String VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber().value();
//  private static final LocalDate VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth().value();
//  private static final String VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail().value();
//
//  private final OpenBankAccountRequestControllerMapper mapper = Mappers.getMapper(OpenBankAccountRequestControllerMapper.class);
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldMapOpenBankAccountRequestToCommand_whenValueAreValid(AccountType accountType, Currency currency) {
//    var accountHolderRequest = new PrimaryAccountHolderRequest(
//        VALID_NAME,
//        VALID_PASSPORT,
//        VALID_DATE_OF_BIRTH,
//        VALID_EMAIL
//      );
//
//    OpenBankAccountRequest request = new OpenBankAccountRequest(
//        accountType,
//        currency,
//        accountHolderRequest
//      );
//
//    OpenBankAccountCommand command = mapper.toCommand(request);
//
//    assertThat(command).isNotNull();
//    assertThat(command.holderName().value()).isEqualTo(VALID_NAME);
//    assertThat(command.passportNumber().value()).isEqualTo(VALID_PASSPORT);
//    assertThat(command.dateOfBirth().value()).isEqualTo(VALID_DATE_OF_BIRTH);
//    assertThat(command.email().value()).isEqualTo(VALID_EMAIL);
//
//    assertThat(command.accountType()).isEqualTo(accountType);
//    assertThat(command.currency()).isEqualTo(currency);
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
//  void shouldConvertLocalDateToDateOfBirth_whenValueIsValid() {
//    DateOfBirth dateOfBirth = mapper.toDateOfBirth(VALID_DATE_OF_BIRTH);
//
//    assertThat(dateOfBirth.value()).isEqualTo(VALID_DATE_OF_BIRTH);
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
//  void shouldReturn_whenMappedRequestIsNull() {
//    OpenBankAccountCommand command = mapper.toCommand(null);
//    assertThat(command).isNull();
//  }
}
