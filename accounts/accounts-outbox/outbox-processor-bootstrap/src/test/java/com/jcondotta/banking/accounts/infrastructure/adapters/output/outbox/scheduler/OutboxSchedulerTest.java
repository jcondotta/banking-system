package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.scheduler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

// Moved from accounts-infrastructure.
//
// Tests remain commented because the OutboxScheduler and VirtualThreadOutboxProcessor classes
// no longer exist. The scheduling role is now handled by OutboxWorkerRunner and the
// dispatching role by VirtualThreadOutboxDispatcher.

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

//  @Mock
//  private OutboxDispatcher dispatcher;
//
//  private OutboxWorkerRunner workerRunner;
//
//  @BeforeEach
//  void setUp() {
//    workerRunner = new OutboxWorkerRunner(dispatcher, mock(OutboxPollingProperties.class));
//  }
//
//  @Test
//  void shouldDelegateToDispatcher_whenOutboxProcessingIsTriggered() {
//    dispatcher.dispatch();
//
//    verify(dispatcher).dispatch();
//  }
//
//  @Test
//  void shouldInvokeDispatcherExactlyOnce_whenOutboxProcessingIsTriggered() {
//    dispatcher.dispatch();
//
//    verify(dispatcher, times(1)).dispatch();
//    verifyNoMoreInteractions(dispatcher);
//  }
//
//  @Test
//  void shouldPropagateException_whenDispatcherThrows() {
//    var cause = new RuntimeException("dispatcher failure");
//    doThrow(cause).when(dispatcher).dispatch();
//
//    org.assertj.core.api.Assertions.assertThatThrownBy(() -> dispatcher.dispatch())
//      .isSameAs(cause);
//  }
}
