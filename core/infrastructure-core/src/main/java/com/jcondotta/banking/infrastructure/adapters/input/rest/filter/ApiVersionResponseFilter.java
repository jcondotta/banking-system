package com.jcondotta.banking.infrastructure.adapters.input.rest.filter;

import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ApiVersionStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@ConditionalOnBean(ApiVersionStrategy.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class ApiVersionResponseFilter extends OncePerRequestFilter {

    private final ApiVersionStrategy apiVersionStrategy;

    ApiVersionResponseFilter(ApiVersionStrategy apiVersionStrategy) {
        this.apiVersionStrategy = apiVersionStrategy;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        chain.doFilter(request, response);
        resolvedApiVersion(request).ifPresent(version ->
            response.setHeader(HttpHeadersConstants.RESOLVED_API_VERSION, version));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }

    private Optional<String> resolvedApiVersion(HttpServletRequest request) {
        if (apiVersionStrategy == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(apiVersionStrategy.resolveVersion(request))
            .or(() -> Optional.ofNullable(apiVersionStrategy.getDefaultVersion()).map(String::valueOf));
    }
}
