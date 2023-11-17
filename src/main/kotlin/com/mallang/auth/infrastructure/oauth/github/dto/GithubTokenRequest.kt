package com.mallang.auth.infrastructure.oauth.github.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GithubTokenRequest(
        val clientId: String,
        val clientSecret: String,
        val code: String,
        val redirectUri: String
)
