package com.mallang.auth.infrastructure.oauth.github

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth.oauth.github")
data class GithubOauthConfig(
        val redirectUri: String,
        val clientId: String,
        val clientSecret: String,
        val scope: Array<String>
)
