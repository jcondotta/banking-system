package com.jcondotta.application.query;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageRequestTest {

  private static final int ZERO = 0;

  @Test
  void shouldCreatePageRequest_whenValuesAreValid() {
    var pageRequest = new PageRequest(ZERO, 20);

    assertThat(pageRequest.page()).isZero();
    assertThat(pageRequest.size()).isEqualTo(20);
  }

  @Test
  void shouldThrowException_whenPageIsNegative() {
    assertThatThrownBy(() -> new PageRequest(-1, 20))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(PageRequest.PAGE_MUST_NOT_BE_NEGATIVE);
  }

  @Test
  void shouldThrowException_whenSizeIsZero() {
    assertThatThrownBy(() -> new PageRequest(ZERO, ZERO))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(PageRequest.SIZE_MUST_BE_POSITIVE);
  }

  @Test
  void shouldThrowException_whenSizeExceedsMaximum() {
    assertThatThrownBy(() -> new PageRequest(ZERO, PageRequest.MAX_SIZE + 1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(PageRequest.SIZE_MUST_NOT_EXCEED_MAXIMUM);
  }
}
