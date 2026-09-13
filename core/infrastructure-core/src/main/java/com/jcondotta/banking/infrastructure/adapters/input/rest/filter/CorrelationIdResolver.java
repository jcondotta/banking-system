package com.jcondotta.banking.infrastructure.adapters.input.rest.filter;

import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class CorrelationIdResolver {

    UUID resolve(HttpServletRequest request) {
        try {
            return Optional.ofNullable(request.getHeader(HttpHeadersConstants.CORRELATION_ID))
                .map(UUID::fromString)
                .orElse(UUID.randomUUID());
        }
        catch (IllegalArgumentException ex) {
            return UUID.randomUUID();
        }
    }
}
