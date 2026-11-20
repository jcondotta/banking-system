package com.jcondotta.banking.infrastructure.adapters.input.rest.filter;

import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdResolverTest {

    private final CorrelationIdResolver resolver = new CorrelationIdResolver();

    @Test
    void shouldReturnCorrelationIdFromHeader_whenHeaderIsValidUUID() {
        var correlationId = UUID.randomUUID();
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());

        assertThat(resolver.resolve(request)).isEqualTo(correlationId);
    }

    @Test
    void shouldGenerateRandomCorrelationId_whenHeaderIsMissing() {
        var request = new MockHttpServletRequest();

        assertThat(resolver.resolve(request)).isNotNull();
    }

    @Test
    void shouldGenerateRandomCorrelationId_whenHeaderIsInvalidUUID() {
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeadersConstants.CORRELATION_ID, "not-a-uuid");

        var result = resolver.resolve(request);

        assertThat(result).isNotNull();
        assertThat(result.toString()).isNotEqualTo("not-a-uuid");
    }
}
