package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox;

import com.jcondotta.banking.accounts.infrastructure.properties.OutboxShardsProperties;
import com.jcondotta.domain.identity.AggregateId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxShardResolver {

  private final OutboxShardsProperties shardsProperties;

  public int resolve(AggregateId<?> aggregateId) {
    return (aggregateId.asString().hashCode() & Integer.MAX_VALUE) % shardsProperties.count();
  }
}