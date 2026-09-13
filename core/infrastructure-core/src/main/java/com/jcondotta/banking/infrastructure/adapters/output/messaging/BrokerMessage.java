package com.jcondotta.banking.infrastructure.adapters.output.messaging;

public record BrokerMessage(String destination, String key, byte[] payload) {}
