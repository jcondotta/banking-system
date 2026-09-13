package com.jcondotta.application.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.SoftAssertions;
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

  @Test
  void shouldLogAtDebugLevel_whenDebugIsCalled() {
    LogContext.timed(logger(), "accounts.debug-test")
      .debug("Debug event logged")
      .log();

    var softly = new SoftAssertions();
    softly.assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.DEBUG);
    softly.assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, "accounts.debug-test");
    softly.assertAll();
  }

  @Test
  void shouldLogAtWarnLevel_withFailureOutcomeAndReason_whenWarnIsCalled() {
    LogContext.timed(logger(), "accounts.warn-test")
      .warn("Warn event logged")
      .failure()
      .with(LogKey.REASON, "some-reason")
      .log();

    var softly = new SoftAssertions();
    softly.assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    softly.assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, "accounts.warn-test")
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "some-reason");
    softly.assertAll();
  }

  @Test
  void shouldLogAtErrorLevel_withThrowable_whenErrorIsCalled() {
    var throwable = new RuntimeException("something went wrong");

    LogContext.timed(logger(), "accounts.error-test")
      .error("Error event logged", throwable)
      .log();

    var throwableProxy = StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getThrowableProxy);

    assertThat(throwableProxy).isNotNull();

    var softly = new SoftAssertions();
    softly.assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    softly.assertThat(throwableProxy.getMessage()).isEqualTo("something went wrong");
    softly.assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, "accounts.error-test");
    softly.assertAll();
  }

  private org.slf4j.Logger logger() {
    return org.slf4j.LoggerFactory.getLogger(LogContextTest.class);
  }
}
