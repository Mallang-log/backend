package com.mallang.auth.infrastructure.oauth.github

import com.mallang.auth.domain.OauthServerType
import com.mallang.auth.domain.OauthServerType.GITHUB
import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProvider
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class GithubAuthCodeRequestUrlProvider(
        private val githubOauthConfig: GithubOauthConfig
) : AuthCodeRequestUrlProvider {

    override fun supportServer(): OauthServerType = GITHUB

    override fun provide(): String =
            UriComponentsBuilder
                    .fromUriString("https://github.com/login/oauth/authorize")
                    .queryParam("response_type", "code")
                    .queryParam("client_id", githubOauthConfig.clientId)
                    .queryParam("redirect_uri", githubOauthConfig.redirectUri)
                    .queryParam("scope", githubOauthConfig.scope.joinToString(","))
                    .toUriString()
}
