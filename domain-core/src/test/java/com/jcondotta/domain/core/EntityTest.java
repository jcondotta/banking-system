package com.jcondotta.domain.core;

import com.jcondotta.domain.testsupport.FakeEntity;
import com.jcondotta.domain.testsupport.FakeId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

  @Test
  void shouldReturnEntityId_whenGetIdIsCalled() {
    FakeId id = FakeId.newId();
    FakeEntity entity = new FakeEntity(id);

    assertThat(entity.getId()).isEqualTo(id);
  }

  @Test
  void shouldBeEqual_whenEntitiesHaveSameId() {
    FakeId id = new FakeId(UUID.randomUUID());

    FakeEntity entity1 = new FakeEntity(id);
    FakeEntity entity2 = new FakeEntity(id);


    assertThat(entity1.equals(entity2)).isTrue();

    assertThat(entity1)
      .isEqualTo(entity2)
      .hasSameHashCodeAs(entity2);
  }

  @Test
  void shouldNotBeEqual_whenEntitiesHaveDifferentIds() {
    FakeEntity entity1 = new FakeEntity(FakeId.newId());
    FakeEntity entity2 = new FakeEntity(FakeId.newId());

    assertThat(entity1).isNotEqualTo(entity2);
  }

  @Test
  void shouldNotBeEqual_whenComparedWithNull() {
    FakeEntity entity = new FakeEntity(FakeId.newId());

    assertThat(entity).isNotEqualTo(null);
  }

  @Test
  void shouldNotBeEqual_whenComparedWithDifferentType() {
    FakeEntity entity = new FakeEntity(FakeId.newId());

    assertThat(entity).isNotEqualTo(new Object());
  }

  @Test
  void shouldHaveConsistentHashCode_whenCalledMultipleTimes() {
    FakeEntity entity = new FakeEntity(new FakeId(UUID.randomUUID()));

    int hash1 = entity.hashCode();
    int hash2 = entity.hashCode();

    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  @SuppressWarnings("all")
  void shouldReturnTrue_whenComparingSameEntityInstance() {
    FakeEntity entity = new FakeEntity(new FakeId(UUID.randomUUID()));
    FakeEntity sameReference = entity;

    assertThat(entity.equals(sameReference)).isTrue();
  }
}