package com.mallang.common.presentation;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Builder;
import org.springframework.http.HttpMethod;
import org.springframework.util.PathMatcher;

@Builder
public record UriAndMethodAndParamCondition(
        Set<String> uriPatterns,
        Set<HttpMethod> httpMethods,
        Map<String, String> params
) {

    public boolean match(PathMatcher pathMatcher, HttpServletRequest request) {
        return matchURI(pathMatcher, request)
                && matchMethod(request)
                && matchParamIfRequired(request);
    }

    private boolean matchURI(PathMatcher pathMatcher, HttpServletRequest request) {
        return uriPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    private boolean matchMethod(HttpServletRequest request) {
        return httpMethods.contains(HttpMethod.valueOf(request.getMethod()));
    }

    private boolean matchParamIfRequired(HttpServletRequest request) {
        if (matchParamIsNotRequired()) {
            return true;
        }
        for (Entry<String, String> entry : params.entrySet()) {
            String value = request.getParameter(entry.getKey());
            if (!entry.getValue().equals(value)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchParamIsNotRequired() {
        return params == null || params.isEmpty();
    }
}
