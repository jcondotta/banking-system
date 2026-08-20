package com.jcondotta.banking.recipients.application.recipient.query.list;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListRecipientsFilterTest {

  @Test
  void shouldCreateEmptyFilter_whenNoFilterIsProvided() {
    var filter = ListRecipientsFilter.none();

    assertThat(filter.name()).isEmpty();
    assertThat(filter.hasName()).isFalse();
  }

  @Test
  void shouldCreateNameFilter_whenNameIsProvided() {
    var filter = ListRecipientsFilter.byName("jef");

    assertThat(filter.name()).contains("jef");
    assertThat(filter.hasName()).isTrue();
  }

  @Test
  void shouldCreateFilter_whenOptionalNameIsProvided() {
    var filter = new ListRecipientsFilter(Optional.of("jef"));

    assertThat(filter.name()).contains("jef");
    assertThat(filter.hasName()).isTrue();
  }

  @Test
  void shouldThrowException_whenOptionalContainerIsNull() {
    assertThatThrownBy(() -> new ListRecipientsFilter(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsFilter.NAME_OPTIONAL_REQUIRED);
  }

  @Test
  void shouldThrowException_whenNameValueIsNull() {
    assertThatThrownBy(() -> ListRecipientsFilter.byName(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsFilter.NAME_VALUE_REQUIRED);
  }
}
