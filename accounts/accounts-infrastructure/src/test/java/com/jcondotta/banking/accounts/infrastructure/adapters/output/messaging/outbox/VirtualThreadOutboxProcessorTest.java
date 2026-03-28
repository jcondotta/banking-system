package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query.OutboxQuery;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query.OutboxRepository;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.OutboxStatus;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProperties;
import com.jcondotta.banking.accounts.infrastructure.properties.PollingProperties;
import com.jcondotta.banking.accounts.infrastructure.properties.ProcessingProperties;
import com.jcondotta.banking.accounts.infrastructure.properties.ShardsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualThreadOutboxProcessorTest {

  private static final int SHARD_ID = 0;
  private static final int BATCH_SIZE = 10;
  private static final Duration PROCESSING_TIMEOUT = Duration.ofMillis(200);

  @Mock
  private ShardExecutor<Integer> shardExecutor;

  @Mock
  private OutboxRepository repository;

  @Mock
  private OutboxPublisher publisher;

  private OutboxProperties properties;
  private VirtualThreadOutboxProcessor processor;

  @BeforeEach
  void setUp() {
    var shardsProperties = new ShardsProperties(1, 1, BATCH_SIZE);
    var processingProperties = new ProcessingProperties(PROCESSING_TIMEOUT);
    var pollingProperties = new PollingProperties(Duration.ofSeconds(5));

    properties = new OutboxProperties(shardsProperties, processingProperties, pollingProperties);
    processor = new VirtualThreadOutboxProcessor(shardExecutor, repository, publisher, properties);
  }

  @Test
  void shouldSkipProcessing_whenTryMarkAsProcessingReturnsFalse() throws InterruptedException {
    var event = pendingOutboxEvent();
    var latch = new CountDownLatch(1);

    when(repository.findEvents(any(OutboxQuery.class))).thenReturn(List.of(event));
    when(repository.tryMarkAsProcessing(event)).thenReturn(false);

    doAnswer(inv -> {
      Supplier<?> task = inv.getArgument(2);
      task.get();
      latch.countDown();
      return null;
    }).when(shardExecutor).execute(eq(SHARD_ID), eq(PROCESSING_TIMEOUT), any());

    processor.process();
    assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();

    verify(repository).tryMarkAsProcessing(event);
    verifyNoInteractions(publisher);
    verify(repository, never()).markAsPublished(any());
  }

  @Test
  void shouldProcessEvent_whenTryMarkAsProcessingReturnsTrue() throws InterruptedException {
    var event = pendingOutboxEvent();
    var latch = new CountDownLatch(1);

    when(repository.findEvents(any(OutboxQuery.class))).thenReturn(List.of(event));
    when(repository.tryMarkAsProcessing(event)).thenReturn(true);

    doAnswer(inv -> {
      Supplier<?> task = inv.getArgument(2);
      task.get();
      latch.countDown();
      return null;
    }).when(shardExecutor).execute(eq(SHARD_ID), eq(PROCESSING_TIMEOUT), any());

    processor.process();
    assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();

    verify(repository).tryMarkAsProcessing(event);
    verify(publisher).publish(event);
    verify(repository).markAsPublished(event);
  }

  private OutboxEntity pendingOutboxEvent() {
    return OutboxEntity.builder()
      .eventId(UUID.randomUUID())
      .status(OutboxStatus.PENDING)
      .build();
  }
}
