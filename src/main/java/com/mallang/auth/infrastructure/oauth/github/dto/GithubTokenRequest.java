package com.mallang.auth.infrastructure.oauth.github.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record GithubTokenRequest(
        String clientId,
        String clientSecret,
        String code,
        String redirectUri
) {
}
