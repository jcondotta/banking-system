package com.jcondotta.banking.recipients.application.common.log;

import org.slf4j.spi.LoggingEventBuilder;

import java.util.concurrent.TimeUnit;

public final class LogEvent {

  private final LoggingEventBuilder builder;
  private final long startNs;

  LogEvent(LoggingEventBuilder builder, long startNs) {
    this.builder = builder;
    this.startNs = startNs;
  }

  public LogEvent success() {
    return with(LogKey.OUTCOME, LogOutcome.SUCCESS);
  }

  public LogEvent failure() {
    return with(LogKey.OUTCOME, LogOutcome.FAILURE);
  }

  public LogEvent with(String key, Object value) {
    builder.addKeyValue(key, value);
    return this;
  }

  public void log() {
    builder.addKeyValue(LogKey.DURATION_MS, durationMs());
    builder.log();
  }

  private long durationMs() {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
  }
}
