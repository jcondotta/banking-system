package com.jcondotta.domain.testsupport;

import com.jcondotta.domain.core.Entity;

public class FakeEntity extends Entity<FakeId> {
  public FakeEntity(FakeId id) {
    super(id);
  }
}