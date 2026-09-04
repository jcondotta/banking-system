package com.jcondotta.banking.transfers.infrastructure.adapters.input.rest.common.filter;

import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import com.jcondotta.banking.infrastructure.adapters.input.rest.correlation.ScopedCorrelationIdProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ApiVersionStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter extends OncePerRequestFilter {

    public static final String REQUEST_START_NS_ATTRIBUTE = CorrelationFilter.class.getName() + ".requestStartNs";

    static final String MDC_CORRELATION_ID = "correlationId";

    private final ApiVersionStrategy apiVersionStrategy;

    public CorrelationFilter(ApiVersionStrategy apiVersionStrategy) {
        this.apiVersionStrategy = apiVersionStrategy;
    }

    @Override
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

        response.setHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());

        var startNs = System.nanoTime();
        request.setAttribute(REQUEST_START_NS_ATTRIBUTE, startNs);

        MDC.put(MDC_CORRELATION_ID, correlationId.toString());

        try {
            doFilterWithScopedCorrelationId(request, response, chain, correlationId);
            resolvedApiVersion(request).ifPresent(version ->
                response.setHeader(HttpHeadersConstants.RESOLVED_API_VERSION, version));
        }
        catch (ServletException | IOException | RuntimeException ex) {
            throw ex;
        }
        finally {
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }

    public static long durationMs(HttpServletRequest request) {
        var startedAt = request.getAttribute(REQUEST_START_NS_ATTRIBUTE);
        if (startedAt instanceof Long startNs) {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        }
        return 0L;
    }

    private static void doFilterWithScopedCorrelationId(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        UUID correlationId
    ) throws ServletException, IOException {
        try {
            ScopedValue.where(ScopedCorrelationIdProvider.CORRELATION_ID, correlationId)
                .run(() -> {
                    try {
                        chain.doFilter(request, response);
                    }
                    catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                    catch (ServletException ex) {
                        throw new FilterChainServletException(ex);
                    }
                });
        }
        catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
        catch (FilterChainServletException ex) {
            throw ex.getCause();
        }
    }

    private Optional<String> resolvedApiVersion(HttpServletRequest request) {
        if (apiVersionStrategy == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(apiVersionStrategy.resolveVersion(request))
            .or(() -> Optional.ofNullable(apiVersionStrategy.getDefaultVersion()).map(String::valueOf));
    }

    private static final class FilterChainServletException extends RuntimeException {

        private FilterChainServletException(ServletException cause) {
            super(cause);
        }

        @Override
        public ServletException getCause() {
            return (ServletException) super.getCause();
        }
    }
}
