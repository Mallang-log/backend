package com.mallang.post.presentation.support;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class PostPasswordContext {

    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public @Nullable String getPassword() {
        return password;
    }
}
