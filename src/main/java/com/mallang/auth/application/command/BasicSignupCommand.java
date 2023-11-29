package com.mallang.auth.application.command;

import com.mallang.auth.domain.BasicMember;
import jakarta.annotation.Nullable;

public record BasicSignupCommand(
        String username,
        String password,
        String nickname,
        @Nullable String profileImageUrl
) {
    public BasicMember toMember() {
        return new BasicMember(
                nickname,
                profileImageUrl,
                username,
                password
        );
    }
}
