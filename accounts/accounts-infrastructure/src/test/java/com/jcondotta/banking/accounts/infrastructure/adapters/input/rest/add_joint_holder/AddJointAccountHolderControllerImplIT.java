package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder;

import com.jcondotta.banking.accounts.infrastructure.container.KafkaTestContainer;
import com.jcondotta.banking.accounts.infrastructure.container.LocalStackTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@ContextConfiguration(initializers = {LocalStackTestContainer.class, KafkaTestContainer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddJointAccountHolderControllerImplIT {

//  @Autowired
//  ObjectMapper objectMapper;
//
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
//  BankAccountLookupUseCase bankAccountLookupUseCase;
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
//      .setBasePath(uriProperties.bankAccountIdPath() + "/account-holders")
//      .setContentType(ContentType.JSON)
//      .setAccept(ContentType.JSON)
//      .build();
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldReturn200OkAndAddJointAccountHolder_whenBankAccountIsActive(AccountType accountType, Currency currency) throws JsonProcessingException {
//    AccountHolderFixtures primaryHolderFixture = AccountHolderFixtures.JEFFERSON;
//    BankAccountId id = createBankAccount(accountType, currency, primaryHolderFixture);
//    activateBankAccountUseCase.execute(new ActivateBankAccountCommand(id));
//
//    AccountHolderFixtures jointHolderFixture = AccountHolderFixtures.PATRIZIO;
//    var addJointAccountHolderRequest =
//      new AddJointAccountHolderRequest(
//        jointHolderFixture.getAccountHolderName().value(),
//        jointHolderFixture.getPassportNumber().value(),
//        jointHolderFixture.getDateOfBirth().value(),
//        jointHolderFixture.getEmail().value()
//      );
//
//    given()
//      .spec(requestSpecification)
//      .pathParam("bank-account-id", id.value())
//      .body(addJointAccountHolderRequest)
//    .when()
//      .post()
//    .then()
//      .statusCode(HttpStatus.OK.value());
//
//    BankAccountDetails bankAccountDetails = bankAccountLookupUseCase.lookup(id);
//
//    assertThat(bankAccountDetails.holders())
//      .hasSize(2)
//      .anySatisfy(holder -> {
//        assertThat(holder.holderName()).isEqualTo(jointHolderFixture.getAccountHolderName());
//        assertThat(holder.passportNumber()).isEqualTo(jointHolderFixture.getPassportNumber());
//        assertThat(holder.dateOfBirth()).isEqualTo(jointHolderFixture.getDateOfBirth());
//        assertThat(holder.email()).isEqualTo(jointHolderFixture.getEmail());
//        assertThat(holder.type().isJoint()).isTrue();
//        assertThat(holder.createdAt()).isNotNull();
//      });
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//  void shouldReturn422UnprocessableEntity_whenBankAccountIsNotActive(AccountType accountType, Currency currency) throws JsonProcessingException {
//    AccountHolderFixtures primaryHolderFixture = AccountHolderFixtures.JEFFERSON;
//    BankAccountId id = createBankAccount(accountType, currency, primaryHolderFixture);
//
//    AccountHolderFixtures jointHolderFixture = AccountHolderFixtures.PATRIZIO;
//
//    var request =
//      new AddJointAccountHolderRequest(
//        jointHolderFixture.getAccountHolderName().value(),
//        jointHolderFixture.getPassportNumber().value(),
//        jointHolderFixture.getDateOfBirth().value(),
//        jointHolderFixture.getEmail().value()
//      );
//
//    given()
//      .spec(requestSpecification)
//      .pathParam("bank-account-id", id.value())
//      .body(request)
//    .when()
//      .post()
//    .then()
//      .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
//
//    BankAccountDetails bankAccountDetails = bankAccountLookupUseCase.lookup(id);
//    assertThat(bankAccountDetails.holders()).hasSize(1);
//  }
//
//  @Test
//  void shouldReturn404NotFound_whenBankAccountIsNotFound() throws JsonProcessingException {
//    var nonExistentBankAccountId = UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe");
//
//    AccountHolderFixtures jointHolderFixture = AccountHolderFixtures.PATRIZIO;
//    var addJointAccountHolderRequest =
//      new AddJointAccountHolderRequest(
//        jointHolderFixture.getAccountHolderName().value(),
//        jointHolderFixture.getPassportNumber().value(),
//        jointHolderFixture.getDateOfBirth().value(),
//        jointHolderFixture.getEmail().value()
//      );
//
//    var problemDetail =
//      given()
//        .spec(requestSpecification)
//        .pathParam("bank-account-id", nonExistentBankAccountId)
//        .body(addJointAccountHolderRequest)
//      .when()
//        .post()
//      .then()
//        .statusCode(HttpStatus.NOT_FOUND.value())
//        .extract()
//        .as(ProblemDetail.class);
//
//    assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
//    assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountHoldersURI(nonExistentBankAccountId));
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
}