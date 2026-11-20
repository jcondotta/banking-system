package com.jcondotta.application.logging;

/** Structured attributes shared by the application's request and event infrastructure. */
public final class ApplicationLogKey {

    public static final String CORRELATION_ID = "correlation_id";
    public static final String IDEMPOTENCY_KEY = "idempotency_key";
    public static final String EVENT_TYPE = "event_type";

    private ApplicationLogKey() {}
}
