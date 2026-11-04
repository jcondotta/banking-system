package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

final class BankAccountLifecycleIntegrationOperations {

  private final BankAccountsURIProperties uriProperties;
  private final Function<String, RequestSpecification> requestSpecification;

  BankAccountLifecycleIntegrationOperations(
    BankAccountsURIProperties uriProperties,
    Function<String, RequestSpecification> requestSpecification
  ) {
    this.uriProperties = uriProperties;
    this.requestSpecification = requestSpecification;
  }

  void blockBankAccount(BankAccountId id) {
    patchBankAccount(id, "/block", HttpStatus.NO_CONTENT);
  }

  void unblockBankAccount(BankAccountId id) {
    patchBankAccount(id, "/unblock", HttpStatus.NO_CONTENT);
  }

  void closeBankAccount(BankAccountId id) {
    patchBankAccount(id, "/close", HttpStatus.NO_CONTENT);
  }

  Response patchBankAccount(BankAccountId id, String actionPath) {
    return given()
      .spec(requestSpecification.apply(uriProperties.bankAccountIdPath() + actionPath))
      .pathParam("bank-account-id", id.value())
      .when()
      .patch()
      .then()
      .extract()
      .response();
  }

  private void patchBankAccount(BankAccountId id, String actionPath, HttpStatus expectedStatus) {
    var response = patchBankAccount(id, actionPath);
    assertThat(response.statusCode()).isEqualTo(expectedStatus.value());
  }
}
