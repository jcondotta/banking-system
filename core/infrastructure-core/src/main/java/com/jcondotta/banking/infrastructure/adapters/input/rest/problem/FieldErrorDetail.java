package com.jcondotta.banking.infrastructure.adapters.input.rest.problem;

import java.util.List;

public record FieldErrorDetail(String field, List<String> messages) {}
