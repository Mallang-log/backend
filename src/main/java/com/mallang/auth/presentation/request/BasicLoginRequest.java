package com.mallang.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record BasicLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
