package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.infrastructure.adapters.output.rest.ScopedCorrelationIdProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrelationFilterTest {

  private ListAppender<ILoggingEvent> logAppender;
  private CorrelationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new CorrelationFilter();

    var logger = (Logger) LoggerFactory.getLogger(CorrelationFilter.class);
    logAppender = new ListAppender<>();
    logAppender.start();
    logger.addAppender(logAppender);
  }

  @AfterEach
  void tearDown() {
    var logger = (Logger) LoggerFactory.getLogger(CorrelationFilter.class);
    logger.detachAppender(logAppender);
    logAppender.stop();
    MDC.clear();
  }

  @Test
  void shouldPopulateMdcAndEmitTransportLog_whenRequestCompletesSuccessfully() throws Exception {
    var correlationId = UUID.randomUUID();
    var request = request("POST", "/api/bank-accounts/123/recipients");
    request.addHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());
    var response = new MockHttpServletResponse();

    filter.doFilter(request, response, (req, res) -> {
      assertThat(MDC.get(CorrelationFilter.MDC_CORRELATION_ID)).isEqualTo(correlationId.toString());
      assertThat(ScopedCorrelationIdProvider.CORRELATION_ID.get()).isEqualTo(correlationId);

      ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_CREATED);
    });

    assertThat(response.getHeader(HttpHeadersConstants.CORRELATION_ID)).isEqualTo(correlationId.toString());
    assertThat(request.getAttribute(CorrelationFilter.REQUEST_START_NS_ATTRIBUTE)).isInstanceOf(Long.class);
    assertThat(MDC.get(CorrelationFilter.MDC_CORRELATION_ID)).isNull();

    var event = logAppender.list.getLast();
    assertThat(event.getLevel()).isEqualTo(Level.INFO);
    assertThat(event.getFormattedMessage()).isEqualTo("HTTP request completed");
    assertThat(keyValues(event))
      .containsEntry("event_type", "http.request")
      .containsEntry("outcome", "success")
      .containsEntry("method", "POST")
      .containsEntry("path", "/api/bank-accounts/123/recipients")
      .containsEntry("http_status", "201");
  }

  @Test
  void shouldGenerateCorrelationIdAndMarkTransportFailure_whenRequestCompletesWithClientError() throws Exception {
    var request = request("GET", "/api/bank-accounts/123/recipients");
    var response = new MockHttpServletResponse();

    filter.doFilter(request, response, (req, res) -> {
      var generatedCorrelationId = MDC.get(CorrelationFilter.MDC_CORRELATION_ID);

      assertThat(generatedCorrelationId).isNotBlank();
      assertThatCodeIsUuid(generatedCorrelationId);
      assertThat(ScopedCorrelationIdProvider.CORRELATION_ID.get()).isEqualTo(UUID.fromString(generatedCorrelationId));

      ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_CONFLICT);
    });

    assertThatCodeIsUuid(response.getHeader(HttpHeadersConstants.CORRELATION_ID));

    var event = logAppender.list.getLast();
    assertThat(event.getLevel()).isEqualTo(Level.INFO);
    assertThat(keyValues(event))
      .containsEntry("event_type", "http.request")
      .containsEntry("outcome", "failure")
      .containsEntry("method", "GET")
      .containsEntry("path", "/api/bank-accounts/123/recipients")
      .containsEntry("http_status", "409");
  }

  @Test
  void shouldEmitErrorTransportLog_whenExceptionEscapesFilterChain() {
    var request = request("DELETE", "/api/bank-accounts/123/recipients/456");
    var response = new MockHttpServletResponse();
    var exception = new ServletException("connection reset");

    assertThatThrownBy(() -> filter.doFilter(request, response, (req, res) -> {
      throw exception;
    }))
      .isSameAs(exception);

    assertThat(MDC.get(CorrelationFilter.MDC_CORRELATION_ID)).isNull();

    var event = logAppender.list.getLast();
    assertThat(event.getLevel()).isEqualTo(Level.ERROR);
    assertThat(event.getFormattedMessage()).isEqualTo("HTTP request failed before response completion");
    assertThat(event.getThrowableProxy()).isNotNull();
    assertThat(keyValues(event))
      .containsEntry("event_type", "http.request")
      .containsEntry("outcome", "failure")
      .containsEntry("method", "DELETE")
      .containsEntry("path", "/api/bank-accounts/123/recipients/456")
      .containsEntry("http_status", "500");
  }

  @Test
  void shouldSkipActuatorRequests() throws Exception {
    var request = request("GET", "/actuator/health");
    var response = new MockHttpServletResponse();

    filter.doFilter(request, response, (req, res) -> ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_OK));

    assertThat(response.getHeader(HttpHeadersConstants.CORRELATION_ID)).isNull();
    assertThat(logAppender.list).isEmpty();
    assertThat(MDC.getCopyOfContextMap()).isNull();
  }

  private static MockHttpServletRequest request(String method, String uri) {
    var request = new MockHttpServletRequest();
    request.setMethod(method);
    request.setRequestURI(uri);
    return request;
  }

  private static Map<String, String> keyValues(ILoggingEvent event) {
    List<org.slf4j.event.KeyValuePair> pairs = event.getKeyValuePairs();
    if (pairs == null) {
      return Map.of();
    }

    return pairs.stream()
      .collect(toMap(
        pair -> pair.key,
        pair -> String.valueOf(pair.value),
        (left, right) -> right
      ));
  }

  private static void assertThatCodeIsUuid(String value) {
    assertThat(value).isNotBlank();
    assertThat(UUID.fromString(value)).isNotNull();
  }
}
