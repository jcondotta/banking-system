package com.jcondotta.banking.infrastructure.outbox.config;

import com.jcondotta.banking.infrastructure.adapters.input.scheduling.outbox.OutboxWorkerRunner;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardConcurrencyPolicy;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.shard.OutboxShardResolver;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.support.TestPropertySourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxConfigurationTest {

  @Test
  void shouldRegisterOnlyWriteInfrastructure_whenWorkerIsDisabled() {
    try (var context = new AnnotationConfigApplicationContext()) {
      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        context,
        "app.outbox.shards.count=4",
        "app.outbox.worker.enabled=false"
      );
      context.register(OutboxWriteConfiguration.class, OutboxWorkerConfiguration.class);

      context.refresh();

      assertThat(context.getBeansOfType(OutboxProperties.class)).hasSize(1);
      assertThat(context.getBeansOfType(OutboxShardResolver.class)).hasSize(1);
      assertThat(context.getBeansOfType(ShardConcurrencyPolicy.class)).isEmpty();
      assertThat(context.getBeansOfType(ShardExecutor.class)).isEmpty();
      assertThat(context.getBeansOfType(OutboxWorkerRunner.class)).isEmpty();
    }
  }

  @Test
  void shouldRegisterOnlyWriteInfrastructure_whenWorkerEnablementIsMissing() {
    try (var context = new AnnotationConfigApplicationContext()) {
      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        context,
        "app.outbox.shards.count=4"
      );
      context.register(
        OutboxWriteConfiguration.class,
        OutboxWorkerConfiguration.class,
        WorkerDependenciesConfiguration.class
      );

      context.refresh();

      assertThat(context.getBeansOfType(OutboxProperties.class)).hasSize(1);
      assertThat(context.getBeansOfType(OutboxShardResolver.class)).hasSize(1);
      assertThat(context.getBeansOfType(ShardConcurrencyPolicy.class)).isEmpty();
      assertThat(context.getBeansOfType(ShardExecutor.class)).isEmpty();
      assertThat(context.getBeansOfType(OutboxWorkerRunner.class)).isEmpty();
    }
  }

  @Test
  void shouldRegisterWriteAndWorkerInfrastructure_whenWorkerIsEnabled() {
    try (var context = new AnnotationConfigApplicationContext()) {
      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        context,
        "app.outbox.shards.count=4",
        "app.outbox.worker.enabled=true",
        "app.outbox.worker.processing.concurrency-per-shard=2",
        "app.outbox.worker.processing.batch-size-per-shard=10",
        "app.outbox.worker.processing.acquire-timeout=10s",
        "app.outbox.worker.processing.publish-timeout=5s",
        "app.outbox.worker.processing.claim-timeout=30s",
        "app.outbox.worker.retry.max-retries=5",
        "app.outbox.worker.polling.interval=10s"
      );
      context.register(
        OutboxWriteConfiguration.class,
        OutboxWorkerConfiguration.class,
        WorkerDependenciesConfiguration.class
      );

      context.refresh();

      assertThat(context.getBeansOfType(OutboxProperties.class)).hasSize(1);
      assertThat(context.getBeansOfType(OutboxShardResolver.class)).hasSize(1);
      assertThat(context.getBeansOfType(ShardConcurrencyPolicy.class)).hasSize(1);
      assertThat(context.getBeansOfType(ShardExecutor.class)).hasSize(1);
      assertThat(context.getBeansOfType(OutboxWorkerRunner.class)).hasSize(1);
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class WorkerDependenciesConfiguration {

    @Bean
    OutboxDispatcher outboxDispatcher() {
      return () -> {};
    }
  }
}
