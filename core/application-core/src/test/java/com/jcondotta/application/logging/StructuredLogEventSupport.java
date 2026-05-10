package com.jcondotta.application.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import org.slf4j.event.KeyValuePair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public final class StructuredLogEventSupport {

  private StructuredLogEventSupport() {}

  public static ListAppender<ILoggingEvent> attachAppender(Class<?> loggerClass) {
    var logger = (Logger) LoggerFactory.getLogger(loggerClass);
    var appender = new ListAppender<ILoggingEvent>();
    appender.start();
    logger.addAppender(appender);
    return appender;
  }

  public static void detachAppender(Class<?> loggerClass, ListAppender<ILoggingEvent> appender) {
    var logger = (Logger) LoggerFactory.getLogger(loggerClass);
    logger.detachAppender(appender);
    appender.stop();
  }

  public static Map<String, String> keyValues(ILoggingEvent event) {
    List<KeyValuePair> pairs = event.getKeyValuePairs();
    if (pairs == null) {
      return Map.of();
    }

    return pairs.stream()
      .collect(toMap(
        pair -> pair.key,
        pair -> String.valueOf(pair.value),
        (left, right) -> right
      ));
  }

  public static List<String> eventTypes(ListAppender<ILoggingEvent> appender) {
    return appender.list.stream()
      .map(StructuredLogEventSupport::keyValues)
      .map(values -> values.get(LogKey.EVENT_TYPE))
      .filter(java.util.Objects::nonNull)
      .toList();
  }

  public static Map<String, String> lastEventKeyValues(ListAppender<ILoggingEvent> appender) {
    return keyValues(appender.list.getLast());
  }

  public static <T> T lastEvent(ListAppender<ILoggingEvent> appender, Function<ILoggingEvent, T> mapper) {
    return mapper.apply(appender.list.getLast());
  }
}
