package com.jcondotta.banking.accounts.infrastructure.adapters.input.scheduling.outbox;

import com.jcondotta.banking.accounts.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.accounts.infrastructure.outbox.properties.OutboxPollingProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class OutboxWorkerRunnerTest {

  @Test
  void shouldStopWorker_whenApplicationShutsDown() {
    var dispatcher = mock(OutboxDispatcher.class);
    var runner = new OutboxWorkerRunner(dispatcher, new OutboxPollingProperties(Duration.ofHours(1)));

    runner.run(mock(ApplicationArguments.class));

    verify(dispatcher, timeout(1_000)).dispatch();
    runner.shutdown();
  }
}
