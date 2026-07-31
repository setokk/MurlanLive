package org.murlan.um.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("=== Incoming Request ===");
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        Collections.list(request.getHeaderNames())
                .forEach(name -> log.info("{}: {}", name, request.getHeader(name)));

        chain.doFilter(request, response);

        log.info("Response status: {}", response.getStatus());
    }
}