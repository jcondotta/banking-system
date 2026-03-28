package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.filter;

import com.jcondotta.banking.accounts.infrastructure.ThreadLocalCorrelationIdProvider;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.HttpHeadersConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationFilter extends OncePerRequestFilter {

  @Override
  @SuppressWarnings("all")
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {

    UUID correlationId;

    try {
      correlationId = Optional.ofNullable(request.getHeader(HttpHeadersConstants.CORRELATION_ID))
        .map(UUID::fromString)
        .orElse(UUID.randomUUID());
    }
    catch (IllegalArgumentException ex) {
      correlationId = UUID.randomUUID();
    }

    ThreadLocalCorrelationIdProvider.set(correlationId);
    response.setHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());

    try {
      chain.doFilter(request, response);
    }
    finally {
      ThreadLocalCorrelationIdProvider.clear();
    }
  }
}