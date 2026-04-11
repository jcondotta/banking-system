package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.shard;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.shard.ShardCalculator;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxShardsProperties;
import com.jcondotta.domain.identity.AggregateId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxShardResolver {

  private final OutboxShardsProperties shardsProperties;

  public int resolve(AggregateId<?> aggregateId) {
    return ShardCalculator.calculate(aggregateId, shardsProperties.count());
  }
}
