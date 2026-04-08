package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class OutboxEventShardProcessorTest {

//  private static final int SHARD = 1;
//  private static final UUID EVENT_ID = UUID.fromString("1091df64-40ea-427b-b570-587372241ce8");
//  private static final Duration PROCESSING_TIMEOUT = Duration.ofMillis(500);
//
//  @Mock
//  private ShardExecutor<Integer> shardExecutor;
//
//  @Mock
//  private OutboxEventStore repository;
//
//  @Mock
//  private OutboxPublisher publisher;
//
//  @Mock
//  private OutboxProcessingProperties processingProperties;
//
//  @Mock
//  private OutboxEntity event;
//
//  private OutboxEventProcessor processor;
//
//  @BeforeEach
//  void setUp() {
//    processor = new OutboxEventProcessor(shardExecutor, repository, publisher, processingProperties);
//
//    when(processingProperties.acquireTimeout()).thenReturn(PROCESSING_TIMEOUT);
//    when(event.getEventId()).thenReturn(EVENT_ID);
//
//    // Make shardExecutor actually execute the submitted task (wraps Runnable in Supplier)
//    doAnswer(invocation -> {
//      Supplier<?> task = invocation.getArgument(2);
//      task.get();
//      return null;
//    }).when(shardExecutor).execute(anyInt(), any(Duration.class), any(Supplier.class));
//  }
//
//  @Test
//  void shouldPublishEvent_whenProcessingSucceeds() {
//    processor.process(SHARD, event);
//
//    verify(publisher).publish(event);
//  }
//
//  @Test
//  void shouldMarkEventAsPublished_whenProcessingSucceeds() {
//    processor.process(SHARD, event);
//
//    verify(repository).markEventAsPublished(event);
//  }
//
//  @Test
//  void shouldPublishBeforeMarkingAsPublished_whenProcessingSucceeds() {
//    InOrder order = inOrder(publisher, repository);
//
//    processor.process(SHARD, event);
//
//    order.verify(publisher).publish(event);
//    order.verify(repository).markEventAsPublished(event);
//  }
//
//  @Test
//  void shouldDelegateToShardExecutorWithCorrectShardAndTimeout_whenProcessingEvent() {
//    processor.process(SHARD, event);
//
//    verify(shardExecutor).execute(eq(SHARD), eq(PROCESSING_TIMEOUT), any(Supplier.class));
//  }
//
//  @Test
//  void shouldSwallowException_whenPublisherThrows() {
//    doThrow(new RuntimeException("Kafka unavailable")).when(publisher).publish(event);
//
//    // must NOT propagate the exception
//    processor.process(SHARD, event);
//
//    verify(publisher).publish(event);
//    verify(repository, never()).markEventAsPublished(event);
//  }
//
//  @Test
//  void shouldSwallowException_whenMarkEventAsPublishedThrows() {
//    doThrow(new RuntimeException("DynamoDB error")).when(repository).markEventAsPublished(event);
//
//    // must NOT propagate the exception
//    processor.process(SHARD, event);
//
//    verify(publisher).publish(event);
//    verify(repository).markEventAsPublished(event);
//  }
//
//  @Test
//  void shouldNotMarkEventAsPublished_whenPublisherThrows() {
//    doThrow(new RuntimeException("Kafka failure")).when(publisher).publish(event);
//
//    processor.process(SHARD, event);
//
//    verify(repository, never()).markEventAsPublished(any());
//  }
}
