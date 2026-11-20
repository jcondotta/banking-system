package com.jcondotta.banking.infrastructure.outbox.log;

public final class OutboxLogKey {

  public static final String AGGREGATE_ID = "aggregate_id";
  public static final String EVENT_ID = "event_id";
  public static final String IN_FLIGHT_TASKS = "in_flight_tasks";
  public static final String ATTEMPT_COUNT = "attempt_count";
  public static final String SHARD = "shard";

  private OutboxLogKey() {}
}
