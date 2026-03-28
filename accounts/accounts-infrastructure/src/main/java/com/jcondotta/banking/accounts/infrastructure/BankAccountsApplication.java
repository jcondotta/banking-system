package com.jcondotta.banking.accounts.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.service.registry.ImportHttpServices;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.jcondotta")
@ImportHttpServices(basePackages = "com.jcondotta.banking.accounts.infrastructure.adapters.input.rest")
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.banking.accounts.infrastructure.properties")
public class BankAccountsApplication {

  static void main(String[] args) {
    SpringApplication.run(BankAccountsApplication.class, args);
  }

}
