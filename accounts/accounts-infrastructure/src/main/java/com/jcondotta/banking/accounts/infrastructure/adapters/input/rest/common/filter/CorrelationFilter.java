package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.filter;

import com.jcondotta.banking.accounts.infrastructure.ScopedCorrelationIdProvider;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.HttpHeadersConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationFilter extends OncePerRequestFilter {


  @Override
  protected void doFilterInternal(
    HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) {

    UUID correlationId;

    try {
      correlationId = Optional.ofNullable(request.getHeader(HttpHeadersConstants.CORRELATION_ID))
        .map(UUID::fromString)
        .orElse(UUID.randomUUID());
    } catch (IllegalArgumentException ex) {
      correlationId = UUID.randomUUID();
    }

    response.setHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());

    ScopedValue.where(ScopedCorrelationIdProvider.CORRELATION_ID, correlationId)
      .run(() -> {
        try {
          chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
          throw new RuntimeException(e);
        }
      });
  }
}