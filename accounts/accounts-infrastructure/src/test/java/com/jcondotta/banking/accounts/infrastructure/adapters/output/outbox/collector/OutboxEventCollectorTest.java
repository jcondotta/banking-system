package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.collector;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventCollectorTest {

  @Mock
  private IntegrationEventCollector integrationEventCollector;

  @Mock
  private OutboxEntityMapper outboxEntityMapper;

  @Mock
  private AggregateRoot<?> aggregate;

  @Mock
  private AggregateId<?> aggregateId;

  private OutboxEventCollector collector;

  @BeforeEach
  void setUp() {
    collector = new OutboxEventCollector(integrationEventCollector, outboxEntityMapper);

    doReturn(aggregateId).when(aggregate).getId();
  }

  @Test
  void shouldReturnEmptyList_whenAggregateHasNoDomainEvents() {
    when(aggregate.pullEvents()).thenReturn(List.of());
    when(integrationEventCollector.collect(List.of())).thenReturn(List.of());

    var result = collector.collect(aggregate);

    assertThat(result).isEmpty();
    verify(outboxEntityMapper, never()).toOutboxEntity(any(), any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnSingleOutboxEntity_whenAggregateHasOneDomainEvent() {
    var domainEvent = mock(DomainEvent.class);
    var integrationEvent = mock(IntegrationEvent.class);
    var outboxEntity = mock(OutboxEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(domainEvent));
    when(integrationEventCollector.collect(List.of(domainEvent))).thenReturn(List.of(integrationEvent));
    when(outboxEntityMapper.toOutboxEntity(aggregateId, integrationEvent)).thenReturn(outboxEntity);

    var result = collector.collect(aggregate);

    assertThat(result).containsExactly(outboxEntity);
    verify(outboxEntityMapper, times(1)).toOutboxEntity(aggregateId, integrationEvent);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnOutboxEntitiesForEachDomainEvent_whenAggregateHasMultipleDomainEvents() {
    var domainEvent1 = mock(DomainEvent.class);
    var domainEvent2 = mock(DomainEvent.class);
    var integrationEvent1 = mock(IntegrationEvent.class);
    var integrationEvent2 = mock(IntegrationEvent.class);
    var outboxEntity1 = mock(OutboxEntity.class);
    var outboxEntity2 = mock(OutboxEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(domainEvent1, domainEvent2));
    when(integrationEventCollector.collect(List.of(domainEvent1, domainEvent2)))
      .thenReturn(List.of(integrationEvent1, integrationEvent2));
    when(outboxEntityMapper.toOutboxEntity(aggregateId, integrationEvent1)).thenReturn(outboxEntity1);
    when(outboxEntityMapper.toOutboxEntity(aggregateId, integrationEvent2)).thenReturn(outboxEntity2);

    var result = collector.collect(aggregate);

    assertThat(result).containsExactly(outboxEntity1, outboxEntity2);
    verify(outboxEntityMapper).toOutboxEntity(aggregateId, integrationEvent1);
    verify(outboxEntityMapper).toOutboxEntity(aggregateId, integrationEvent2);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldPassAggregateIdToMapper_whenMappingEachIntegrationEvent() {
    var domainEvent = mock(DomainEvent.class);
    var integrationEvent = mock(IntegrationEvent.class);
    var outboxEntity = mock(OutboxEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(domainEvent));
    when(integrationEventCollector.collect(List.of(domainEvent))).thenReturn(List.of(integrationEvent));
    when(outboxEntityMapper.toOutboxEntity(aggregateId, integrationEvent)).thenReturn(outboxEntity);

    collector.collect(aggregate);

    verify(aggregate).getId();
    verify(outboxEntityMapper).toOutboxEntity(eq(aggregateId), eq(integrationEvent));
  }
}
