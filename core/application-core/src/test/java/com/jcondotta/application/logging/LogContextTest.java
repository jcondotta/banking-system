package com.jcondotta.application.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogContextTest {

  private ListAppender<ILoggingEvent> logAppender;

  @BeforeEach
  void setUp() {
    logAppender = StructuredLogEventSupport.attachAppender(LogContextTest.class);
  }

  @AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(LogContextTest.class, logAppender);
  }

  @Test
  void shouldLogOperationWithoutEventType_whenNoEventIsPresent() {
    LogContext.timed(logger(), "accounts.activate")
      .info("Bank account activated")
      .success()
      .log();

    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, "accounts.activate")
      .doesNotContainKey(LogKey.EVENT_TYPE);
  }

  @Test
  void shouldLogOperationAndEventType_whenAnEventIsPresent() {
    LogContext.timed(logger(), "outbox.publish")
      .with(LogKey.EVENT_TYPE, "bank-account-activated")
      .info("Outbox event published")
      .success()
      .log();

    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, "outbox.publish")
      .containsEntry(LogKey.EVENT_TYPE, "bank-account-activated");
  }

  private org.slf4j.Logger logger() {
    return org.slf4j.LoggerFactory.getLogger(LogContextTest.class);
  }
}
