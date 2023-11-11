package com.mallang.post.presentation.support;

import static com.mallang.post.presentation.support.PostPresentationConstant.PROTECTED_PASSWORD_SESSION;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class ExtractPostPasswordInterceptor implements HandlerInterceptor {

    private final PostPasswordContext postPasswordContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Optional.ofNullable(request.getSession(false))
                .map(it -> it.getAttribute(PROTECTED_PASSWORD_SESSION))
                .map(Object::toString)
                .ifPresent(postPasswordContext::setPassword);
        return true;
    }
}
