package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.filter;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.infrastructure.adapters.output.rest.ScopedCorrelationIdProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationFilter extends OncePerRequestFilter {

  public static final String REQUEST_START_NS_ATTRIBUTE = CorrelationFilter.class.getName() + ".requestStartNs";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

    UUID correlationId;

    try {
      correlationId = Optional.ofNullable(request.getHeader(HttpHeadersConstants.CORRELATION_ID))
        .map(UUID::fromString)
        .orElse(UUID.randomUUID());
    }
    catch (IllegalArgumentException ex) {
      correlationId = UUID.randomUUID();
    }

    response.setHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());

    MDC.put("correlationId", correlationId.toString());
    request.setAttribute(REQUEST_START_NS_ATTRIBUTE, System.nanoTime());

    try {
      ScopedValue.where(ScopedCorrelationIdProvider.CORRELATION_ID, correlationId)
        .run(() -> {
          try {
            chain.doFilter(request, response);
          }
          catch (IOException | ServletException e) {
            throw new RuntimeException(e);
          }
        });
    }
    finally {
      MDC.remove("correlationId");
    }
  }

  public static long durationMs(HttpServletRequest request) {
    var startedAt = request.getAttribute(REQUEST_START_NS_ATTRIBUTE);
    if (startedAt instanceof Long startNs) {
      return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
    }

    return 0L;
  }
}
