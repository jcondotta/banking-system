package com.jcondotta.banking.recipients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.jcondotta.banking")
@ConfigurationPropertiesScan(basePackages = "com.jcondotta.banking")
public class RecipientsApplication {

  static void main(String[] args) {
    SpringApplication.run(RecipientsApplication.class, args);
  }
}

