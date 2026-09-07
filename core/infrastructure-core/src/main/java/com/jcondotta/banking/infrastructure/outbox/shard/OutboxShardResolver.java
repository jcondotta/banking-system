package com.jcondotta.banking.infrastructure.outbox.shard;

import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.domain.identity.AggregateId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutboxShardResolver {

    private final OutboxProperties.Shards shards;

    public int resolve(AggregateId<?> aggregateId) {
        return (aggregateId.asString().hashCode() & Integer.MAX_VALUE) % shards.count();
    }
}
