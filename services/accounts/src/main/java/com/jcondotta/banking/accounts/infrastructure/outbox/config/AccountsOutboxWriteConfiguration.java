package com.jcondotta.banking.accounts.infrastructure.outbox.config;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.infrastructure.outbox.collector.OutboxEventCollector;
import com.jcondotta.banking.infrastructure.outbox.config.OutboxWriteConfiguration;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OutboxWriteConfiguration.class)
class AccountsOutboxWriteConfiguration {

  @Bean
  OutboxEventCollector<OutboxEntity> outboxEventCollector(
    EventPublicationRegistry publicationRegistry,
    OutboxEntityMapper<OutboxEntity> outboxMapper,
    CorrelationIdProvider correlationIdProvider,
    EventSourceProvider eventSourceProvider
  ) {
    return new OutboxEventCollector<>(publicationRegistry, outboxMapper, correlationIdProvider, eventSourceProvider);
  }
}
