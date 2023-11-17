package com.mallang.auth.infrastructure.oauth.github.client;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.mallang.auth.infrastructure.oauth.github.dto.GithubMemberResponse;
import com.mallang.auth.infrastructure.oauth.github.dto.GithubToken;
import com.mallang.auth.infrastructure.oauth.github.dto.GithubTokenRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface GithubApiClient {

    @PostExchange(url = "https://github.com/login/oauth/access_token", accept = APPLICATION_JSON_VALUE)
    GithubToken fetchToken(@RequestBody GithubTokenRequest request);

    @GetExchange("https://api.github.com/user")
    GithubMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}
