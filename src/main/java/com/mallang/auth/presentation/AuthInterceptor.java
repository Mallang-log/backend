package com.mallang.auth.presentation;

import com.mallang.auth.exception.NoAuthenticationSessionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectProvider<PathMatcher> pathMatcher;
    private final AuthContext authContext;
    private final Set<UriAndMethodCondition> noAuthRequiredConditions = new HashSet<>();

    public void setNoAuthRequiredConditions(UriAndMethodCondition... noAuthRequiredConditions) {
        this.noAuthRequiredConditions.clear();
        this.noAuthRequiredConditions.addAll(Arrays.asList(noAuthRequiredConditions));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (noAuthenticationRequired(request)) {
            return true;
        }
        if (authContext.unAuthenticated()) {
            throw new NoAuthenticationSessionException();
        }
        return true;
    }

    private boolean noAuthenticationRequired(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        return noAuthRequiredConditions.stream()
                .anyMatch(it -> it.match(pathMatcher.getIfAvailable(), requestURI, HttpMethod.valueOf(method)));
    }

    @Builder
    public record UriAndMethodCondition(
            Set<String> uriPatterns,
            Set<HttpMethod> httpMethods
    ) {
        public boolean match(PathMatcher pathMatcher, String uri, HttpMethod method) {
            boolean matchUri = uriPatterns.stream()
                    .anyMatch(pattern -> pathMatcher.match(pattern, uri));
            return matchUri && httpMethods.contains(method);
        }
    }
}
