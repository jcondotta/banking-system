package com.jcondotta.domain.core;

import com.jcondotta.domain.identity.EntityId;
import com.jcondotta.domain.support.DomainPreconditions;

public abstract class Entity<ID extends EntityId<?>> {

  protected static final String ID_NOT_PROVIDED = "id must be provided.";

  private final ID id;

  protected Entity(ID id) {
    this.id = DomainPreconditions.required(id, ID_NOT_PROVIDED);
  }

  public ID getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Entity<?> entity = (Entity<?>) o;
    return id.equals(entity.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}