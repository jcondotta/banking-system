package com.jcondotta.application.query;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record PageResult<T>(
  List<T> content,
  int page,
  int size,
  long totalElements,
  int totalPages
) {

  public static final String CONTENT_REQUIRED = "content must be provided";

  public PageResult {
    requireNonNull(content, CONTENT_REQUIRED);
    if (page < 0) throw new IllegalArgumentException("page must not be negative");
    if (size <= 0) throw new IllegalArgumentException("size must be positive");
    if (totalPages < 0) throw new IllegalArgumentException("totalPages must not be negative");
    if (totalElements == 0 && totalPages > 0)
      throw new IllegalArgumentException("totalPages must be 0 when totalElements is 0");
    content = List.copyOf(content);
  }

  public boolean hasNext() {
    return page + 1 < totalPages;
  }

  public boolean hasPrevious() {
    return page > 0;
  }
}
