package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;
import java.util.UUID;

import static com.jcondotta.banking.accounts.integration.testsupport.rest.RestAssuredTestConstants.API_VERSION_1;
import static com.jcondotta.banking.accounts.integration.testsupport.rest.RestAssuredTestConstants.BASE_URI;

public abstract class BankAccountIntegrationSupport {

  @Autowired
  protected BankAccountsURIProperties uriProperties;

  @Autowired
  protected BankAccountRepository bankAccountRepository;

  @Autowired
  private DynamoDbTable<OutboxEntity> outboxTable;

  private OpenBankAccountIntegrationOperations openBankAccountOperations;
  private ActivateBankAccountIntegrationOperations activateBankAccountOperations;
  private AddJointHolderIntegrationOperations addJointHolderOperations;
  private GetBankAccountIntegrationOperations getBankAccountOperations;
  private BankAccountLifecycleIntegrationOperations bankAccountLifecycleOperations;
  private OutboxIntegrationOperations outboxOperations;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = BASE_URI;
    RestAssured.port = port;
    openBankAccountOperations = new OpenBankAccountIntegrationOperations(uriProperties, this::requestSpecification);
    activateBankAccountOperations = new ActivateBankAccountIntegrationOperations(uriProperties, this::requestSpecification);
    addJointHolderOperations = new AddJointHolderIntegrationOperations(uriProperties, this::requestSpecification);
    getBankAccountOperations = new GetBankAccountIntegrationOperations(uriProperties, this::requestSpecification);
    bankAccountLifecycleOperations = new BankAccountLifecycleIntegrationOperations(uriProperties, this::requestSpecification);
    outboxOperations = new OutboxIntegrationOperations(outboxTable);
  }

  protected BankAccountId openBankAccount(AccountType accountType, Currency currency) {
    return openBankAccountOperations.openBankAccount(accountType, currency);
  }

  protected Response postOpenBankAccount(OpenBankAccountRequest request) {
    return openBankAccountOperations.postOpenBankAccount(request);
  }

  protected Response postOpenBankAccount(OpenBankAccountRequest request, UUID key) {
    return openBankAccountOperations.postOpenBankAccount(request, key);
  }

  protected void activateBankAccount(BankAccountId id) {
    activateBankAccountOperations.activateBankAccount(id);
  }

  protected void activateBankAccount(BankAccountId id, UUID correlationId) {
    activateBankAccountOperations.activateBankAccount(id, correlationId);
  }

  protected void blockBankAccount(BankAccountId id) {
    bankAccountLifecycleOperations.blockBankAccount(id);
  }

  protected void unblockBankAccount(BankAccountId id) {
    bankAccountLifecycleOperations.unblockBankAccount(id);
  }

  protected void closeBankAccount(BankAccountId id) {
    bankAccountLifecycleOperations.closeBankAccount(id);
  }

  protected Response patchBankAccount(BankAccountId id, String actionPath) {
    return bankAccountLifecycleOperations.patchBankAccount(id, actionPath);
  }

  protected void addJointHolder(BankAccountId id, AccountHolderFixtures fixture) {
    addJointHolderOperations.addJointHolder(id, fixture);
  }

  protected Response postJointHolder(BankAccountId id, AddJointHolderRequest request) {
    return addJointHolderOperations.postJointHolder(id, request);
  }

  protected Response postJointHolder(BankAccountId id, AddJointHolderRequest request, UUID key) {
    return addJointHolderOperations.postJointHolder(id, request, key);
  }

  protected BankAccountDetailsResponse getBankAccount(BankAccountId id) {
    return getBankAccountOperations.getBankAccount(id);
  }

  protected BankAccountDetailsResponse getBankAccountByIban(String iban) {
    return getBankAccountOperations.getBankAccountByIban(iban)
      .as(BankAccountDetailsResponse.class);
  }

  protected Response getBankAccountByIbanResponse(String iban) {
    return getBankAccountOperations.getBankAccountByIban(iban);
  }

  protected Response getBankAccount(UUID id) {
    return getBankAccountOperations.getBankAccount(id);
  }

  protected List<OutboxEntity> findOutboxEvents(BankAccountId id) {
    return outboxOperations.findOutboxEvents(id);
  }

  protected void clearOutboxEvents() {
    outboxOperations.clearOutboxEvents();
  }

  protected void assertOutboxEvents(BankAccountId id, String... eventTypes) {
    outboxOperations.assertOutboxEvents(id, eventTypes);
  }

  protected RequestSpecification requestSpecification(String basePath) {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(basePath)
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .addHeader(HttpHeadersConstants.API_VERSION, API_VERSION_1)
      .build();
  }
}
