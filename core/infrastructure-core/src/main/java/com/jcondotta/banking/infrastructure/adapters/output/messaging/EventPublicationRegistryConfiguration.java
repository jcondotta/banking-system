package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(EventPublicationRegistry.class)
public class EventPublicationRegistryConfiguration {

  @Bean
  EventPublicationRegistry eventPublicationRegistry(ObjectProvider<EventPublicationFactory<?>> factories) {
    return new EventPublicationRegistryFactory().create(factories.orderedStream().toList());
  }
}
