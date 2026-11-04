package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

final class GetBankAccountIntegrationOperations {

  private final BankAccountsURIProperties uriProperties;
  private final Function<String, RequestSpecification> requestSpecification;

  GetBankAccountIntegrationOperations(
    BankAccountsURIProperties uriProperties,
    Function<String, RequestSpecification> requestSpecification
  ) {
    this.uriProperties = uriProperties;
    this.requestSpecification = requestSpecification;
  }

  BankAccountDetailsResponse getBankAccount(BankAccountId id) {
    return given()
      .spec(requestSpecification.apply(uriProperties.bankAccountIdPath()))
      .pathParam("bank-account-id", id.value())
      .when()
      .get()
      .then()
      .statusCode(HttpStatus.OK.value())
      .extract()
      .as(BankAccountDetailsResponse.class);
  }

  Response getBankAccountByIban(String iban) {
    return given()
      .spec(requestSpecification.apply(uriProperties.rootPath()))
      .queryParam("iban", iban)
      .when()
      .get()
      .then()
      .extract()
      .response();
  }

  Response getBankAccount(UUID id) {
    return given()
      .spec(requestSpecification.apply(uriProperties.bankAccountIdPath()))
      .pathParam("bank-account-id", id)
      .when()
      .get()
      .then()
      .extract()
      .response();
  }
}
