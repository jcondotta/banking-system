package com.jcondotta.domain.core;

import com.jcondotta.domain.testsupport.FakeCollection;
import com.jcondotta.domain.testsupport.FakeEntity;
import com.jcondotta.domain.testsupport.FakeId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainCollectionTest {

  private FakeCollection createCollection(List<FakeEntity> items) {
    return new FakeCollection(items);
  }

  private FakeEntity createItem() {
    return new FakeEntity(FakeId.newId());
  }

  @Test
  void shouldReturnStreamWithAllElements_whenCallingStream() {
    var item1 = createItem();
    var item2 = createItem();

    var collection = createCollection(List.of(item1, item2));

    var result = collection.stream().toList();

    assertThat(result)
      .containsExactly(item1, item2);
  }

  @Test
  void shouldReturnEmptyStream_whenCollectionIsEmpty() {
    var collection = createCollection(List.of());

    assertThat(collection.stream())
      .isEmpty();
  }

  @Test
  void shouldIterateOverAllElements() {
    var item1 = createItem();
    var item2 = createItem();

    var collection = createCollection(List.of(item1, item2));

    assertThat(collection)
      .containsExactly(item1, item2);
  }

  @Test
  void shouldThrowException_whenModifyingImmutableValuesList() {
    var item = createItem();
    var collection = createCollection(List.of(item));

    var values = collection.values();

    assertThatThrownBy(() -> values.add(item))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldCreateDefensiveCopy_whenCollectionIsCreatedFromExternalList() {
    var item = createItem();

    var list = new ArrayList<FakeEntity>();
    list.add(item);

    var collection = createCollection(list);

    list.clear();

    assertThat(collection.values())
      .containsExactly(item);
  }

  @Test
  void shouldThrowException_whenRemovingElementUsingIterator() {
    var item = createItem();
    var collection = createCollection(List.of(item));

    var iterator = collection.iterator();

    iterator.next();

    assertThatThrownBy(iterator::remove)
      .isInstanceOf(UnsupportedOperationException.class);
  }
}