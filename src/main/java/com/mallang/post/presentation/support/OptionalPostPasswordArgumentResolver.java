package com.mallang.post.presentation.support;

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
public class OptionalPostPasswordArgumentResolver implements HandlerMethodArgumentResolver {

    private final PostPasswordContext postPasswordContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OptionalPostPassword.class)
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return postPasswordContext.getPassword();
    }
}
