package com.mallang.statistics.statistic.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static Cookie[] getRequestCookies() {
        HttpServletRequest request = HttpUtils.getHttpServletRequest();
        if (request.getCookies() == null) {
            return new Cookie[]{};
        }
        return request.getCookies();
    }

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        Objects.requireNonNull(response);
        return response;
    }
}
