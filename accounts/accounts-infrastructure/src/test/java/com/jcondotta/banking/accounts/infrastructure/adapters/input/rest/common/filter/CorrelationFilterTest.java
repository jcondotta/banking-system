package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.filter;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.infrastructure.adapters.output.rest.ScopedCorrelationIdProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.accept.ApiVersionStrategy;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CorrelationFilterTest {

  private static final String API_VERSION = "1.0";

  private final ApiVersionStrategy apiVersionStrategy = mock(ApiVersionStrategy.class);
  private final CorrelationFilter filter = new CorrelationFilter(apiVersionStrategy);

  @Test
  void shouldUseCorrelationIdFromHeader_whenHeaderIsValid() throws Exception {
    var correlationId = UUID.randomUUID();
    var request = request("/api/v1/bank-accounts");
    var response = new MockHttpServletResponse();
    var scopedCorrelationId = new AtomicReference<UUID>();

    request.addHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());
    when(apiVersionStrategy.resolveVersion(request)).thenReturn(API_VERSION);

    filter.doFilterInternal(request, response, (servletRequest, servletResponse) ->
      scopedCorrelationId.set(ScopedCorrelationIdProvider.CORRELATION_ID.get()));

    assertThat(response.getHeader(HttpHeadersConstants.CORRELATION_ID)).isEqualTo(correlationId.toString());
    assertThat(response.getHeader(HttpHeadersConstants.RESOLVED_API_VERSION)).isEqualTo(API_VERSION);
    assertThat(scopedCorrelationId).hasValue(correlationId);
    assertThat(request.getAttribute(CorrelationFilter.REQUEST_START_NS_ATTRIBUTE)).isInstanceOf(Long.class);
    assertThat(MDC.get(CorrelationFilter.MDC_CORRELATION_ID)).isNull();
  }

  @Test
  void shouldGenerateCorrelationId_whenHeaderIsMissing() throws Exception {
    var request = request("/api/v1/bank-accounts");
    var response = new MockHttpServletResponse();

    when(apiVersionStrategy.resolveVersion(request)).thenReturn(API_VERSION);

    filter.doFilterInternal(request, response, new MockFilterChain());

    assertThat(response.getHeader(HttpHeadersConstants.CORRELATION_ID))
      .isNotNull()
      .satisfies(value -> assertThat(UUID.fromString(value)).isNotNull());
  }

  @Test
  void shouldGenerateCorrelationId_whenHeaderIsInvalid() throws Exception {
    var request = request("/api/v1/bank-accounts");
    var response = new MockHttpServletResponse();

    request.addHeader(HttpHeadersConstants.CORRELATION_ID, "not-a-uuid");
    when(apiVersionStrategy.resolveVersion(request)).thenReturn(API_VERSION);

    filter.doFilterInternal(request, response, new MockFilterChain());

    assertThat(response.getHeader(HttpHeadersConstants.CORRELATION_ID))
      .isNotEqualTo("not-a-uuid")
      .satisfies(value -> assertThat(UUID.fromString(value)).isNotNull());
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

  @Test
  void shouldPropagateServletException_andClearMdc() {
    var request = request("/api/v1/bank-accounts");
    var response = new MockHttpServletResponse();
    var exception = new ServletException("filter failed");

    assertThatThrownBy(() -> filter.doFilterInternal(request, response, new FailingServletFilterChain(exception)))
      .isSameAs(exception);

    assertThat(MDC.get(CorrelationFilter.MDC_CORRELATION_ID)).isNull();
  }

  @Test
  void shouldPropagateIOException_andClearMdc() {
    var request = request("/api/v1/bank-accounts");
    var response = new MockHttpServletResponse();
    var exception = new IOException("filter failed");

    assertThatThrownBy(() -> filter.doFilterInternal(request, response, new FailingIoFilterChain(exception)))
      .isSameAs(exception);

    assertThat(MDC.get(CorrelationFilter.MDC_CORRELATION_ID)).isNull();
  }

  private static MockHttpServletRequest request(String uri) {
    var request = new MockHttpServletRequest();
    request.setRequestURI(uri);
    request.setMethod("POST");
    return request;
  }

  private static final class FailingServletFilterChain implements FilterChain {

    private final ServletException exception;

    private FailingServletFilterChain(ServletException exception) {
      this.exception = exception;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response)
      throws ServletException {
      throw exception;
    }
  }

  private static final class FailingIoFilterChain implements FilterChain {

    private final IOException exception;

    private FailingIoFilterChain(IOException exception) {
      this.exception = exception;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response)
      throws IOException {
      throw exception;
    }
  }
}
