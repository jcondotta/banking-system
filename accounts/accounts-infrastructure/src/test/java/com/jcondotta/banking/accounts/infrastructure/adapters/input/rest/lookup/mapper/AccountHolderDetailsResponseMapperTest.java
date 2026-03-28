package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.mapper;

class AccountHolderDetailsResponseMapperTest {

//  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();
//
//  private static final AccountHolderName VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName();
//  private static final PassportNumber VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber();
//  private static final DateOfBirth VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth();
//  private static final Email VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail();
//
//  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);
//
//  private final AccountHolderDetailsResponseMapper mapper =
//    Mappers.getMapper(AccountHolderDetailsResponseMapper.class);
//
//  @ParameterizedTest
//  @EnumSource(AccountHolderType.class)
//  void shouldMapAccountHolderDetailsToResponse_whenValuesAreValid(AccountHolderType type) {
//    AccountHolderDetails details = new AccountHolderDetails(
//      ACCOUNT_HOLDER_ID,
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL,
//      type,
//      CREATED_AT
//    );
//
//    AccountHolderDetailsResponse response = mapper.toResponse(details);
//
//    assertThat(response).isNotNull();
//    assertThat(response.id()).isEqualTo(ACCOUNT_HOLDER_ID.value());
//    assertThat(response.holderName()).isEqualTo(VALID_NAME.value());
//    assertThat(response.passportNumber()).isEqualTo(VALID_PASSPORT.value());
//    assertThat(response.dateOfBirth()).isEqualTo(VALID_DATE_OF_BIRTH.value());
//    assertThat(response.email()).isEqualTo(VALID_EMAIL.value());
//    assertThat(response.type()).isEqualTo(type);
//    assertThat(response.createdAt()).isEqualTo(CREATED_AT);
//  }
//
//  @Test
//  void shouldReturnNull_whenAccountHolderDetailsIsNull() {
//    AccountHolderDetailsResponse response = mapper.toResponse(null);
//
//    assertThat(response).isNull();
//  }
}