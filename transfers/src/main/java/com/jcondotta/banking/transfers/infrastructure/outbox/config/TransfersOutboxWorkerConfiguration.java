package com.jcondotta.banking.transfers.infrastructure.outbox.config;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessageSender;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.config.OutboxWorkerConfiguration;
import com.jcondotta.banking.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.infrastructure.outbox.factory.KafkaOutboxWorkerFactory;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxEventStore;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(
  prefix = "app.outbox.worker",
  name = "enabled",
  havingValue = "true"
)
@Import(OutboxWorkerConfiguration.class)
class TransfersOutboxWorkerConfiguration {

  @Bean
  OutboxDispatcher outboxDispatcher(
    OutboxEventStore<OutboxJpaEntity> eventStore,
    BrokerMessageSender messageSender,
    ShardExecutor<Integer> shardExecutor,
    OutboxProperties outboxProperties
  ) {
    return KafkaOutboxWorkerFactory.createDispatcher(eventStore, messageSender, shardExecutor, outboxProperties);
  }
}
