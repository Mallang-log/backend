package com.mallang.auth.application.command;

import com.mallang.auth.domain.BasicMember;
import com.mallang.auth.domain.Password;
import jakarta.annotation.Nullable;

public record BasicSignupCommand(
        String username,
        String password,
        String nickname,
        @Nullable String profileImageUrl
) {
    public BasicMember toMember(Password password) {
        return new BasicMember(
                nickname,
                profileImageUrl,
                username,
                password
        );
    }
}
