package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.scheduler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

//  @Mock
//  private VirtualThreadOutboxProcessor processor;
//
//  private OutboxScheduler scheduler;
//
//  @BeforeEach
//  void setUp() {
//    scheduler = new OutboxScheduler(processor);
//  }
//
//  @Test
//  void shouldDelegateToProcessor_whenOutboxProcessingIsTriggered() {
//    scheduler.processOutbox();
//
//    verify(processor).process();
//  }
//
//  @Test
//  void shouldInvokeProcessorExactlyOnce_whenOutboxProcessingIsTriggered() {
//    scheduler.processOutbox();
//
//    verify(processor, times(1)).process();
//    verifyNoMoreInteractions(processor);
//  }
//
//  @Test
//  void shouldPropagateException_whenProcessorThrows() {
//    var cause = new RuntimeException("processor failure");
//    doThrow(cause).when(processor).process();
//
//    org.assertj.core.api.Assertions.assertThatThrownBy(() -> scheduler.processOutbox())
//      .isSameAs(cause);
//  }
}
