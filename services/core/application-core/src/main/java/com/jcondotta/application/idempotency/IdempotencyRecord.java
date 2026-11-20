package com.jcondotta.application.idempotency;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.domain.identity.AggregateId;

import static java.util.Objects.requireNonNull;

public record IdempotencyRecord<ID extends AggregateId<?>>(IdempotencyKey key, String requestHash, ID aggregateId) {

    public IdempotencyRecord {
        requireNonNull(key);
        requireNonNull(requestHash);
        requireNonNull(aggregateId);
    }
}
