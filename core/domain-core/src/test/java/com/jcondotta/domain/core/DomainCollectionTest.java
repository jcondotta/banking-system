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
  void shouldReturnAllElements_whenStreamingCollection() {
    var item1 = createItem();
    var item2 = createItem();

    var collection = createCollection(List.of(item1, item2));

    assertThat(collection.stream())
      .containsExactly(item1, item2);
  }

  @Test
  void shouldIterateOverAllElements_whenUsingIterator() {
    var item1 = createItem();
    var item2 = createItem();

    var collection = createCollection(List.of(item1, item2));

    assertThat(collection)
      .containsExactly(item1, item2);
  }

  @Test
  void shouldThrowException_whenModifyingReturnedValuesList() {
    var item = createItem();
    var collection = createCollection(List.of(item));

    var values = collection.values();

    assertThatThrownBy(() -> values.add(createItem()))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldCreateDefensiveCopy_whenConstructedWithExternalList() {
    var item = createItem();

    var external = new ArrayList<FakeEntity>();
    external.add(item);

    var collection = createCollection(external);

    external.clear();

    assertThat(collection.values())
      .containsExactly(item);
  }

  @Test
  void shouldThrowException_whenRemovingElementViaIterator() {
    var item = createItem();
    var collection = createCollection(List.of(item));

    var iterator = collection.iterator();
    iterator.next();

    assertThatThrownBy(iterator::remove)
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldAddElement_whenUsingInternalMutationMethod() {
    var collection = new FakeCollection(List.of());

    var item = createItem();

    collection.add(item);

    assertThat(collection.values())
      .containsExactly(item);
  }

  @Test
  void shouldNotChangeInternalState_whenModifyingReturnedList() {
    var item = createItem();
    var collection = createCollection(List.of(item));

    var values = collection.values();

    assertThatThrownBy(values::clear)
      .isInstanceOf(UnsupportedOperationException.class);

    assertThat(collection.values())
      .containsExactly(item);
  }

  @Test
  void shouldReturnNewListInstance_whenAccessingValuesMultipleTimes() {
    var item = createItem();
    var collection = createCollection(List.of(item));

    var values1 = collection.values();
    var values2 = collection.values();

    assertThat(values1).isNotSameAs(values2);
  }
}