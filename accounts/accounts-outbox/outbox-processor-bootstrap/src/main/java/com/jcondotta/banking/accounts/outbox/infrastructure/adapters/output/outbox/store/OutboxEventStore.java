package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxQuery;

import java.util.List;
import java.util.Optional;

public interface OutboxEventStore {

    List<OutboxEntity> findPendingEvents(OutboxQuery query);

    Optional<OutboxEntity> tryClaimEvent(OutboxEntity event);

    void deletePublishedEvent(OutboxEntity item);

    void deadLetterEvent(OutboxEntity event);
}
