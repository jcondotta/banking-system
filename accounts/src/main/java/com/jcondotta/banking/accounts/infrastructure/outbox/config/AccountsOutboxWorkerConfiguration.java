package com.jcondotta.banking.accounts.infrastructure.outbox.config;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.config.OutboxWorkerConfiguration;
import com.jcondotta.banking.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.infrastructure.outbox.factory.KafkaOutboxWorkerFactory;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxEventStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
  prefix = "app.outbox.worker",
  name = "enabled",
  havingValue = "true"
)
@Import(OutboxWorkerConfiguration.class)
class AccountsOutboxWorkerConfiguration {

  @Bean
  OutboxDispatcher outboxDispatcher(
    OutboxEventStore<OutboxEntity> eventStore,
    KafkaTemplate<String, byte[]> kafkaTemplate,
    ShardExecutor<Integer> shardExecutor,
    OutboxProperties outboxProperties
  ) {
    return KafkaOutboxWorkerFactory.createDispatcher(eventStore, kafkaTemplate, shardExecutor, outboxProperties);
  }
}
