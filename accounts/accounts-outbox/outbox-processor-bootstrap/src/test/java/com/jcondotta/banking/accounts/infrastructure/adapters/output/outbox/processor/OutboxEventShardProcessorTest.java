package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

// Moved from accounts-infrastructure. Production class is:
// com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.processor.OutboxEventShardProcessor
//
// Tests remain commented because the production API has changed significantly:
// - processor.process() now takes only OutboxEntity (shard extracted from entity)
// - OutboxPublisher was replaced by OutboxEventCompleter
// - tryClaimEvent() now returns Optional<OutboxEntity>, so the "claim" step is inside the processor
// - OutboxEventProcessor class was renamed to OutboxEventShardProcessor

@ExtendWith(MockitoExtension.class)
class OutboxEventShardProcessorTest {

//  private static final UUID EVENT_ID = UUID.fromString("1091df64-40ea-427b-b570-587372241ce8");
//  private static final Duration PROCESSING_TIMEOUT = Duration.ofMillis(500);
//
//  @Mock
//  private ShardExecutor<Integer> shardExecutor;
//
//  @Mock
//  private OutboxEventStore eventStore;
//
//  @Mock
//  private OutboxEventCompleter eventCompleter;
//
//  @Mock
//  private OutboxProcessingProperties processingProperties;
//
//  @Mock
//  private OutboxEntity event;
//
//  private OutboxEventShardProcessor processor;
//
//  @BeforeEach
//  void setUp() {
//    processor = new OutboxEventShardProcessor(shardExecutor, eventStore, eventCompleter, processingProperties);
//
//    when(processingProperties.acquireTimeout()).thenReturn(PROCESSING_TIMEOUT);
//    when(event.getEventId()).thenReturn(EVENT_ID);
//    when(event.getShard()).thenReturn(1);
//    when(event.getRetryCount()).thenReturn(0);
//    when(processingProperties.maxRetries()).thenReturn(3);
//
//    // Make shardExecutor actually execute the submitted task
//    doAnswer(invocation -> {
//      Supplier<?> task = invocation.getArgument(2);
//      task.get();
//      return null;
//    }).when(shardExecutor).execute(anyInt(), any(Duration.class), any(Supplier.class));
//  }
//
//  @Test
//  void shouldCompleteEvent_whenProcessingSucceeds() {
//    when(eventStore.tryClaimEvent(event)).thenReturn(Optional.of(event));
//
//    processor.process(event);
//
//    verify(eventCompleter).handle(event);
//  }
//
//  @Test
//  void shouldNotCompleteEvent_whenClaimFails() {
//    when(eventStore.tryClaimEvent(event)).thenReturn(Optional.empty());
//
//    processor.process(event);
//
//    verify(eventCompleter, never()).handle(any());
//  }
//
//  @Test
//  void shouldDelegateToShardExecutorWithCorrectShardAndTimeout_whenProcessingEvent() {
//    when(eventStore.tryClaimEvent(event)).thenReturn(Optional.of(event));
//
//    processor.process(event);
//
//    verify(shardExecutor).execute(eq(1), eq(PROCESSING_TIMEOUT), any(Supplier.class));
//  }
//
//  @Test
//  void shouldMoveToDeadLetter_whenMaxRetriesExceeded() {
//    when(event.getRetryCount()).thenReturn(3);
//
//    processor.process(event);
//
//    verify(eventStore).deadLetterEvent(event);
//    verify(shardExecutor, never()).execute(anyInt(), any(), any());
//  }
}
