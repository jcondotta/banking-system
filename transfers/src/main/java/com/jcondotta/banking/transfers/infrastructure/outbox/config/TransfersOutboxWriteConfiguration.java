package com.jcondotta.banking.transfers.infrastructure.outbox.config;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.infrastructure.outbox.collector.OutboxEventCollector;
import com.jcondotta.banking.infrastructure.outbox.config.OutboxWriteConfiguration;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OutboxWriteConfiguration.class)
class TransfersOutboxWriteConfiguration {

  @Bean
  OutboxEventCollector<OutboxJpaEntity> outboxEventCollector(
    EventPublicationRegistry publicationRegistry,
    OutboxEntityMapper<OutboxJpaEntity> outboxMapper,
    CorrelationIdProvider correlationIdProvider,
    EventSourceProvider eventSourceProvider
  ) {
    return new OutboxEventCollector<>(publicationRegistry, outboxMapper, correlationIdProvider, eventSourceProvider);
  }
}
