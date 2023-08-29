package com.mallang.auth.presentation;

import com.mallang.auth.exception.NoAuthenticationSessionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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
        return noAuthRequiredConditions.stream()
                .anyMatch(it -> it.match(pathMatcher.getIfAvailable(), request));
    }

    @Builder
    public record UriAndMethodCondition(
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
            for (String param : params.keySet()) {
                String value = request.getParameter(param);
                if (!params.get(param).equals(value)) {
                    return false;
                }
            }
            return true;
        }

        private boolean matchParamIsNotRequired() {
            return params == null || params.isEmpty();
        }
    }
}
