package com.jcondotta.application.idempotency;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.domain.identity.AggregateId;

import static java.util.Objects.requireNonNull;

public record IdempotentSummary<ID extends AggregateId<?>>(
    IdempotencyKey key,
    String requestHash,
    boolean alreadyProcessed,
    ID aggregateId)
{

    public IdempotentSummary {
        requireNonNull(key);
        requireNonNull(requestHash);
        if (alreadyProcessed) {
            requireNonNull(aggregateId);
        }
    }

    public static <ID extends AggregateId<?>> IdempotentSummary<ID> newOperation(IdempotencyKey key, String requestHash) {
        return new IdempotentSummary<>(key, requestHash, false, null);
    }

    public static <ID extends AggregateId<?>> IdempotentSummary<ID> replay(IdempotencyRecord<ID> record) {
        return new IdempotentSummary<>(record.key(), record.requestHash(), true, record.aggregateId());
    }
}
