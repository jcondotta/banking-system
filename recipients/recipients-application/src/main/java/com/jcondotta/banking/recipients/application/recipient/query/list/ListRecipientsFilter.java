package com.jcondotta.banking.recipients.application.recipient.query.list;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public record ListRecipientsFilter(Optional<String> name) {

  static final String NAME_OPTIONAL_REQUIRED = "name Optional container must not be null";
  static final String NAME_VALUE_REQUIRED    = "name value must not be null";

  public ListRecipientsFilter {
    requireNonNull(name, NAME_OPTIONAL_REQUIRED);
  }

  public static ListRecipientsFilter none() {
    return new ListRecipientsFilter(Optional.empty());
  }

  public static ListRecipientsFilter byName(String name) {
    return new ListRecipientsFilter(Optional.of(requireNonNull(name, NAME_VALUE_REQUIRED)));
  }

  public boolean hasName() {
    return name.isPresent();
  }
}
