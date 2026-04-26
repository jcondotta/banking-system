package com.jcondotta.domain.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class DomainCollection<T> implements Iterable<T> {

  private final List<T> values;

  protected DomainCollection(Collection<T> values) {
    this.values = new ArrayList<>(values);
  }

  public List<T> values() {
    return List.copyOf(values);
  }

  protected void add(T value) {
    values.add(value);
  }

  public Stream<T> stream() {
    return values.stream();
  }

  @Override
  @SuppressWarnings("all")
  public Iterator<T> iterator() {
    return values().iterator();
  }
}