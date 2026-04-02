package com.jcondotta.domain.identity;

public interface EntityId<T> {
  T value();

  default String asString() {
    return value().toString();
  }
}
