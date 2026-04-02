package com.jcondotta.banking.accounts.infrastructure.support;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.Iterator;
import java.util.List;

public final class DynamoPageIterable {

  private DynamoPageIterable() {}

  @SuppressWarnings("all")
  public static <T> PageIterable<T> pageOf(List<T> items) {
    return new PageIterable<>() {

      @Override
      public SdkIterable<T> items() {
        return items::iterator;
      }

      @Override
      public Iterator<Page<T>> iterator() {
        return List.of(Page.create(items)).iterator();
      }
    };
  }

  public static <T> PageIterable<T> emptyPage() {
    return pageOf(List.of());
  }
}