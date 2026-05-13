package com.jcondotta.banking.accounts.config.events;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.application.events.IntegrationEventCollector;
import com.jcondotta.application.events.mapper.DomainEventIntegrationEventMapper;
import com.jcondotta.application.events.mapper.DomainEventMapperRegistry;
import com.jcondotta.application.events.mapper.DomainEventMapperRegistryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BankAccountEventMapperConfig {

  @Bean
  DomainEventMapperRegistry domainEventMapperRegistry(
    List<DomainEventIntegrationEventMapper<?, ?>> mappers) {

    return new DomainEventMapperRegistryFactory().create(mappers);
  }

  @Bean
  IntegrationEventCollector integrationEventCollector(
    DomainEventMapperRegistry registry,
    CorrelationIdProvider correlationIdProvider, EventSourceProvider eventSourceProvider) {

    return new IntegrationEventCollector(registry, correlationIdProvider, eventSourceProvider);
  }
}
