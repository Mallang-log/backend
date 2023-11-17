package com.mallang.auth.infrastructure.oauth.github

import com.mallang.auth.domain.Member
import com.mallang.auth.domain.OauthServerType
import com.mallang.auth.domain.OauthServerType.GITHUB
import com.mallang.auth.domain.oauth.OauthMemberClient
import com.mallang.auth.infrastructure.oauth.github.client.GithubApiClient
import com.mallang.auth.infrastructure.oauth.github.dto.GithubTokenRequest
import org.springframework.stereotype.Component

@Component
class GithubMemberClient(
        private val githubApiClient: GithubApiClient,
        private val githubOauthConfig: GithubOauthConfig
) : OauthMemberClient {

    override fun supportServer(): OauthServerType = GITHUB


    override fun fetch(authCode: String): Member {
        val token = githubApiClient.fetchToken(tokenRequestParams(authCode))
        val githubMemberResponse = githubApiClient.fetchMember("Bearer " + token.accessToken)
        return githubMemberResponse.toMember()
    }

    private fun tokenRequestParams(authCode: String): GithubTokenRequest {
        return GithubTokenRequest(
                githubOauthConfig.clientId,
                githubOauthConfig.clientSecret,
                authCode,
                githubOauthConfig.redirectUri
        )
    }
}
