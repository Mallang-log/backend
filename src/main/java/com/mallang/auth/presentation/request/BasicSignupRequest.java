package com.mallang.auth.presentation.request;

import com.mallang.auth.application.command.BasicSignupCommand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record BasicSignupRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String nickname,
        @Nullable String profileImageUrl
) {
    public BasicSignupCommand toCommand() {
        return new BasicSignupCommand(
                nickname,
                profileImageUrl,
                username,
                password
        );
    }
}
