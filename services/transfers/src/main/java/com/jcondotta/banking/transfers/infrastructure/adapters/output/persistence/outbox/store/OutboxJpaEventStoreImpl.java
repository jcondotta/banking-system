package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.store;

import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxEventAlreadyProcessedException;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxEventStore;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxQuery;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntity;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxJpaEventStoreImpl implements OutboxEventStore<OutboxJpaEntity> {

  private final OutboxJpaEntityRepository repository;
  private final OutboxProperties outboxProperties;

  @Override
  @Transactional(readOnly = true)
  public List<OutboxJpaEntity> findPendingEvents(OutboxQuery query) {
    return repository.findPendingByShard(query.shard(), Instant.now(), PageRequest.of(0, query.limit()));
  }

  @Override
  @Transactional
  public Optional<OutboxJpaEntity> tryClaimEvent(OutboxJpaEntity event) {
    var now = Instant.now();
    var newVisibility = now.plus(outboxProperties.worker().processing().claimTimeout());

    int updated = repository.tryClaimById(event.getEventId(), now, newVisibility);
    if (updated == 0) {
      return Optional.empty();
    }

    return Optional.of(event.toBuilder()
      .nextAttemptAt(newVisibility)
      .attemptCount(event.getAttemptCount() + 1)
      .build());
  }

  @Override
  @Transactional
  public void deletePublishedEvent(OutboxJpaEntity event) {
    int deleted = repository.deleteByEventIdAndNextAttemptAt(event.getEventId(), event.getNextAttemptAt());
    if (deleted == 0) {
      throw new OutboxEventAlreadyProcessedException(event.getEventId());
    }
  }

  @Override
  @Transactional
  public void deadLetterEvent(OutboxJpaEntity event) {
    repository.deleteById(event.getEventId());
  }
}
