package com.mallang.auth.presentation.support;

import com.mallang.auth.exception.NoAuthenticationSessionException;
import com.mallang.common.presentation.UriAndMethodAndParamCondition;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectProvider<PathMatcher> pathMatcher;
    private final AuthContext authContext;
    private final Set<UriAndMethodAndParamCondition> noAuthRequiredConditions = new HashSet<>();

    public void setNoAuthRequiredConditions(UriAndMethodAndParamCondition... noAuthRequiredConditions) {
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
}
