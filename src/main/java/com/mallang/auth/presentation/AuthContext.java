package com.mallang.auth.presentation;

import com.mallang.auth.exception.IncorrectUseAuthAtException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class AuthContext {

    private Long memberId;

    public boolean unAuthenticated() {
        return memberId == null;
    }

    public Long getMemberId() {
        if (unAuthenticated()) {
            throw new IncorrectUseAuthAtException();
        }
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
