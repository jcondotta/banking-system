package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

final class ActivateBankAccountIntegrationOperations {

  private final BankAccountsURIProperties uriProperties;
  private final Function<String, RequestSpecification> requestSpecification;

  ActivateBankAccountIntegrationOperations(
    BankAccountsURIProperties uriProperties,
    Function<String, RequestSpecification> requestSpecification
  ) {
    this.uriProperties = uriProperties;
    this.requestSpecification = requestSpecification;
  }

  void activateBankAccount(BankAccountId id) {
    activateBankAccount(id, null);
  }

  void activateBankAccount(BankAccountId id, UUID correlationId) {
    var request = given()
      .spec(requestSpecification.apply(uriProperties.bankAccountIdPath() + "/activate"))
      .pathParam("bank-account-id", id.value());

    if (correlationId != null) {
      request.header(HttpHeadersConstants.CORRELATION_ID, correlationId);
    }

    var response = request
      .when()
      .patch()
      .then()
      .extract()
      .response();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }
}
