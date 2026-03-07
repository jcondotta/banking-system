package com.jcondotta.domain.testsupport;

import com.jcondotta.domain.core.DomainCollection;

import java.util.Collection;

public class FakeCollection extends DomainCollection<FakeEntity> {

  public FakeCollection(Collection<FakeEntity> values) {
    super(values);
  }
}
