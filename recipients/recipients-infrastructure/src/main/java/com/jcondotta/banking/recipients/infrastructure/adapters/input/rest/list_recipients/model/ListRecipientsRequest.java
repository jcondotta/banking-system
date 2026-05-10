package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ListRecipientsRequest(
    @Min(0) Integer page,
    @Min(1) @Max(100) Integer size,
    @Size(max = 50) String name
) {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;

  public ListRecipientsRequest {
    page = (page != null) ? page : DEFAULT_PAGE;
    size = (size != null) ? size : DEFAULT_SIZE;
  }
}
