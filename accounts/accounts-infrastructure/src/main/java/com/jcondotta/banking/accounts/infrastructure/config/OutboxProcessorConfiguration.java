package com.jcondotta.banking.accounts.infrastructure.config;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.SemaphoreShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxProcessorConfiguration {

    @Bean
    ShardExecutor<Integer> outboxShardExecutor(OutboxProperties outboxProperties) {
        return new SemaphoreShardExecutor<>(
          outboxProperties.shards().shardIds(),
          outboxProperties.shards().concurrencyPerShard()
        );
    }
}