package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import com.jcondotta.banking.accounts.infrastructure.container.KafkaTestContainer;
import com.jcondotta.banking.accounts.infrastructure.container.LocalStackTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@ContextConfiguration(initializers = {LocalStackTestContainer.class, KafkaTestContainer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankAccountLookupControllerImplIT {

//  @Autowired
//  BankAccountsURIProperties uriProperties;
//
//  @Autowired
//  OpenBankAccountUseCase openBankAccountUseCase;
//
//  @Autowired
//  ActivateBankAccountUseCase activateBankAccountUseCase;
//
//  @Autowired
//  AddJointAccountHolderUseCase addJointAccountHolderUseCase;
//
//  RequestSpecification requestSpecification;
//
//  @BeforeAll
//  static void beforeAll() {
//    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
//  }
//
//  @BeforeEach
//  void beforeEach(@LocalServerPort int port) {
//    RestAssured.baseURI = "http://localhost";
//    RestAssured.port = port;
//
//    requestSpecification = new RequestSpecBuilder()
//      .setBaseUri(RestAssured.baseURI)
//      .setPort(RestAssured.port)
//      .setBasePath(uriProperties.bankAccountIdPath())
//      .setContentType(ContentType.JSON)
//      .setAccept(ContentType.JSON)
//      .build();
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldReturn200OkWithBankAccountDetails_whenBankAccountIsFound(AccountType accountType, Currency currency) {
//    AccountHolderFixtures primaryHolderFixture = AccountHolderFixtures.JEFFERSON;
//    BankAccountId id = createBankAccount(accountType, currency, primaryHolderFixture);
//
//    var bankAccountLookupResponse = given()
//      .spec(requestSpecification)
//      .pathParam("bank-account-id", id.value())
//    .when()
//      .get()
//    .then()
//      .statusCode(HttpStatus.OK.value())
//      .extract()
//      .response()
//      .as(BankAccountLookupResponse.class);
//
//    assertThat(bankAccountLookupResponse.bankAccount())
//      .satisfies(bankAccount -> Assertions.assertAll(
//        () -> assertThat(bankAccount.id()).isEqualTo(id.value()),
//        () -> assertThat(bankAccount.accountType()).isEqualTo(accountType),
//        () -> assertThat(bankAccount.currency()).isEqualTo(currency),
//        () -> assertThat(bankAccount.iban()).isNotBlank(),
//        () -> assertThat(bankAccount.createdAt()).isNotNull(),
//        () -> assertThat(bankAccount.holders())
//          .hasSize(1)
//          .singleElement()
//          .satisfies(accountHolderDetails -> {
//            assertThat(accountHolderDetails.holderName()).isEqualTo(primaryHolderFixture.getAccountHolderName().value());
//            assertThat(accountHolderDetails.passportNumber()).isEqualTo(primaryHolderFixture.getPassportNumber().value());
//            assertThat(accountHolderDetails.dateOfBirth()).isEqualTo(primaryHolderFixture.getDateOfBirth().value());
//            assertThat(accountHolderDetails.email()).isEqualTo(primaryHolderFixture.getEmail().value());
//            assertThat(accountHolderDetails.type().isPrimary()).isTrue();
//            assertThat(accountHolderDetails.createdAt()).isNotNull();
//          })
//      ));
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldReturn200OkWithPrimaryAndJointAccountHolders_whenBankAccountHasMultipleHolders(AccountType accountType, Currency currency) {
//    AccountHolderFixtures primaryHolderFixture = AccountHolderFixtures.JEFFERSON;
//    BankAccountId id = createBankAccount(accountType, currency, primaryHolderFixture);
//    activateBankAccountUseCase.execute(new ActivateBankAccountCommand(id));
//
//    AccountHolderFixtures jointHolderFixture = AccountHolderFixtures.PATRIZIO;
//    addJointAccountHolder(id, jointHolderFixture);
//
//    var bankAccountLookupResponse =
//      given()
//        .spec(requestSpecification)
//        .pathParam("bank-account-id", id.value())
//      .when()
//        .get()
//      .then()
//        .statusCode(HttpStatus.OK.value())
//        .extract()
//        .as(BankAccountLookupResponse.class);
//
//    assertThat(bankAccountLookupResponse.bankAccount())
//      .satisfies(bankAccount -> Assertions.assertAll(
//        () -> assertThat(bankAccount.id()).isEqualTo(id.value()),
//        () -> assertThat(bankAccount.accountType()).isEqualTo(accountType),
//        () -> assertThat(bankAccount.currency()).isEqualTo(currency),
//        () -> assertThat(bankAccount.iban()).isNotBlank(),
//        () -> assertThat(bankAccount.createdAt()).isNotNull(),
//        () -> assertThat(bankAccount.holders())
//          .hasSize(2)
//          .anySatisfy(accountHolderDetails -> {
//            assertThat(accountHolderDetails.holderName()).isEqualTo(primaryHolderFixture.getAccountHolderName().value());
//            assertThat(accountHolderDetails.passportNumber()).isEqualTo(primaryHolderFixture.getPassportNumber().value());
//            assertThat(accountHolderDetails.dateOfBirth()).isEqualTo(primaryHolderFixture.getDateOfBirth().value());
//            assertThat(accountHolderDetails.email()).isEqualTo(primaryHolderFixture.getEmail().value());
//            assertThat(accountHolderDetails.type().isPrimary()).isTrue();
//            assertThat(accountHolderDetails.createdAt()).isNotNull();
//          })
//          .anySatisfy(accountHolderDetails -> {
//            assertThat(accountHolderDetails.holderName()).isEqualTo(jointHolderFixture.getAccountHolderName().value());
//            assertThat(accountHolderDetails.passportNumber()).isEqualTo(jointHolderFixture.getPassportNumber().value());
//            assertThat(accountHolderDetails.dateOfBirth()).isEqualTo(jointHolderFixture.getDateOfBirth().value());
//            assertThat(accountHolderDetails.email()).isEqualTo(jointHolderFixture.getEmail().value());
//            assertThat(accountHolderDetails.type().isJoint()).isTrue();
//            assertThat(accountHolderDetails.createdAt()).isNotNull();
//          })
//      ));
//  }
//
//  @Test
//  void shouldReturn404NotFound_whenBankAccountIsNotFound() {
//    var nonExistentBankAccountId = UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe");
//
//    var problemDetail =
//      given()
//        .spec(requestSpecification)
//        .pathParam("bank-account-id", nonExistentBankAccountId)
//      .when()
//        .get()
//      .then()
//        .statusCode(HttpStatus.NOT_FOUND.value())
//        .extract()
//        .as(ProblemDetail.class);
//
//    assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
//    assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.bankAccountURI(nonExistentBankAccountId));
//  }
//
//  private BankAccountId createBankAccount(AccountType accountType, Currency currency, AccountHolderFixtures fixture) {
//    OpenBankAccountCommand command = new OpenBankAccountCommand(
//      fixture.getAccountHolderName(),
//      fixture.getPassportNumber(),
//      fixture.getDateOfBirth(),
//      fixture.getEmail(),
//      accountType,
//      currency
//    );
//
//    return openBankAccountUseCase.execute(command).id();
//  }
//
//  private void addJointAccountHolder(BankAccountId id, AccountHolderFixtures fixture) {
//    addJointAccountHolderUseCase.execute(
//      new AddJointAccountHolderCommand(
//        id,
//        fixture.getAccountHolderName(),
//        fixture.getPassportNumber(),
//        fixture.getDateOfBirth(),
//        fixture.getEmail()
//      )
//    );
//  }
}