package com.jcondotta.banking.accounts.infrastructure.config;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.ConcurrencyAwareShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.ConcurrencyPolicy;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.SemaphoreConcurrencyPolicy;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProcessingProperties;
import com.jcondotta.banking.accounts.infrastructure.properties.ShardsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxProcessorConfiguration {

    @Bean
    ConcurrencyPolicy<Integer> outboxConcurrencyPolicy(ShardsProperties shardsProperties) {
        return new SemaphoreConcurrencyPolicy<>(
          shardsProperties.shardIds(),
          shardsProperties.concurrencyPerShard()
        );
    }

    @Bean
    ShardExecutor<Integer> outboxShardExecutor(ConcurrencyPolicy<Integer> outboxConcurrencyPolicy,
                                               OutboxProcessingProperties processingProperties) {
        return new ConcurrencyAwareShardExecutor<>(outboxConcurrencyPolicy, processingProperties.timeout());
    }
}