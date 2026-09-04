package com.jcondotta.banking.infrastructure.outbox.config;

import com.jcondotta.banking.infrastructure.outbox.concurrency.DefaultShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.concurrency.SemaphoreConcurrencyPolicy;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardConcurrencyPolicy;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProcessingProperties;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxShardsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxProcessorConfiguration {

    @Bean
    ShardConcurrencyPolicy<Integer> outboxConcurrencyPolicy(OutboxShardsProperties properties) {
        return new SemaphoreConcurrencyPolicy<>(properties.shardIds(), properties.concurrencyPerShard());
    }

    @Bean
    ShardExecutor<Integer> outboxShardExecutor(ShardConcurrencyPolicy<Integer> policy, OutboxProcessingProperties properties) {
        return new DefaultShardExecutor<>(policy, properties.acquireTimeout());
    }
}
