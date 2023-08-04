package com.mallang.auth.presentation;

import com.mallang.auth.exception.IncorrectUseAuthAtException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthContext {

    private Long memberId;

    public Long getMemberId() {
        if (memberId == null) {
            throw new IncorrectUseAuthAtException();
        }
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
