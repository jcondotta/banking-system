package com.jcondotta.banking.infrastructure.adapters.input.rest.filter;

import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.infrastructure.adapters.input.rest.correlation.ScopedCorrelationIdProvider;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter extends OncePerRequestFilter {

    public static final String REQUEST_START_NS_ATTRIBUTE = CorrelationFilter.class.getName() + ".requestStartNs";

    static final String MDC_CORRELATION_ID = LogKey.CORRELATION_ID;

    private final CorrelationIdResolver correlationIdResolver;

    public CorrelationFilter(CorrelationIdResolver correlationIdResolver) {
        this.correlationIdResolver = correlationIdResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        var correlationId = correlationIdResolver.resolve(request);
        response.setHeader(HttpHeadersConstants.CORRELATION_ID, correlationId.toString());

        request.setAttribute(REQUEST_START_NS_ATTRIBUTE, System.nanoTime());
        MDC.put(MDC_CORRELATION_ID, correlationId.toString());
        try {
            doFilterWithScopedCorrelationId(request, response, chain, correlationId);
        }
        finally {
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
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
