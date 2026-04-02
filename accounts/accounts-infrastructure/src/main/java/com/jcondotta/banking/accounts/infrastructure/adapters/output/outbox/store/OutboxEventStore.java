package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;

import java.util.List;

public interface OutboxEventStore {

    List<OutboxEntity> findPendingEvents(OutboxQuery query);
    boolean tryClaimEvent(OutboxEntity event);
    void markEventAsPublished(OutboxEntity item);
}