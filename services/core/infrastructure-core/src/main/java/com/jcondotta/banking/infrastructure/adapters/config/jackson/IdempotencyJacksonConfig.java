package com.jcondotta.banking.infrastructure.adapters.config.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration(proxyBeanMethods = false)
public class IdempotencyJacksonConfig {

    // Keep the HTTP mapper auto-configuration and unqualified injection independent.
    @Bean(defaultCandidate = false)
    public JsonMapper idempotencyJsonMapper() {
        return JsonMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.ALWAYS))
            .build();
    }
}
