package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log;

public final class OutboxLogKey {

  public static final String AGGREGATE_ID = "aggregate_id";
  public static final String EVENT_ID = "event_id";
  public static final String IN_FLIGHT_TASKS = "in_flight_tasks";
  public static final String OUTBOX_EVENT_TYPE = "outbox_event_type";
  public static final String RETRY_COUNT = "retry_count";
  public static final String SHARD = "shard";

  private OutboxLogKey() {}
}
