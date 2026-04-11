package com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler;

import java.util.List;

public record FieldMessageError(String field, List<String> messages) {}