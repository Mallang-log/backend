package com.mallang.common.log;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.cors.CorsUtils;

@Slf4j
public class RequestLoggingFilter implements Filter {

    private final Set<String> ignoredUrls = new HashSet<>();

    public RequestLoggingFilter(String... ignoredUrls) {
        this.ignoredUrls.addAll(Arrays.asList(ignoredUrls));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (CorsUtils.isPreFlightRequest(httpRequest) || isIgnoredUrl(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        try {
            MDC.put("requestId", getRequestId(httpRequest));
            stopWatch.start();
            log.info("request start [api: {}]", httpRequest.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            stopWatch.stop();
            log.info("request end [time: {}ms]", stopWatch.getTotalTimeMillis());
            MDC.clear();
        }
    }

    private String getRequestId(HttpServletRequest httpRequest) {
        String requestId = httpRequest.getHeader("X-Request-ID");
        if (ObjectUtils.isEmpty(requestId)) {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        return requestId;
    }

    private boolean isIgnoredUrl(HttpServletRequest request) {
        return ignoredUrls.contains(request.getRequestURI());
    }
}
