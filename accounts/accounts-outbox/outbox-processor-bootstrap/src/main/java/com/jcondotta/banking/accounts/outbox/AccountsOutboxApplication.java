package com.jcondotta.banking.accounts.outbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.jcondotta.banking.accounts.outbox")
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.banking.accounts.outbox")
public class AccountsOutboxApplication {

  static void main(String[] args) {
    SpringApplication.run(AccountsOutboxApplication.class, args);
  }

}
