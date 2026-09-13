package com.jcondotta.banking.infrastructure.adapters.input.rest.filter;

import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.accept.ApiVersionStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiVersionResponseFilterTest {

    private static final String API_VERSION = "1.0";

    private final ApiVersionStrategy apiVersionStrategy = mock(ApiVersionStrategy.class);
    private final ApiVersionResponseFilter filter = new ApiVersionResponseFilter(apiVersionStrategy);

    @Test
    void shouldSetResolvedApiVersionHeader_whenVersionIsResolved() throws Exception {
        var request = request("/api/v1/bank-accounts");
        var response = new MockHttpServletResponse();
        when(apiVersionStrategy.resolveVersion(request)).thenReturn(API_VERSION);

        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getHeader(HttpHeadersConstants.RESOLVED_API_VERSION)).isEqualTo(API_VERSION);
    }

    @Test
    void shouldUseDefaultApiVersion_whenResolvedVersionIsMissing() throws Exception {
        var request = request("/api/v1/bank-accounts");
        var response = new MockHttpServletResponse();
        when(apiVersionStrategy.resolveVersion(request)).thenReturn(null);
        doReturn(API_VERSION).when(apiVersionStrategy).getDefaultVersion();

        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getHeader(HttpHeadersConstants.RESOLVED_API_VERSION)).isEqualTo(API_VERSION);
    }

    @Test
    void shouldNotFilterActuatorRequests() {
        var request = request("/actuator/health");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    private static MockHttpServletRequest request(String uri) {
        var request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }
}
