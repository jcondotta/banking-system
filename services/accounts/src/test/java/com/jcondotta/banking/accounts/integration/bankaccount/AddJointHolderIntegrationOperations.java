package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

final class AddJointHolderIntegrationOperations {

  private final BankAccountsURIProperties uriProperties;
  private final Function<String, RequestSpecification> requestSpecification;

  AddJointHolderIntegrationOperations(
    BankAccountsURIProperties uriProperties,
    Function<String, RequestSpecification> requestSpecification
  ) {
    this.uriProperties = uriProperties;
    this.requestSpecification = requestSpecification;
  }

  void addJointHolder(BankAccountId id, AccountHolderFixtures fixture) {
    var response = postJointHolder(id, BankAccountRequestFactory.addJointHolder(fixture));
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  Response postJointHolder(BankAccountId id, AddJointHolderRequest request) {
    return postJointHolder(id, request, UUID.randomUUID());
  }

  Response postJointHolder(BankAccountId id, AddJointHolderRequest request, UUID idempotencyKey) {
    return given()
      .spec(requestSpecification.apply(uriProperties.bankAccountIdPath() + "/account-holders"))
      .header(HttpHeadersConstants.IDEMPOTENCY_KEY, idempotencyKey)
      .pathParam("bank-account-id", id.value())
      .body(request)
      .when()
      .post()
      .then()
      .extract()
      .response();
  }
}
