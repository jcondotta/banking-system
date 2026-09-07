package com.jcondotta.banking.infrastructure.outbox.config;

import com.jcondotta.banking.infrastructure.adapters.input.scheduling.outbox.OutboxWorkerRunner;
import com.jcondotta.banking.infrastructure.outbox.concurrency.DefaultShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.concurrency.SemaphoreConcurrencyPolicy;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardConcurrencyPolicy;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Opt-in worker configuration imported by bounded contexts that process an outbox.
 * It deliberately has no component stereotype so merely depending on infrastructure-core
 * does not activate or bind worker infrastructure.
 *
 * <p>This configuration is already self-gated by {@code app.outbox.worker.enabled=true}.
 * The {@code @ConditionalOnProperty} on the consuming module's own configuration class
 * is necessary to prevent injection failures for beans that depend on beans declared here
 * (e.g. {@code ShardExecutor}) when the worker is disabled.
 */
@ConditionalOnProperty(
  prefix = "app.outbox.worker",
  name = "enabled",
  havingValue = "true"
)
@EnableConfigurationProperties(OutboxProperties.class)
public class OutboxWorkerConfiguration {

  @Bean
  ShardConcurrencyPolicy<Integer> outboxConcurrencyPolicy(OutboxProperties outboxProperties) {
    var shards = outboxProperties.shards();
    var processing = outboxProperties.worker().processing();
    return new SemaphoreConcurrencyPolicy<>(shards.shardIds(), processing.concurrencyPerShard());
  }

  @Bean
  ShardExecutor<Integer> outboxShardExecutor(
    ShardConcurrencyPolicy<Integer> policy,
    OutboxProperties outboxProperties
  ) {
    return new DefaultShardExecutor<>(policy, outboxProperties.worker().processing().acquireTimeout());
  }

  @Bean
  OutboxWorkerRunner outboxWorkerRunner(OutboxDispatcher dispatcher, OutboxProperties outboxProperties) {
    return new OutboxWorkerRunner(dispatcher, outboxProperties.worker().polling().interval());
  }
}
