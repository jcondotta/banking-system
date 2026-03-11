package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.unblock;

import com.jcondotta.banking.accounts.infrastructure.container.KafkaTestContainer;
import com.jcondotta.banking.accounts.infrastructure.container.LocalStackTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@ContextConfiguration(initializers = { LocalStackTestContainer.class, KafkaTestContainer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UnblockBankAccountControllerImplIT {

//  @Autowired
//  OpenBankAccountUseCase openBankAccountUseCase;
//
//  @Autowired
//  ActivateBankAccountUseCase activateBankAccountUseCase;
//
//  @Autowired
//  BlockBankAccountUseCase blockBankAccountUseCase;
//
//  @Autowired
//  BankAccountLookupUseCase bankAccountLookupUseCase;
//
//  @Autowired
//  BankAccountsURIProperties uriProperties;
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
//      .setBasePath(uriProperties.bankAccountIdPath() + "/unblock")
//      .setContentType(ContentType.JSON)
//      .setAccept(ContentType.JSON)
//      .build();
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldReturn204NoContentAndUnblockBankAccount_whenBankAccountIsBlocked(
//    AccountType accountType,
//    Currency currency
//  ) {
//
//    AccountHolderFixtures primary = AccountHolderFixtures.JEFFERSON;
//
//    BankAccountId id =
//      createBankAccount(accountType, currency, primary);
//
//    activateBankAccountUseCase.execute(
//      new ActivateBankAccountCommand(id)
//    );
//
//    blockBankAccountUseCase.execute(
//      new BlockBankAccountCommand(id)
//    );
//
//    given()
//      .spec(requestSpecification)
//      .pathParam("bank-account-id", id.value())
//      .when()
//      .patch()
//      .then()
//      .statusCode(HttpStatus.NO_CONTENT.value());
//
//    BankAccountDetails details =
//      bankAccountLookupUseCase.lookup(id);
//
//    assertThat(details.accountStatus().isActive()).isTrue();
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldReturn422UnprocessableEntity_whenBankAccountIsNotBlocked(
//    AccountType accountType,
//    Currency currency
//  ) {
//
//    AccountHolderFixtures primary = AccountHolderFixtures.JEFFERSON;
//
//    BankAccountId id =
//      createBankAccount(accountType, currency, primary);
//
//    given()
//      .spec(requestSpecification)
//      .pathParam("bank-account-id", id.value())
//    .when()
//      .patch()
//    .then()
//      .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
//  }
//
//  @Test
//  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
//
//    UUID nonExistentId =
//      UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe");
//
//    var problem =
//      given()
//        .spec(requestSpecification)
//        .pathParam("bank-account-id", nonExistentId)
//        .when()
//        .patch()
//        .then()
//        .statusCode(HttpStatus.NOT_FOUND.value())
//        .extract()
//        .as(ProblemDetail.class);
//
//    assertThat(problem.getTitle())
//      .isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
//
//    assertThat(problem.getInstance())
//      .isEqualTo(uriProperties.unblockBankAccountURI(nonExistentId));
//  }
//
//  private BankAccountId createBankAccount(
//    AccountType accountType,
//    Currency currency,
//    AccountHolderFixtures fixture
//  ) {
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
}