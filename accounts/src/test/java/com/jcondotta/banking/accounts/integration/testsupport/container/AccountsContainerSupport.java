package com.jcondotta.banking.accounts.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.lifecycle.Startables;

@Slf4j
public final class AccountsContainerSupport {

  private static boolean started;

  private AccountsContainerSupport() {
  }

  public static synchronized void start() {
    if (started) {
      return;
    }

    try {
      Startables.deepStart(
        LocalStackContainerSupport.container(),
        KafkaContainerSupport.container()
      ).join();

      LocalStackContainerSupport.initializeDynamoDb();
      KafkaContainerSupport.logStarted();

      started = true;
    }
    catch (Exception ex) {
      log.error("Failed to start accounts integration containers: {}", ex.getMessage());
      throw new RuntimeException("Failed to start accounts integration containers", ex);
    }
  }
}
