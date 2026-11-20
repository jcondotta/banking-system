package com.jcondotta.application.idempotency.repository;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;

import java.util.Optional;

public interface IdempotencyRepository<R> {

  Optional<R> find(IdempotencyKey key);
}
