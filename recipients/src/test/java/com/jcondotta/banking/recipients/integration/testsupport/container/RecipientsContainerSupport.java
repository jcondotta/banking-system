package com.jcondotta.banking.recipients.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.lifecycle.Startables;

@Slf4j
public final class RecipientsContainerSupport {

  private static boolean started;

  private RecipientsContainerSupport() {
  }

  public static synchronized void start() {
    if (started) {
      return;
    }

    try {
      Startables.deepStart(
        PostgreSQLContainerSupport.container(),
        KafkaContainerSupport.container()
      ).join();

      PostgreSQLContainerSupport.logStarted();
      KafkaContainerSupport.logStarted();

      started = true;
    }
    catch (Exception ex) {
      log.error("Failed to start recipients integration containers: {}", ex.getMessage());
      throw new RuntimeException("Failed to start recipients integration containers", ex);
    }
  }
}
