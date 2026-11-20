package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

final class OpenBankAccountIntegrationOperations {

  private final BankAccountsURIProperties uriProperties;
  private final Function<String, RequestSpecification> requestSpecification;

  OpenBankAccountIntegrationOperations(BankAccountsURIProperties uriProperties, Function<String, RequestSpecification> requestSpecification) {
    this.uriProperties = uriProperties;
    this.requestSpecification = requestSpecification;
  }

  BankAccountId openBankAccount(AccountType accountType, Currency currency) {
    var request = BankAccountRequestFactory.openBankAccount(
      accountType,
      currency,
      AccountHolderFixtures.JEFFERSON
    );
    var response = postOpenBankAccount(request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    return extractBankAccountId(response);
  }

  Response postOpenBankAccount(OpenBankAccountRequest request) {
    return postOpenBankAccount(request, UUID.randomUUID());
  }

  Response postOpenBankAccount(OpenBankAccountRequest request, UUID idempotencyKey) {
    return given()
      .spec(requestSpecification.apply(uriProperties.rootPath()))
      .header(HttpHeadersConstants.IDEMPOTENCY_KEY, idempotencyKey)
      .body(request)
      .when()
      .post()
      .then()
      .extract()
      .response();
  }

  private static BankAccountId extractBankAccountId(Response response) {
    var location = response.getHeader("Location");
    assertThat(location).isNotBlank();

    return BankAccountId.of(UUID.fromString(location.substring(location.lastIndexOf('/') + 1)));
  }
}
