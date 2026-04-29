package com.jcondotta.application.events.mapper;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.events.mapper.exceptions.DuplicateDomainEventMapperException;
import com.jcondotta.application.testsupport.FakeDomainEvent;
import com.jcondotta.application.testsupport.FakeDomainEventMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainEventMapperRegistryFactoryTest {

  private final DomainEventMapperRegistryFactory factory =
    new DomainEventMapperRegistryFactory();

  private final FakeDomainEventMapper fakeMapper =
    new FakeDomainEventMapper();

  @Test
  void shouldCreateRegistrySuccessfully_whenValidMappersProvided() {

    var registry = factory.create(List.of(fakeMapper));

    assertThat(registry).isNotNull();
  }

  @Test
  void shouldCreateRegistrySuccessfully_whenUsingVarargsFactoryMethod() {

    var registry = DomainEventMapperRegistryFactory.of(fakeMapper);

    assertThat(registry).isNotNull();
  }

  @Test
  void shouldThrowException_whenMapperListIsNull() {

    assertThatThrownBy(() -> factory.create(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(DomainEventMapperRegistryFactory.DOMAIN_EVENT_MAPPERS_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenVarargsMappersIsNull() {

    assertThatThrownBy(() ->
      DomainEventMapperRegistryFactory.of((DomainEventIntegrationEventMapper<?, ?>[]) null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(DomainEventMapperRegistryFactory.DOMAIN_EVENT_MAPPERS_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenAnyMapperIsNull() {
    List<DomainEventIntegrationEventMapper<?, ?>> mappers = new ArrayList<>();
    mappers.add(fakeMapper);
    mappers.add(null);

    assertThatThrownBy(() -> factory.create(mappers))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(DomainEventMapperRegistryFactory.DOMAIN_EVENT_MAPPER_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenDomainEventTypeIsNull() {
    var mapper = new DomainEventIntegrationEventMapper<FakeDomainEvent, IntegrationEvent<?>>() {

      @Override
      public Class<FakeDomainEvent> domainEventType() {
        return null;
      }

      @Override
      public IntegrationEvent<?> map(IntegrationEventMetadata metadata, FakeDomainEvent event) {
        return null;
      }
    };

    assertThatThrownBy(() ->
      factory.create(List.of(mapper)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(DomainEventMapperRegistryFactory.DOMAIN_EVENT_TYPE_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenDuplicateMapperRegistered() {
    var duplicateMapper = new FakeDomainEventMapper();

    assertThatThrownBy(() ->
      factory.create(List.of(fakeMapper, duplicateMapper)))
      .isInstanceOf(DuplicateDomainEventMapperException.class)
      .hasMessageContaining(
        duplicateMapper.domainEventType().getSimpleName()
      );
  }
}
