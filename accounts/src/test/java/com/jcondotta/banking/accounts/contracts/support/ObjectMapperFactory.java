package com.jcondotta.banking.accounts.contracts.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

public final class ObjectMapperFactory {

  public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
    .changeDefaultPropertyInclusion(v -> v.withValueInclusion(JsonInclude.Include.NON_NULL))
    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    .disable(
      DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
      DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
    )
    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
//    .addModule(new JavaTimeModule())
//    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .build();

  private ObjectMapperFactory() {}

  public static List<String> fieldNamesOf(JsonNode node) {
    return node.properties()
      .stream()
      .map(Map.Entry::getKey)
      .toList();
  }
}