package com.jcondotta.banking.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication(scanBasePackages = "com.jcondotta.banking")
@ImportHttpServices(basePackages = "com.jcondotta.banking.accounts.infrastructure.adapters.input.rest")
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.banking")
public class AccountsApplication {

  static void main(String[] args) {
    SpringApplication.run(AccountsApplication.class, args);
  }
}
