package com.jcondotta.domain.core.repository;

import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.identity.AggregateId;

import java.util.Optional;

public interface AggregateRepository<A extends AggregateRoot<ID>, ID extends AggregateId<?>> {

  Optional<A> findById(ID id);

  void save(A aggregate);
}