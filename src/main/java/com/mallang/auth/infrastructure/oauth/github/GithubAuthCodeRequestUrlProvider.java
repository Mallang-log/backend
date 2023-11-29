package com.mallang.auth.infrastructure.oauth.github;

import com.mallang.auth.domain.OauthId.OauthServerType;
import com.mallang.auth.domain.oauth.AuthCodeRequestUrlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
public class GithubAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

    private final GithubOauthConfig githubOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.GITHUB;
    }

    @Override
    public String provide() {
        return UriComponentsBuilder
                .fromUriString("https://github.com/login/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", githubOauthConfig.clientId())
                .queryParam("redirect_uri", githubOauthConfig.redirectUri())
                .queryParam("scope", String.join(",", githubOauthConfig.scope()))
                .toUriString();
    }
}
