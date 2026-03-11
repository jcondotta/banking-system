package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}