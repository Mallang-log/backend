package com.mallang.auth.infrastructure.oauth.github.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GithubToken(
        val tokenType: String,
        val accessToken: String
)
