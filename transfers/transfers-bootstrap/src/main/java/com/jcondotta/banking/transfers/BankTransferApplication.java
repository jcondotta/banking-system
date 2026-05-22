package com.jcondotta.banking.transfers;

import com.jcondotta.banking.transfers.infrastructure.adapters.output.bank_account.BankAccountLookupClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication(scanBasePackages = "com.jcondotta.banking")
@ImportHttpServices(group = "accounts", types = BankAccountLookupClient.class)
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.banking")
@EnableResilientMethods
public class BankTransferApplication {

  static void main(String[] args) {
    SpringApplication.run(BankTransferApplication.class, args);
  }
}
