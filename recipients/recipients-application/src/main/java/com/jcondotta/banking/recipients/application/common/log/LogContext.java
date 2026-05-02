package com.jcondotta.banking.recipients.application.common.log;

import org.slf4j.Logger;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class LogContext {

  private final Map<String, Object> entries;
  private final Logger logger;
  private final long startNs;

  private LogContext(Logger logger, Map<String, Object> entries, long startNs) {
    this.logger = logger;
    this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    this.startNs = startNs;
  }

  public static LogContext timed(Logger logger, String eventType) {
    return new LogContext(
      logger,
      Map.of(LogKey.EVENT_TYPE, eventType),
      System.nanoTime()
    );
  }

  public LogContext with(String key, Object value) {
    var nextEntries = new LinkedHashMap<>(entries);
    nextEntries.put(key, value);

    return new LogContext(logger, nextEntries, startNs);
  }

  public LogEvent info(String message) {
    return event(logger.atInfo(), message);
  }

  public LogEvent debug(String message) {
    return event(logger.atDebug(), message);
  }

  public LogEvent warn(String message) {
    return event(logger.atWarn(), message);
  }

  public LogEvent error(String message, Throwable throwable) {
    return event(logger.atError().setCause(throwable), message);
  }

  private LogEvent event(LoggingEventBuilder builder, String message) {
    builder.setMessage(message);
    entries.forEach(builder::addKeyValue);

    return new LogEvent(builder, startNs);
  }
}
