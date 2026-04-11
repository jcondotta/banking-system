package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxMetrics {

  private final MeterRegistry meterRegistry;

  public void incrementClaimSkipped(int shard) {
    meterRegistry.counter("outbox.claim.skipped", "shard", String.valueOf(shard)).increment();
  }

  public void incrementPublishSuccess(int shard) {
    meterRegistry.counter("outbox.publish.success", "shard", String.valueOf(shard)).increment();
  }

  public void incrementPublishFailure(int shard) {
    meterRegistry.counter("outbox.publish.failure", "shard", String.valueOf(shard)).increment();
  }

  public Timer.Sample startTimer() {
    return Timer.start(meterRegistry);
  }

  public void stopShardWaitTimer(Timer.Sample sample, int shard) {
    sample.stop(meterRegistry.timer("outbox.shard.wait.duration", "shard", String.valueOf(shard)));
  }

  public void stopPublishTimer(Timer.Sample sample, int shard) {
    sample.stop(meterRegistry.timer("outbox.publish.duration", "shard", String.valueOf(shard)));
  }

  public void incrementSemaphoreTimeout(int shard) {
    meterRegistry.counter("outbox.shard.semaphore.timeout", "shard", String.valueOf(shard)).increment();
  }

  public void incrementDeleteFailure(int shard) {
    meterRegistry.counter("outbox.delete.failure", "shard", String.valueOf(shard)).increment();
  }

  public void incrementDeadLetterEvent(int shard) {
    meterRegistry.counter("outbox.dead.letter", "shard", String.valueOf(shard)).increment();
  }
}
