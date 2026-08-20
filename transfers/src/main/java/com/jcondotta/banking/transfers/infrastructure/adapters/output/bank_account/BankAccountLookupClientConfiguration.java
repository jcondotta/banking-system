package com.jcondotta.banking.transfers.infrastructure.adapters.output.bank_account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

@Configuration
class BankAccountLookupClientConfiguration {

  @Bean
  RestClientHttpServiceGroupConfigurer accountsHttpServiceGroupConfigurer(
    @Value("${spring.http.serviceclient.accounts.base-url:http://localhost:8080}") String accountsBaseUrl,
    @Value("${spring.http.serviceclient.accounts.api-version:1.0}") String accountsApiVersion
  ) {
    return groups -> groups
      .filterByName("accounts")
      .forEachClient((group, restClientBuilder) -> restClientBuilder
        .baseUrl(accountsBaseUrl)
        .defaultHeader("X-API-Version", accountsApiVersion));
  }
}
