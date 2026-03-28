package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;

import java.util.List;

public interface OutboxRepository {

    List<OutboxEntity> findEvents(OutboxQuery query);
    boolean tryMarkAsProcessing(OutboxEntity event);
    void markAsPublished(OutboxEntity item);
}