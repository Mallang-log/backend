package com.mallang.auth.presentation;

import com.mallang.common.auth.OptionalAuth;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class OptionalAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthContext authContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OptionalAuth.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public @Nullable Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        if (authContext.unAuthenticated()) {
            return null;
        }
        return authContext.getMemberId();
    }
}
