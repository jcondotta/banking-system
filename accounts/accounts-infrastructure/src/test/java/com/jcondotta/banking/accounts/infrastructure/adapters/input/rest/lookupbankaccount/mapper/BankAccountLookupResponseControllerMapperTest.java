package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.mapper;

class BankAccountLookupResponseControllerMapperTest {

//  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
//  private static final AccountType ACCOUNT_TYPE_CHECKING = AccountType.CHECKING;
//  private static final Currency CURRENCY_EUR = Currency.EUR;
//  private static final AccountStatus ACCOUNT_STATUS_ACTIVE = AccountStatus.ACTIVE;
//
//  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();
//
//  private static final AccountHolderName VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName();
//  private static final PassportNumber VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber();
//  private static final DateOfBirth VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth();
//  private static final Email VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail();
//
//  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);
//
//  public static final Iban VALID_IBAN = Iban.of("ES3801283316232166447417");
//
//  private final BankAccountLookupResponseControllerMapper mapper =
//    new BankAccountLookupResponseControllerMapperImpl(new AccountHolderDetailsResponseMapperImpl());
//
//  @ParameterizedTest
//  @EnumSource(AccountHolderType.class)
//  void shouldMapBankAccountDetailsToLookupResponse_whenValuesAreValid(AccountHolderType type) {
//    AccountHolderDetails accountHolderDetails = new AccountHolderDetails(
//      ACCOUNT_HOLDER_ID,
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL,
//      type,
//      CREATED_AT
//    );
//
//    BankAccountDetails bankAccountDetails = new BankAccountDetails(
//      BANK_ACCOUNT_ID,
//      ACCOUNT_TYPE_CHECKING,
//      CURRENCY_EUR,
//      VALID_IBAN,
//      ACCOUNT_STATUS_ACTIVE,
//      CREATED_AT,
//      List.of(accountHolderDetails)
//    );
//
//    BankAccountLookupResponse response = mapper.toResponse(bankAccountDetails);
//
//    assertThat(response).isNotNull();
//
//    BankAccountDetailsResponse details = response.bankAccount();
//    assertThat(details.id()).isEqualTo(BANK_ACCOUNT_ID.value());
//    assertThat(details.accountType()).isEqualTo(ACCOUNT_TYPE_CHECKING);
//    assertThat(details.currency()).isEqualTo(CURRENCY_EUR);
//    assertThat(details.iban()).isEqualTo(VALID_IBAN.value());
//    assertThat(details.createdAt()).isEqualTo(CREATED_AT);
//    assertThat(details.accountStatus()).isEqualTo(ACCOUNT_STATUS_ACTIVE);
//
//    assertThat(details.holders()).hasSize(1);
//    AccountHolderDetailsResponse holder = details.holders().getFirst();
//    assertThat(holder.id()).isEqualTo(ACCOUNT_HOLDER_ID.value());
//    assertThat(holder.holderName()).isEqualTo(VALID_NAME.value());
//    assertThat(holder.passportNumber()).isEqualTo(VALID_PASSPORT.value());
//    assertThat(holder.dateOfBirth()).isEqualTo(VALID_DATE_OF_BIRTH.value());
//    assertThat(holder.email()).isEqualTo(VALID_EMAIL.value());
//    assertThat(holder.type()).isEqualTo(type);
//    assertThat(holder.createdAt()).isEqualTo(CREATED_AT);
//  }
}