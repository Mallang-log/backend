package com.mallang.auth.infrastructure.oauth.github;

import com.mallang.auth.domain.oauth.OauthMemberClient;
import com.mallang.auth.infrastructure.oauth.github.client.GithubApiClient;
import com.mallang.auth.infrastructure.oauth.github.dto.GithubMemberResponse;
import com.mallang.auth.infrastructure.oauth.github.dto.GithubToken;
import com.mallang.auth.infrastructure.oauth.github.dto.GithubTokenRequest;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthServerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GithubMemberClient implements OauthMemberClient {

    private final GithubApiClient githubApiClient;
    private final GithubOauthConfig githubOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.GITHUB;
    }

    @Override
    public Member fetch(String authCode) {
        GithubToken token = githubApiClient.fetchToken(tokenRequestParams(authCode));
        GithubMemberResponse githubMemberResponse = githubApiClient.fetchMember("Bearer " + token.accessToken());
        return githubMemberResponse.toMember();
    }

    private GithubTokenRequest tokenRequestParams(String authCode) {
        return new GithubTokenRequest(
                githubOauthConfig.clientId(),
                githubOauthConfig.clientSecret(),
                authCode,
                githubOauthConfig.redirectUri());
    }
}
