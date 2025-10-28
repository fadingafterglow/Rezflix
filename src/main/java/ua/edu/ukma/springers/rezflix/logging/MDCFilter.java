package ua.edu.ukma.springers.rezflix.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MDCFilter extends OncePerRequestFilter {

    private final SecurityUtils securityUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        MDC.put("remoteAddr", request.getRemoteAddr());
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("userId", String.valueOf(securityUtils.getCurrentUserId()));
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            MDC.clear();
        }
    }
}
