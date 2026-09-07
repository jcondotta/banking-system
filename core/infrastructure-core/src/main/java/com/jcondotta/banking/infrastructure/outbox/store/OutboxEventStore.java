package com.jcondotta.banking.infrastructure.outbox.store;

import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;

import java.util.List;
import java.util.Optional;

public interface OutboxEventStore<T extends OutboxRecord> {

  List<T> findPendingEvents(OutboxQuery query);

  /**
   * Attempts to claim {@code event} for processing via optimistic locking.
   *
   * <p>Returns the updated event (with incremented attempt count and extended visibility
   * deadline) if the claim succeeded, or empty if the event was already claimed by
   * another worker instance before this call could update it.
   */
  Optional<T> tryClaimEvent(T event);

  void deletePublishedEvent(T event);

  void deadLetterEvent(T event);
}
