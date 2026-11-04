package com.jcondotta.application.idempotency;

import java.util.Optional;

public interface IdempotencyRepository<R> {

  Optional<R> find(IdempotencyKey key);
}
