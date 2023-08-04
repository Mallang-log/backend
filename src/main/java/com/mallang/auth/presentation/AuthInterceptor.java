package com.mallang.auth.presentation;

import static com.mallang.common.auth.AuthConstant.JSESSION_ID;

import com.mallang.auth.exception.InvalidAuthenticationAttributeException;
import com.mallang.auth.exception.NoAuthenticationSessionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthContext authContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = getSession(request);
        Long memberId = Optional.ofNullable(session.getAttribute(JSESSION_ID))
                .map(id -> (Long) id)
                .orElseThrow(InvalidAuthenticationAttributeException::new);
        authContext.setMemberId(memberId);
        return true;
    }

    private HttpSession getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new NoAuthenticationSessionException();
        }
        return session;
    }
}
