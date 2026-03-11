package com.jcondotta.banking.accounts.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.jcondotta.banking")
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.banking.accounts.infrastructure.properties")
public class BankAccountsApplication {

  public static void main(String[] args) {
    SpringApplication.run(BankAccountsApplication.class, args);
  }

}
