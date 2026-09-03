package com.jcondotta.banking.accounts.infrastructure.outbox.config;

import com.jcondotta.banking.accounts.infrastructure.outbox.concurrency.ConcurrencyAwareShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.outbox.concurrency.ConcurrencyPolicy;
import com.jcondotta.banking.accounts.infrastructure.outbox.concurrency.SemaphoreConcurrencyPolicy;
import com.jcondotta.banking.accounts.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.outbox.properties.OutboxProcessingProperties;
import com.jcondotta.banking.accounts.infrastructure.outbox.properties.OutboxShardsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxProcessorConfiguration {

    @Bean
    ConcurrencyPolicy<Integer> outboxConcurrencyPolicy(OutboxShardsProperties outboxShardsProperties) {
        return new SemaphoreConcurrencyPolicy<>(
          outboxShardsProperties.shardIds(),
          outboxShardsProperties.concurrencyPerShard()
        );
    }

    @Bean
    ShardExecutor<Integer> outboxShardExecutor(ConcurrencyPolicy<Integer> outboxConcurrencyPolicy,
                                               OutboxProcessingProperties processingProperties) {
        return new ConcurrencyAwareShardExecutor<>(outboxConcurrencyPolicy, processingProperties.acquireTimeout());
    }
}
